package com.baidu.spark.service.impl.helper.copyspace.transformer.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.model.card.property.DummyProperty;
import com.baidu.spark.service.CardTypeBasicService;
import com.baidu.spark.service.impl.helper.copyspace.metadata.Metadata;
import com.baidu.spark.service.impl.helper.copyspace.metadata.impl.CardPropertyMetadata;
import com.baidu.spark.service.impl.helper.copyspace.option.ImportOption;
import com.baidu.spark.service.impl.helper.copyspace.validation.ValidationResult;
import com.baidu.spark.service.impl.helper.copyspace.validation.impl.DeserializeError;
import com.baidu.spark.service.impl.helper.copyspace.validation.impl.InvalidDataError;
import com.baidu.spark.util.MessageHolder;
import com.baidu.spark.util.json.CollectionAppender;
import com.baidu.spark.util.json.JsonAppender;
import com.baidu.spark.util.json.JsonUtils;

/**
 * cardproperty的转换器
 * @author zhangjing_pe
 *
 */
public class CardPropertyTransformer extends PojoTransformer<CardPropertyMetadata, CardProperty> {
	
	private CardTypeBasicService cardTypeService;
	
	private static final String JSON_ROOT = "cardPropertyList";

	@Override
	public String getJson(List<CardProperty> list) {
		JsonAppender appender = new JsonAppender();
		appender.appendList(JSON_ROOT,new CollectionAppender<CardProperty>(list){
			@Override
			public String[] getNames() {
				return new String[]{"name","type", "localId","hidden","info","cardTypeLocalIds","sort"};
			}
			@Override
			protected Object[] getValues(CardProperty obj) {
				StringBuilder sb = new StringBuilder();
				if (CollectionUtils.isNotEmpty(obj.getCardTypes())) {
					for (CardType cardType : obj.getCardTypes()) {
						if (sb.length() > 0) {
							sb.append(",");
						}
						sb.append(cardType.getLocalId());
					}
				}
				return new Object[]{obj.getName(), obj.getType(), obj.getLocalId(),obj.getHidden(),obj.getInfo(),sb.toString(),obj.getSort()};
			}
		});
		return appender.getJsonString();
	}

	@Override
	public CardPropertyMetadata getMetadata(String jsonData) {
		CardPropertyMetadata metadata = new CardPropertyMetadata();
		metadata.setResultData(jsonData);
		Map<String,List<Map<String, String>>> cardPropertyListMap = JsonUtils.getObjectByJsonString(jsonData, new TypeReference<Map<String,List<Map<String, String>>>>(){});
		if(cardPropertyListMap == null|| cardPropertyListMap.size()==0){
			return metadata;
		}
		List<Map<String,String>> mapList = cardPropertyListMap.get(JSON_ROOT);
		if(CollectionUtils.isNotEmpty(mapList)){
			for(Map<String,String> map:mapList){
				DummyProperty cardProperty = new DummyProperty();
				cardProperty.setName(map.get("name"));
				cardProperty.setType(map.get("type"));
				cardProperty.setLocalId(Long.parseLong(map.get("localId")));
				cardProperty.setHidden(Boolean.parseBoolean(map.get("hidden")));
				cardProperty.setInfo(map.get("info"));
				cardProperty.setSort(map.get("sort")==null?null:Integer.parseInt(map.get("sort")));
				
				if(map.get("cardTypeLocalIds")!=null&&!map.get("cardTypeLocalIds").equals("null")){
					Set<CardType> cardTypes = new HashSet<CardType>();
					String cardTypeLocalIds = map.get("cardTypeLocalIds");
					StringTokenizer st = new StringTokenizer(cardTypeLocalIds, ",");
					while(st.hasMoreTokens()){
						String id = st.nextToken();
						CardType cardType = new CardType();
						cardType.setLocalId(Long.parseLong(id));
						cardTypes.add(cardType);
					}
					cardProperty.setCardTypes(cardTypes);
				}
				metadata.addPojo(cardProperty.generateActualProperty());
			}
		}
		return metadata;
	}
	@Override
	protected void importPojos(Space space,List<CardProperty> pojos,List<ImportOption<?>> importOptions) {
		Assert.notNull(space);
		Assert.notNull(space.getId());
		//给每个对象设置spaceId，并保存。然后设置上下级的关联关系
		if(CollectionUtils.isNotEmpty(pojos)){
			List<CardType> spaceCardTypes = cardTypeService.getAllCardTypes(space);
			Map<Long,CardType> cardTypeLocalIdMap = new HashMap<Long,CardType>();
			for(CardType type:spaceCardTypes){
				cardTypeLocalIdMap.put(type.getLocalId(), type);
			}
			for(CardProperty cardProperty:pojos){
				//暂时清除parent
				Set<CardType> cardTypes = cardProperty.getCardTypes();
				Set<CardType> targetCardTypes = new HashSet<CardType>();
				
				//处理cp和ct的对应关系
				for(CardType cardType:cardTypes){
					CardType dbCardType = cardTypeLocalIdMap.get(cardType.getLocalId());
					targetCardTypes.add(dbCardType);
				}
				cardProperty.setCardTypes(targetCardTypes);
				cardProperty.setSpace(space);
				cardTypeService.saveCardProperty(cardProperty,cardProperty.getCardTypes());
				
			}
//			for(CardType cardType:cardTypeLocalIdMap.values()){
//				cardTypeService.updateCardType(cardType);
//			}
		}
		
	}

	@Override
	public CardPropertyMetadata exportMetadata(Space space) {
		Assert.notNull(space);
		Assert.notNull(space.getId());
		List<CardProperty> cardPropertyList = cardTypeService.getAllCardProperties(space.getId());
		CardPropertyMetadata metadata = new CardPropertyMetadata();
		metadata.setResultData(getJson(cardPropertyList));
		return metadata;
	}

	@Override
	public boolean match(Metadata metadata) {
		if(metadata instanceof CardPropertyMetadata){
			return true;
		}
		return false;
	}

	@Override
	public List<ValidationResult> validateImportData(String jsonData) {
		List<ValidationResult> results = new ArrayList<ValidationResult>();
		Set<Long> localIdSet = new HashSet<Long>();
		try{
			CardPropertyMetadata metadata  = getMetadata(jsonData);
			for(CardProperty cardProperty : metadata.getPojos()){
				if(cardProperty.getId()!=null){
					results.add(new InvalidDataError(CardProperty.class,MessageHolder.get("spacecopy.validation.error.invalidData.cardProperty.cardPropertyIdNotNull",cardProperty.getId())));
				}
				//发现重复定义的localId
				if(localIdSet.contains(cardProperty.getLocalId())){
					results.add(new InvalidDataError(CardProperty.class,MessageHolder.get("spacecopy.validation.error.invalidData.cardProperty.duplicateCardPropertyLocalId")));	
				}
				localIdSet.add(cardProperty.getId());

				//注释掉 cp和ct改为多对多关系，cp并不一定拥有cardTypeLocalIds
				//				if(cardProperty.getCardType()==null||cardProperty.getCardType().getLocalId()==null){
//					results.add(new InvalidDataError(CardProperty.class,MessageHolder.get("spacecopy.validation.error.invalidData.cardProperty.cardTypeLocalIdInCardPropertyIsNull",cardProperty.getName())));
//				}
				
				//TODO 如何判断cardProperty中的cardTypeLocalId已存在呢？
			}
		}catch(Exception e){
			results.add(new DeserializeError(CardProperty.class,e));
		}
		return results;
	}
	
	@Autowired
	public void setCardTypeService(CardTypeBasicService cardTypeService) {
		this.cardTypeService = cardTypeService;
	}
	

}
