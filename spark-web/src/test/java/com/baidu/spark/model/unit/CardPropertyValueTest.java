package com.baidu.spark.model.unit;

import static org.junit.Assert.assertNotNull;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

/**
 * 诡异JDK bug测试用例.
 * <p>
 * 使用方法：
 * <ol>
 * <li>直接运行测试用例，第一个case挂掉，第二个case正常</li>
 * <li>删除CardPropertyValue及其子类中的validateStringValue方法，再次运行，第一个case正常，第二个case会挂掉</li>
 * <li>删除DatePropertyValue中的getDisplayValue方法，再次运行，两个case均会挂掉</li>
 * <li>删除CardPropertyValue及其子类中的getValue或setValue方法，再次运行，所有case都会正常</li>
 * </ol>
 * </p>
 * 
 * @author GuoLin
 *
 */
public class CardPropertyValueTest {

	@Test
	@Ignore //by 田雨松.如类上注释所说,会诡异地挂掉...
	public void reflectionGetCardPropertyFromTextPropertyValue() {
		PropertyDescriptor descriptor = getDescriptor(TextPropertyValue.class, "cardProperty");
		assertNotNull(descriptor);
		assertNotNull(descriptor.getReadMethod());
		assertNotNull(descriptor.getWriteMethod());
	}
	
	@Test
	public void reflectionGetCardPropertyFromDatePropertyValue() {
		PropertyDescriptor descriptor = getDescriptor(DatePropertyValue.class, "cardProperty");
		assertNotNull(descriptor);
		assertNotNull(descriptor.getReadMethod());
		assertNotNull(descriptor.getWriteMethod());
	}
	
	public static abstract class CardProperty  {
	}
	
	public static class DateProperty extends CardProperty {
	}

	public static class TextProperty extends CardProperty {
	}
	
	public static abstract class CardPropertyValue<T> {
		
		public CardProperty getCardProperty() { return null; }

		public void setCardProperty(CardProperty cardProperty) { }
		
		public abstract T getValue();
		
		public abstract void setValue(T value);
		
		protected void validateStringValue(String value) { }
		
		public String getDisplayValue() { return null; }

	}

	public static class DatePropertyValue extends CardPropertyValue<Date>{

		@Override
		public Date getValue() { return null; }

		@Override
		public void setValue(Date value) { }

		@Override
		public DateProperty getCardProperty() { return null; }

		@Override
		protected void validateStringValue(String value) { }

		@Override
		public String getDisplayValue() { return null; }
		
	}
	
	public static class TextPropertyValue extends CardPropertyValue<String>{

		@Override
		public String getValue() { return null; }

		@Override
		public void setValue(String value) { }

		@Override
		public TextProperty getCardProperty() { return null; }
		
		@Override
		protected void validateStringValue(String value) { }
		
	}

	private static PropertyDescriptor getDescriptor(Class<?> clazz, String propertyName) {
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException ex) {
			return null;
		}
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		for (int i = 0; i < descriptors.length; i++) {
			if (propertyName.equals(descriptors[i].getName())) {
				return descriptors[i];
			}
		}
		return null;
	}
	
}
