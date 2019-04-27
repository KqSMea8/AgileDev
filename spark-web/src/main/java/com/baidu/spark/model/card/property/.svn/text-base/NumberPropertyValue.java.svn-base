package com.baidu.spark.model.card.property;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.lang.StringUtils;

import com.baidu.spark.exception.PropertyValueValidationException;


/**
 * 数字型属性值
 * @author chenhui
 *
 */
@Entity
@DiscriminatorValue(value=NumberPropertyValue.TYPE)
public class NumberPropertyValue extends CardPropertyValue<Integer>{
	
	/** 数字类型常量. */
	public static final String TYPE = "number";
	
	@Column(name="intvalue")
	private Integer value;

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public void setValue(Integer value) {
		this.value = value;
	}

	@Override
	public void initValueWithString(String value) {
		if(!StringUtils.isEmpty(value)){
			this.value = Integer.parseInt(value);	
		}
	}
	
	@Override
	protected void validateStringValue(String value) throws PropertyValueValidationException{
		super.validateStringValue(value);
		try {
			if(!StringUtils.isEmpty(value)){
				Integer.parseInt(value);
			}
		} catch (NumberFormatException ex) {
			throw new PropertyValueValidationException("cardpropertyvalue.validate.number.format",cardProperty.getName(),value);
		}
	}
	
	@Override
	public NumberPropertyValue clone()  {
		NumberPropertyValue pv = new NumberPropertyValue();
		pv.setCard(this.getCard());
		pv.setCardProperty(this.getCardProperty());
		pv.setValue(this.getValue());
		return pv;
	}
	
}
