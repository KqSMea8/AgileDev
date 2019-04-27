package com.baidu.spark.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

/**
 * 国际化静态工具类.
 * <p>
 * 用于简化对{@link MessageSource}的调用.
 * 解决每次在Controller中调用MessageSource必须注入{@link MessageSource}的麻烦,
 * 同时也可以提供给非HTTP请求或者不经过Controller的类（比如VO或工具类等）使用.
 * <p>
 * 
 * @author GuoLin
 *
 */
public class MessageHolder {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageHolder.class);
	
	/** 存储Locale对象的本地线程变量. */
	private static ThreadLocal<Locale> locale = new ThreadLocal<Locale>();

	/** Spring的MessageSource. */
	private static MessageSource messageSource;
	
	/**
	 * 获取指定标识的国际化消息，并将参数注入进去.
	 * <p>
	 * 参数注入方式依据于{@link java.text.MessageFormat}
	 * </p>
	 * @param code 国际化消息标识
	 * @param arguments 参数
	 * @return 完成后的国际化消息
	 * @see java.text.MessageFormat
	 */
	public static String get(String code, Object... arguments) {
		Locale locale = MessageHolder.locale.get();
		return messageSource.getMessage(code, arguments, (locale != null) ? locale : Locale.getDefault());
	}
	
	/**
	 * 获取所有国际化消息，以Map的形式返回.
	 * @return 所有国际化消息
	 */
	public static Map<String, String> all() {
		Locale locale = MessageHolder.locale.get();
		if (messageSource instanceof CustomResourceBundleMessageSource) {
			return ((CustomResourceBundleMessageSource) messageSource).getAllMessages(locale);
		} 
		else {
			if (logger.isWarnEnabled()) {
				logger.warn("The message source injected is not a CustomResourceBundleMessageSource, so we cannot get all messages.");
			}
			return new HashMap<String, String>();
		}
	}
	
	/**
	 * 获取当前的地址对象.
	 * @return 地址对象
	 */
	public static Locale getLocale() {
		Locale locale = MessageHolder.locale.get();
		return (locale != null) ? locale : Locale.getDefault();
	}
	
	/**
	 * 设置Locale.
	 * <p>
	 * 设置的locale将存储到ThreadLocal中供线程调用.
	 * </p>
	 * @param locale 需设定的Locale对象
	 */
	public static void setLocale(Locale locale) {
		MessageHolder.locale.set(locale);
	}

	/**
	 * 设置消息源.
	 * <p>
	 * 一般而言应该注入{@link org.springframework.context.support.ResourceBundleMessageSource}
	 * </p>
	 * @param messageSource 消息源
	 */
	public void setMessageSource(MessageSource messageSource) {
		MessageHolder.messageSource = messageSource;
	}
	
}
