package com.baidu.spark.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.baidu.spark.dao.GroupDao;
import com.baidu.spark.dao.GroupSpaceDao;
import com.baidu.spark.model.Group;
import com.baidu.spark.model.Space;
import com.baidu.spark.security.PermissionService;
import com.baidu.spark.service.GroupService;

/**
 * Group Service.
 * 
 * @author zhangjing_pe
 *
 */
@Service
public class GroupServiceImpl implements GroupService {
	
	private GroupDao groupDao;
	
	private GroupSpaceDao groupSpaceDao;

	private PermissionService permissionService;
	
	@Override
	public void saveGroup(Group group){
		Assert.notNull(group);
		Assert.notNull(group.getName());
		if(group.getLocked() == null){
			group.setLocked(false);
		}
		groupDao.save(group);
		//增加冗余操作，确保GroupSpace中有记录
		groupSpaceDao.merge(group, group.getOwner());
		
	}
	
	@Override
	public void saveGroup(Group group,Space space,Integer permissionSet) {
		Assert.notNull(group);
		Assert.isTrue(group.getOwner() == null && space == null
				|| group.getOwner() != null && space != null
				&& group.getOwner().getId().equals(space.getId()));
		saveGroup(group);
		if(permissionSet == null){
			permissionSet = 0;
		}
		//修改acl的信息
		saveGroupPermission(group, space, permissionSet);
	}
	
	@Override
	public void saveGroupPermission(Group group,Space space,Integer permissionSet){
		//修改acl的信息
		groupSpaceDao.merge(group, space);	
		List<Integer> perList = new ArrayList<Integer>();
		perList.add(permissionSet);
		
		if(!isGlobal(space)){
			permissionService.updatePermission(group,space, perList);	
		}else{
			permissionService.updateSystemPermission(group,perList);	
		}
	}
	
	@Override
	public void deleteGroupSpaceMapping(Group group, Space space) {
		Assert.notNull(group);
		groupSpaceDao.delete(group, space);
		if(space != null && space.getId() >0 ){
			permissionService.deletePermission(group,space);	
		}else{
			permissionService.deleteSystemPermission(group);	
		}
		
	}
	
	@Override
	public boolean checkConflicts(Group group){
		Assert.notNull(group);
		Assert.hasText(group.getName());
		Criteria criteria = groupDao.createCriteria();
		if(group.getOwner() == null){
			criteria.add(Restrictions.isNull("owner"));
		}else{
			criteria.createAlias("owner","owner").add(Restrictions.eq("owner.id", group.getOwner().getId()));
		}
		
		criteria.add(Restrictions.eq("name", group.getName()));
		if( group.getId() != null ){
			criteria.add(Restrictions.ne("id", group.getId()));
		}
		return criteria.list().size() != 0;
	}
	
	@Override
	public List<Group> getGroups(Space space){
		List<Group> groups = getGroupsByOwner(space);
		
		//根据group_space来获取group
		Set<Group> groupSet = new LinkedHashSet<Group>();
		groupSet.addAll(groups);
		groupSet.addAll(groupSpaceDao.getGroups(space));
		return new ArrayList<Group>(groupSet);
	}
	
	@Override
	public Group getGroup(Long groupId){
		Assert.notNull(groupId);
		return groupDao.get(groupId);
	}
	
	/**
	 * 根据owner获取空间的用户组。
	 * @param space 指定的空间。如果空间为空，则返回全局用户组
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Group> getGroupsByOwner(Space space){
		Criteria criteria = groupDao.createCriteria();
		if(isGlobal(space)){
			criteria.add(Restrictions.isNull("owner"));
		}else{
			criteria.add(Restrictions.eq("owner", space));
		}
		return criteria.list();
	}

	@Override
	public void deleteGroup(Group group){
		Assert.notNull(group);
		//删除acl信息
		deletePermisisonBeforeCleanGroupSpace(group);
		groupSpaceDao.deleteAll(group);
		groupDao.delete(group);
	}
	
	@Override
	public void deleteGroups(Space space){
		Assert.notNull(space);
		List<Group> groups = getGroupsByOwner(space);
		for(Group group:groups){
			deleteGroup(group);
		}
	}

	/**删除权限数据。读取GroupSpace中的记录，删除相关的acl信息。所以，请在删除GroupSpace的数据前调用
	 * acl不提供根据sid获取或删除ace的方法。修改acl的所有与group相关的权限信息
	 * @param group
	 */
	private void deletePermisisonBeforeCleanGroupSpace(Group group) {
		List<Space> spaces = groupSpaceDao.getSpaces(group);
		if(spaces!=null&&!spaces.isEmpty()){
			for(Space space:spaces){
				if(isGlobal(space)){
					permissionService.deleteSystemPermission(group);
				}else{
					permissionService.deletePermission(group, space);
				}
			}
		}
		//删除在系统级别上的空间定义
		permissionService.deleteSystemPermission(group);
	}
	
	private boolean isGlobal(Space space){
		return space == null;
	}

	@Autowired
	public void setGroupDao(GroupDao groupDao) {
		this.groupDao = groupDao;
	}

	@Autowired
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
	
	@Autowired
	public void setGroupSpaceDao(GroupSpaceDao groupSpaceDao) {
		this.groupSpaceDao = groupSpaceDao;
	}

	
}
