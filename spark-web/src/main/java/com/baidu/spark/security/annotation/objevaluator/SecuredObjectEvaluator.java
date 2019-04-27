package com.baidu.spark.security.annotation.objevaluator;

import org.aopalliance.intercept.MethodInvocation;

import com.baidu.spark.security.voter.AccessPermissionBean;
/**
 * 解析
 * @author zhangjing_pe
 *
 */
public interface SecuredObjectEvaluator {

	public AccessPermissionBean getAccessPermissionBean(MethodInvocation invocation);
	
}
