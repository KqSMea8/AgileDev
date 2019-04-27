package com.baidu.spark.web.taglib;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;

import com.baidu.spark.exception.SparkRuntimeException;
import com.baidu.spark.util.SparkConfig;

/**
 * URL Taglib
 * 
 * @author GuoLin
 *
 */
public class UrlTag extends BodyTagSupport implements DynamicAttributes {
	
	private static final long serialVersionUID = -841139275857329796L;
	
	/** 获取的参数 */
	private final Map<String, Object> params = new HashMap<String, Object>();
	
	/** URL值 */
	private String value;
	
	/** 是否包含参数 */
	private boolean includeParams = false;
	
	public void setValue(String value) {
		this.value = value;
	}

	public void setIncludeParams(boolean includeParams) {
		this.includeParams = includeParams;
	}

	@SuppressWarnings("unchecked")
	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		
		// TODO value应该允许自动绑定contextPath
		StringBuilder result = new StringBuilder(value);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		// 获取所有的当前页面参数
		if (includeParams) {
			paramMap.putAll(pageContext.getRequest().getParameterMap());
		}
		
		// 用设定的参数覆盖已有参数
		paramMap.putAll(params);
		
		result.append("?").append(serializeParams(paramMap));
		
		try {
			out.print(result);
		} catch (IOException ex) {
			throw new JspException(ex);
		}
		
		return super.doEndTag();
	}
	
	/**
	 * 序列化参数.
	 * @param paramMap 参数Map
	 * @return 序列化后的参数CharSequence
	 */
	private CharSequence serializeParams(Map<String, Object> paramMap) {
		StringBuilder result = new StringBuilder();
		for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
			
			// 转换String[]类型
			Object v = entry.getValue();
			if (v instanceof Object[]) {
				for (Object o : (Object[])v){
					if (result.length() > 0) {
						result.append("&");
					}
					appendParamValue(result, entry.getKey(), o.toString());
				}
			}else{
				appendParamValue(result, entry.getKey(), v.toString());
			}
		}
		return result;
	}
	/**
	 * 拼接queryParameter.因为是拼接查询串，对于参数值进行URLEncoding
	 * @param sb 拼接查询串的stringBuilder
	 * @param paramName 参数名
	 * @param paramValue 参数值
	 */
	private void appendParamValue(StringBuilder sb,String paramName,String paramValue){
		if (sb.length() > 0) {
			sb.append("&");
		}
		try {
			sb.append(paramName).append("=").append(URLEncoder.encode(paramValue, SparkConfig.getCharacterEncoding()));
		} catch (UnsupportedEncodingException e) {
			throw new SparkRuntimeException("error encoding parameter");
		}
	}
	
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
		params.put(localName, value);
	}

}
