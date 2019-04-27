package com.baidu.spark.service.impl.helper.copyspace.validation;

import com.baidu.spark.util.MessageHolder;

/**
 * 空间定义反序列化的验证结果
 * @author zhangjing_pe
 *
 */
public class ValidationResult {
	
	protected static final String METADATA_MESSAGE_PREFIX = "spacecopy.validation.error.";

	protected String errorMsg;
	
	/**
	 * 构造器
	 * @param key 国际化消息key
	 * @param arguments 可变国际化消息参数
	 */
	public ValidationResult(String key,Object... arguments ) {
		errorMsg = MessageHolder.get(key, arguments);
	}
	
	public String getError(){
		return errorMsg;
	}
}
