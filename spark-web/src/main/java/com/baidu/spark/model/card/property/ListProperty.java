package com.baidu.spark.model.card.property;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.codehaus.jackson.type.TypeReference;
import com.baidu.spark.util.json.JsonAppender;
import com.baidu.spark.util.json.JsonUtils;

/**
 * 列表类型的卡片值
 * @author zhangjing_pe
 *
 */
@Entity
@DiscriminatorValue(value=ListProperty.TYPE)
public class ListProperty extends CardProperty {

	/** 下拉列表类型. */
	public static final String TYPE = "list";
	
	/**
	 * 获取下拉列表选项值.
	 * <p>
	 * 返回LinkedHashMap,按照实际配置的顺序来获取到Map中
	 * </p>
	 * @return LinkedHashMap,其中key为值的localId,value为属性的显示值
	 */
	public LinkedHashMap<String, String> getOptionMap(){
		//TODO 有没有更好的转换方法？
		try{
			if (null == getInfo()){
				return new LinkedHashMap<String, String>();
			}
			LinkedHashMap<String, String> map = JsonUtils.getObjectByJsonString(getInfo(), new TypeReference<LinkedHashMap<String, String>>(){});
			return map;
		}catch(Exception e){
			logger.warn("read list property values error: info:{}, error message:{}", getInfo(), e.getMessage());
			return new LinkedHashMap<String, String>();
		}
	}
	
	@Override
	protected ListPropertyValue instantiateValue() {
		return new ListPropertyValue();
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	/**
	 * 页面编辑时列表的显示。为了兼容dummyProperty在列表option的验证失败的回显。因为key有可能都为空，所以不能放在map中返回
	 * @return
	 */
	public List<String> getListKey() {
		return new ArrayList<String>(getOptionMap().keySet());
	}
	/**
	 * 页面编辑时列表的显示。为了兼容dummyProperty在列表option的验证失败的回显。因为key有可能都为空，所以不能放在map中返回
	 * @return
	 */
	public List<String> getListValue() {
		return  new ArrayList<String>(getOptionMap().values());
	}
	
	/**
	 * 根据key和value设置info字段
	 * @param listKey
	 * @param listValue
	 */
	public void setInfoFromKeyAndValue(List<String> listKey,List<String> listValue){
		if (listKey != null && listValue != null
				&& listKey.size() == listValue.size()) {
			JsonAppender appender = new JsonAppender();
			for (int i = 0; i < listKey.size(); i++) {
				String key = listKey.get(i);
				String value = listValue.get(i);
				appender.append(key, value);
			}
			setInfo(appender.getJsonString());
		}
	}
	
}
