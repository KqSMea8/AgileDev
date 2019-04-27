package com.baidu.spark.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.Assert;

/**
 * 自定义国际化资源消息源.
 * <p>
 * 继承于{@link ResourceBundleMessageSource}, 用于扩展出一次性获取所有国际化资源的功能, 
 * 同时又能够使用Spring原有的缓存机制.
 * </p>
 * <p>
 * 要使用此功能必须直接使用实现类，而不能通过接口调用.
 * </p>
 * @see ResourceBundleMessageSource
 * 
 * @author GuoLin
 *
 */
public class CustomResourceBundleMessageSource extends ResourceBundleMessageSource {
	
	/** 资源文件名称. */
	protected String[] basenames = new String[0];

	/**
	 * 获取所有的国际化消息资源.
	 * @param locale 地域信息
	 * @return 国际消息资源Map，key为国际化消息标识，value为国际化消息内容
	 */
	public Map<String, String> getAllMessages(Locale locale) {
		Map<String, String> messages = new HashMap<String, String>();
		for (int i = 0; i < this.basenames.length; i++) {
			ResourceBundle bundle = getResourceBundle(this.basenames[i], locale);
			if (bundle == null) {
				continue;
			}
			for (String key : bundle.keySet()) {
				messages.put(key, bundle.getString(key));
			}
		}
		return messages;
	}
	
	/**
	 * 重写此方法用于将设置的basenames保存在当前类.
	 * 否则此类将无法获取到basenames
	 */
	@Override
	public void setBasenames(String[] basenames)  {
		// 断言
		if (basenames != null) {
			this.basenames = new String[basenames.length];
			for (int i = 0; i < basenames.length; i++) {
				String basename = basenames[i];
				Assert.hasText(basename, "Basename must not be empty");
				this.basenames[i] = basename.trim();
			}
		}
		else {
			this.basenames = new String[0];
		}
		
		super.setBasenames(basenames);
	}
	
}
