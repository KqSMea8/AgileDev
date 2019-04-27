package com.baidu.spark.model.card.property;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 文本类型的卡片值
 * @author zhangjing_pe
 *
 */
@Entity
@DiscriminatorValue(value=TextProperty.TYPE)
public class TextProperty extends CardProperty {

	/** 文本类型. */
	public static final String TYPE = "text";
	
	@Override
	protected TextPropertyValue instantiateValue() {
		return new TextPropertyValue();
	}

	@Override
	public String getType() {
		return TYPE;
	}
	
}
