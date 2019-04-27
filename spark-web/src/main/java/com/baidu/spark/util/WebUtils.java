package com.baidu.spark.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import com.baidu.spark.exception.ResponseStatusException;
import com.baidu.spark.exception.UnhandledViewException;

/**
 * web端工具方法
 * @author zhangjing_pe
 * @author shixiaolei
 *
 */
public abstract class WebUtils {
	
	public static final String INFO_MESSAGE_KEY = "spark_web_info_message";
	/**
	 * 设置页面提示的信息
	 * 使用公共的key设置到flashscope中。页面使用统一的template页面来显示info信息
	 * @param request
	 * @param info 页面提示的信息。
	 */
	@SuppressWarnings("unchecked")
	public static void setInfoMessage(HttpServletRequest request,String info){
		Assert.notNull(request);
		((Map<String,String>)FlashScope.getCurrent(request)).put(INFO_MESSAGE_KEY, info);
	}
	
	/**
	 * 仿Play框架Controller类的工具方法,如果o为null, 抛出404状态的ResponseStatusException.
	 * @param o
	 */
	public static void notFoundIfNull(Object object){
		if (object == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * 仿Play框架Controller类的工具方法, 抛出500状态的ResponseStatusException.
	 * @param messageCode 错误信息的国际化key
	 */
	public static void internalServerError(String messageCode){
		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, messageCode);
	}
	
	/**
	 * 仿Play框架Controller类的工具方法,抛出500状态的ResponseStatusException.
	 */
	public static void innerServerError(){
		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 仿Play框架Controller类的工具方法,如果o为null, 抛出{@link com.baidu.spark.exception.UnhandledViewException}.
	 */
	public static void unhandledViewIfNull(Object object, String messageCode, Object... arguments){
		if(object == null){
			throw new UnhandledViewException(messageCode, arguments);
		}
	}
	
}
