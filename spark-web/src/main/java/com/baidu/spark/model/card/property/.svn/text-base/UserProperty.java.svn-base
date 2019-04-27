package com.baidu.spark.model.card.property;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 用户类型的卡片值
 * @author Adun
 */
@Entity
@DiscriminatorValue(value=UserProperty.TYPE)
public class UserProperty extends CardProperty {

	/** 用户类型. */
	public static final String TYPE = "user";
	
	@Override
	public String getType() {
		return "user";
	}

	@Override
	protected CardPropertyValue<?> instantiateValue() {
		return new UserPropertyValue();
	}

}
