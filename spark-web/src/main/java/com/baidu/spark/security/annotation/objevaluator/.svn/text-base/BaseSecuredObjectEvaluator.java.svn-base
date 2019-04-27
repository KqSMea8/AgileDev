package com.baidu.spark.security.annotation.objevaluator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;

import com.baidu.spark.security.SparkPermissionEnum;
import com.baidu.spark.security.annotation.SecuredMethod;
import com.baidu.spark.security.voter.AccessPermissionBean;
/**
 * 抽象的解析器
 * @author zhangjing_pe
 *
 */
public abstract class BaseSecuredObjectEvaluator implements SecuredObjectEvaluator {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected boolean supports = true;
	/**所需要的空间权限*/
	protected List<Permission> requiredSparkPermission = null;
	/**资源对象的类型*/
	protected Class<?> securedObjClass = null;
	/**资源对象id*/
	protected Object securedObjId = null;
	
	protected List<Permission> getRequiredPermission(MethodInvocation methodInvocation){
		Method method = methodInvocation.getMethod();
		SecuredMethod securedMethodAnno = method.getAnnotation(SecuredMethod.class);
		List<Permission> ret = new ArrayList<Permission>();
		SparkPermissionEnum[] enums = securedMethodAnno.permission();
		if(enums!=null&&enums.length>0){
			for(SparkPermissionEnum ee:enums){
				ret.add(ee.getPermission());
			}
		}
		return ret;
	}
	
	public AccessPermissionBean getAccessPermissionBean(MethodInvocation methodInvocation){
		requiredSparkPermission = getRequiredPermission(methodInvocation);
		Method method = methodInvocation.getMethod();
		SecuredMethod securedMethodAnno = method.getAnnotation(SecuredMethod.class);
		setSecuredObjectInfo(methodInvocation.getMethod(), methodInvocation.getArguments(), securedMethodAnno);
		return new AccessPermissionBean(supports, requiredSparkPermission, securedObjClass, securedObjId);
	}
	/**
	 * 设置securedObj的几个属性
	 * @param method
	 * @param args
	 * @param securedMethodAnno
	 */
	protected abstract void setSecuredObjectInfo(Method method,Object[] args,SecuredMethod securedMethodAnno);
	/**
	 * @param arg
	 * @param identityProperty
	 */
	protected Object getObjectIdentity(Object arg, String identityProperty) {
		Object objId = null;
		if(StringUtils.isEmpty(identityProperty)){
			objId = arg;
		}else{
			try {
				objId = PropertyUtils.getProperty(arg, identityProperty);
			} catch (IllegalAccessException e) {
				objId = null;
				logger.warn("get Identity from secure object error! arg:"+arg+" securedObjClass:"+securedObjClass + " idProperty:"+identityProperty,e);
			} catch (InvocationTargetException e) {
				objId = null;
				logger.warn("get Identity from secure object error! arg:"+arg+" securedObjClass:"+securedObjClass + " idProperty:"+identityProperty,e);
			} catch (NoSuchMethodException e) {
				objId = null;
				logger.warn("get Identity from secure object error! arg:"+arg+" securedObjClass:"+securedObjClass + " idProperty:"+identityProperty,e);
			} catch (Exception e) {
				//其他所有情况，设置为objId为空，使用全局权限进行判断
				objId = null;
				logger.warn("get Identity from secure object error! arg:"+arg+" securedObjClass:"+securedObjClass + " idProperty:"+identityProperty,e);
			}
		}
		return objId;
	}
}
