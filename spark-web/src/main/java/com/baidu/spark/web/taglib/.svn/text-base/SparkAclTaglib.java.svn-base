package com.baidu.spark.web.taglib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.ExpressionEvaluationUtils;

import com.baidu.spark.model.User;
import com.baidu.spark.security.SparkPermission;
import com.baidu.spark.security.voter.AccessPermissionBean;
import com.baidu.spark.security.voter.UserAccessResolver;
/**
 * spark的acl权限判断tag
 * @author zhangjing_pe
 *
 */
public class SparkAclTaglib extends TagSupport {
    //~ Static fields/initializers =====================================================================================

	private static final long serialVersionUID = 6140472035834935559L;

	protected static final Log logger = LogFactory.getLog(SparkAclTaglib.class);

    //~ Instance fields ================================================================================================

    private ApplicationContext applicationContext;
    private Object domainObject;
    private PermissionFactory permissionFactory;
    private UserAccessResolver resolver;
    private String hasPermission = "";
    private User user;

    //~ Methods ========================================================================================================

    public int doStartTag() throws JspException {
        if ((null == hasPermission) || "".equals(hasPermission)) {
            return Tag.SKIP_BODY;
        }

        initializeIfRequired();

        final String evaledPermissionsString = ExpressionEvaluationUtils.evaluateString("hasPermission", hasPermission,
                pageContext);

        List<Permission> requiredPermissions = parsePermissionsString(evaledPermissionsString);

        Object resolvedDomainObject = null;

        if (domainObject instanceof String) {
            resolvedDomainObject = ExpressionEvaluationUtils.evaluate("domainObject", (String) domainObject,
                    Object.class, pageContext);
        } else {
            resolvedDomainObject = domainObject;
        }

        if (resolvedDomainObject == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("domainObject resolved to null, so including tag body");
            }

            // Of course they have access to a null object!
            return Tag.EVAL_BODY_INCLUDE;
        }
        AccessPermissionBean data = new AccessPermissionBean(resolvedDomainObject,requiredPermissions);
        
        User evaluatedUser;
        if(user == null || user.getId() == null){
        	 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
             if (SecurityContextHolder.getContext().getAuthentication() == null) {
                 if (logger.isDebugEnabled()) {
                     logger.debug(
                         "SecurityContextHolder did not return a non-null Authentication object, so skipping tag body");
                 }

                 return Tag.SKIP_BODY;
             }
             evaluatedUser = (User)authentication.getPrincipal();
        }else{
        	evaluatedUser = user; 
        }

        // Obtain aclEntrys applying to the current Authentication object
        try {
            if (resolver.isGranted(evaluatedUser.getId(), data)) {
                return Tag.EVAL_BODY_INCLUDE;
            } else {
                return Tag.SKIP_BODY;
            }
        } catch (NotFoundException nfe) {
            return Tag.SKIP_BODY;
        }
    }

    /**
     * Allows test cases to override where application context obtained from.
     *
     * @param pageContext so the <code>ServletContext</code> can be accessed as required by Spring's
     *        <code>WebApplicationContextUtils</code>
     *
     * @return the Spring application context (never <code>null</code>)
     */
    protected ApplicationContext getContext(PageContext pageContext) {
        ServletContext servletContext = pageContext.getServletContext();

        return WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
    }

    public Object getDomainObject() {
        return domainObject;
    }

    public String getHasPermission() {
        return hasPermission;
    }
    
    public User getUser() {
		return user;
	}

	private void initializeIfRequired() throws JspException {
        if (applicationContext != null) {
            return;
        }

        this.applicationContext = getContext(pageContext);

        resolver = getBeanOfType(UserAccessResolver.class);

        permissionFactory = getBeanOfType(PermissionFactory.class);

        if (permissionFactory == null) {
            permissionFactory = new DefaultPermissionFactory(SparkPermission.class);
        }
    }

    private <T> T getBeanOfType(Class<T> type) throws JspException {
        Map<String, T> map = applicationContext.getBeansOfType(type);

        for (ApplicationContext context = applicationContext.getParent();
            context != null; context = context.getParent()) {
            map.putAll(context.getBeansOfType(type));
        }

        if (map.size() == 0) {
            return null;
        } else if (map.size() == 1) {
            return map.values().iterator().next();
        }

        throw new JspException("Found incorrect number of " + type.getSimpleName() +" instances in "
                    + "application context - you must have only have one!");
    }

    private List<Permission> parsePermissionsString(String permissionsString) throws NumberFormatException {
        final Set<Permission> permissions = new HashSet<Permission>();
        final StringTokenizer tokenizer;
        tokenizer = new StringTokenizer(permissionsString, ",", false);

        while (tokenizer.hasMoreTokens()) {
            String permission = tokenizer.nextToken();
            try {
                permissions.add(permissionFactory.buildFromMask(Integer.valueOf(permission)));
            } catch (NumberFormatException nfe) {
                // Not an integer mask. Try using a name
                permissions.add(permissionFactory.buildFromName(permission));
            }
        }

        return new ArrayList<Permission>(permissions);
    }

    public void setDomainObject(Object domainObject) {
        this.domainObject = domainObject;
    }

    public void setHasPermission(String hasPermission) {
        this.hasPermission = hasPermission;
    }
    
    public void setUser(User user) {
		this.user = user;
	}
}
