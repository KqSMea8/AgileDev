package com.baidu.spark.model.card.property;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 文本类型的卡片值
 * @author zhangjing_pe
 *
 */
@Entity
@DiscriminatorValue(value=DateProperty.TYPE)
public class DateProperty extends CardProperty {
	
	/** 日期时间类型. */
	public static final String TYPE = "date";
	
	@Override
	protected DatePropertyValue instantiateValue() {
		return new DatePropertyValue();
	}

	@Override
	public String getType() {
		return TYPE;
	}
	
}
