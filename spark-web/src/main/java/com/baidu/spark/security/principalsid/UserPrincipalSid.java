package com.baidu.spark.security.principalsid;

import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import com.baidu.spark.model.User;

/**
 * @author zhangjing_pe
 * 用户类型的principalSid
 */
public class UserPrincipalSid  extends PrincipalSid{
	
	private static final long serialVersionUID = -6895312573115199625L;

	private static final String USER_PRINCIPAL_PREFIX = "user_";

	public UserPrincipalSid(Long principal) {
        super( USER_PRINCIPAL_PREFIX + principal);
        Assert.notNull(principal, "Principal required");
    }
	
	public UserPrincipalSid(Authentication authentication) {
        super(getPrincipleFromAuthenitcation(authentication));
        Assert.isTrue(!USER_PRINCIPAL_PREFIX.equals(getPrincipal()),"error init from authentication");
        
    }
	
	private static String getPrincipleFromAuthenitcation(Authentication authentication){
		String principal = null;
		if(authentication!=null){
			if (authentication.getPrincipal() instanceof User) {
	            principal = USER_PRINCIPAL_PREFIX + ((User) authentication.getPrincipal()).getId();
	        } else if (authentication.getPrincipal() instanceof UserDetails) {
	            principal = ((UserDetails) authentication.getPrincipal()).getUsername();
	        } else{
	            principal = authentication.getPrincipal().toString();
	        }
		}

        return principal;
	}
	
	 //~ Methods ========================================================================================================

    public boolean equals(Object object) {
        if ((object == null) || !(object instanceof UserPrincipalSid || object instanceof PrincipalSid)) {
            return false;
        }

        // Delegate to getPrincipal() to perform actual comparison (both should be identical)
        return ((PrincipalSid) object).getPrincipal().equals(this.getPrincipal());
    }

    public int hashCode() {
        return this.getPrincipal().hashCode();
    }



    public String toString() {
        return "UserPrincipalSid[" + getPrincipal() + "]";
    }
}
