package com.baidu.spark.util.mapper;

import java.util.HashSet;
import java.util.Set;

import com.baidu.spark.util.mapper.SparkMapperImpl.MappingConfig;

/**
 * 例外路径回调类.
 * 
 * @author GuoLin
 *
 */
public class ExcludePathCallback implements MappingCallback {
	
	/** 例外路径集合. */
	private Set<String> excludePaths = new HashSet<String>();
	
	/**
	 * 构造器.
	 * @param excludePaths 例外路径列表
	 */
	public ExcludePathCallback(String... excludePaths) {
		for (String path : excludePaths) {
			this.excludePaths.add(path);
		}
	}

	@Override
	public Object callback(Object sourceParent, Object destinationParent, Object source,
			Object destination, MappingConfig config) {
		
		if (excludePaths.contains(config.getPath())) {
			return null;
		}
		
		return CONTINUE;
	}

}
