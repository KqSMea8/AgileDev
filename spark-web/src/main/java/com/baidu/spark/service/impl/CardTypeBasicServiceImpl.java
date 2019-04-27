package com.baidu.spark.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.baidu.spark.dao.CardPropertyDao;
import com.baidu.spark.dao.CardTypeDao;
import com.baidu.spark.dao.SpaceSequenceDao;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.service.CardBasicService;
import com.baidu.spark.service.CardTypeBasicService;
import com.baidu.spark.util.BeanMapperSingletonWrapper;
import com.baidu.spark.util.SpringSecurityUtils;
import com.baidu.spark.util.StringUtils;

/**
 * 卡片类型和属性服务类
 * @author chenhui
 *
 */
@Service
public class CardTypeBasicServiceImpl implements CardTypeBasicService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private CardTypeDao cardTypeDao;
	
	private SpaceSequenceDao spaceSequenceDao;
	
	private CardPropertyDao cardPropertyDao;
	
	private CardBasicService cardService;
	
	@Override
	public CardType getCardType(Long cardTypeId){
		Assert.notNull(cardTypeId);
		return cardTypeDao.get(cardTypeId);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public CardType getCardType(Long spaceId,Long cardTypeLocalId){
		Assert.notNull(spaceId);
		Assert.notNull(cardTypeLocalId);
		Criterion c2 = Restrictions.eq("localId", cardTypeLocalId);
		Criteria c = cardTypeDao.createCriteria(c2);
		c.createAlias("space","space").add(Restrictions.eq("space.id", spaceId));
		List<CardType> cardTypes = c.list();
		if (!CollectionUtils.isEmpty(cardTypes)) {
			return cardTypes.get(0);
		}
		return null;
	}

	
	@Override
	public List<CardType> getAllCardTypes(Space space) {
		Assert.notNull(space);
		Assert.notNull(space.getId());
		String hql = "from CardType where space = ?";
		return cardTypeDao.find(hql, space); 
	}
	
	@Override
	public boolean checkConflicts(CardType cardType){
		Assert.notNull(cardType);
		Assert.notNull(cardType.getSpace());
		Assert.hasText(cardType.getName());
		Criteria criteria = cardTypeDao.createCriteria();
		criteria.add(Restrictions.eq("space", cardType.getSpace()));
		criteria.add(Restrictions.eq("name", cardType.getName()));
		if( cardType.getId() != null ){
			criteria.add(Restrictions.ne("id", cardType.getId()));
		}
		return criteria.list().size() != 0;
	}

	@Override
	public List<CardType> getValidCardTypesAsParent(CardType cardType) {
		Assert.notNull(cardType);
		Assert.notNull(cardType.getId());
		Assert.notNull(cardType.getSpace());
		List<CardType> all = getAllCardTypes(cardType.getSpace());
		all.remove(cardType);
		all.removeAll(cardType.getAllChildrenTypes());
		return all;
	}
	
	@Override
	public void saveCardType(CardType cardType) {
		assertBasicCardType(cardType);
		Assert.isTrue(cardType.getId() == null );
		if (cardType.getLocalId() == null) {
			Long localId = spaceSequenceDao.getCardTypeLocalIdAndIncrese(cardType.getSpace().getId());
			if(localId == null){
				throw new RuntimeException("card property localId is null");
			}
			cardType.setLocalId(localId);
		}
		cardTypeDao.save(cardType);
	}
	
	@Override
	public List<String> saveNewListOptionKey(Long spaceId,List<String> keys){
		Assert.notNull(spaceId);
		if(keys == null){
			return keys;
		}
		List<String> ret = new ArrayList<String>();
		for(String key:keys){
			if(StringUtils.notEmpty(key)){
				ret.add(key);
			}else{
				Long localId = spaceSequenceDao.getListPropertyValueLocalIdAndIncrease(spaceId);
				if(localId == null){
					throw new RuntimeException("card property localId is null");
				}
				ret.add(localId.toString());
			}
		}
		return ret;
	}
	
	
	@Override
	public void updateCardType(final CardType cardType){
		assertBasicCardType(cardType);
		CardType dbCardType = getCardType(cardType.getId());
		if ( dbCardType!= null ){
			//是否更新而取消自循环
			if( dbCardType.getRecursive() && !cardType.getRecursive()){
				List<Card> cards = cardService.getAllCardsByType(dbCardType);
				User user = SpringSecurityUtils.getCurrentUser();
				for( Card card : cards){
					//只需要修改有上级且类型相同的卡片，无上级或类型不同则不受到自循环的影响
					if( card.getParent() != null && card.getType().getId().equals(card.getParent().getType().getId())){
						//无自循环则将此种类型的所有卡片设置为最近不同类型上级
						card.setParent(getNearestParentOfDifferentType(card));
						card.setLastModifiedUser(user);
						cardService.updateCard(card);
					}
				}
			}
		}
		BeanMapperSingletonWrapper.getInstance().map(cardType, dbCardType, "cardType-basic");
		dbCardType.setParent(cardType.getParent());//mapping the parent if null
		dbCardType.setCardProperties(cardType.getCardProperties());
		cardTypeDao.save(dbCardType);
		
//		暂时不使用线程更新卡片索引，避免因线程调度而造成无法读取另一线程中的事务未提交的数据，防止出现不可预知的操作结果
		new CardByTypeIndexUpdateThread(dbCardType, dbCardType.getSpace()).run();
	}
	
	/**
	 * 校验卡片类型的基本参数
	 * space not null
	 * name not empty
	 * recursive not null
	 * parent is valid
	 * @param cardType
	 */
	private void assertBasicCardType(CardType cardType){
		Assert.notNull(cardType);
		Assert.notNull(cardType.getSpace());
		Assert.notNull(cardType.getRecursive());
		Assert.hasText(cardType.getName());
		if( cardType.getParent() != null ){
			CardType parent = cardType.getParent();
			Assert.isTrue(!parent.equals(cardType));
			Assert.isTrue(!cardType.getAllChildrenTypes().contains(parent));
			if( parent.getSpace() != null ){
				Assert.isTrue(cardType.getSpace().equals(parent.getSpace()));
			}
		}
	}
	
	@Override
	public void deleteCardType(CardType cardType){
		Assert.notNull(cardType);
		logger.info("start delete cardType: {} ", cardType);
		List<Card> cards = cardService.getAllCardsByType(cardType);
		logger.info("start delete {} cards of this type ", cards.size());
		for( Card card : cards){
			//更改该卡片的所有下级为当前卡片的上级
			moveChildCardToValidParent(card);
			//删除该卡片
			cardService.deleteCard(card);
		}
		logger.info("complete all cards of this type.");
		//更改该类型的所有下级类型为当前卡片类型的上级
		moveChildTypeToUpperParent(cardType);
		cardTypeDao.delete(cardType);
		logger.info("complete to delete cardType : {}", cardType);
	}
	
	/**
	 * 设置当前卡片的所有下级为合适的上级
	 * 若当前类型支持自循环，则设置为第一个不同类型的上级
	 * 否则直接设置为当前卡片的上级
	 * @param card 当前卡片
	 */
	private void moveChildCardToValidParent(Card card){
		Assert.notNull(card);
		//TODO card的tostring方法
		logger.info("start to move children to parent of card :{}", card);
		List<Card> children = cardService.getAllCardsByParent(card);
		Card parent = getNearestParentOfDifferentType(card);
		User user = SpringSecurityUtils.getCurrentUser();
		for (Card child : children) {
			child.setParent(parent);
			child.setLastModifiedUser(user);
			cardService.updateCard(child);
		}
	}
	
	/**
	 * 获取当前卡片在卡片树上最近的不同类型的上级卡片
	 * 当前类型若不支持自循环则直接返回上级
	 * 否则递归找到第一个不同类型的上级
	 * @param card 当前卡片
	 * @return 最近的不同类型的上级卡片
	 */
	private Card getNearestParentOfDifferentType(Card card){
		if( card.getParent() != null ){
			if( card.getType().equals(card.getParent().getType())){
				return getNearestParentOfDifferentType(card.getParent());
			}else{
				return card.getParent();
			}
		}
		return null;
	}
	
	/**
	 * 设置当前卡片类型的所有下级为上级的上级
	 * @param cardType
	 */
	private void moveChildTypeToUpperParent(CardType cardType){
		logger.info("start to move children to parent of type :{}", cardType);
		Assert.notNull(cardType);
		Assert.notNull(cardType.getChildren());
		for(CardType child : cardType.getChildren()){
			child.setParent(cardType.getParent());
			cardTypeDao.save(child);
		}
	}

	@Override
	public void saveCardProperty(final CardProperty cardProperty,Collection<CardType> originCardTypes){
		Assert.notNull(cardProperty);
		Assert.notNull(cardProperty.getSpace());
		Assert.notNull(cardProperty.getSpace().getId());

		// 由于同步原因，可能产生相同的sequence，但不影响使用
		if (cardProperty.getSort() == null) {
			Integer maxSequence = cardPropertyDao.findInt("select max(sort) from CardProperty where space = ?", cardProperty.getSpace());
			cardProperty.setSort((maxSequence == null) ? 0 : maxSequence + 1);
		}
		
		if (cardProperty.getLocalId() == null) {
			Long localId = spaceSequenceDao.getCardPropertyLocalIdAndIncrese(cardProperty.getSpace().getId());
			if(localId == null){
				throw new RuntimeException("card property localId is null");
			}
			cardProperty.setLocalId(localId);
		}
		cardPropertyDao.save(cardProperty);
		
		List<CardType> newCardTypes = new ArrayList<CardType>();
		if(cardProperty.getCardTypes() != null){
			newCardTypes.addAll(cardProperty.getCardTypes());
		}
		final List<CardType> diffTypes = getDiffCardType(newCardTypes, originCardTypes);
		//clear session 缓存，避免从session中读取过期的cardType,造成卡片的自定义字段不正确
		cardTypeDao.clear();
//		暂时不使用线程更新卡片索引，避免因线程调度而造成无法读取另一线程中的事务未提交的数据，防止出现不可预知的操作结果
		new CardByTypeIndexUpdateThread(diffTypes, cardProperty.getSpace()).run();
//		// 写历史
//		writeCardPropertyHistory(cardProperty);
	}
	/**
	 * 获取两组cardType的diff
	 * @param cardtypes1 
	 * @param cardtype2
	 * @return
	 */
	private List<CardType> getDiffCardType(Collection<CardType> cardTypes1,Collection<CardType> cardTypes2){
		if(cardTypes1 == null){
			cardTypes1 = new ArrayList<CardType>();
		}
		if(cardTypes2 == null){
			cardTypes2 = new ArrayList<CardType>();
		}
		Map<Long,CardType> map1 = new HashMap<Long,CardType>();
		Map<Long,CardType> map2 = new HashMap<Long,CardType>();
		for(CardType cardType:cardTypes1){
			map1.put(cardType.getId(), cardType);
		}
		for(CardType cardType:cardTypes2){
			map2.put(cardType.getId(), cardType);
		}
		
		List<CardType> retList = new ArrayList<CardType>();
		for(Long key1:map1.keySet()){
			if(!map2.containsKey(key1)){
				retList.add(map1.get(key1));
			}
		}
		for(Long key2:map2.keySet()){
			if(!map1.containsKey(key2)){
				retList.add(map2.get(key2));
			}
		}
		return retList;
	}
	
	@Override
	public CardProperty getCardProperty(Long id) {
		Assert.notNull(id);
		return cardPropertyDao.get(id);
	}
	@SuppressWarnings("unchecked")
	@Override
	public CardProperty getCardProperty(Long spaceId,Long cardPropertyLocalId){
		Assert.notNull(spaceId);
		Assert.notNull(cardPropertyLocalId);
		Criterion c2 = Restrictions.eq("localId", cardPropertyLocalId);
		Criteria c = cardPropertyDao.createCriteria(c2);
		c.add(Restrictions.eq("space.id", spaceId));
		List<CardProperty> properties = c.list();
		if (!CollectionUtils.isEmpty(properties)) {
			return properties.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CardProperty> getAllCardProperties(Long spaceId){
		Assert.notNull(spaceId);
		Criteria c = cardPropertyDao.createCriteria();
		c.add(Restrictions.eq("space.id", spaceId));
		List<CardProperty> properties = c.list();
		return properties;
	}
	
	

	@Override
	public void deleteCardProperty(CardProperty cardProperty) {
		Assert.notNull(cardProperty);
		Assert.notNull(cardProperty.getId());
		cardPropertyDao.delete(cardProperty);

		// 写历史
		writeCardPropertyHistory(cardProperty);
	}
	
	
	/**
	 * 写入卡片属性历史.
	 * @param cardProperty 卡片属性
	 * @param operation 操作类型
	 * @param user 用户
	 */
	private void writeCardPropertyHistory(CardProperty cardProperty) {
		//Adun与2010-08-02注释掉了.暂时先不支持增加删除自定义字段时的历史记录
//		User user = SpringSecurityUtils.getCurrentUser();
//		List<Card> cards = cardService.getAllCardsByType(cardProperty.getCardType());
//		for (Card card : cards) {
//			// 写历史之前先清理实体关联的卡片属性
//			Iterator<CardPropertyValue<?>> iter = card.getPropertyValues().iterator();
//			while (iter.hasNext()) {
//				CardPropertyValue<?> value = iter.next();
//				if (cardProperty.getId().equals(value.getCardProperty().getId())) {
//					iter.remove();
//				}
//			}
//			// 写历史
//			cardHistoryService.saveHistory(card, OpType.Edit, user);
//		}
	}
	
	@Autowired
	public void setCardTypeDao(CardTypeDao cardTypeDao) {
		this.cardTypeDao = cardTypeDao;
	}
	
	@Autowired
	public void setCardService(CardBasicService cardService) {
		this.cardService = cardService;
	}

	@Autowired
	public void setCardPropertyDao(CardPropertyDao cardPropertyDao) {
		this.cardPropertyDao = cardPropertyDao;
	}

	@Autowired
	public void setSpaceSequenceDao(SpaceSequenceDao spaceSequenceDao) {
		this.spaceSequenceDao = spaceSequenceDao;
	}
	/**
	 * 按卡片类型更新卡片索引的线程
	 * @author zhangjing_pe
	 *
	 */
	private class CardByTypeIndexUpdateThread extends Thread{
		
		private List<CardType> cardTypes = null;
		
		private Space space = null;
		
		public CardByTypeIndexUpdateThread(List<CardType> cardTypes,Space space){
			this.cardTypes = cardTypes;
			this.space = space;
			if(cardTypes == null){
				cardTypes = new ArrayList<CardType>();
			}
//			else{
//				for(CardType cardType:cardTypes){
//					cardType.getCardProperties();
//				}
//			}
		}
		public CardByTypeIndexUpdateThread(CardType cardType,Space space){
			cardTypes = new ArrayList<CardType>();
			if(cardType!=null){
				cardTypes.add(cardType);
//				cardType.getCardProperties();
			}
			this.space = space;
		}
		
		public void run(){
			Long begin = System.currentTimeMillis();
			logger.info("cardProperty change invoke card index update begin!space:{}",space);
			int total = 0;
			for(CardType cardType:cardTypes){
				try{
					total += cardService.initCardTypeCardIndex(cardType, true, false);
				}catch(Exception e){
					logger.error("cardProperty index init exception",e);
				}
			}
			logger.info("cardProperty change invoke card index update finished!space:{} total:{} cost:{}",new Object[]{space.getId(),total,System.currentTimeMillis()-begin});
		}
	}

}

