package com.baidu.spark.security.annotation.objevaluator;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baidu.spark.security.SparkSystemResource;
import com.baidu.spark.security.annotation.SecuredMethod;
/**
 * 系统级别权限配置的构造器
 * @author zhangjing_pe
 *
 */
@Service
public class SparkSystemResourceEvaluator extends BaseSecuredObjectEvaluator{
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected void setSecuredObjectInfo(Method method,Object[] args,SecuredMethod securedMethodAnno){
		securedObjClass = SparkSystemResource.class;
		securedObjId = SparkSystemResource.getResource().getId();
	}
}
