package com.baidu.spark.service.impl.helper.copyspace.transformer.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.baidu.spark.exception.SparkRuntimeException;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.service.CardTypeBasicService;
import com.baidu.spark.service.impl.helper.copyspace.metadata.Metadata;
import com.baidu.spark.service.impl.helper.copyspace.metadata.impl.CardTypeMetadata;
import com.baidu.spark.service.impl.helper.copyspace.option.ImportOption;
import com.baidu.spark.service.impl.helper.copyspace.validation.ValidationResult;
import com.baidu.spark.service.impl.helper.copyspace.validation.impl.DeserializeError;
import com.baidu.spark.service.impl.helper.copyspace.validation.impl.InvalidDataError;
import com.baidu.spark.util.MessageHolder;
import com.baidu.spark.util.json.CollectionAppender;
import com.baidu.spark.util.json.JsonAppender;
import com.baidu.spark.util.json.JsonUtils;

/**
 * cardtype的转换器
 * @author zhangjing_pe
 *
 */
public class CardTypeTransformer extends PojoTransformer<CardTypeMetadata, CardType> {
	
	private CardTypeBasicService cardTypeService;
	
	private static final String JSON_ROOT = "cardTypeList";

	@Override
	public String getJson(List<CardType> list) {
		JsonAppender appender = new JsonAppender();
		appender.appendList(JSON_ROOT,new CollectionAppender<CardType>(list){
			@Override
			public String[] getNames() {
				return new String[]{"name", "localId","parentLocalId","recursive","color"};
			}
			@Override
			protected Object[] getValues(CardType obj) {
				return new Object[]{obj.getName(), obj.getLocalId(), obj.getParent()==null?null:obj.getParent().getLocalId(),
						obj.getRecursive(),obj.getColor()};
				}
			});
		return appender.getJsonString();
	}

	@Override
	public CardTypeMetadata getMetadata(String jsonData) {
		CardTypeMetadata metadata = new CardTypeMetadata();
		Map<String,ArrayList<LinkedHashMap<String, String>>> cardTypeListMap = JsonUtils.getObjectByJsonString(jsonData, new TypeReference<HashMap<String,ArrayList<LinkedHashMap<String, String>>>>(){});
		if(cardTypeListMap == null|| cardTypeListMap.size()==0){
			return metadata;
		}
		List<LinkedHashMap<String,String>> mapList = cardTypeListMap.get(JSON_ROOT);
		if(CollectionUtils.isNotEmpty(mapList)){
			for(LinkedHashMap<String,String> map:mapList){
				CardType cardType = new CardType();
				cardType.setName(map.get("name"));
				cardType.setColor(map.get("color"));
				cardType.setLocalId(Long.parseLong(map.get("localId")));
				cardType.setRecursive(Boolean.parseBoolean(map.get("recursive")));
				
				if(map.get("parentLocalId")!=null&&!map.get("parentLocalId").equals("null")){
					CardType parentCardType = new CardType();
					parentCardType.setLocalId(Long.parseLong(map.get("parentLocalId")));
					cardType.setParent(parentCardType);
				}
				metadata.addPojo(cardType);
			}
		}
		return metadata;
	}
	@Override
	protected void importPojos(Space space,List<CardType> pojos,List<ImportOption<?>> importOptions) {
		Assert.notNull(space);
		Assert.notNull(space.getId());
		
		Map<Long,Long> cardTypeParentLocalId = new HashMap<Long,Long>();
		//给每个对象设置spaceId，并保存。然后设置上下级的关联关系
		if(CollectionUtils.isNotEmpty(pojos)){
			for(CardType cardType:pojos){
				//设置space
				cardType.setSpace(space);
				
				//暂时清除parent
				CardType parent = cardType.getParent();
				cardType.setParent(null);
				cardTypeService.saveCardType(cardType);
				if(parent!=null&&parent.getLocalId()!=null){
					cardTypeParentLocalId.put(cardType.getId(), parent.getLocalId());
				}
			}
			//设置parent的关系
			for(CardType cardType:pojos){
				if(cardTypeParentLocalId.containsKey(cardType.getId())){
					CardType parent = cardTypeService.getCardType(space.getId(), cardTypeParentLocalId.get(cardType.getId()));
					if(parent!=null){
						cardType.setParent(parent);
						cardTypeService.updateCardType(cardType);
					}else{
						//这里如果预先通过了validation，就不会进入这个分支
						throw new SparkRuntimeException("cardtype parentLocalId not exists!");
					}
				}
			}
			
		}
		
	}

	@Override
	public CardTypeMetadata exportMetadata(Space space) {
		List<CardType> cardType = cardTypeService.getAllCardTypes(space);
		CardTypeMetadata metadata = new CardTypeMetadata();
		metadata.setResultData(getJson(cardType));
		return metadata;
	}

	@Override
	public boolean match(Metadata metadata) {
		if(metadata instanceof CardTypeMetadata){
			return true;
		}
		return false;
	}

	@Override
	public List<ValidationResult> validateImportData(String jsonData) {
		List<ValidationResult> results = new ArrayList<ValidationResult>();
		Set<Long> parentLocalIdSet = new HashSet<Long>();
		Set<Long> localIdSet = new HashSet<Long>();
		try{
			CardTypeMetadata metadata = getMetadata(jsonData);
			List<CardType> cardTypeList = metadata.getPojos();
			for(CardType cardType : cardTypeList){
				if(cardType.getId()!=null){
					results.add(new InvalidDataError(CardType.class,MessageHolder.get("spacecopy.validation.error.invalidData.cardType.cardTypeIdNotNull",cardType.getId())));
				}
				//发现重复定义的localId
				if(localIdSet.contains(cardType.getLocalId())){
					results.add(new InvalidDataError(CardType.class,MessageHolder.get("spacecopy.validation.error.invalidData.cardType.duplicateCardTypeLocalId")));	
				}
				localIdSet.add(cardType.getId());
				if(cardType.getParent()!=null&&cardType.getParent().getLocalId()!=null){
					parentLocalIdSet.add(cardType.getId());	
				}
			}
			//parent中的localId未在空间中定义
			if(!localIdSet.containsAll(parentLocalIdSet)){
				results.add(new InvalidDataError(CardType.class,MessageHolder.get("spacecopy.validation.error.invalidData.cardType.parentLocalIdNotFound")));
			}
			//验证是否有循环引用的关系
			List<CardType> toCheckList = new ArrayList<CardType>();
			List<CardType> swapList = new ArrayList<CardType>();
			Set<Long> existsLocalIdSet = new HashSet<Long>();
			
			toCheckList.addAll(cardTypeList);
			int toCheckSum = -1;
			while(CollectionUtils.isNotEmpty(toCheckList)){
				if(toCheckSum == toCheckList.size()){
					StringBuilder sb = new StringBuilder();
					for(CardType cardType:toCheckList){
						sb.append(cardType.getName()).append(" ");
					}
					results.add(new InvalidDataError(CardType.class,MessageHolder.get("spacecopy.validation.error.invalidData.cardType.parentLocalIdCycleReferenceFound",sb.toString())));
					break;
				}
				for(CardType cardType:toCheckList){
					if (cardType.getParent() != null
							&& cardType.getParent().getId() != null) {
						if (!existsLocalIdSet.contains(cardType.getParent()
								.getId())) {
							//记录了parentLocalId，而且parent的LocalId还未被处理过，则下一轮再处理
							swapList.add(cardType);
						}
					}
					toCheckList = swapList;
				}
			}
		}catch(Exception e){
			results.add(new DeserializeError(CardType.class,e));
		}
		return results;
	}
	
	@Autowired
	public void setCardTypeService(CardTypeBasicService cardTypeService) {
		this.cardTypeService = cardTypeService;
	}
	

}
