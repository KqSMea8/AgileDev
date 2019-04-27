package com.baidu.spark.security;

import java.util.List;

import org.springframework.security.acls.model.Permission;

import com.baidu.spark.model.Group;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.Card;

/**
 * 权限服务接口
 * @author zhangjing_pe
 *
 */
public interface PermissionService {

	/**
	 * 创建space的acl对象
	 * @param space
	 */
	public void createAcl(Space space);
	
	/**
	 * 创建card的acl对象
	 * @param card
	 */
	public void createAcl(Card card);
	/**
	 * 获取用户对空间的权限列表
	 * @param space 空间对象
	 * @return 用户的权限集合
	 */
	public List<Permission> getUserPermission(Long userId,Space space);
	/**
	 * 获取用户对卡片的权限列表
	 * @param card 卡片对象
	 * @return 用户的权限集合
	 */
	public List<Permission> getUserPermission(Long userId,Card card);
	/**
	 * 删除卡片关联的权限
	 * @param card
	 */
	public void deletePermission(Card card);
	/**
	 * 删除空间关联的权限
	 * @param space
	 */
	public void deletePermission(Space space);
	/**
	 * 删除用户组对指定空间的权限
	 * @param group
	 * @param space
	 */
	public void deletePermission(Group group,Space space);
	/**
	 * 删除用户组对系统级别的权限
	 * @param group
	 */
	public void deleteSystemPermission(Group group);
	/**
	 * 更新用户组对制定空间的权限
	 * @param group
	 * @param space
	 * @param permissionMaskList
	 */
	public void updatePermission(Group group,Space space,List<Integer> permissionMaskList);
	/**
	 * 更新用户组对系统级别的权限
	 * @param group
	 * @param permissionMaskList 权限掩码的list
	 */
	public void updateSystemPermission(Group group,List<Integer> permissionMaskList);
	
	/**
	 * 更新用户组对空间的权限
	 * @param group
	 * @param space
	 * @param permissionMask 单一的权限掩码
	 */
	public void updatePermission(Group group,Space space,Integer permissionMask);
	/**
	 * 获取用户组对指定空间的权限集合
	 * @param group 用户组
	 * @param space 空间
	 * @return merged后的权限
	 */
	public Permission getMergedPermission(Group group , Space space);
	/**
	 * 获取用户组对系统级别的权限集合
	 * @param group 用户组 
	 * @return merged后的权限
	 */
	public Permission getSystemMergedPermission(Group group);
	
}
