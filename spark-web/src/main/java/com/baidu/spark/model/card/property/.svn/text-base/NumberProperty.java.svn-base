package com.baidu.spark.model.card.property;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**列表类型的卡片值
 * @author zhangjing_pe
 *
 */
@Entity
@DiscriminatorValue(value=NumberProperty.TYPE)
public class NumberProperty extends CardProperty {

	/** 整形数字类型. */
	public static final String TYPE = "number";
	
	@Override
	protected NumberPropertyValue instantiateValue() {
		return new NumberPropertyValue();
	}

	@Override
	public String getType() {
		return TYPE;
	}
	
}
