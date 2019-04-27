package com.baidu.spark.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import com.baidu.spark.exception.SparkRuntimeException;
import com.baidu.spark.model.Space;
import com.baidu.spark.service.SpaceCopyService;
import com.baidu.spark.service.impl.helper.copyspace.metadata.Metadata;
import com.baidu.spark.service.impl.helper.copyspace.option.ImportOption;
import com.baidu.spark.service.impl.helper.copyspace.transformer.Transformer;
import com.baidu.spark.service.impl.helper.copyspace.validation.ValidationResult;

/**
 * 空间复制的方法
 * @author zhangjing_pe
 *
 */
public class SpaceCopyServiceImpl implements SpaceCopyService {

	private List<Transformer<Metadata>> transformers = null;
	
	@Override
	public List<Metadata> getSpaceMetadata(Space space){
		if(transformers!=null){
			List<Metadata> retList = new ArrayList<Metadata>();
			for(Transformer<Metadata> transformer:transformers){
				Metadata metadata = transformer.exportMetadata(space);
				if(metadata!=null){
					retList.add(metadata);
				}
			}
			return retList;
		}
		return null;
	}
	
	@Override
	public void importMetadata(Space space,List<Metadata> metadatas){
		if(transformers!=null){
			for(Metadata metadata:metadatas){
				Transformer<Metadata> transformer = getTransformer(metadata);
				transformer.importMetadata(space, metadata.getResultData(),metadata.getImportOptions());
			}
		}
	}
	
	
	@Override
	public List<ValidationResult> validation(List<Metadata> metadatas){
		if(transformers!=null){
			List<ValidationResult> retList = new ArrayList<ValidationResult>();
			for(Metadata metadata:metadatas){
				Transformer<Metadata> transformer = getTransformer(metadata);
				List<ValidationResult> result = transformer.validateImportData(metadata.getResultData());
				if(result!=null){
					retList.addAll(result);
				}
			}
			return retList;
		}
		return null;
	}
	
	@Override
	public List<ImportOption<?>> getImportOptions(List<Metadata> metadatas) {
		List<ImportOption<?>> retList = new ArrayList<ImportOption<?>>();
		if(CollectionUtils.isNotEmpty(metadatas)){
			for(Metadata metadata : metadatas){
				if(CollectionUtils.isNotEmpty(metadata.getImportOptions())){
					retList.addAll(metadata.getImportOptions());
				}
			}
		}
		return retList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Metadata> buildImportOption(HttpServletRequest request,List<Metadata> metadatas){
		List<ImportOption<?>> options = getImportOptions(metadatas);
		Map<String,String[]> map = request.getParameterMap();
		for(String key:map.keySet()){
			if(!key.startsWith(ImportOption.INPUT_NAME_PREFIX)){
				continue;
			}
			String[] values = map.get(key);
			if(values!=null&&values.length>1){//当前认为，option的key不能重复
				throw new SparkRuntimeException("duplicate option key!");
			}
			if(values!=null&&values.length ==1){
				String value = values[0];
				Iterator<ImportOption<?>> it = options.iterator();
				while(it.hasNext()){
					ImportOption<?> option = it.next();
					if(option.getInputName().equals(key)){
						option.setValue(value);
						it.remove();
					}
				}
			}
		}
		//清理未传来的值
		for(ImportOption<?> option:options){
			option.setValue(null);
		}
		return metadatas;
	}
	
	public Transformer<Metadata> getTransformer(Metadata metadata){
		Assert.notNull(metadata);
		if(transformers!=null){
			for(Transformer<Metadata> transformer:transformers){
				if(transformer.match(metadata)){
					return transformer;
				}
			}
		}
		return null;
	}

	public void setTransformers(List<Transformer<Metadata>> transformers) {
		this.transformers = transformers;
	}

}
