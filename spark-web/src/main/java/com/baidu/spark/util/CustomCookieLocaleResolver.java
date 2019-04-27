package com.baidu.spark.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.i18n.CookieLocaleResolver;

/**
 * 自定义基于cookie的国际化解析器.
 * 
 * @author GuoLin
 *
 */
public class CustomCookieLocaleResolver extends CookieLocaleResolver {

	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		Locale locale = super.resolveLocale(request);
		
		// 将locale注入到MessageHolder的ThreadLocale中
		MessageHolder.setLocale(locale);
		
		return locale;
	}
}
