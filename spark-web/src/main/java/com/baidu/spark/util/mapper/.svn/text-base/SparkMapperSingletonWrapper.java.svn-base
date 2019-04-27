package com.baidu.spark.util.mapper;

/**
 * Spark映射器单例包装器.
 * 
 * @author GuoLin
 *
 */
public abstract class SparkMapperSingletonWrapper {

	/** 映射器实例. */
	private static SparkMapper instance = new SparkMapperImpl();
	
	/**
	 * 获取映射器单例实例.
	 * @return 映射器实例
	 */
	public static SparkMapper getInstance() {
		return instance;
	}
}
