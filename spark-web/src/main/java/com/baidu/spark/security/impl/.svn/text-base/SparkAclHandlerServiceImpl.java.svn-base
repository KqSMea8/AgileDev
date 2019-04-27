package com.baidu.spark.security.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AlreadyExistsException;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.baidu.spark.security.SparkAclHandlerService;
import com.baidu.spark.security.SparkMutableAclService;
import com.baidu.spark.security.SparkPermission;
import com.baidu.spark.security.SparkPermissionEnum;

/**
 * 权限服务接口
 * 
 * @author zhangjing_pe
 */
@Service
public class SparkAclHandlerServiceImpl implements SparkAclHandlerService {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private SparkMutableAclService aclService;
	

	@Override
	public List<Permission> loadSplitPermission(Sid sid, Class<?> clz,
			Serializable clzId) {
		Assert.notNull(sid);
		Assert.notNull(clz);
		Assert.notNull(clzId);
		ObjectIdentity objectIdentity = getObjectIdentity(clz, clzId);
		List<Sid> sidList = new ArrayList<Sid>();
		sidList.add(sid);
		Acl acl = null;
		try {
			acl = aclService.readAclById(objectIdentity);
		} catch (NotFoundException e) {
			return new ArrayList<Permission>();
		}
		AccessControlEntry ace = getAceFromAcl(acl, sid);
		if (ace != null) {
			return new ArrayList<Permission>(splitPermission(ace
					.getPermission()));
		}
		return new ArrayList<Permission>();
	}

	@Override
	public Permission loadMergedPermission(Sid sid, Class<?> clz,
			Serializable clzId) {
		Assert.notNull(sid);
		Assert.notNull(clz);
		Assert.notNull(clzId);
		ObjectIdentity objectIdentity = getObjectIdentity(clz, clzId);
		List<Sid> sidList = new ArrayList<Sid>();
		sidList.add(sid);
		Acl acl = null;
		try {
			acl = aclService.readAclById(objectIdentity);
		} catch (NotFoundException e) {
			return SparkPermission.NONE;
		}
		AccessControlEntry ace = getAceFromAcl(acl, sid);
		if (ace != null) {
			return ace.getPermission();
		}
		return SparkPermission.NONE;
	}

	@Override
	public void updatePermission(Sid sid, Class<?> clz, Serializable clzId,
			Permission[] permissionList) {
		Assert.notNull(sid);
		Assert.notNull(clz);
		Assert.notNull(clzId);
		Permission mergedPermission = mergePermissions(permissionList);
		updatePermission(sid, clz, clzId, mergedPermission);
	}

	@Override
	public void updatePermission(Sid sid, Class<?> clz, Serializable clzId,
			List<Integer> maskList) {
		Assert.notNull(sid);
		Assert.notNull(clz);
		Assert.notNull(clzId);
		Permission mergedPermission = mergePermissions(maskList);
		updatePermission(sid, clz, clzId, mergedPermission);
	}

	@Override
	public void updatePermission(Sid sid, Class<?> clz, Serializable clzId,
			Permission mergedPermission) {
		Assert.notNull(sid);
		Assert.notNull(clz);
		Assert.notNull(clzId);
		Assert.notNull(mergedPermission);
		ObjectIdentity objectIdentity = getObjectIdentity(clz, clzId);
		MutableAcl acl = aclService.readOrCreateAclById(objectIdentity);
		AccessControlEntry ace = getAceFromAcl(acl, sid);
		if (ace == null) {
			acl.insertAce(acl.getEntries().size(), mergedPermission, sid, true);
		} else {
			updateAclObjPermission(acl, sid, mergedPermission);
		}
		aclService.updateAcl(acl);
	}

	@Override
	public void deletePermission(Sid sid, Class<?> clz, Serializable clzId) {
		Assert.notNull(sid);
		Assert.notNull(clz);
		Assert.notNull(clzId);
		ObjectIdentity oid = getObjectIdentity(clz, clzId);
		MutableAcl acl = aclService.readOrCreateAclById(oid);
		deleteAclObjPermission(acl, sid);
		aclService.updateAcl(acl);
	}

	@Override
	public void deletePermission(Class<?> clz, Serializable clzId) {
		Assert.notNull(clz);
		Assert.notNull(clzId);
		ObjectIdentity oid = getObjectIdentity(clz, clzId);
		aclService.deleteAcl(oid, false);
	}
	
	@Override
	public void createAcl(Class<?> clz, Serializable clzId) {
		try{
			aclService.createAcl(getObjectIdentity(clz,clzId));
		}catch(AlreadyExistsException e){
			logger.info("acl resource exists:{}",clz.toString());
		}
	}
	
	/**
	 * 根据资源类型和资源id获取objectIdentity
	 * 
	 * @param clz
	 * @param clzId
	 * @return
	 */
	private ObjectIdentity getObjectIdentity(Class<?> clz, Serializable clzId) {
		ObjectIdentity objectIdentity = new ObjectIdentityImpl(clz, clzId);
		return objectIdentity;
	}

	/**
	 * 根据acl，以及sid的数据返回ace对象
	 * 
	 * @param acl
	 * @param sid
	 * @return
	 */
	private AccessControlEntry getAceFromAcl(Acl acl, Sid sid) {
		List<AccessControlEntry> aceList = acl.getEntries();
		for (AccessControlEntry ace : aceList) {
			if (ace.getSid().equals(sid)) {
				return ace;
			}
		}
		return null;
	}

	/**
	 * 将权限数据划分成单个权限的集合
	 * 
	 * @param permission
	 * @return
	 */
	private Set<Permission> splitPermission(Permission permission) {
		Set<Permission> permissionList = new LinkedHashSet<Permission>();
		if (permission instanceof CumulativePermission) {
			for (SparkPermissionEnum it : SparkPermissionEnum.values()) {
				if ((permission.getMask() & it.getPermission().getMask()) == it
						.getPermission().getMask()) {
					permissionList.add(it.getPermission());
				}
			}
		} else {
			permissionList.add(permission);
		}
		return permissionList;
	}

	/**
	 * 将权限数据划分成单个权限的集合
	 * 
	 * @param permission
	 * @return
	 */
	private Set<Permission> splitPermission(int mask) {
		Set<Permission> permissionList = new LinkedHashSet<Permission>();
		for (SparkPermissionEnum it : SparkPermissionEnum.values()) {
			if ((mask & it.getPermission().getMask()) == it.getPermission()
					.getMask()) {
				permissionList.add(it.getPermission());
			}
		}
		return permissionList;
	}

	/**
	 * 将权限集合组合成单条权限
	 * 
	 * @param permissions
	 * @return
	 */
	private Permission mergePermissions(Permission[] permissions) {
		if(permissions == null){
			return SparkPermission.NONE;
		}
		CumulativePermission permission = new CumulativePermission();
		if (permissions == null || permissions.length == 0) {
			return permission;
		}
		for (Permission pp : permissions) {
			permission.set(pp);
		}
		return permission;
	}

	/**
	 * 将权限掩码组合成单条权限对象
	 * 
	 * @param masks
	 * @return
	 */
	private Permission mergePermissions(List<Integer> masks) {
		if(masks == null){
			return SparkPermission.NONE;
		}
		CumulativePermission permission = new CumulativePermission();
		if (masks == null || masks.size() == 0) {
			return permission;
		}
		for (int mask : masks) {
			Set<Permission> permissions = splitPermission(mask);
			for (Permission per : permissions) {
				permission.set(per);
			}
		}
		return permission;
	}

	/**
	 * 根据掩码获得权限对象
	 * 
	 * @param mask
	 * @return
	 */
	/*private Permission getSparkPermission(int mask) {
		for (SparkPermissionEnum it : SparkPermissionEnum.values()) {
			if (it.getPermission().getMask() == mask) {
				return it.getPermission();
			}
		}
		return SparkPermission.NONE;
	}*/

	/**
	 * 更新指定sid的权限数据，更新acl对象 <B>并不进行实际的保存操作
	 * 
	 * @param acl
	 *            acl对象
	 * @param sid
	 *            sid对象
	 * @param permission
	 *            权限数据
	 * @return
	 */
	private Acl updateAclObjPermission(MutableAcl acl, Sid sid,
			Permission permission) {
		for (int i = 0; i < acl.getEntries().size(); i++) {
			AccessControlEntry ace = acl.getEntries().get(i);
			if (!(ace.getSid() instanceof PrincipalSid)) {
				continue;
			}
			PrincipalSid acesid = (PrincipalSid) ace.getSid();
			if (acesid.equals(sid)) {
				acl.updateAce(i, permission);
				return acl;
			}
		}
		return acl;
	}

	/**
	 * 删除指定sid的指定的权限数据，更新acl对象 <B>并不进行实际的保存操作
	 * 
	 * @param acl
	 * @param sid
	 * @return
	 */
	private Acl deleteAclObjPermission(MutableAcl acl, Sid sid) {
		for (int i = 0; i < acl.getEntries().size(); i++) {
			AccessControlEntry ace = acl.getEntries().get(i);
			if (!(ace.getSid() instanceof PrincipalSid)) {
				continue;
			}
			PrincipalSid acesid = (PrincipalSid) ace.getSid();
			if (acesid.equals(sid)) {
				acl.deleteAce(i);
				return acl;
			}
		}
		return acl;

	}

	@Autowired
	public void setAclService(SparkMutableAclService aclService) {
		this.aclService = aclService;
	}

}
