package com.baidu.spark.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Flash Scope对象.
 * 
 * <p>用于模拟Ruby on Rails中的Flash作用域. 允许用户调用{@link #getCurrent(HttpServletRequest)}方法获取到当前的FlashMap.</p>
 * 
 * <p>关于Flash Scope的概念源于Ruby on Rails，见<a href="http://api.rubyonrails.org/classes/ActionController/Flash.html">ActionController::Flash</a>：
 * <quote>
 * The flash provides a way to pass temporary objects between actions. Anything you place in the flash will be exposed 
 * to the very next action and then cleared out. This is a great way of doing notices and alerts, such as a create action 
 * that sets flash[:notice] = "Successfully created" before redirecting to a display action that can then expose the flash 
 * to its template. Actually, that exposure is automatically done.
 * </quote></p>
 * 
 * <p>关于此扩展方式，见Spring官网JIRA <a href="https://jira.springframework.org/browse/SPR-6464">Flash Scope for Spring MVC</a>.</p>
 * 
 * @author GuoLin
 * @see FlashScopeFilter
 *
 */
public final class FlashScope {
	
	static final String FLASH_SCOPE_ATTRIBUTE = FlashScope.class.getName();
	
	/**
	 * 私有构造器.
	 */
	private FlashScope() {
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, ?> getCurrent(HttpServletRequest request) {
		HttpSession session = request.getSession(); 
		Map<String, ?> flash = (Map<String, ?>) session.getAttribute(FLASH_SCOPE_ATTRIBUTE);
		if (flash == null) {
			flash = new HashMap<String, Object>();
			session.setAttribute(FLASH_SCOPE_ATTRIBUTE, flash);
		}
		return flash;
	}
	
}
