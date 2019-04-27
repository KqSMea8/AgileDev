package com.baidu.spark.security.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.baidu.spark.model.Group;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.security.PermissionService;
import com.baidu.spark.security.SparkAclHandlerService;
import com.baidu.spark.security.SparkPermissionEnum;
import com.baidu.spark.security.SparkSystemResource;
import com.baidu.spark.security.principalsid.GroupPrincipalSid;
import com.baidu.spark.security.voter.AccessPermissionBean;
import com.baidu.spark.security.voter.UserAccessResolver;

@Service
public class PermissionServiceImpl implements PermissionService {
	
	private UserAccessResolver resolver = null;
	
	private SparkAclHandlerService handler = null;

	@Override
	public void createAcl(Space space){
		Assert.notNull(space);
		Assert.notNull(space.getId());
		handler.createAcl(Space.class, space.getId());
	}
	
	@Override
	public void createAcl(Card card){
		Assert.notNull(card);
		Assert.notNull(card.getId());
		handler.createAcl(Card.class, card.getId());
	}
	
	@Override
	public void deletePermission(Card card){
		Assert.notNull(card);
		Assert.notNull(card.getId());
		handler.deletePermission(Card.class, card.getId());
	}
	@Override
	public void deletePermission(Space space){
		Assert.notNull(space);
		Assert.notNull(space.getId());
		handler.deletePermission(Space.class, space.getId());
	}
	@Override
	public List<Permission> getUserPermission(Long userId,Space space){
		Assert.notNull(userId);
		Assert.notNull(space);
		Assert.notNull(space.getId());
		return getUserResourcePermission(userId, space);
	}

	@Override
	public List<Permission> getUserPermission(Long userId,Card card){
		Assert.notNull(userId);
		Assert.notNull(card);
		Assert.notNull(card.getId());
		return getUserResourcePermission(userId, card);
	}
	/**获取指定用户对于指定资源的权限列表
	 * @param userId
	 * @param resource
	 * @return
	 */
	private List<Permission> getUserResourcePermission(Long userId, Object resource) {
		Assert.notNull(userId);
		Assert.notNull(resource);
		List<Permission> retList = new ArrayList<Permission>();
		for(SparkPermissionEnum pp :SparkPermissionEnum.values()){
			List<Permission> testP = new ArrayList<Permission>();
			testP.add(pp.getPermission());
			AccessPermissionBean bean = new AccessPermissionBean(resource,testP);
			if(resolver.isGranted(userId, bean)){
				retList.add(pp.getPermission());
			}
		}
		return retList;
	}
	@Override
	public void deletePermission(Group group,Space space){
		assertSpaceAndGroup(group, space);
		PrincipalSid principalSid = new GroupPrincipalSid(group.getId());
		handler.deletePermission(principalSid, Space.class, space.getId());
	}
	@Override
	public void deleteSystemPermission(Group group){
		assertGroup(group);
		PrincipalSid principalSid = new GroupPrincipalSid(group.getId());
		handler.deletePermission(principalSid, SparkSystemResource.class, SparkSystemResource.getResource().getId());
	}
	@Override
	public void updatePermission(Group group,Space space,List<Integer> permissionMaskList){
		assertSpaceAndGroup(group, space);
		PrincipalSid principalSid = new GroupPrincipalSid(group.getId());
		handler.updatePermission(principalSid, Space.class, space.getId(), permissionMaskList);	
	}
	@Override
	public void updatePermission(Group group,Space space,Integer permissionMask){
		assertSpaceAndGroup(group, space);
		PrincipalSid principalSid = new GroupPrincipalSid(group.getId());
		List<Integer> maskList = new ArrayList<Integer>();
		maskList.add(permissionMask);
		handler.updatePermission(principalSid, Space.class, space.getId(), maskList);	
	}
	@Override
	public void updateSystemPermission(Group group,List<Integer> permissionMaskList){
		assertGroup(group);
		PrincipalSid principalSid = new GroupPrincipalSid(group.getId());
		handler.updatePermission(principalSid, SparkSystemResource.class, SparkSystemResource.getResource().getId(), permissionMaskList);
	}

	
	@Override
	public Permission getMergedPermission(Group group, Space space) {
		assertSpaceAndGroup(group, space);
		PrincipalSid principalSid = new GroupPrincipalSid(group.getId());
		return handler.loadMergedPermission(principalSid, Space.class, space.getId());
	}
	

	@Override
	public Permission getSystemMergedPermission(Group group) {
		assertGroup(group);
		PrincipalSid principalSid = new GroupPrincipalSid(group.getId());
		return handler.loadMergedPermission(principalSid, SparkSystemResource.class,SparkSystemResource.getResource().getId());
	}

	/**
	 * @param group
	 * @param space
	 */
	private void assertSpaceAndGroup(Group group, Space space) {
		assertGroup(group);
		Assert.notNull(space);
		Assert.notNull(space.getId());
	}
	
	/**
	 * @param group
	 */
	private void assertGroup(Group group) {
		Assert.notNull(group);
		Assert.notNull(group.getId());
	}

	@Autowired
	public void setResolver(UserAccessResolver resolver) {
		this.resolver = resolver;
	}

	@Autowired
	public void setHandler(SparkAclHandlerService handler) {
		this.handler = handler;
	}

}
