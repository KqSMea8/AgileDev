package com.baidu.spark.exception;

import org.springframework.http.HttpStatus;

/**
 * 用于抛出响应状态的异常.
 * <p>该异常将被{@link com.baidu.spark.util.MappingExceptionResolver}捕获并分发到<code>errors/ajax/xxx</code>
 * 等模板渲染出纯文本的exception.message, 建议在<strong>ajax请求</strong>中使用该异常</p>
 * @author GuoLin
 *
 */
public class ResponseStatusException extends SparkRuntimeException {

	private static final long serialVersionUID = 8389846726216156579L;
	
	/** Http状态. */
	private HttpStatus status;
	
	/**
	 * 异常状态构造器.
	 * @param status Http状态，必须是客户端异常（4xx）或服务器端异常（5xx）
	 */
	public ResponseStatusException(HttpStatus status) {
		this(status, "");
	}

	/**
	 * 构造器.
	 * @param status Http状态，必须是客户端异常（4xx）或服务器端异常（5xx）
	 * @param messageCode 国际化消息code
	 * @param arguments 可变国际化消息参数
	 */
	public ResponseStatusException(HttpStatus status, String messageCode, Object... arguments) {
		super(messageCode, arguments);
		this.status = status;
		if (HttpStatus.Series.SERVER_ERROR != status.series() && HttpStatus.Series.CLIENT_ERROR != status.series()) {
			throw new IllegalArgumentException("Status must be a error status(4xx or 5xx), but " + status + ".");
		}
	}

	/**
	 * 获取异常的Http状态.
	 * @return Http状态
	 */
	public HttpStatus getStatus() {
		return status;
	}

}
