package com.baidu.spark.service.impl.helper.copyspace.validation.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.spark.service.impl.helper.copyspace.validation.ValidationResult;
import com.baidu.spark.util.MessageHolder;

/**
 * 反序列化验证错误
 * @author zhangjing_pe
 *
 */
public class DeserializeError extends ValidationResult{
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String KEY = "spacecopy.validation.error.deserialize";
	
	/**
	 * 构造国际化消息
	 * @param type
	 */
	public DeserializeError(Class<?> type,Exception e) {
		super(KEY, MessageHolder.get(METADATA_MESSAGE_PREFIX+type.getSimpleName()));
		logger.error("deserializeError from "+type.getSimpleName(), e);
	}

}
