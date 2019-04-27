package com.baidu.spark.service.impl.helper.copyspace.transformer.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import com.baidu.spark.model.Space;
import com.baidu.spark.model.SpaceView;
import com.baidu.spark.model.User;
import com.baidu.spark.service.SpaceViewService;
import com.baidu.spark.service.impl.helper.copyspace.metadata.Metadata;
import com.baidu.spark.service.impl.helper.copyspace.metadata.impl.SpaceViewMetadata;
import com.baidu.spark.service.impl.helper.copyspace.option.CheckboxImportOption;
import com.baidu.spark.service.impl.helper.copyspace.option.ImportOption;
import com.baidu.spark.service.impl.helper.copyspace.validation.ValidationResult;
import com.baidu.spark.service.impl.helper.copyspace.validation.impl.DeserializeError;
import com.baidu.spark.service.impl.helper.copyspace.validation.impl.InvalidDataError;
import com.baidu.spark.util.MessageHolder;
import com.baidu.spark.util.json.CollectionAppender;
import com.baidu.spark.util.json.JsonAppender;
import com.baidu.spark.util.json.JsonUtils;

/**
 * spaceView的转换器
 * @author zhangjing_pe
 *
 */
public class SpaceViewTransformer extends PojoTransformer<SpaceViewMetadata, SpaceView> {
	
	private SpaceViewService spaceViewService;
	
	private static final String JSON_ROOT = "spaceViewList";

	@Override
	public String getJson(List<SpaceView> list) {
		JsonAppender appender = new JsonAppender();
		appender.appendList(JSON_ROOT,new CollectionAppender<SpaceView>(list){
			@Override
			public String[] getNames() {
				return new String[]{"name","url","sort"};
			}
			@Override
			protected Object[] getValues(SpaceView obj) {
				return new Object[]{obj.getName(), obj.getUrl(), obj.getSort()};
				}
			});
		return appender.getJsonString();
	}

	@Override
	public SpaceViewMetadata getMetadata(String jsonData) {
		SpaceViewMetadata metadata = new SpaceViewMetadata();
		Map<String,ArrayList<LinkedHashMap<String, String>>> cardPropertyListMap = JsonUtils.getObjectByJsonString(jsonData, new TypeReference<HashMap<String,ArrayList<LinkedHashMap<String, String>>>>(){});
		if(cardPropertyListMap == null|| cardPropertyListMap.isEmpty()){
			return metadata;
		}
		List<LinkedHashMap<String,String>> mapList = cardPropertyListMap.get(JSON_ROOT);
		if(CollectionUtils.isNotEmpty(mapList)){
			for(LinkedHashMap<String,String> map:mapList){
				SpaceView view = new SpaceView();
				view.setName(map.get("name"));
				view.setSort(NumberUtils.toInt(map.get("sort"), 0));
				view.setUrl(map.get("url"));
				
				metadata.addPojo(view);
			}
		}
		return metadata;
	}
	@Override
	protected void importPojos(Space space,List<SpaceView> pojos,List<ImportOption<?>> importOptions) {
		Assert.notNull(space);
		Assert.notNull(space.getId());
		boolean importSpaceView = true;
		for(ImportOption<?> option:importOptions){
			if(SpaceViewMetadata.IMPORT_SPACE_VIEW_KEY.equals(option.getKey())&&!((CheckboxImportOption<?>)option).checked()){
				importSpaceView = false;
			}
		}
		//不导入直接返回
		if(!importSpaceView){
			return;
		}
		//给每个对象设置spaceId，并保存。然后设置上下级的关联关系
		if(CollectionUtils.isNotEmpty(pojos)){
			for(SpaceView spaceView:pojos){
				spaceView.setSpace(space);
				spaceView.setUser((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
				spaceViewService.save(spaceView);
			}
		}
		
	}

	@Override
	public SpaceViewMetadata exportMetadata(Space space) {
		Assert.notNull(space);
		Assert.notNull(space.getId());
		List<SpaceView> spaceViewList = spaceViewService.listAllInSpace(space);

		SpaceViewMetadata metadata = new SpaceViewMetadata();
		metadata.setResultData(getJson(spaceViewList));
		return metadata;
	}

	@Override
	public boolean match(Metadata metadata) {
		return metadata instanceof SpaceViewMetadata;
	}

	@Override
	public List<ValidationResult> validateImportData(String jsonData) {
		//view的名称不能重复
		List<ValidationResult> results = new ArrayList<ValidationResult>();
		Set<String> nameSet = new HashSet<String>();
		try{
			SpaceViewMetadata metadata = getMetadata(jsonData);
			for(SpaceView spaceView : metadata.getPojos()){
				if(!nameSet.contains(spaceView.getName())){
					nameSet.add(spaceView.getName());
				}else{
					results.add(new InvalidDataError(SpaceView.class,MessageHolder.get("spacecopy.validation.error.invalidData.spaceView.duplicateViewNameFound",spaceView.getName())));
				}
			}
		}catch(Exception e){
			results.add(new DeserializeError(SpaceView.class,e));
		}
		return results;
	}
	
	@Autowired
	public void setSpaceViewService(SpaceViewService spaceViewService) {
		this.spaceViewService = spaceViewService;
	}

}
