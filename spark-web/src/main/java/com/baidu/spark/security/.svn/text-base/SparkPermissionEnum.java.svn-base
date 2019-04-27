package com.baidu.spark.security;

import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.model.Permission;

import com.baidu.spark.exception.SparkRuntimeException;
 /**
  * 空间权限枚举
  * @author zhangjing_pe
  *read:卡片：读取卡片信息；空间：读取所有卡片信息，空间访问权限；系统：所有空间访问权限
  *createchildren:卡片：创建下级卡片信息；空间：创建根卡片及下级卡片；系统：创建空间
  *write:卡片：编辑卡片信息；空间：编辑空间内的信息；系统：编辑所有空间的卡片信息
  *delete:卡片：删除卡片信息；空间：删除空间；系统：删除所有空间
  *admin:卡片：管理员卡片；空间：管理所有卡片及当前空间；系统：管理所有空间
  */
 public enum SparkPermissionEnum{
	 /**读取单一空间信息*/
	 READ(SparkPermission.READ,"permission.read"),
	 /**创建下级*/
	 CREATE_CHILDREN(SparkPermission.CREATE_CHILDREN,"permission.createchildren"),
	 /**修改某空间中自己的卡片信息*/
	 WRITE(SparkPermission.WRITE,"permission.write"),
	 /**修改某空间中所有卡片信息*/
	 DELETE(SparkPermission.DELETE,"permission.delete"),
	 /**创建空间中的卡片*/
	 ADMIN(SparkPermission.ADMIN,"permission.admin");
	/**单一权限 */
	private Permission permission;
	/**对应生效的权限集*/
	private Permission permissionSet = new CumulativePermission();
	
	private String messageCode;

	SparkPermissionEnum(Permission pp,String messageCode) {
		permission = pp;
		for(Permission ppIt:SparkPermission.getPermissions()){
			if(ppIt.getMask()<=pp.getMask()){
				((CumulativePermission)permissionSet).set(ppIt);
			}
		}
		this.messageCode = messageCode; 
	}

	public Permission getPermission() {
		return permission;
	}
	
	public Permission getPermissionSet(){
		return permissionSet;
	}

	public String getMessageCode() {
		return messageCode;
	}
	/**
	 * 获取指定权限掩码对应的permission对象
	 * @param mask
	 * @return
	 */
	public static Permission getSparkPermission(int mask){
		for(SparkPermissionEnum cp : SparkPermissionEnum.values()){
			if(cp.getPermission().getMask() == mask){
				return cp.getPermission();
			}
		}
		throw new SparkRuntimeException("Error space permission mask:"+mask);
	}
}
