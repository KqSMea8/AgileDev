package com.baidu.spark.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.util.Assert;

/**
 * 反射的Utils函数集合.
 * 
 * @author calvin
 * @author GuoLin
 */
public abstract class ReflectionUtils {
	
	/**
	 * 获取指定bean上的指定属性的值.
	 * @param bean 指定bean
	 * @param propertyName 指定属性
	 * @return 执行指定bean上属性的getter方法后的返回值
	 */
	public static Object getPropertyValue(Object bean, String propertyName) {
		// 获取属性描述
        PropertyDescriptor descriptor = getPropertyDescriptor(bean.getClass(), propertyName);
		if (descriptor == null) {
			throw new RuntimeException(new NoSuchMethodException("Specified property '"
					+ propertyName + "' on '" + bean.getClass() + "' could not found."));
		}
		
		// 获取getter方法
		Method readMethod = descriptor.getReadMethod();
		if (readMethod == null) {
			throw new RuntimeException(new NoSuchMethodException("Property '" + propertyName
					+ "' has no getter method in class '" + bean.getClass() + "'"));
		}
		
		// 执行getter方法并返回值
		return invokeMethod(bean, readMethod);
	}
	
	/**
	 * 执行一个方法.
	 * @param bean 方法所在bean
	 * @param method 方法
	 * @param arguments 参数
	 * @return 方法执行后的返回值
	 */
	public static Object invokeMethod(Object bean, Method method, Object... arguments) {
		try {
			return method.invoke(bean, arguments);
		} catch (IllegalArgumentException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		} catch (InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 获取指定类上的PropertyDescriptor数组.
	 * @param beanClass 类型
	 * @return PropertyDescriptor数组，不会为null
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> beanClass) {
		if (beanClass == null) {
			throw new IllegalArgumentException("Arguments must not be null, beanClass: " + beanClass);
		}
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(beanClass);
		} catch (IntrospectionException e) {
			return (new PropertyDescriptor[0]);
		}
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		return (descriptors == null) ? new PropertyDescriptor[0] : descriptors;
	}
	
	/**
	 * 获取指定类上的PropertyDescriptor.
	 * @param beanClass 类型
	 * @param propertyName 属性名称
	 * @return 如果没有找到指定属性返回null，否则返回指定属性的值PropertyDescriptor
	 */
	public static PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String propertyName) {
		if (beanClass == null || propertyName == null) {
			throw new IllegalArgumentException("Arguments must not be null, beanClass: '" 
					+ beanClass + "', propertyName: '" + propertyName + "'");
		}
		PropertyDescriptor[] descriptors = getPropertyDescriptors(beanClass);
		for (int i = 0; i < descriptors.length; i++) {
			if (propertyName.equals(descriptors[i].getName())) {
				return (descriptors[i]);
			}
		}
		return null;
	}
	
	/**
	 * 直接读取对象属性值,无视private/protected修饰符,不经过getter函数.
	 * @param object 待获取值的源对象
	 * @param fieldName 对象上的属性名称
	 * @return 获取到的属性的值
	 */
	public static Object getFieldValue(final Object object, final String fieldName) {
		Field field = getDeclaredField(object, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
		}

		makeAccessible(field);

		Object result = null;
		try {
			result = field.get(object);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
		return result;
	}

	/**
	 * 直接设置对象属性值,无视private/protected修饰符,不经过setter函数.
	 * @param object 待获取值的源对象
	 * @param fieldName 对象上的属性名称
	 * @param value 待设置的值
	 */
	public static void setFieldValue(final Object object, final String fieldName, final Object value) {
		Field field = getDeclaredField(object, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
		}

		makeAccessible(field);

		try {
			field.set(object, value);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 循环向上转型,获取对象的DeclaredField.
	 * @param object 待获取字段的源对象
	 * @param fieldName 字段名称
	 * @return 字段
	 */
	public static Field getDeclaredField(final Object object, final String fieldName) {
		Assert.notNull(object);
		return getDeclaredField(object.getClass(), fieldName);
	}

	/**
	 * 循环向上转型,获取类的DeclaredField.
	 * @param clazz 类型
	 * @param fieldName 字段名称
	 * @return 字段
	 */
	@SuppressWarnings("unchecked")
	public static Field getDeclaredField(final Class clazz, final String fieldName) {
		Assert.notNull(clazz);
		Assert.hasText(fieldName);
		for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException ex) {
				// Field不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	/**
	 * 实例化一个类.
	 * @param <T> 待实例化的类型
	 * @param clazz 待实例化的类型
	 * @return 实例化完成后的实例
	 */
	public static <T> T instantiate(Class<T> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		if (clazz.isInterface()) {
			throw new RuntimeException("Specified class is an interface");
		}
		try {
			return clazz.newInstance();
		} catch (InstantiationException ex) {
			throw new RuntimeException("Is it an abstract class?", ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException("Is the constructor accessible?", ex);
		}
	}
	
	/**
	 * 强制转换fileld可访问.
	 * @param field 待修改的字段
	 */
	public static void makeAccessible(Field field) {
		if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
			field.setAccessible(true);
		}
	}

}
