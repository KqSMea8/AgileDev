package com.baidu.spark.exception;

/**
 * 视图层异常.
 * <p>
 * 当Controller中发生异常，但无法确定异常发生出
 * 加入到统一的Request错误处理对象中时
 * </p>
 * 
 * @author GuoLin
 *
 */
public class UnhandledViewException extends SparkRuntimeException {

	private static final long serialVersionUID = 7413964936869977032L;

	/**
	 * 构造器.
	 * <p>
	 * 将根据参数自动组装内容.
	 * </p>
	 * @param messageCode 国际化消息标识
	 * @param arguments 可变国际化消息参数
	 */
	public UnhandledViewException(String messageCode, Object... arguments) {
		super(messageCode, arguments);
	}

}
