package com.baidu.spark.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baidu.spark.exception.ResponseStatusException;
import com.baidu.spark.model.User;
import com.baidu.spark.security.SparkPermission;
import com.baidu.spark.security.voter.AccessPermissionBean;
import com.baidu.spark.security.voter.UserAccessResolver;
import com.baidu.spark.service.UserService;
import com.baidu.spark.util.ApplicationContextHolder;
import com.baidu.spark.util.SparkConfig;
import com.baidu.spark.util.SpringSecurityUtils;

/**
 * 管理员功能前端控制器.
 * @author shixiaolei
 *
 */
@Controller
@RequestMapping("/system")
public class SystemController {
	
	/** 扮演之前的用户名. */
	private static final String SESSION_ORIGINAL_USER = "ORIGINAL_USER";
	/** 用户服务 */
	private UserService userService;
	/** 权限判断服务 */
	private UserAccessResolver resolver;
	
	/**
	 * 化身功能的页面.
	 */
	@RequestMapping("/shadow")
	public String shadow(HttpSession session, ModelMap modelMap){
		User originalUser = getOriginalUser(session);
		User currentUser = SpringSecurityUtils.getCurrentUser();
		modelMap.put("original", originalUser);
		modelMap.put("shadow", originalUser.equals(currentUser) ? null : currentUser);
		return "admin/shadow";
	}
	
	/**
	 * 化身.
	 */
	@RequestMapping(value = "/shadow", method = RequestMethod.PUT)
	@ResponseBody
	public void play(@RequestParam String username, HttpSession session, HttpServletRequest request){
		User originalUser = getOriginalUser(session);
		if(originalUser == null || !isAdmin(originalUser)){
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		User shadowUser = userService.getUserByUserName(username);
		if(shadowUser == null || isAdmin(shadowUser) 
				|| isSameUser(originalUser, shadowUser)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		session.setAttribute(SESSION_ORIGINAL_USER, originalUser.getUsername());
		convertCurrentUser(request, shadowUser);
	}

	/**
	 * 恢复原始用户.
	 */
	@RequestMapping(value = "/shadow", method = RequestMethod.DELETE)
	@ResponseBody
	public void revert(HttpSession session, HttpServletRequest request){
		User originalUser = getOriginalUser(session);
		if(originalUser == null || !isAdmin(originalUser)){
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		convertCurrentUser(request, originalUser);
	}

	/**
	 * 模拟登录页面.
	 */
	@RequestMapping(value = "/mock", method = RequestMethod.GET)
	public String mock(){
		return "admin/mock";
	}
	
	/**
	 * 模拟登录.
	 */
	@RequestMapping(value = "/mock", method = RequestMethod.PUT)
	public String doMock(@RequestParam String username, HttpServletRequest request){
		if(!isDebugMode()){
			return "admin/mock";
		}
		User mockedUser = userService.getUserByUserName(username);
		if(mockedUser == null){
			return "admin/mock";
		}
		convertCurrentUser(request, mockedUser);
		return "redirect:/";
	}
	
	/**
	 * 得到原始登录用户.
	 * @param session 
	 * @return
	 */
	private User getOriginalUser(HttpSession session){
		Object username = session.getAttribute(SESSION_ORIGINAL_USER);
		if(username == null){
			return SpringSecurityUtils.getCurrentUser();
		}
		User originalUser = userService.getUserByUserName(username.toString());
		if(originalUser == null) {
			return SpringSecurityUtils.getCurrentUser();
		}
		return originalUser; 
	}
	
	/**
	 * 将当前用户转化为user.
	 * @param request
	 * @param user
	 */
	private void convertCurrentUser(HttpServletRequest request, User user) {
		UserDetailsService userDetailService = ApplicationContextHolder.getBean(UserDetailsService.class);
		userDetailService.loadUserByUsername(user.getUsername());
		SpringSecurityUtils.saveUserDetailsToContext(user, request);
	}
	
	/**
	 * 判断user是否为系统管理员.
	 * @param user
	 * @return
	 */
	private boolean isAdmin(User user){
		if(user == null){
			return false;
		}
		List<Permission> permissions = new ArrayList<Permission>(1);
		permissions.add(SparkPermission.ADMIN);
		AccessPermissionBean accessBean = new AccessPermissionBean(null, permissions);
		return resolver.isGranted(user.getId(), accessBean);
	}
	
	/**
	 * 判断是否为同一个用户.
	 * @param originalUser
	 * @param shadowUser
	 * @return
	 */
	private boolean isSameUser(User originalUser, User shadowUser) {
		return originalUser.getUsername().equals(shadowUser.getUsername());
	}
	
	/**
	 * 判断系统是否处于Debug模式.
	 * @return
	 */
	private boolean isDebugMode(){
		return SparkConfig.isDebugMode();
	}
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	@Autowired
	public void setResolver(UserAccessResolver resolver) {
		this.resolver = resolver;
	}
	
}
