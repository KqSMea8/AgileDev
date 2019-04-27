package com.baidu.spark.model.card.property;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 列表属性值.
 * 
 * @author GuoLin
 *
 */
@Entity
@DiscriminatorValue(value=ListPropertyValue.TYPE)
public class ListPropertyValue extends CardPropertyValue<String> {

	/** 下拉列表类型. */
	public static final String TYPE = "list";
	
	@Column(name="strvalue")
	private String value;

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void initValueWithString(String value) {
		this.value = value;
	}

	@Override
	public String getDisplayValue() {
		String key = getValue();
		Map<String,String> map = ((ListProperty)getCardProperty()).getOptionMap();
		if(key == null || map == null || !map.containsKey(key) || map.get(key) == null){
			return "";
		}
		return map.get(key);
	}
	
	@Override
	public ListPropertyValue clone()  {
		ListPropertyValue pv = new ListPropertyValue();
		pv.setCard(this.getCard());
		pv.setCardProperty(this.getCardProperty());
		pv.setValue(this.getValue());
		return pv;
	}

}
