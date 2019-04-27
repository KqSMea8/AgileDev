package com.baidu.spark.security.voter;

import java.lang.reflect.Method;
import java.util.Collection;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import com.baidu.spark.model.User;
import com.baidu.spark.security.annotation.SecuredMethod;
import com.baidu.spark.security.annotation.objevaluator.SecuredObjectEvaluator;
import com.baidu.spark.util.ApplicationContextHolder;
/**
 * spark Acl Voter
 * 根据annotation中指定的permission和资源，对当前登录用户进行验证
 * 对资源进行级联地查找，卡片->卡片上级->空间->spark全局配置
 * TODO 支持多种权限组合的验证
 * @author zhangjing_pe
 * 
 */
public class SparkAclVoter implements AccessDecisionVoter
{
	
	//~ Static fields/initializers =====================================================================================

    //~ Instance fields ================================================================================================
    private UserAccessResolver resolver;
    
    private String processConfigAttribute;
    
    
    //~ Constructors ===================================================================================================

    public SparkAclVoter(String processConfigAttribute){
    	this.processConfigAttribute = processConfigAttribute;
    }
    //~ Methods ========================================================================================================
    @Override
    public boolean supports(ConfigAttribute attribute) {
    	if ((attribute.getAttribute() != null) && attribute.getAttribute().equals(getProcessConfigAttribute())) {
            return true;
        } else {
            return false;
        }

    }
    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
    	if (!(object instanceof MethodInvocation)) {
			return ACCESS_ABSTAIN;
		}
    	Method method = ((MethodInvocation)object).getMethod();
		if (!method.isAnnotationPresent(SecuredMethod.class)) {
			return ACCESS_ABSTAIN;
		}
		SecuredMethod securedMethodAnno = method.getAnnotation(SecuredMethod.class);
		SecuredObjectEvaluator evaluator = ApplicationContextHolder.getBean(securedMethodAnno.objEvaluator());
		AccessPermissionBean voteData = evaluator.getAccessPermissionBean((MethodInvocation)object);
    	return voteUserPermission(authentication, voteData);
    }
    /**
     * 验证用户是否有指定的操作权限
     * @param authentication
     * @param voteData
     * @return
     */
    public int voteUserPermission(Authentication authentication,AccessPermissionBean voteData){
    	User user = (User)authentication.getPrincipal();
    	boolean granted = resolver.isGranted(user.getId(), voteData);
    	if(granted){
    		return ACCESS_GRANTED;
    	}else{
    		return ACCESS_DENIED;
    	}
    }
    public boolean supports(Class<?> clazz) {
        if (MethodInvocation.class.isAssignableFrom(clazz)) {
            return true;
        } else {
            return false;
        }
    }
    
    public String getProcessConfigAttribute() {
		return processConfigAttribute;
	}

	public void setProcessConfigAttribute(String processConfigAttribute) {
		this.processConfigAttribute = processConfigAttribute;
	}
	@Autowired
	public void setResolver(UserAccessResolver resolver) {
		this.resolver = resolver;
	}
}
