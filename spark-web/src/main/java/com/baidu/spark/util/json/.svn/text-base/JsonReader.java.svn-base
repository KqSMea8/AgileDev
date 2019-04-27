package com.baidu.spark.util.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;

import com.baidu.spark.util.DateUtils;

/**
 * 读取Json数据的工具类
 * @author Adun
 */
public class JsonReader {
	
	private JsonNode node;
	
	public JsonReader(String content) throws JsonProcessingException, IOException {
		this.node = JsonUtils.getMapper().readTree(content);
	}
	
	private JsonReader(JsonNode node){
		this.node = node;
	}
	
	public String readObject(String name){
		JsonNode valueNode = node.get(name);
		if (null == valueNode || valueNode.isNull()){
			return null;
		}
		return valueNode.getValueAsText();
	}
	
	public Long readLong(String name){
		JsonNode valueNode = node.get(name);
		if (null == valueNode || valueNode.isNull()){
			return null;
		}
		return new Long(valueNode.getLongValue());
	}
	
	public String readString(String name){
		JsonNode valueNode = node.get(name);
		if (null == valueNode || valueNode.isNull()){
			return null;
		}
		return valueNode.getTextValue();
	}
	
	public Date readDate(String name){
		JsonNode valueNode = node.get(name);
		if (null == valueNode || valueNode.isNull()){
			return null;
		}
		try{
			return DateUtils.DATE_TIME_PATTERN_FORMAT.parse(valueNode.getTextValue());
		}catch(Exception e){
			return null;
		}
	}
	
	public List<JsonReader> getSubReaderList(String name){
		JsonNode valueNode = node.get(name);
		if (null == valueNode){
			return new ArrayList<JsonReader>();
		}
		List<JsonReader> subReaderList = new ArrayList<JsonReader>();
		for (JsonNode subNode:valueNode){
			subReaderList.add(new JsonReader(subNode));
		}
		return subReaderList;
	}
}
