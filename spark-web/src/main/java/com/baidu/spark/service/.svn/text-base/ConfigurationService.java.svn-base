package com.baidu.spark.service;

import java.util.List;
import java.util.Map;

/**
 * 系统环境变量服务接口
 * 
 * @author shixiaolei
 * 
 */
public interface ConfigurationService {

	/**
	 * 得到key对应的环境变量的值.
	 * 
	 * @param key
	 *            环境变量key
	 * @return key对应的环境变量的值.
	 */
	public String get(String key);

	/**
	 * 记录一组键值分别为key/value的环境变量.
	 * 
	 * @param key
	 *            环境变量key
	 * @param value
	 *            key对应的环境变量的值.
	 */
	public void set(String key, String value);

	/**
	 * 得到系统所有环境变量的key.
	 * 
	 */
	public List<String> getKeys();

	/**
	 * 得到全部环境变量，以键值的形式返回.
	 * 
	 */
	public Map<String, String> getConfigurations();

}
