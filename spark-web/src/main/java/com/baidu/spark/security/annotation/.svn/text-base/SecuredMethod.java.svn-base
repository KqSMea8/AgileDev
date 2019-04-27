package com.baidu.spark.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.baidu.spark.security.SparkPermissionEnum;
import com.baidu.spark.security.annotation.objevaluator.DefaultSecuredObjectEvaluator;
import com.baidu.spark.security.annotation.objevaluator.SecuredObjectEvaluator;
/**
 * 方法权限规则定义的annotation
 * @author zhangjing_pe
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SecuredMethod {
	/**
	 * 所需要的空间权限
	 * @return
	 */
	public SparkPermissionEnum[] permission() default {};
	//TODO 增加对多种权限组合的判断 ，可使用另外一组annotation public SecuredParam[] params() default {};
	
	public Class<? extends SecuredObjectEvaluator> objEvaluator() default DefaultSecuredObjectEvaluator.class;
}
