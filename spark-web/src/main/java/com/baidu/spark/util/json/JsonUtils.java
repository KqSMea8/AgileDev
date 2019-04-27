package com.baidu.spark.util.json;

import java.io.StringWriter;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.baidu.spark.exception.SparkRuntimeException;
import com.baidu.spark.util.ApplicationContextHolder;

/**
 * 使用jackson进行对json的处理
 * @author Adun
 * 2010-06-09
 */
public class JsonUtils{

	public static ObjectMapper getMapper() throws NoSuchBeanDefinitionException{
		ObjectMapper mapper = ApplicationContextHolder.getBean(ObjectMapper.class);
		if (null == mapper){
			throw new NoSuchBeanDefinitionException(ObjectMapper.class);
		}
		return mapper;
	}
	
	/**
	 * 将一个对象转换为json格式的字符串
	 * @param <T>
	 * @param t
	 * @return
	 */
	public static <T> String getJsonString(T t){
		try {
			StringWriter writer = new StringWriter();
			getMapper().writeValue(writer, t);
			return writer.getBuffer().toString();
		} catch (Exception e) {
			throw new SparkRuntimeException("生成json数据时发生异常:", e);
		}
	}
	
	/**
	 * 根据json字符串和给定的class,解析为指定class的一个实例
	 * @param <T>
	 * @param jsonString
	 * @param clazz
	 * @return
	 */
	public static <T> T getObjectByJsonString(String jsonString, Class<T> clazz){
		try{
			T obj = getMapper().<T>readValue(jsonString, clazz);
			return obj;
		}catch(Exception e){
			throw new SparkRuntimeException("解析json数据时发生异常:", e);
		}
	}
	
	/**
	 * 根据json字符串和给定的typeReference,解析为指定class的一个实例
	 * @param <T>
	 * @param jsonString
	 * @param type
	 * @return
	 */
	public static <T> T getObjectByJsonString(String jsonString, TypeReference<T> type){
		try{
			return getMapper().<T>readValue(jsonString, type);
		}catch(Exception e){
			throw new SparkRuntimeException("解析json数据时发生异常:", e);
		}
	}

	public static ArrayNode getArrayNode() {
		return getMapper().createArrayNode();
	}
}
