package com.baidu.spark.security;

import java.util.List;

import org.springframework.security.acls.model.AlreadyExistsException;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
/**
 * spark的AclService实现
 * <p>
 * createAcl时，会使用SecurityContext中的{@link org.springframework.security.core.Authentication}和{@link org.springframework.security.acls.domain.PrincipalSid}构造函数进行acl的Owner设置。
 * </p>
 * <p>
 * 该构造函数会使用UserDetails中的userName作为Sid的值。而我们系统中并不使用用户名，而是使用“前缀+id”的方式保存sid。会导致增加一条多余的sid数据
 * </p>
 * 不要调用带有List<Sid>的readAcl(s)...相关方法，他们并不会根据sid进行过滤。如果将默认实现进行更改来实现根据sid过了ace，调用updateAcl由于参数是acl，有可能会删除掉一些ace数据
 * @see {@link org.springframework.security.acls.jdbc.BasicLookupStrategy#readAclsById(List, List)}
 * @author zhangjing_pe
 *
 */
public interface SparkMutableAclService extends MutableAclService {
	
	/**
	 * 根据ObjectIdentity读取acl信息。如果不存在对应的ObjectIdentity,则创建一个
	 * @param object ObjectIdentity对象，资源对象
	 * @return acl对象
	 */
	public MutableAcl readOrCreateAclById(ObjectIdentity object);
	
	/**
	 * 根据ObjectIdentity读取acl信息。如果不存在对应的ObjectIdentity,则创建一个
	 * @param object ObjectIdentity对象，资源对象
	 * @param sids 主体对象列表。在acl3.0.3版本中，此对象被忽略
	 * @return
	 * @see {@link org.springframework.security.acls.jdbc.BasicLookupStrategy#readAclsById(List, List)}
	 */
	public MutableAcl readOrCreateAclById(ObjectIdentity object,List<Sid> sids);
	
	
	/**
	 * createAcl方法
	 * @param objectIdentity objectIdentity对象
	 * @param owner owner
	 */
	public MutableAcl createAcl(ObjectIdentity objectIdentity,Sid owner) throws AlreadyExistsException ;
}
