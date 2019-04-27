package com.baidu.spark.security.voter;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.util.Assert;

import com.baidu.spark.security.SparkSystemResource;

/**
 * {@link SparkAclVoter} 调用vote时进行参数传递的辅助bean
 * 用于保存从annotation配置中分析出的结果，并进行permission扩展和转化的辅助计算等
 * 
 * @author zhangjing_pe
 * 
 */
public class AccessPermissionBean {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	/**是否应由SparkAclVoter支持*/
	private boolean supports = true;
	/**所需要的空间权限*/
	private List<Permission> requiredSparkPermission = null;
	/**资源对象的类型*/
	private Class<?> securedObjClass = null;
	/**资源对象id*/
	private Object securedObjId = null;
	
	public AccessPermissionBean(boolean supports,
			List<Permission> requiredSparkPermission, Class<?> securedObjClass,
			Object securedObjId) {
		super();
		this.supports = supports;
		this.requiredSparkPermission = requiredSparkPermission;
		this.securedObjClass = securedObjClass;
		this.securedObjId = securedObjId;
	}


	public AccessPermissionBean(Object object,List<Permission> permissions){
		Assert.notEmpty(permissions);
		if(object != null){
			securedObjClass = object.getClass();
			securedObjId = getObjectIdentity(object, "id");
		}else{
			securedObjClass = SparkSystemResource.class;
			securedObjId = SparkSystemResource.getResource().getId();
		}
		requiredSparkPermission = permissions;
	}
	
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
	
	public Class<?> getSecuredObjClass() {
		return securedObjClass;
	}

	public void setSecuredObjClass(Class<?> securedObjClass) {
		this.securedObjClass = securedObjClass;
	}

	public String getSecuredObjId() {
		if(securedObjId == null){
			return null;
		}
		return securedObjId.toString();
	}

	public void setSecuredObjId(Object securedObjId) {
		this.securedObjId = securedObjId;
	}

	public boolean isSupports() {
		return supports;
	}
	
	public List<Permission> getRequiredSparkPermission() {
		return requiredSparkPermission;
	}

	public void setSupports(boolean supports) {
		this.supports = supports;
	}

	public void setRequiredSparkPermission(List<Permission> requiredSparkPermission) {
		this.requiredSparkPermission = requiredSparkPermission;
	}
	
}
