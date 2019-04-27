package com.baidu.spark.exception;


/**
 * 自定义字段验证相关的异常
 * @author zhangjing_pe
 */
public class PropertyValueValidationException extends Exception {
	
	private static final long serialVersionUID = -3332948450640366416L;
	/**
	 * 错误码
	 */
	private String messageCode = null;
	/**
	 * 参数
	 */
	private Object[] arguments = null;
	
	public PropertyValueValidationException(String messageCode, Object... arguments) {
		this.messageCode = messageCode;
		this.arguments = arguments;
    }

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}
	
}
