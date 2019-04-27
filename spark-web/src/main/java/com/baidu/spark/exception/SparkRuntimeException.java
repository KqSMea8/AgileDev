package com.baidu.spark.exception;

import com.baidu.spark.util.MessageHolder;

/**
 * Spark项目顶级异常类.
 * <p>
 * 所有自定义异常类必须继承此异常类.
 * 此异常为Unchecked异常，以减少客户端使用代价.
 * </p>
 * 
 * @author GuoLin
 *
 */
public class SparkRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -2768963939243111263L;

	/**
	 * 异常构造器.
	 * @param messageCode 国际化消息key
	 * @param arguments 可变国际化消息参数
	 */
	public SparkRuntimeException(String messageCode, Object... arguments) {
		super(MessageHolder.get(messageCode, arguments));
	}
	
	/**
	 * 异常构造器.
	 * @param messageCode 国际化消息key
	 * @param cause 待包装的异常
	 */
	public SparkRuntimeException(String messageCode, Throwable cause) {
		super(MessageHolder.get(messageCode), cause);
	}
	
	/**
	 * 异常构造器.
	 * @param messageCode 国际化消息key
	 * @param arguments 可变国际化消息参数
	 * @param cause 待包装的异常
	 */
	public SparkRuntimeException(String messageCode, Object[] arguments, Throwable cause) {
		super(MessageHolder.get(messageCode, arguments), cause);
	}
}
