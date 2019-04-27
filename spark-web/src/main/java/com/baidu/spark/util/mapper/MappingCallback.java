package com.baidu.spark.util.mapper;

import com.baidu.spark.util.mapper.SparkMapperImpl.MappingConfig;

/**
 * 映射回调接口.
 * 
 * @author GuoLin
 *
 */
public interface MappingCallback {
	
	/** 继续处理常量对象. */
	public static final Object CONTINUE = new Object();

	/**
	 * 回调函数.
	 * @param sourceParent 源对象的父对象
	 * @param destinationParent 目标对象的父对象
	 * @param source 源对象
	 * @param destination 父对象
	 * @param config 配置实例
	 * @return 若返回值等于{@link #CONTINUE}则继续处理，否则则认为是最终目标值
	 */
	public Object callback(Object sourceParent, Object destinationParent, Object source, Object destination, MappingConfig config);
	
}
