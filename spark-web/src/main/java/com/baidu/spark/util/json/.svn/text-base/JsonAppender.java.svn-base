package com.baidu.spark.util.json;

import java.util.Date;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.baidu.spark.exception.SparkRuntimeException;
import com.baidu.spark.util.DateUtils;

/**
 * builder风格的json拼接工具
 * @author Adun
 * 2010-06-10
 */
//TODO Adun 需要考虑如何能够嵌套使用
public class JsonAppender {
	ObjectNode node;
	
	/**
	 * 构造函数.第二个参数,是为了只能在JsonUtils中进行创建.(由于JsonUtils不能实例化)
	 * @param node
	 * @param object
	 */
	public JsonAppender(){
		this.node = JsonUtils.getMapper().createObjectNode();
	}
	
	/**
	 * 增加一个key value对
	 * @param name
	 * @param value
	 * @return
	 */
//	public JsonAppender append(String name, Long value){
//		node.put(name, value);
//		return this;
//	}
//	
//	public JsonAppender append(String name, Integer value){
//		node.put(name, value);
//		return this;
//	}
//	
//	public JsonAppender append(String name, String value){
//		node.put(name, value);
//		return this;
//	}
//	
//	public JsonAppender append(String name, Date value){
//		String strValue;
//		if (null == value){
//			strValue = "";
//		}else{
//			
//		}
//		node.put(name, strValue);
//		return this;
//	}
	
	public JsonAppender append(String name, Object value){
		
		if (null == value){
			String v = null;
			node.put(name, v);
		}else{
			if (value instanceof Date){
				String strValue;
				strValue = DateUtils.DATE_TIME_PATTERN_FORMAT.format(value);
				node.put(name, strValue);
			}else if (value instanceof String){
				node.put(name, (String)value);
			}else if (value instanceof Long){
				node.put(name, (Long)value);
			}else if (value instanceof Integer){
				node.put(name, (Integer)value);
			}else{
				throw new SparkRuntimeException("not defined value Type: " + value.getClass().getCanonicalName());
			}
		}
		
		return this;
	}
	
	/**
	 * 获取json字符串
	 * @return
	 */
	public String getJsonString(){
		return node.toString();
	}

	/**
	 * json中增加一个array对象
	 * @param <T>
	 * @param listPropertyName
	 * @param listAppender
	 * @return
	 */
	public <T> JsonAppender appendList(String listPropertyName, CollectionAppender<T> listAppender) {
		ArrayNode arrayNode = this.node.putArray(listPropertyName);
		String[] names = listAppender.getNames();
		for (Object[] values:listAppender.getValuesList()){
			ObjectNode objectNode = arrayNode.addObject();
			for (int i=0;i<Math.min(names.length, values.length);i++){
				if (null == values[i]){
					String v = null;
					objectNode.put(names[i], v);
				}else if(values[i] instanceof Date){
					objectNode.put(names[i], DateUtils.DATE_TIME_PATTERN_FORMAT.format((Date)values[i]));
				}else if(values[i] instanceof Long) {
					objectNode.put(names[i], (Long)values[i]);
				}else if(values[i] instanceof Integer) {
					objectNode.put(names[i], (Integer)values[i]);
				}else{
					objectNode.put(names[i], values[i].toString());
				}
			}
		}
		return this;
	}
}
