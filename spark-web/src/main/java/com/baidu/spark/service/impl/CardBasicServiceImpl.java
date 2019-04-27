package com.baidu.spark.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.dozer.Mapper;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.ObjectError;

import com.baidu.spark.dao.CardDao;
import com.baidu.spark.dao.CardPropertyValueDao;
import com.baidu.spark.dao.Pagination;
import com.baidu.spark.dao.SpaceSequenceDao;
import com.baidu.spark.exception.IndexException;
import com.baidu.spark.exception.PropertyValueValidationException;
import com.baidu.spark.exception.SparkRuntimeException;
import com.baidu.spark.index.converter.CardIndexConverter;
import com.baidu.spark.index.engine.CardIndexEngine;
import com.baidu.spark.model.Attachment;
import com.baidu.spark.model.OpType;
import com.baidu.spark.model.Project;
import com.baidu.spark.model.QueryVO;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.model.QueryConditionVO.QueryOperationType;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.model.card.property.CardPropertyValue;
import com.baidu.spark.model.card.property.DummyPropertyValue;
import com.baidu.spark.security.PermissionService;
import com.baidu.spark.service.CardBasicService;
import com.baidu.spark.service.CardHistoryService;
import com.baidu.spark.service.CardTypeBasicService;
import com.baidu.spark.service.UserService;
import com.baidu.spark.util.BeanMapperSingletonWrapper;
import com.baidu.spark.util.SpringSecurityUtils;

/**
 * @author zhangjing_pe
 * 
 */
@Service
public class CardBasicServiceImpl implements CardBasicService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private CardDao cardDao;

	private CardPropertyValueDao cardPropertyValueDao;

	private SpaceSequenceDao spaceSequenceDao;

	private CardIndexEngine cardIndexService;

	private CardTypeBasicService cardTypeService;
	
	private PermissionService permissionService;
	
	private UserService userService;
	
	private CardHistoryService cardHistoryService;
	
	/**
	 * 批量更新索引的个数
	 */
	private static final int BATCH_UPDATE_INDEX_SIZE = 1000;
	
	private final static String REQUEST_PROPERTYVALUE_PREFIX = "property_";

	@Override
	public Pagination<Card> getCardsBySpaceId(Long spaceId,
			Pagination<Card> page) {
		return cardDao.getCardsBySpaceId(spaceId, page);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Card getCardBySpaceAndSeq(Space space, Long sequence) {
		Criteria criteria = cardDao.createCriteria();
		criteria.add(Restrictions.eq("sequence", sequence));
		criteria.add(Restrictions.eq("space", space));
		List<Card> cards = criteria.list();
		if (cards != null && !cards.isEmpty()) {
			return cards.get(0);
		}
		return null;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Card> getCardsByIdList(Collection<Long> ids) {
		Criteria criteria = cardDao.createCriteria();
		criteria.add(Restrictions.in("id", ids.toArray()));
		return (List<Card>)criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Card> getRecentUpdateCards(User user, int count) {
		Criteria criteria = cardDao.createCriteria();
		criteria.add(Restrictions.eq("lastModifiedUser", user));
		criteria.addOrder(Order.desc("lastModifiedTime"));
		criteria.setFirstResult(0);
		criteria.setMaxResults(count);
		return criteria.list();
	}
	@Override
	public List<Card> getCardInHierarchy(QueryVO queryVo,Long spaceId,Long parentId) throws IndexException{
		Assert.notNull(queryVo);
		Assert.notNull(spaceId);
		queryVo.addQueryCondition(CardIndexConverter.CARD_SPACE_ID, QueryOperationType.EQUALS, spaceId);
		try {
			return cardIndexService.hierarchyQuery(queryVo, parentId);
		} catch (IndexException e) {
			logger.error("indexQueryError--queryVo:"
					+ (queryVo != null ? queryVo.toString() : "null")
					+ " parentId:"
					+ (parentId != null ? parentId.toString() : "null"));
			throw e;
		}
		
	}

	@Override
	public void deleteCard(Card card) {
		Assert.notNull(card);
		Assert.notNull(card.getId());
		logger.info("delete card by id: {}", card.getId());
		Card dbcard = getCard(card.getId());
		//设置下级的上级路径
		Set<Card> children = dbcard.getChildren();
		if(children != null){
			for(Card child: children){
				child.setParent(card.getParent());
				cardDao.save(child); 
			}
		}
		Card parent = card.getParent();
		if(parent != null && !(parent instanceof HibernateProxy)){
			parent.getChildren().remove(card);
		}
		//删除卡片及索引
		cardDao.delete(card.getId()); 
		try {
			cardIndexService.deleteIndex(card);
		} catch (IndexException e) {
			logger.error("deleteCard index error!", e);
		}
		//设置原有下级的lucene卡片路径
		updateSubCardIndex(card);
		//删除资源
		permissionService.deletePermission(card);
	}

	@Override
	public void saveCard(Card card) {
		Assert.notNull(card);
		Assert.isNull(card.getId());
		Assert.notNull(card.getSpace());
		Assert.notNull(card.getSpace().getId());// 用于生成sequence
		Assert.notNull(card.getCreatedUser());// 用于新增历史
		Long spaceSeq = spaceSequenceDao
				.getCardSeqAndIncrease(card.getSpace().getId());
		if (spaceSeq == null) {
			throw new RuntimeException("not space sequence defined!");
		}
		card.setSequence(spaceSeq);
		card.setCreatedTime(new Date());
		card.setLastModifiedTime(card.getCreatedTime());
		card.setLastModifiedUser(card.getCreatedUser());
		cardDao.save(card);

		if (!CollectionUtils.isEmpty(card.getPropertyValues())) {
			for (CardPropertyValue<?> value : card.getPropertyValues()) {
				value.setCard(card);
				cardPropertyValueDao.save(value);
			}
		}
		
		// 索引卡片
		try {
			cardIndexService.addIndex(card);
		} catch (IndexException e) {
			logger.error("saveCard index error!", e);
		}
		
		//添加资源
		permissionService.createAcl(card);
	}

	@Override
	public Integer initCardTypeCardIndex(CardType cardType,boolean batch,boolean clear) {
		Assert.notNull(cardType);
		Assert.notNull(cardType.getId());
		if(clear){
			try {
				cardIndexService.deleteIndexByField(CardIndexConverter.CARD_TYPE_ID, cardType.getId());
			} catch (IndexException e1) {
				logger.error("cardIndexService clearCardTypeCard error--cardTypeId:"+cardType.getId(), e1);
			}
		}
		Pagination<Card> page = new Pagination<Card>(BATCH_UPDATE_INDEX_SIZE, 1);
		page = getAllCardsByType(cardType, page);
		while (page.getResults() != null && page.getResults().size() > 0) {
			updateCardsIndex(batch, clear, page.getResults());
			cardDao.clear();
			page = new Pagination<Card>( BATCH_UPDATE_INDEX_SIZE,page.getPage() + 1);
			page = getAllCardsByType(cardType, page);
			
		}
		return page.getTotal();
	}

	
	@Override
	public Integer initSpaceCardIndex(Long spaceId,boolean batch,boolean clear) {
		Assert.notNull(spaceId);
		if(clear){
			try {
				cardIndexService.deleteIndexByField(CardIndexConverter.CARD_SPACE_ID, spaceId);
			} catch (IndexException e1) {
				logger.error("cardIndexService clearSpaceCard error--spaceId:"+spaceId, e1);
			}
		}
		Pagination<Card> page = new Pagination<Card>(BATCH_UPDATE_INDEX_SIZE, 1);
		page = getCardsBySpaceId(spaceId, page);
		while (page.getResults() != null && page.getResults().size() > 0) {
			updateCardsIndex(batch, clear, page.getResults());
			cardDao.clear();
			page = new Pagination<Card>(BATCH_UPDATE_INDEX_SIZE,page.getPage() + 1);
			page = getCardsBySpaceId(spaceId, page);
		}
		return page.getTotal();
	}

	/**
	 * @param batch
	 * @param isCreate
	 * @param cardList
	 */
	private void updateCardsIndex(boolean batch, boolean isCreate,
			List<Card> cardList) {
		if(batch){
			try {
				cardIndexService.batchAddIndex(cardList, isCreate, false);
			} catch (IndexException e1) {
				logger.error("initAllCardIndex error--", e1);
			}
		}else{
			for(Card card:cardList){
				try {
					if (isCreate) {
						cardIndexService.addIndex(card);
					} else {
						cardIndexService.updateIndex(card);
					}
				} catch (IndexException e1) {
					logger.error("initAllCardIndex error--", e1);
				}
			}
		}
	}

	@Override
	public Integer initAllCardIndex(boolean batch,boolean clear) {
		if(clear){
			try {
				cardIndexService.clearAllIndex();
			} catch (IndexException e1) {
				logger.error("cardIndexService clearAll error--", e1);
			}
		}
		Pagination<Card> page = new Pagination<Card>(BATCH_UPDATE_INDEX_SIZE, 1);
		page = cardDao.findAll(page);
		while (page.getResults() != null && page.getResults().size() > 0) {
			updateCardsIndex(batch, clear, page.getResults());
			cardDao.clear();
			page = new Pagination<Card>(BATCH_UPDATE_INDEX_SIZE, page.getPage() + 1);
			page = cardDao.findAll(page);
		}
		return page.getTotal();
	}

	/**
	 * 更新卡片信息
	 * 
	 * @param card
	 *            view层的卡片基本信息
	 * @param cpvList
	 *            自定义属性值
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void updateCard(Card card) {
		Assert.notNull(card.getLastModifiedUser());
		Assert.notNull(card.getLastModifiedUser().getId());
		Collection<CardPropertyValue<?>> cpvList = card.getPropertyValues();
		// 更新卡片基本数据信息
		Card dbCard = cardDao.get(card.getId());
		//计算上级是否有变更
		boolean parentChanged = false;
		if(card.getParent() == null && dbCard.getParent()!=null || dbCard.getParent()== null&&card.getParent()!= null || dbCard.getParent()!=null&&card.getParent()!=null&&!dbCard.getParent().getId().equals(card.getParent().getId())){
			parentChanged = true;
		}
		Mapper mapper = BeanMapperSingletonWrapper.getInstance();
		mapper.map(card, dbCard, "cardType-updateCard");
		dbCard.setLastModifiedTime(new Date());
		dbCard.setParent(card.getParent());
		List<Attachment> attachments = dbCard.getAttachments();
		card.setAttachments(attachments);

		// 更新卡片自定义字段信息--初始化辅助数据
		List<CardPropertyValue> dbValues = cardPropertyValueDao.findByProperty(
				"card", dbCard);
		Map<Long, CardPropertyValue<?>> dbPropertyValueMap = new HashMap<Long, CardPropertyValue<?>>();
		for (CardPropertyValue<?> cpv : dbValues) {
			dbPropertyValueMap.put(cpv.getCardProperty().getId(), cpv);
		}
		// 计算更新的卡片自定义字段信息，（增加或更新，删除）
		List<CardPropertyValue<?>> saveOrUpdateList = new ArrayList<CardPropertyValue<?>>();

		if (!CollectionUtils.isEmpty(card.getPropertyValues())) {
			for (CardPropertyValue<?> cpv : cpvList) {
				// 更新
				if (dbPropertyValueMap.containsKey(cpv.getCardProperty()
						.getId())) {
					CardPropertyValue dbCpv = dbPropertyValueMap.get(cpv
							.getCardProperty().getId());
					dbPropertyValueMap.remove(cpv.getCardProperty().getId());
					dbCpv.setValue(cpv.getValue());
					saveOrUpdateList.add(dbCpv);
				} else {
					// 新增
					saveOrUpdateList.add(cpv);
				}
			}
		}
		// 更新信息
		for (CardPropertyValue<?> cpv : saveOrUpdateList) {
			cpv.setCard(dbCard);
			cardPropertyValueDao.save(cpv);
		}
		// 删除无效的信息
		for (CardPropertyValue<?> cpv : dbPropertyValueMap.values()) {
			cardPropertyValueDao.delete(cpv);
		}
		dbCard.setPropertyValues(saveOrUpdateList);
		
		cardDao.save(dbCard);
		card.setPropertyValues(saveOrUpdateList);
		try {
			cardIndexService.updateIndex(dbCard);
		} catch (IndexException e) {
			logger.error("updateCard index error!", e);
		}
		//如果卡片上级变更，则更新卡片的所有下级
		if (parentChanged) {
			updateSubCardIndex(dbCard);
		}
	}
	
	private void updateSubCardIndex(Card parentCard){
		List<Card> cardList = getAllSubCard(parentCard);
		logger.debug("update subCardIndex size:"+cardList.size());
		updateCardsIndex(false,false,cardList);
	}

	private List<Card> getAllSubCard(Card parentCard){
		List<Card> retList = new ArrayList<Card>();
		if(parentCard == null){
			return retList;
		}
		Set<Card> cardSet = parentCard.getChildren();
		Set<Long> cardIds = new HashSet<Long>();
		if (cardSet != null && cardSet.size() > 0) {
			while(!cardSet.isEmpty()){
				Set<Card> targetSet = new HashSet<Card>();
				for(Card it : cardSet){
					retList.add(it);
					targetSet.addAll(it.getChildren());
					if(cardIds.contains(it.getId())){
						throw new SparkRuntimeException("card children in cycle found!");
					}
					cardIds.add(it.getId());
				}
				cardSet = targetSet;
				targetSet = new HashSet<Card>();
			}
			
		}
		return retList;
	}

	@Override
	public Card getCard(Long id) {
		return cardDao.get(id);
	}

	@Override
	public List<Card> getAllCardsByType(CardType cardType) {
		Assert.notNull(cardType);
		String hql = "from Card where type = ?";
		return cardDao.find(hql, cardType);
	}
	
	@Override
	public Pagination<Card> getAllCardsByType(CardType cardType,Pagination<Card> pagination) {
		Assert.notNull(cardType);
		String hql = "from Card where type = ?";
		return cardDao.find(pagination,hql, cardType);
	}

	@Override
	public Long getCountOfCardsByType(Long cardTypeId) {
		Assert.notNull(cardTypeId);
		String hql = "select count(*) from Card where type.id = ?";
		return cardDao.findLong(hql, cardTypeId);
	}

	@Override
	public Long getCountOfCardsByParentType(Long parentTypeId) {
		Assert.notNull(parentTypeId);
		String hql = "select count(*) from Card where parent.type.id = ?";
		return cardDao.findLong(hql, parentTypeId);
	}

	@Override
	public Long getCountOfCardByParentType(Long parentTypeId, Long currentTypeId) {
		Assert.notNull(parentTypeId);
		String hql = "select count(*) from Card where parent.type.id = ? and parent.type.id = ?";
		return cardDao.findLong(hql, currentTypeId, parentTypeId);
	}

	@Override
	public List<Card> getAllCardsByParent(Card parent) {
		Assert.notNull(parent);
		String hql = "from Card where parent = ?";
		return cardDao.find(hql, parent);
	}

	@Override
	public Pagination<Card> getAllCardsByParent(Card parent,
			Pagination<Card> pagination) {
		Assert.notNull(parent);
		String hql = "from Card where parent = ?";
		return cardDao.find(pagination, hql, parent);
	}

	@Override
	public List<Card> queryByCardQueryVO(final QueryVO queryVo, Long spaceId,
			Pagination<Card> cardPage) throws IndexException{
		Assert.notNull(spaceId);
		Assert.notNull(queryVo);
		queryVo.addQueryCondition(CardIndexConverter.CARD_SPACE_ID, QueryOperationType.EQUALS, spaceId);
		queryVo.addQuerySort("sequence", true);
		try {
			return cardIndexService.queryByCardQueryVO(queryVo, cardPage);
		} catch (IndexException e) {
			logger.error("indexQueryError--queryVo:"
					+ (queryVo != null ? queryVo.toString() : "null")
					+ " spaceId:" + spaceId + " cardPage:"
					+ (cardPage != null ? cardPage.toString() : "null"));
			throw e;
		}
	}

	@Override
	public List<Card> queryByCardQueryVO(final QueryVO queryVo,
			Pagination<Card> cardPage) throws IndexException{
		Assert.notNull(queryVo);
		try {
			return cardIndexService.queryByCardQueryVO(queryVo, cardPage);
		} catch (IndexException e) {
			logger.error("indexQueryError--queryVo:"
					+ (queryVo != null ? queryVo.toString() : "null")
					+ " cardPage:"
					+ (cardPage != null ? cardPage.toString() : "null"));
			throw e;
		}
	}

	@Override
	public List<Card> getRootCardsValidForCardParent(Card currentCard) {
		Assert.notNull(currentCard);
		Assert.notNull(currentCard.getSpace());

		Space space = currentCard.getSpace();
		CardType currentCardType = currentCard.getType();
		List<Card> results = new LinkedList<Card>();
		List<Card> results1 = new LinkedList<Card>();
		List<Card> results2 = new LinkedList<Card>();
		String hql, validParentTypeIds = generateValidParentTypeIds(currentCardType);

		if (!StringUtils.isBlank(validParentTypeIds)) {
			hql = "from Card where parent is null and space = ? and type in ("
					+ validParentTypeIds + ")";
			results1 = cardDao.find(hql, space);
			results.addAll(results1);
		}

		// 如果本卡片类型可以自循环
		if (currentCardType.getRecursive()) {
			// 那么从根节点找，则与本卡片同类型的根节点卡片，也可作上级
			hql = "from Card where parent is null and space = ? and type = ? and id != ?";
			results2 = cardDao.find(hql, space, currentCardType, currentCard
					.getId());
			results.addAll(results2);
		}

		return results;
	}

	@Override
	public List<Card> getCardsUnderBaseCardValidForCardParent(Card currentCard,
			Card baseCard) {
		Assert.notNull(currentCard);
		Assert.notNull(currentCard.getSpace());
		Assert.notNull(baseCard);
		Assert.notNull(baseCard.getSpace());
		Assert.isTrue(baseCard.getSpace().equals(currentCard.getSpace()));

		Space space = currentCard.getSpace();
		CardType currentCardType = currentCard.getType();
		List<Card> results = new LinkedList<Card>();
		List<Card> results1 = new LinkedList<Card>();
		List<Card> results2 = new LinkedList<Card>();
		String hql, validParentTypeIds = generateValidParentTypeIds(currentCardType);

		if (!StringUtils.isBlank(validParentTypeIds)) {
			hql = "from Card where parent = ? and space = ? and type in ("
					+ validParentTypeIds + ")";
			results1 = cardDao.find(hql, baseCard, space);
			results.addAll(results1);
		}

		// 如果本卡片类型可以自循环，那么与本卡片同类型、而且不是本卡片的子卡片的卡片，也可作上级
		if (currentCardType.getRecursive()) {
			List<Card> invalidForRarentCards = getAllCardsByParent(currentCard);
			StringBuilder sb = new StringBuilder();
			for (Card invalidForRarentCard : invalidForRarentCards) {
				sb.append(invalidForRarentCard.getId());
				sb.append(",");
			}
			sb.append(currentCard.getId());
			String invalidForRarentCardIds = sb.toString();
			hql = "from Card where parent  = ? and space  = ? and type = ? ";
			if (StringUtils.isNotBlank(invalidForRarentCardIds)) {
				hql += " and id not in (" + invalidForRarentCardIds + ")";
			}
			results2 = cardDao.find(hql, baseCard, space, currentCardType);
			results.addAll(results2);
		}
		return results;
	}

	@Override
	public List<Card> getRootCardsValidForParentByCardType(
			CardType currentCardType) {
		Assert.notNull(currentCardType);
		Assert.notNull(currentCardType.getSpace());

		Space space = currentCardType.getSpace();
		String validParentTypeIds = generateValidParentTypeIds(currentCardType);
		if(currentCardType.getRecursive()){
			if(StringUtils.isBlank(validParentTypeIds)){
				validParentTypeIds = currentCardType.getId().toString();
			}else{
				validParentTypeIds += ", " + currentCardType.getId();
			}
		}
		List<Card> results = new LinkedList<Card>();
		if (!StringUtils.isBlank(validParentTypeIds)) {
			String hql = "from Card where parent is null and space = ? and type in ("
					+ validParentTypeIds + ")";
			results = cardDao.find(hql, space);
		}
		return results;
	}

	@Override
	public List<Card> getCardsUnderBaseCardValidForParentByCardType(
			CardType currentCardType, Card baseCard) {
		Assert.notNull(currentCardType);
		Assert.notNull(currentCardType.getSpace());
		Assert.notNull(baseCard);
		Assert.notNull(baseCard.getSpace());
		Assert.isTrue(baseCard.getSpace().equals(currentCardType.getSpace()));

		Space space = currentCardType.getSpace();
		String validParentTypeIds = generateValidParentTypeIds(currentCardType);
		if(currentCardType.getRecursive()){
			if(StringUtils.isBlank(validParentTypeIds)){
				validParentTypeIds = currentCardType.getId().toString();
			}else{
				validParentTypeIds += ", " + currentCardType.getId();
			}
		}
		List<Card> results = new LinkedList<Card>();

		if (!StringUtils.isBlank(validParentTypeIds)) {
			String hql = "from Card where parent = ? and space = ? and type in ("
					+ validParentTypeIds + ")";
			results = cardDao.find(hql, baseCard, space);
		}
		return results;
	}


	/**
	 * 返回指定卡片类型上级卡片类型的ID字符串
	 * 
	 * @param currentCardType
	 * @return
	 */
	private String generateValidParentTypeIds(CardType currentCardType) {
		// 所有类型为本卡片类型祖先的节点均可作为上级
		StringBuilder sb = new StringBuilder();
		for(CardType type = currentCardType.getParent() ; type !=null ; type = type.getParent()){
			sb.append(type.getId());
			sb.append(",");
		}
		return StringUtils.removeEnd(sb.toString(), ",");
	}
 
	 

	@Override
	public boolean isAssignableAsParent(Card card_to_be_changed,
			Card card_as_parent) {
		if (card_to_be_changed == null || card_as_parent == null) {
			return false;
		}

		if (card_to_be_changed.equals(card_as_parent)) {
			return false;
		}

		Space card_as_parent_space = card_as_parent.getSpace();
		Space card_to_be_changed_space = card_to_be_changed.getSpace();

		if (card_as_parent_space == null || card_to_be_changed_space == null
				|| !card_as_parent_space.getId().equals(card_to_be_changed_space.getId())) {
			return false;
		}

		CardType card_to_be_changed_type = card_to_be_changed.getType();
		CardType card_as_parent_type = card_as_parent.getType();
		if (card_to_be_changed_type == null || card_as_parent_type == null) {
			return false;
		}

		List<CardType> validParentTypes = cardTypeService
				.getValidCardTypesAsParent(card_to_be_changed_type);
		if (validParentTypes.contains(card_as_parent_type)) {
			return true;
		}

		if (!card_to_be_changed_type.equals(card_as_parent_type)
				|| !card_to_be_changed_type.getRecursive()) {
			return false;
		}

		List<Card> invalidForRarentCards = getAllCardsByParent(card_to_be_changed);
		if (invalidForRarentCards.contains(card_as_parent)) {
			return false;
		}
		return true;
	}

	@Override
	public List<Card> getAllRootCards(Space space) {
		Assert.notNull(space);
		Assert.notNull(space.getId());
		String hql = "from Card where parent is null and space = ?";
		return cardDao.find(hql, space);
	}

	@Override
	public Pagination<Card> getAllRootCards(Space space,
			Pagination<Card> pagination) {
		Assert.notNull(space);
		Assert.notNull(space.getId());
		String hql = "from Card where parent is null and space = ?";
		return cardDao.find(pagination, hql, space);
	}
	
	@Override
	public List<Card> getCardsInProject(Project project) {
		Assert.notNull(project);
		Assert.notNull(project.getId());
		Assert.notNull(project.getSpace());
		String hql = "from Card where project = ?";
		return cardDao.find(hql, project);
	}

	@Override
	public List<Long> getAncestorCardIds(Card card) {
		Assert.notNull(card);
		List<Long> ids = new LinkedList<Long>();
		for(Card p = card.getParent(); p!= null ; p = p.getParent()){
			ids.add(p.getId());
		}
		return ids;
	}
	
	@Override
	public Collection<CardPropertyValue<?>> getCardPropertyValueFromCard(
			Card card) {
		Assert.notNull(card);
		Assert.notNull(card.getType());
		CardType cardType = card.getType();
		List<CardPropertyValue<?>> valueList = card.getPropertyValues();
		Map<Long, CardPropertyValue<?>> valueMap = new HashMap<Long, CardPropertyValue<?>>();
		if (valueList != null) {
			for (CardPropertyValue<?> value : valueList) {
				valueMap.put(value.getCardProperty().getId(), value);
			}
		}
		if (cardType != null && cardType.getCardProperties() != null) {
			List<CardPropertyValue<?>> list = new ArrayList<CardPropertyValue<?>>();
			for (CardProperty cp : cardType.getCardProperties()) {
				if (valueMap.containsKey(cp.getId())) {
					list.add(valueMap.get(cp.getId()));
				} else {
					list.add(cp.generateValue(null));
				}

			}
			return list;
		}
		return null;
	}
	
	@Override
	public List<CardProperty> getCommonProperties(Collection<Card> cards) {
		Assert.notNull(cards);
		Set<CardType> types = new HashSet<CardType>();
		for(Card card : cards){
			types.add(card.getType());
		}
		if(types.isEmpty()){
			return Collections.<CardProperty>emptyList();
		}
		Set<CardProperty> properties = new HashSet<CardProperty>(types.iterator().next().getCardProperties());
		for(CardType type : types){
			properties.retainAll(type.getCardProperties());
		}
		List<CardProperty> list = new LinkedList<CardProperty>(properties);
		Collections.sort(list, new Comparator<CardProperty>(){
			@Override
			public int compare(CardProperty cp1, CardProperty cp2) {
				return cp1.getId() > cp2.getId() ? 1 : cp1.getId().equals(cp2.getId()) ? 0 : -1;
			}
		});
		return list;
	}

	@Override
	public List<CardPropertyValue<?>> generateCardPropertyValues(
			Map<String, ?> parameterMap, Collection<CardProperty> properties, List<ObjectError> validationErrors) {
		Assert.notNull(parameterMap);
		Assert.notNull(validationErrors);
		
		if ( CollectionUtils.isEmpty(properties)) {
			return Collections.<CardPropertyValue<?>>emptyList();
		}
		List<CardPropertyValue<?>> ret = new ArrayList<CardPropertyValue<?>>();
		// 从parameter中获取对应的值
		for (CardProperty cp : properties) {
			String key = generatePropertyValueKey(cp);
			Object originalValue =  parameterMap.get(key);
			String value;
			if(originalValue == null){
				value = null;
			} else if(originalValue instanceof String){
				value = (String) originalValue;
			} else if(originalValue instanceof String[]){
				String[] values = (String[]) originalValue;
				value = ArrayUtils.isEmpty(values) ? null : values[0];
			} else {
				value = null;
			}
			
			// 如果传入的是"保持原值"的标志位, 则什么也不做, 加入DummyPropertyValue
			if(CardPropertyValue.RETAIN_FLAG.equals(value)){
				ret.add(new DummyPropertyValue(cp, null));
				continue;
			}
			
			//如果是用户类型数据,则转换成id
			//TODO 这段是不是应该放到UserPropertyValue里？
			if ("user".equals(cp.getType())){
				User user = userService.getUserByUserName(value);
				value = user==null ? null :user.getId().toString();
			}
			
			try {
				cp.validatePropertyValue(value);
				ret.add(cp.generateValue(value));
			} catch (PropertyValueValidationException ex) {
				validationErrors.add(new ObjectError("propertyValue",
						new String[] { ex.getMessageCode() },
						ex.getArguments(), null));
				ret.add(new DummyPropertyValue(cp, value));
			}
		}
		return ret;
	}
	
	private String generatePropertyValueKey(CardProperty cp) {
		return REQUEST_PROPERTYVALUE_PREFIX + cp.getId() ;
	}
	
	@Override
	public void batchUpdateCards(Space space, Collection<Card> cards, 
			List<CardPropertyValue<?>> newCpvList) {
		for(Card card : cards){
			updateCardProperties(space,card,newCpvList);
		}
	}
	
	@Override
	public void updateCardProperties(Space space,Card card,List<CardPropertyValue<?>> newCpvList){
		User user = SpringSecurityUtils.getCurrentUser();
		// 验证通过，保存卡片信息
		List<CardPropertyValue<?>> oldCpvList = card.getPropertyValues();
		mergePropertyValues(newCpvList, oldCpvList);
		card.setLastModifiedTime(new Date());
		card.setLastModifiedUser(user);
		updateCard(card);
		cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
	}

	@SuppressWarnings("unchecked")
	private void mergePropertyValues(List<CardPropertyValue<?>> newCpvList, List<CardPropertyValue<?>> oldCpvList){
		Map<Long, CardPropertyValue<?>> oldCpvMap = new HashMap<Long, CardPropertyValue<?>>(oldCpvList.size());
	 	for(CardPropertyValue<?> oldCpv : oldCpvList){
	 		oldCpvMap.put(oldCpv.getCardProperty().getId(), oldCpv);
	 	}
	 	for(CardPropertyValue<?> newCpv : newCpvList){
	 		if(newCpv instanceof DummyPropertyValue) continue ;
	 		CardPropertyValue oldCpv = oldCpvMap.get(newCpv.getCardProperty().getId());
	 		if(oldCpv == null){
	 			oldCpvList.add(newCpv.clone()) ;
	 		} else{
	 			oldCpv.setValue(newCpv.getValue());
	 		}
	 	}
	}
	
	@Override
	public void cascadeDelete(Card card) {
		Assert.notNull(card);
		Assert.notNull(card.getId());
		logger.info("delete card by id: {}", card.getId());
		Card dbcard = getCard(card.getId());
		if(dbcard == null) return;
		
		Set<Card> children = dbcard.getChildren();
		for(Card child : children){
			cascadeDelete(child);
		}
		cardDao.delete(dbcard);
		
		try {
			cardIndexService.deleteIndex(dbcard);
		} catch (IndexException e) {
			logger.error("deleteCard index error!", e);
		}
		permissionService.deletePermission(dbcard);
		
	}

	@Autowired
	public void setCardDao(CardDao cardDao) {
		this.cardDao = cardDao;
	}

	@Autowired
	public void setSpaceSequenceDao(SpaceSequenceDao spaceSequenceDao) {
		this.spaceSequenceDao = spaceSequenceDao;
	}

	@Autowired
	public void setCardPropertyValueDao(
			CardPropertyValueDao cardPropertyValueDao) {
		this.cardPropertyValueDao = cardPropertyValueDao;
	}

	@Autowired
	public void setCardIndexService(CardIndexEngine cardIndexService) {
		this.cardIndexService = cardIndexService;
	}

	@Autowired
	public void setCardTypeService(CardTypeBasicService cardTypeService) {
		this.cardTypeService = cardTypeService;
	}
	
	@Autowired
	public void setPermissionService(PermissionService permissionService){
		this.permissionService = permissionService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Autowired
	public void setCardHistoryService(CardHistoryService cardHistoryService) {
		this.cardHistoryService = cardHistoryService;
	}

}
