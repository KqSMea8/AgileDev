package com.baidu.spark.security;

import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.core.GrantedAuthority;

/**
 * <p>验证能否执行指定的acl权限变化操作。对于资源的owner，以及配置的grantedAuthority，准许操作。</p>
 * <p>spark的aclAuthorizationStategy实现。与AclAuthorizationStrategyImpl实现基本相同。
 * 不同的是，使用UserPrincipalSid替换掉PrincipalSid的实现，对管理权限使用{@link SparkPermission#ADMIN}</p>
 * TODO 对于编辑权限来说，未来可能并不符合这个规则，或者允许空间管理员组的权限进行操作，无法使用GrantedAuthority。到时要屏蔽此类的方法
 * @author zhangjing_pe
 *
 */
public class SparkAclAuthorizationStrategyImpl implements
		AclAuthorizationStrategy {

	
    /**
     * Constructor. The only mandatory parameter relates to the system-wide {@link GrantedAuthority} instances that
     * can be held to always permit ACL changes.
     *
     * @param auths an array of <code>GrantedAuthority</code>s that have
     * special permissions (index 0 is the authority needed to change
     * ownership, index 1 is the authority needed to modify auditing details,
     * index 2 is the authority needed to change other ACL and ACE details) (required)
     */
    public SparkAclAuthorizationStrategyImpl(GrantedAuthority[] auths) {
     
    }

    //~ Methods ========================================================================================================

    public void securityCheck(Acl acl, int changeType) {
    }

}
