package com.baidu.spark.security.integrate;

import java.io.Serializable;

import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.ObjectIdentity;
/**
 * 无效的aclcache实现。不对数据进行cache
 * @author zhangjing_pe
 *
 */
public class DummyAclCache implements AclCache {

	@Override
	public void clearCache() {
	}

	@Override
	public void evictFromCache(Serializable pk) {
	}

	@Override
	public void evictFromCache(ObjectIdentity objectIdentity) {
	}

	@Override
	public MutableAcl getFromCache(ObjectIdentity objectIdentity) {
		return null;
	}

	@Override
	public MutableAcl getFromCache(Serializable pk) {
		return null;
	}

	@Override
	public void putInCache(MutableAcl acl) {
	}

}
