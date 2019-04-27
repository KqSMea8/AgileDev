package com.baidu.spark.filter;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.baidu.spark.util.MessageHolder;

/**
 * 从request中获取locale信息,放入MessageHolder中
 * @author 阿蹲
 * 2010-11-08
 */
public class MessageHolderFilter extends OncePerRequestFilter {
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 	throws ServletException, IOException {
		Locale locale = (Locale) request.getLocale();
		if ( null != locale ){
			MessageHolder.setLocale( locale );
		}
		filterChain.doFilter(request, response);
	}
}


