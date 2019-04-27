package com.baidu.spark.index.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.baidu.spark.index.engine.helper.ListFieldComparatorSource;
import com.baidu.spark.model.card.property.ListProperty;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.baidu.spark.exception.SparkRuntimeException;
import com.baidu.spark.model.QueryConditionVO;
import com.baidu.spark.model.QuerySortVO;
import com.baidu.spark.model.QueryVO;
import com.baidu.spark.model.User;
import com.baidu.spark.model.QueryConditionVO.QueryOperationType;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.model.card.property.CardPropertyValue;
import com.baidu.spark.model.card.property.TextProperty;
import com.baidu.spark.service.CardBasicService;
import com.baidu.spark.service.CardTypeBasicService;
import com.baidu.spark.service.UserService;
import com.baidu.spark.util.LuceneUtils;
import com.baidu.spark.util.ReflectionUtils;
/**
 * 卡片与索引结果的转换器
 * @author zhangjing_pe
 * @author shixiaolei
 */
@Service("cardIndexConverter")
public class CardIndexConverter extends SimpleReflectionIndexConverter<Card> {
	
	/**一个实际卡片属性值不可能取到的值.*/
	private static final String IMPOSSIBLE_PROPERTY_VALUE = "-1";
	
	/**自定义字段进行索引时的固定前缀*/
	//不再用前缀标识。下面的代码中，自定义字段都以 是否是数字 来标识
	public static final String INDEX_CUS_FIELD_PREFIX = "";
	/**祖辈字段的分隔符*/
	public static final String ANCESTOR_SEPERATOR = "-";
	
	/**卡片创建人*/
	public static final String CARD_CREATED_USER_ID = "createdUser";
	/**卡片上次修改人人*/
	public static final String CARD_LAST_MODIFIED_USER_ID = "lastModifiedUser";
	/**卡片类型*/
	public static final String CARD_TYPE_ID = "cardType";
	/**父卡片id*/
	public static final String PARENT_CARD_ID = "parent";
	/**父卡片sequenceid*/
	public static final String PARENT_CARD_SEQUENCE= "parentSequence";
	/**祖辈id*/
	public static final String ANCESTOR = "ancestor";
	/**空间id*/
	public static final String CARD_SPACE_ID = "space";
	/**项目id*/
	public static final String CARD_PROJECT_ID = "project";
	/**卡片字符串描述的info */
	public static final String CARD_STR_INFO = "cardInfo";
	/**最后修改时间*/
	public static final String CARD_LAST_MODIFIED_TIME = "lastModifiedTime";
	/**反射的属性名*/
	private static final String[] reflectField = {"id","sequence","title","detail","createdTime",CARD_LAST_MODIFIED_TIME};
	
	private CardBasicService cardService;
	
	private CardTypeBasicService cardTypeService;
	
	private UserService userService ;
	
	/**
	 * 获取卡片对应的document对象
	 * 处理关联关系和自定义字段值
	 */
	@Override
	public Document getIndexableDocument(Card card) {
		Assert.notNull(card);
		Assert.notNull(card.getId());
		Document document = super.getIndexableDocument(card);
		//处理卡片的关联关系
		document.add(getFieldFromObject(CARD_CREATED_USER_ID, card.getCreatedUser()==null?null:card.getCreatedUser().getId(),Long.class));
		document.add(getFieldFromObject(CARD_LAST_MODIFIED_USER_ID, card.getLastModifiedUser()==null?null:card.getLastModifiedUser().getId(),Long.class));
		document.add(getFieldFromObject(CARD_TYPE_ID, card.getType()==null?null:card.getType().getId(),Long.class));
		document.add(getFieldFromObject(PARENT_CARD_ID, card.getParent()==null?null:card.getParent().getId(),Long.class));
		document.add(getFieldFromObject(PARENT_CARD_SEQUENCE, card.getParent()==null?null:card.getParent().getSequence(),Long.class));
		document.add(getFieldFromObject(CARD_SPACE_ID, card.getSpace()==null?null:card.getSpace().getId(),Long.class));
		document.add(getFieldFromObject(CARD_PROJECT_ID, card.getProject()==null?null:card.getProject().getId(),Long.class));
		//处理卡片文本信息，拼装为一个查询字段
		document.add(getFieldFromObject(CARD_STR_INFO,getCardStrInfo(card),String.class));
		//处理自定义字段
		Collection<CardPropertyValue<?>> values = getFullCardPropertyValue(card);
		if(values != null){
			for(CardPropertyValue<?> value:values){
				if(value.getCardProperty() == null || value.getCardProperty().getId() == null){
					throw new SparkRuntimeException("global.exception.indexException");
				}
				document.add(getFieldFromObject(INDEX_CUS_FIELD_PREFIX+value.getCardProperty().getId(), value.getValue(),ReflectionUtils.getDeclaredField(value, "value").getType()));
			}
		}
		//处理祖辈的路径字段
		StringBuilder sb = new StringBuilder(ANCESTOR_SEPERATOR);
		if(card.getParent()!=null){
			Card current = cardService.getCard(card.getParent().getId());
			while( current != null){
				sb.insert(0, current.getId());
				sb.insert(0, ANCESTOR_SEPERATOR);
				current = current.getParent();
			}
		}
		document.add(getFieldFromObject(ANCESTOR, sb.toString()));
		return document;
	}
	
	/**
	 * 获取完整的卡片定义。
	 * 从卡片中获得卡片属性，并从cardType中的cardProperty列表中与卡片原有属性进行merge
	 * 新建卡片属性后，通过批量更新索引来支持对新增卡片属性的查询的支持
	 * @param card
	 * @return
	 */
	private Collection<CardPropertyValue<?>> getFullCardPropertyValue(Card card) {
		Assert.notNull(card);
		Assert.notNull(card.getType());
		CardType cardType = card.getType();
		List<CardPropertyValue<?>> cardPropertyValue = card.getPropertyValues();
		
		Map<Long, CardPropertyValue<?>> valueMap = new HashMap<Long, CardPropertyValue<?>>();
		if (cardPropertyValue != null) {
			for (CardPropertyValue<?> value : cardPropertyValue) {
				valueMap.put(value.getCardProperty().getId(), value);
			}
		}
		if(cardType!=null){
			cardType = cardTypeService.getCardType(cardType.getId());
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
		return new ArrayList<CardPropertyValue<?>>();
	}

	/**
	 * 根据id到库里查询数据记录返回
	 * 从lucene获取的对象，仍然要根据关联关系到库中查询各种关联对象，直接从库里使用外连接获取card和关联对象。
	 * 对于分页查询的方法，使用这种方式获取关联对象，避免多次查询，效率并不低
	 * 如果查询不到，返回null
	 * TODO 增加额外方法，实现从lucene数据中生成对象，或根据制定的字段生成对象，为一些特殊的计算逻辑避免过多的数据库操作
	 */
	@Override
	public Card getOriginObj(Document document) {
		Assert.notNull(document);
		//从索引中获取id，再到库里查询记录。
		String value = document.get(getKeyFieldName());
		Card card = cardService.getCard(Long.parseLong(value));
		return card;
		/*super.setOriginObj(document, card);
		//处理关联关系
		String value = null;
		value = document.get(CARD_CREATED_USER_ID);
		if(StringUtils.notEmpty(value)){
			User user = userService.getUserById(Long.parseLong(value));
			if(user != null){
				card.setCreatedUser(user);
			}
		}
		value = document.get(CARD_LAST_MODIFIED_USER_ID);
		if(StringUtils.notEmpty(value)){
			User user = userService.getUserById(Long.parseLong(value));
			if(user != null){
				card.setLastModifiedUser(user);
			}
		}
		value = document.get(CARD_TYPE_ID);
		if(StringUtils.notEmpty(value)){
			CardType cardType = cardTypeService.getCardTypeById(Long.parseLong(value));
			if(cardType != null){
				card.setType(cardType);
			}
		}
		value = document.get(PARENT_CARD_ID);
		if(StringUtils.notEmpty(value)){
			Card parent = cardService.getCardById(Long.parseLong(value));
			if(parent != null){
				card.setParent(parent);
			}
		}
		
		//设置自定义属性字段
		 */
	}
	@Override
	public List<Card> getOriginObj(List<Document> documents){
		List<Card> retList = new ArrayList<Card>();
		List<Long> cardIds = new ArrayList<Long>();
		if(!CollectionUtils.isEmpty(documents)){
			for(Document document:documents){
				String value = document.get(getKeyFieldName());
				try{
					cardIds.add(Long.parseLong(value));
				}catch(NumberFormatException e){
					logger.error("cardId format error", e);
				}
			}
			if(!CollectionUtils.isEmpty(cardIds)){
				List<Card> result = cardService.getCardsByIdList(cardIds);
				//排出正确的顺序
				for(Long cardId:cardIds){
					Iterator<Card> cardIt = result.iterator();
					while(cardIt.hasNext()){
						Card card = cardIt.next();
						if(card.getId().equals(cardId)){
							retList.add(card);
							break;
						}
					}
				}
			}
		}
		return retList;
	}
	
	@Override
	public Query getQueryFromVo(final QueryVO queryVo){
		BooleanQuery retQuery = (BooleanQuery)super.getQueryFromVo(queryVo);
		List<QueryConditionVO> queryParamList = null;
		if(queryVo != null){
			queryParamList = queryVo.getQueryConditionList();
		}
		Map<String, BooleanQuery> positiveQueryMap = new HashMap<String, BooleanQuery>();
		Map<String, BooleanQuery> negativeQueryMap = new HashMap<String, BooleanQuery>();
		if(queryParamList!=null&&!queryParamList.isEmpty()){
		//从查询条件中获取spaceId.
			Long spaceId = 0L;
			for (QueryConditionVO param : queryParamList){
				if (param.getFieldName().equals(CARD_SPACE_ID)){
					spaceId = com.baidu.spark.util.StringUtils.parseLong(param.getValue());
					break;
				}
			}
			for(QueryConditionVO param:queryParamList){
				String fieldName = param.getFieldName();
				if(ArrayUtils.contains(reflectField, fieldName)){
					//父类已处理
					continue;
				}
				//处理自定义字段
				if(null != fieldName && fieldName.length()>0 && StringUtils.isNumeric(fieldName)){
					if(spaceId == 0L){
						throw new SparkRuntimeException("need SpaceId query param!");
					}
					CardProperty property = null;
					try{
						property = cardTypeService.getCardProperty(spaceId, Long.parseLong(fieldName));
					}catch(NumberFormatException e){
						logger.error("number format exception {}" , param.toString(),e);
						throw new SparkRuntimeException("global.exception.indexException");
					}
					if(property==null){
						continue;
					}
					if(property.getCardTypes() == null){
						continue;
					}
					//获取value对象，反射出实际属性的类型
					CardPropertyValue<?> propertyValue = property.generateValue(null);
					Class<?> fieldClazz = getReflectFieldType(propertyValue, "value");
					
					Object value = param.getValue();
					//对于用户类型的字段进行转换.从userName转换成userId
					if (property.getType().equals("user")){
						String strValue = param.getValue();
						if(StringUtils.isBlank(strValue)){
							value = null;
						}else{
							User user = userService.getUserByUserName(strValue);
							value = null == user ? IMPOSSIBLE_PROPERTY_VALUE : user.getId().toString();
						}
					}
					value = getObjectFromQueryValue((String)value, fieldClazz);
					QueryOperationType type = param.getOperationType();
					groupQueryByField(property.getId().toString(), type , value, positiveQueryMap, negativeQueryMap);
				}else{
					if (fieldName.equals(CARD_CREATED_USER_ID)
							|| fieldName.equals(CARD_LAST_MODIFIED_USER_ID)
							|| fieldName.equals(PARENT_CARD_ID)
							|| fieldName.equals(PARENT_CARD_SEQUENCE)
							|| fieldName.equals(CARD_SPACE_ID)
							|| fieldName.equals(CARD_PROJECT_ID)) {
						try {
							//对创建用户字段进行特殊处理
							Object value = param.getValue();
							//对于用户类型的字段进行转换.从userName转换成userId
							if (fieldName.equals(CARD_CREATED_USER_ID)){
								User user = userService.getUserByUserName(param.getValue());
								value = null==user?null:user.getId().toString();
							}
							value = getObjectFromQueryValue((String)value, Long.class);
							groupQueryByField(fieldName, param.getOperationType() , value, positiveQueryMap, negativeQueryMap);
						} catch (NumberFormatException e) {
							logger.error("number format exception:{}"
									, param.toString(), e);
							throw new SparkRuntimeException(
									"global.exception.indexException");
						}
					} else if (fieldName.equals(CARD_TYPE_ID)) {
						//将cardType的localId转为实际id
						CardType cardType = cardTypeService.getCardType(spaceId, Long.parseLong(param.getValue()));
						groupQueryByField(fieldName, param.getOperationType() , cardType == null?0L:cardType.getId() , positiveQueryMap, negativeQueryMap);
					} else if(fieldName.equals(CARD_STR_INFO)){
						groupQueryByField(fieldName, param.getOperationType() , param.getValue() , positiveQueryMap, negativeQueryMap);
					} else if(fieldName.equals(ANCESTOR)){
						groupQueryByField(fieldName, QueryOperationType.LIKE, param.getValue()==null||param.getValue().isEmpty()?"-":"-"+param.getValue()+"-" , positiveQueryMap, negativeQueryMap);
					} else {
						groupQueryByField(fieldName, param.getOperationType() , Long.parseLong(param.getValue()) , positiveQueryMap, negativeQueryMap);
					}
				}
			}
		}
		BooleanQuery cardQuery = generateBooleanQueryWithFieldGroups(positiveQueryMap, negativeQueryMap);
		if(cardQuery.clauses()!=null && !cardQuery.clauses().isEmpty()){
			retQuery.add(cardQuery, BooleanClause.Occur.MUST);
		}
		if(retQuery.clauses()==null||retQuery.clauses().isEmpty()){
			return new MatchAllDocsQuery();
		}
		return retQuery;
	}
	
	@Override
	public List<SortField> getSortFromVo(final QueryVO queryVo) {
		//排序需要要按顺序，不能直接调用父类的方法super.getSortFromVo(vo);
		List<QuerySortVO> sorts = null;
		if(queryVo != null){
			sorts = queryVo.getQuerySortList();
		}
		List<SortField> sortList = new ArrayList<SortField>();
		Card pojo = generatePojo();
		if(sorts!=null && !sorts.isEmpty()){
			//从查询条件中获取spaceId.
			Long spaceId = 0L;
			if(queryVo!=null&&!CollectionUtils.isEmpty(queryVo.getQueryConditionList())){
				for (QueryConditionVO param : queryVo.getQueryConditionList()){
					if (param.getFieldName().equals(CARD_SPACE_ID)){
						spaceId = com.baidu.spark.util.StringUtils.parseLong(param.getValue());
					}
				}
			}
			for(QuerySortVO sort:sorts){
				SortField sortField = null;
				String fieldName = sort.getFieldName();
				if (ArrayUtils.contains(reflectField, fieldName)) {
					sortField = getSortByReflection(pojo, sort);
//				}else if(fieldName.startsWith(INDEX_CUS_FIELD_PREFIX)){
				}else if(StringUtils.isNumeric(fieldName)){//自定义字段，不再用前缀标识。而是以数字开头来标识
					//处理自定义字段
					if(spaceId == 0L){
						throw new SparkRuntimeException("need SpaceId query param!");
					}
					CardProperty property = null;
					try{
						property = cardTypeService.getCardProperty(spaceId, Long.parseLong(fieldName));
					}catch(NumberFormatException e){
						logger.error("number format exception" + fieldName.toString(),e);
						throw new SparkRuntimeException("global.exception.indexException");
					}
					if(property==null||property.getCardTypes() == null){
						continue;
					}
					//如果是列表类型的自定义字段,则需要根据列表值的顺序来显示,而不是localId的值大小
					if (ListProperty.TYPE.equals(property.getType()) && property instanceof ListProperty){
						FieldComparatorSource fieldComparatorSource = new ListFieldComparatorSource((ListProperty)property);
						sortField = new SortField(property.getId().toString(), fieldComparatorSource, !sort.isDesc());
					}else{
						//获取value对象，反射出实际属性的类型
						CardPropertyValue<?> propertyValue = property.generateValue(null);
						Class<?> fieldClazz = getReflectFieldType(propertyValue, "value");
						sortField = new SortField(property.getId().toString(), LuceneUtils.getLuceneSortType(fieldClazz),sort.isDesc());
					}
				}else{
					if(fieldName.equals(CARD_CREATED_USER_ID)){
						sortField = new SortField(sort.getFieldName(),
								LuceneUtils.getLuceneSortType(Long.class),sort.isDesc());
					}else if(fieldName.equals(CARD_LAST_MODIFIED_USER_ID)){
						sortField = new SortField(sort.getFieldName(),
								LuceneUtils.getLuceneSortType(Long.class),sort.isDesc());
					}else if(fieldName.equals(CARD_TYPE_ID)){
						sortField = new SortField(sort.getFieldName(),
								LuceneUtils.getLuceneSortType(Long.class),sort.isDesc());
					}else if(fieldName.equals(PARENT_CARD_ID)){
						sortField = new SortField(sort.getFieldName(),
								LuceneUtils.getLuceneSortType(Long.class),sort.isDesc());
					}else if(fieldName.equals(PARENT_CARD_SEQUENCE)){
						sortField = new SortField(sort.getFieldName(),
								LuceneUtils.getLuceneSortType(Long.class),sort.isDesc());
					}else if(fieldName.equals(CARD_SPACE_ID)){
						sortField = new SortField(sort.getFieldName(),
								LuceneUtils.getLuceneSortType(Long.class),sort.isDesc());
					}else if(fieldName.equals(CARD_PROJECT_ID)){
						sortField = new SortField(sort.getFieldName(),
								LuceneUtils.getLuceneSortType(Long.class),sort.isDesc());
					}else{
						sortField = new SortField(sort.getFieldName(),
								LuceneUtils.getLuceneSortType(String.class),sort.isDesc());
					}
				}
				if(sortField!=null){
					sortList.add(sortField);	
				}
				
			}
		}
		return sortList;
	}
	/**
	 * 获取卡片的字符串字段的集合的信息
	 * @param card
	 * @return
	 */
	private String getCardStrInfo(Card card){
		StringBuilder sb = new StringBuilder();
		sb.append(card.getTitle()).append("|").append(card.getDetail()).append("|");
		List<CardPropertyValue<?>> values = card.getPropertyValues();
		for(CardPropertyValue<?> value:values){
			if(value.getCardProperty() instanceof TextProperty){
				sb.append(value.getValueString()).append("|");
			}
		}
		return sb.toString();
	}
	
	@Override
	protected String[] getIndexFields(){
		return reflectField;
	}

	@Override
	public String getKeyFieldName() {
		return "id";
	}

	@Autowired
	public void setCardService(CardBasicService cardService) {
		this.cardService = cardService;
	}
	@Autowired
	public void setCardTypeService(CardTypeBasicService cardTypeService) {
		this.cardTypeService = cardTypeService;
	}
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	@Override
	public Card generatePojo(){
		return new Card();
	}
	

}
