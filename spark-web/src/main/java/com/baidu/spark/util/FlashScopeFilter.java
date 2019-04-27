package com.baidu.spark.util;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 用于扩展Spring MVC的Flash Scope.
 * 
 * <p>通过在Session中存储{@link FlashScope}对象来实现Flash Scope.</p>
 * 
 * @author GuoLin
 * @see FlashScope
 *
 */
public class FlashScopeFilter extends OncePerRequestFilter {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	/**url过滤的正则表达式*/
	protected Pattern FLASH_SCOPE_FILTER_PATTERN = null;
	
	protected void initFilterBean() throws ServletException {
		String pattern = getFilterConfig().getInitParameter("filterPattern");
		FLASH_SCOPE_FILTER_PATTERN = Pattern.compile(pattern);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		logger.debug(request.getRequestURI());
		Matcher m = FLASH_SCOPE_FILTER_PATTERN.matcher(request.getRequestURI().toLowerCase());
		if(m.matches()){
			logger.debug("match!!!");
			HttpSession session = request.getSession(false);
			if (session != null) {
				Map<String, ?> flash = (Map<String, ?>) session.getAttribute(FlashScope.FLASH_SCOPE_ATTRIBUTE);
				if (flash != null) {
					for (Map.Entry<String, ?> entry : flash.entrySet()) {
						Object currentValue = request.getAttribute(entry.getKey());
						
						// 将Session中的值转存到Request中
						if (currentValue == null) {
							request.setAttribute(entry.getKey(), entry.getValue());
						}					
					}
					session.removeAttribute(FlashScope.FLASH_SCOPE_ATTRIBUTE);
				}
			}
		}
		filterChain.doFilter(request, response);
	}

}
