package com.baidu.spark.model.card.property;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.baidu.spark.exception.PropertyValueValidationException;



/**
 * 文本型属性值
 * @author chenhui
 *
 */
@Entity
@DiscriminatorValue(value=TextPropertyValue.TYPE)
public class TextPropertyValue extends CardPropertyValue<String>{

	/** 文本类型常量. */
	public static final String TYPE = "text";
	
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
	protected void validateStringValue(String value) throws PropertyValueValidationException{
	}
	
	@Override
	public TextPropertyValue clone()  {
		TextPropertyValue pv = new TextPropertyValue();
		pv.setCard(this.getCard());
		pv.setCardProperty(this.getCardProperty());
		pv.setValue(this.getValue());
		return pv;
	}
	
}
