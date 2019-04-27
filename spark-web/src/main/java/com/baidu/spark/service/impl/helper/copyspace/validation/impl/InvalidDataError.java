package com.baidu.spark.service.impl.helper.copyspace.validation.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.spark.service.impl.helper.copyspace.validation.ValidationResult;
import com.baidu.spark.util.MessageHolder;

/**
 * 不合法的数据错误
 * @author zhangjing_pe
 *
 */
public class InvalidDataError extends ValidationResult{

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String KEY1 = "spacecopy.validation.error.invalidData1";
	
	private static final String KEY2 = "spacecopy.validation.error.invalidData2";
	
	public InvalidDataError(Class<?> type) {
		super(KEY1, MessageHolder.get(METADATA_MESSAGE_PREFIX+type.getSimpleName()));
	}
	
	public InvalidDataError(Class<?> type,String str) {
		super(KEY2, MessageHolder.get(METADATA_MESSAGE_PREFIX+type.getSimpleName()),str);
		logger.error("InvalidDataError:{}",str);
	}

}
