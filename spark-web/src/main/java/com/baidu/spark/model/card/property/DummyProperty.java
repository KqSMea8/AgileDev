package com.baidu.spark.model.card.property;

import java.util.Set;

import javax.validation.constraints.NotNull;

/**
 * 傀儡属性.
 * <p>
 * 用于实例化未知类型属性用.
 * </p>
 * 
 * @author GuoLin
 * 
 */
public class DummyProperty extends CardProperty {

	/** 某一个未知类型. */
	@NotNull
	private String type;


	public void setType(String type) {
		this.type = type;
	}

	@Override
	protected CardPropertyValue<?> instantiateValue() {
		throw new UnsupportedOperationException(
				"instantiateValue method cannot be used in dummy property.");
	}

	@Override
	public String getType() {
		return type;
	}

	/**
	 * 工厂方法.
	 * <p>
	 * 根据DummyProperty的值创建真实的Property对象.
	 * </p>
	 * 
	 * @return 真实的Property对象
	 * @throws IllegalArgumentException
	 *             info字段转化时进行验证。若验证失败，抛出异常
	 * @throws UnsupportedOperationException
	 *             若找不到匹配的操作，抛出异常
	 */
	public CardProperty generateActualProperty() {
		CardProperty actualProperty = null;
		if (DateProperty.TYPE.equals(type)) {
			actualProperty = new DateProperty();
		} else if (ListProperty.TYPE.equals(type)) {
			actualProperty = new ListProperty();
		} else if (NumberProperty.TYPE.equals(type)) {
			actualProperty = new NumberProperty();
		} else if (TextProperty.TYPE.equals(type)) {
			actualProperty = new TextProperty();
		} else if (UserProperty.TYPE.equals(type)) {
			actualProperty = new UserProperty();
		} else {
			throw new UnsupportedOperationException("The specified type: ["
					+ type + "] is not supported.");
		}
		actualProperty.setId(getId());
		actualProperty.setHidden(getHidden());
		actualProperty.setInfo(getInfo());
		actualProperty.setName(getName());
		actualProperty.setSort(getSort());
		actualProperty.setLocalId(getLocalId());
		actualProperty.setCardTypes(getCardTypes());
		actualProperty.setSpace(getSpace());
		actualProperty.setCardTypes(getCardTypes());
		return actualProperty;
	}

	public void setValues(Set<CardPropertyValue<?>> values) {
		throw new UnsupportedOperationException(
		"setValues method cannot be used in dummy property.");
	}
}
