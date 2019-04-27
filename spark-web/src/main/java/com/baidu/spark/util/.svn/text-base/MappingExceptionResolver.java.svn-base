package com.baidu.spark.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.baidu.spark.exception.ResponseStatusException;

/**
 * 错误处理类.
 * 
 * @author zhangjing_pe
 * @author GuoLin
 * @author shixiaolei
 * 
 */
public class MappingExceptionResolver extends SimpleMappingExceptionResolver {

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {

		// 判断异常类型是否为ResponseStatusException
		if (ex instanceof ResponseStatusException) {
			ResponseStatusException rse = (ResponseStatusException) ex;
			HttpStatus status = rse.getStatus();
			response.setStatus(status.value());
			switch (status) {
				case NOT_FOUND:
					return super.getModelAndView("errors/ajax/404", ex, request);  
				case FORBIDDEN:
					return super.getModelAndView("errors/ajax/403", ex, request);
				default:
					return super.getModelAndView("errors/ajax/500", ex, request);
			}
		} else {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return super.doResolveException(request, response, handler, ex);
		}
	}

	protected ModelAndView getModelAndView(String viewName, Exception ex) {
		// TODO 对异常种类进行判断，决定是否输入日志
		logger.error("", ex);
		return super.getModelAndView(viewName, ex);
	}

}
