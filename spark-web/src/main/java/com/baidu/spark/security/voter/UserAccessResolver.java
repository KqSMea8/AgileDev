package com.baidu.spark.security.voter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.UnloadedSidException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.baidu.spark.dao.CardDao;
import com.baidu.spark.exception.SparkRuntimeException;
import com.baidu.spark.model.Group;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.security.SparkMutableAclService;
import com.baidu.spark.security.SparkSystemResource;
import com.baidu.spark.security.principalsid.GroupPrincipalSid;
import com.baidu.spark.security.principalsid.UserPrincipalSid;
import com.baidu.spark.service.UserService;
import com.baidu.spark.util.SparkConfig;
/**
 * 对于卡片、空间和系统级的资源，对用户进行指定类型的权限判断
 * @author zhangjing_pe
 *
 */
@Service
public class UserAccessResolver {

	private static final Log logger = LogFactory
			.getLog(UserAccessResolver.class);

	private UserService userService;

	private CardDao cardDao;

	private SparkMutableAclService aclService;

	private static final int GRANTED = 1;
	private static final int ABSTAIN = 0;
	private static final int DENIED = -1;
	
	private Group everyOneGroup = null;

	/**
	 * 判断用户是否具有指定操作对象及权限
	 * @param userId
	 * @param accessBean
	 * @return
	 */
	public boolean isGranted(Long userId, AccessPermissionBean accessBean) {
		if(everyOneGroup == null){
			everyOneGroup = SparkConfig.getEveryOneGroup();
		}
		Assert.notNull(userId);
		Assert.notNull(accessBean);
		User user = userService.getUserById(userId);
		if( user == null){
			throw new SparkRuntimeException("can not get user!userId:"+userId);
		}
		List<Sid> sids = new ArrayList<Sid>();
		sids.add(new UserPrincipalSid(user.getId()));
		if (user.getAuthorities() != null) {
			for (GrantedAuthority auth : user.getAuthorities()) {
				sids.add(new GrantedAuthoritySid(auth));
			}
		}
		// 默认的全局管理员
		if (user.getUsername() != null
				&& SparkConfig.isDefaultAdminAccount(user.getUsername())) {
			return true;
		}
		//全局用户组
		sids.add(new GroupPrincipalSid(everyOneGroup.getId()));
		// 获取用户所在用户组，生成对应的GroupPrincipalSid
		if (user.getGroups() != null) {
			for (Group group : user.getGroups()) {
				if (!group.getLocked()&&group.getId() != null) {
					sids.add(new GroupPrincipalSid(group.getId()));
				}

			}
		}

		return isGranted(sids, accessBean);
	}

	/**
	 * 判断sids是否有操作权限
	 * <p>
	 * 按照卡片->上级卡片->空间->系统的顺序，进行权限的判断
	 * </p>
	 * 
	 * @param sids
	 * @param voteData
	 * @return
	 */
	private boolean isGranted(List<Sid> sids, AccessPermissionBean voteData) {
		Assert.notEmpty(sids);
		Assert.notNull(voteData);
		List<Permission> permissions = voteData.getRequiredSparkPermission();
		Object currentObject = null;

		boolean testOwner = true;
		if (voteData.getSecuredObjId() == null) {// 若传入对象为空，则使用全局的权限进行判断
			currentObject = SparkSystemResource.getResource();
		} else {
			// TODO 当前不会在卡片上设置权限。考虑直接使用空间的权限设置进行判断
			if (voteData.getSecuredObjClass().equals(Card.class)) {
				try {
					currentObject = cardDao.get(Long.parseLong(voteData
							.getSecuredObjId()));
				} catch (Exception e) {
					throw new SparkRuntimeException(
							"SparkAclVoter error! Card Id is not a number:"
									+ voteData.getSecuredObjId(), e);
				}
			} else if (voteData.getSecuredObjClass().equals(Space.class)) {
				try {
					Space space = new Space();
					space.setId(Long.parseLong(voteData.getSecuredObjId()));
					currentObject = space;
				} catch (Exception e) {
					throw new SparkRuntimeException(
							"SparkAclVoter error! Card Id is not a number:"
									+ voteData.getSecuredObjId(), e);
				}
			} else {
				currentObject = SparkSystemResource.getResource();
			}
		}
		while (currentObject != null) {
			if(currentObject.equals(SparkSystemResource.getResource())){
				testOwner = false;//system資源不進行owner的判斷
			}else{
				testOwner = true;
			}
			int granted = isPermissionGranted(sids, permissions,
					getObjectIdentity(currentObject),testOwner);
			if (granted == GRANTED) {
				return true;
			} else if (granted == DENIED) {
				return false;
			} else {
				currentObject = getParentObject(currentObject);
				//owner一直进行判断
//				testOwner = false;//計算過上級后，不進行owner的判斷
			}
		}
		return false;
	}

	/**
	 * 根据类型获取指定对象的上级对象 若传入对象为卡片，且parent不为空，则返回上级卡片；否则，返回所属空间
	 * 若传入对象为空间，则返回SparkSystemResource 否则，返回空
	 * 
	 * @see {@link SparkSystemResource}
	 * @param object
	 * @return
	 */
	private Object getParentObject(Object object) {
		if (object instanceof Card) {
			Card card = (Card) object;
			if (card.getParent() != null) {
				return card.getParent();
			} else {
				return card.getSpace();
			}
		} else if (object instanceof Space) {
			return SparkSystemResource.getResource();
		} else if (object instanceof SparkSystemResource) {
			return null;
		}
		return null;
	}

	/**
	 * 获取对象对应的objectIdentity ObjectIdentityImpl 构造函数默认使用对象的getId方法进行id的获取
	 * 
	 * @param object
	 *            资源的实体bean
	 * @return
	 * @see {@link org.springframework.security.acls.domain.ObjectIdentityImpl}
	 */
	private ObjectIdentity getObjectIdentity(Object object) {
		return new ObjectIdentityImpl(object);
	}

	/**
	 * 判断对于指定的主体，指定的资源上对应的权限是否可获取
	 * 
	 * @param sids
	 *            权限主体列表
	 * @param requiredPermission
	 *            所需要的权限列表
	 * @param objectIdentity
	 *            资源列表
	 * @param ownerGotFullPermission
	 * 			  	是否让owner具有所有权限
	 * @return 若指定资源上的权限满足条件，则返回“授权”，若定义的权限为拒绝，则返回“拒绝”；其他情况，返回“弃权”，由其他逻辑继续判断
	 */
	private int isPermissionGranted(List<Sid> sids,
			List<Permission> requiredPermission, ObjectIdentity objectIdentity,boolean ownerGotFullPermission) {
		Acl acl = null;
		try {
			// Lookup only ACLs for SIDs we're interested in
			acl = aclService.readAclById(objectIdentity, sids);
		} catch (NotFoundException nfe) {
			if (logger.isDebugEnabled()) {
				logger
						.debug("Voting to deny access - no ACLs apply for this principal");
			}
			return ABSTAIN;
		}
		//owner应该具有所有权限
		if(ownerGotFullPermission&&acl.getOwner()!=null&&sids.contains(acl.getOwner())){
			return GRANTED;
		}
		try {
			int result = isAclGranted(acl, requiredPermission, sids) ;
			if ( result == GRANTED) {
				if (logger.isDebugEnabled()) {
					logger.debug("Voting to grant access");
				}

				return GRANTED;
			} else if(result == DENIED){
				if (logger.isDebugEnabled()) {
					logger
							.debug("Voting to deny access - ACLs returned, but insufficient permissions for this principal");
				}

				return DENIED;
			}
		} catch (NotFoundException nfe) {
			if (logger.isDebugEnabled()) {
				logger
						.debug("Voting to deny access - no ACLs apply for this principal");
			}

		}
		return ABSTAIN;
	}

	/**
	 * acl中是否包含指定sid的指定permission。
	 * 不使用AclImpl.isGranted方法。在方法中，只有permission与acl中的mask完全相同时才视为相同
	 * 
	 * @param acl
	 * @param permission
	 * @param sids
	 * @return
	 */
	private int isAclGranted(Acl acl, List<Permission> permission,
			List<Sid> sids) {
		Assert.notEmpty(permission, "Permissions required");
		Assert.notEmpty(sids, "SIDs required");
		
		if (!acl.isSidLoaded(sids)) {
			throw new UnloadedSidException(
					"ACL was not loaded for one or more SID");
		}

		for (Permission p : permission) {
			for (Sid sid : sids) {
				// Attempt to find exact match for this permission mask and SID
				boolean scanNextSid = true;

				for (AccessControlEntry ace : acl.getEntries()) {
					if (((ace.getPermission().getMask() & p.getMask()) == p
							.getMask())
							&& ace.getSid().equals(sid)) {
						// Found a matching ACE, so its authorization decision
						// will prevail
						if (ace.isGranting()) {
							// Success
							return GRANTED;
						}else{
							return DENIED;
						}
					}
				}

				if (!scanNextSid) {
					break; // exit SID for loop (now try next permission)
				}
			}
		}
		return ABSTAIN;

	}
	@Autowired
	public void setAclService(SparkMutableAclService aclService) {
		this.aclService = aclService;
	}
    @Autowired
	public void setCardDao(CardDao cardDao) {
		this.cardDao = cardDao;
	}
    @Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
