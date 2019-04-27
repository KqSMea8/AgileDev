package com.baidu.spark.service;

import java.util.List;

import org.springframework.security.access.annotation.Secured;

import com.baidu.spark.model.Group;
import com.baidu.spark.model.Space;
import com.baidu.spark.security.SparkPermissionEnum;
import com.baidu.spark.security.annotation.SecuredMethod;
import com.baidu.spark.security.annotation.SecuredObj;

/**
 * Group Service.
 * 用户组服务接口
 * 
 * @author zhangjing_pe
 *
 */
public interface GroupService {
	
	/**
	 * 保存用户组对象，并对空间用户组增加冗余操作确认空间对用户组的映射。不处理acl数据
	 * 此方法会确保GroupSpace中有一条与owner的映射数据.若空间为空，则GroupSpace中的space也为空
	 * @param group 用户组对象，name必填
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission=SparkPermissionEnum.ADMIN)
	public void saveGroup(@SecuredObj(clazz=Space.class,idPropertyName="owner.id") Group group);
	
	/**
	 * 保存用户组信息、人员信息、与空间的映射关系、权限数据，并添加或更新acl中principal和aclentry的记录
	 * 对于space为空，则保存为全局的系统权限
	 * @param group 用户组数据
	 * @param space 空间数据  对于space为空，则保存为全局的系统权限
	 * @param permissionSet acl权限组掩码
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission=SparkPermissionEnum.ADMIN)
	public void saveGroup(@SecuredObj(clazz=Space.class,idPropertyName="owner.id") Group group,Space space,Integer permissionSet);
	/**
	 * 保存用户组和空间的映射关系，并添加或更新acl中principal和aclentry的记录
	 * 对于space为空，则保存为全局的系统权限
	 * @param group 用户组数据
	 * @param space 空间数据 对于space为空，则保存为全局的系统权限
	 * @param permissionSet acl权限组掩码
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission=SparkPermissionEnum.ADMIN)
	public void saveGroupPermission(Group group,@SecuredObj Space space,Integer permissionSet);
	/**
	 * 验证用户组命名是否重复
	 * 判断相同的空间内是否定义了相同名称的用户组
	 * @param group
	 * @return
	 */
	public boolean checkConflicts(Group group);
	
	/**
	 * 删除用户组和空间的映射关系和权限数据，并删除acl中principal和aclentry的记录
	 * @param group 用户组数据
	 * @param space 
	 * @param permissionSet acl权限组掩码
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission=SparkPermissionEnum.ADMIN)
	public void deleteGroupSpaceMapping(Group group,@SecuredObj Space space);
	
	/**
	 * 删除用户组
	 * 删除用户组与空间的关联关系
	 * 删除用户组与用户的关联关系
	 * 根据用户组与空间的关联关系，删除权限数据
	 * @param group
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission=SparkPermissionEnum.ADMIN)
	public void deleteGroup(@SecuredObj(clazz=Space.class,idPropertyName="owner.id") Group group);
	
	/**
	 * 按空间删除用户组
	 * 删除用户组
	 * 删除用户组与用户的关联关系
	 * 根据用户组与空间的关联关系，删除权限数据
	 * @param group
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission=SparkPermissionEnum.ADMIN)
	public void deleteGroups(@SecuredObj Space space);
	
	/**
	 * 根据空间id获取定义的所有用户组
	 * 可以根据owner，以及GroupSpace映射表中的数据取的Group的汇总
	 * @param space 传入null表示全局范围使用的用户组
	 * @return
	 */
	public List<Group> getGroups(Space space);
	
	/**
	 * 根据groupId获取用户组
	 * @param groupId
	 * @return
	 */
	public Group getGroup(Long groupId);
	
}
