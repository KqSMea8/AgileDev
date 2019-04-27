package com.baidu.spark.util.mapper;

/**
 * Spark映射器.
 * 
 * @author GuoLin
 *
 */
public interface SparkMapper {

	/**
	 * 克隆一个对象.
	 * @param <T> 对象类型
	 * @param source 源对象
	 * @return 克隆完成后的对象
	 */
	public <T> T clone(T source);
	
	/**
	 * 克隆一个对象.
	 * @param <T> 对象类型
	 * @param source 源对象
	 * @param callback 回调函数
	 * @return 克隆完成后的对象
	 */
	public <T> T clone(T source, MappingCallback callback);
	
}
