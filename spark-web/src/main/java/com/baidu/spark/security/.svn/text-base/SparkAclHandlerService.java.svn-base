package com.baidu.spark.security;

import java.io.Serializable;
import java.util.List;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

public interface SparkAclHandlerService {

	/**
	 * 读取指定sid对应的资源的权限，并将权限集合拆分成权限列表（CumulativePermission拆分成Permission）
	 * @param sid 主体对象
	 * @param clz 资源类型
	 * @param clzId 资源id
	 * @return 权限列表
	 */
	public List<Permission> loadSplitPermission(Sid sid, Class<?> clz,
			Serializable clzId);
	/**
	 * 读取指定sid对应的资源的权限，返回acl中记录的mask对应的权限对象.
	 * 为空则返回“无权限”对象{@link SparkPermission.NONE}
	 * @param sid 主体对象
	 * @param clz 资源类型
	 * @param clzId 资源id
	 * @return 权限对象
	 */
	public Permission loadMergedPermission(Sid sid, Class<?> clz,
			Serializable clzId);
	/**
	 * 更新sid的权限。包括新增和更新的两种情况
	 * @param sid
	 * @param clz
	 * @param clzId
	 * @param permissionList
	 */
	public void updatePermission(Sid sid, Class<?> clz, Serializable clzId,
			Permission[] permissionList);
	
	/**
	 * 更新sid的权限。包括新增和更新的两种情况
	 * @param sid
	 * @param clz
	 * @param clzId
	 * @param maskList 权限掩码列表
	 */
	public void updatePermission(Sid sid, Class<?> clz, Serializable clzId,
			List<Integer> maskList);
	/**
	 * 更新sid的权限。包括新增和更新的两种情况
	 * @param sid
	 * @param clz
	 * @param clzId
	 * @param mergedPermission 合并后的权限对象
	 */
	public void updatePermission(Sid sid, Class<?> clz, Serializable clzId,
			Permission mergedPermission) ;

	/**
	 * 删除某主体对某资源的权限
	 * @param sid 主体对象 
	 * @param clz 资源对象类
	 * @param clzId 资源id
	 */
	public void deletePermission(Sid sid, Class<?> clz, Serializable clzId);

	/**
	 * 删除某资源的所有权限数据
	 * @param sid 主体对象 
	 * @param clz 资源对象类
	 * @param clzId 资源id
	 */
	public void deletePermission(Class<?> clz, Serializable clzId);
	
	/**
	 * 创建acl对象
	 * @param clz
	 * @param clzId
	 */
	public void createAcl(Class<?> clz, Serializable clzId);
}