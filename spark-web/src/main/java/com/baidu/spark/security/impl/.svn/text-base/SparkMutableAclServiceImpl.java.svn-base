package com.baidu.spark.security.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AlreadyExistsException;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import com.baidu.spark.security.SparkMutableAclService;
import com.baidu.spark.security.principalsid.UserPrincipalSid;
/**
 * 
 * @author zhangjing_pe
 *
 */
public class SparkMutableAclServiceImpl extends JdbcMutableAclService implements SparkMutableAclService{
	public SparkMutableAclServiceImpl(DataSource dataSource, LookupStrategy lookupStrategy, AclCache aclCache) {
        super(dataSource, lookupStrategy, aclCache);
    }
	/**
	 * createAcl方法
	 * 使用UserPrincipalSid，而不是默认的PrincipalSid构造函数，避免Sid表中的无用记录
	 * @param objectIdentity objectIdentity对象
	 */
	@Override
	public MutableAcl createAcl(ObjectIdentity objectIdentity) throws AlreadyExistsException {
        Assert.notNull(objectIdentity, "Object Identity required");

        // Check this object identity hasn't already been persisted
        if (retrieveObjectIdentityPrimaryKey(objectIdentity) != null) {
            throw new AlreadyExistsException("Object identity '" + objectIdentity + "' already exists");
        }

        // Need to retrieve the current principal, in order to know who "owns" this ACL (can be changed later on)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalSid sid = new UserPrincipalSid(auth);

        // Create the acl_object_identity row
        createObjectIdentity(objectIdentity, sid);

        // Retrieve the ACL via superclass (ensures cache registration, proper retrieval etc)
        Acl acl = readAclById(objectIdentity);
        Assert.isInstanceOf(MutableAcl.class, acl, "MutableAcl should be been returned");

        return (MutableAcl) acl;
    }
	
	/**
	 * createAcl方法
	 * @param objectIdentity objectIdentity对象
	 * @param owner owner
	 */
	@Override
	public MutableAcl createAcl(ObjectIdentity objectIdentity,Sid owner) throws AlreadyExistsException {
        Assert.notNull(objectIdentity, "Object Identity required");

        // Check this object identity hasn't already been persisted
        if (retrieveObjectIdentityPrimaryKey(objectIdentity) != null) {
            throw new AlreadyExistsException("Object identity '" + objectIdentity + "' already exists");
        }

        // Create the acl_object_identity row
        createObjectIdentity(objectIdentity, owner);

        // Retrieve the ACL via superclass (ensures cache registration, proper retrieval etc)
        Acl acl = readAclById(objectIdentity);
        Assert.isInstanceOf(MutableAcl.class, acl, "MutableAcl should be been returned");

        return (MutableAcl) acl;
    }
	@Override
	public MutableAcl readOrCreateAclById(ObjectIdentity object) {
		return (MutableAcl)readOrCreateAclById(object,null);
    }
	
	@Override
	public MutableAcl readOrCreateAclById(ObjectIdentity object,List<Sid> sids){
		MutableAcl acl = null;
		try {
            acl = (MutableAcl) readAclById(object);
        } catch (NotFoundException nfe) {
            acl = createAcl(object);
        }
        return acl;
	}
}
