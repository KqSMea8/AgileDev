package com.baidu.spark.security.principalsid;

import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.util.Assert;

/**
 * 空间用户组的principalSid
 * @author zhangjing_pe
 * 
 */
public class GroupPrincipalSid extends PrincipalSid {
	
	private static final long serialVersionUID = -6895312573115199625L;

	public static final String GROUP_PRINCIPAL_PREFIX = "group_";

	public GroupPrincipalSid(Long principal) {
		super( GROUP_PRINCIPAL_PREFIX + principal);
        Assert.notNull(principal, "Principal required");
    }
	
	 //~ Methods ========================================================================================================

    public boolean equals(Object object) {
        if ((object == null) || !(object instanceof GroupPrincipalSid||object instanceof PrincipalSid)) {
            return false;
        }

        // Delegate to getPrincipal() to perform actual comparison (both should be identical)
        return ((PrincipalSid) object).getPrincipal().equals(this.getPrincipal());
    }

    public int hashCode() {
        return this.getPrincipal().hashCode();
    }

    public String toString() {
        return "GroupPrincipalSid[" + getPrincipal() + "]";
    }
}
