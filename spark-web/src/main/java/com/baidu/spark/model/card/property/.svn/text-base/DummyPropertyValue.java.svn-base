package com.baidu.spark.model.card.property;

import com.baidu.spark.exception.PropertyValueValidationException;



/**
 * 傀儡属性值
 * 用于返回错误数据信息
 * @author zhangjing_pe
 *
 */
public class DummyPropertyValue extends CardPropertyValue<String>{

	/** 文本类型常量. */
	private String value;

	public String getValue() {
		return value;
	}
	
	public DummyPropertyValue(CardProperty cardProperty,String value ){
		setCardProperty(cardProperty);
		this.value = value;
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
	protected void validateStringValue(String value) throws PropertyValueValidationException{
		return ;
	}
	
	@Override
	public DummyPropertyValue clone()  {
		return new DummyPropertyValue(this.getCardProperty(), this.getValue());
	}
	
}
