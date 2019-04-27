package com.baidu.spark.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.baidu.spark.model.User;
import com.baidu.spark.service.UserService;

/**
 * Spring Security UserDetailsService-implementation.
 * @author chenhui
 *
 */
public class UserDetailsServiceImpl implements UserDetailsService{

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private UserService userService;
	
	// ========= Spring Security UserDetailsService implementation ==========
	@Override
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException, DataAccessException {
		User user = userService.getUserByUserName(userName);
		if (user == null) {
			throw new UsernameNotFoundException("User not found for '" + userName + "'");
		}
		user.setAuthorities(getDefaultAuthorities());
		logger.debug("loadUserByUserName success for '" + userName + "'");
		return user;
	}
	
	/**
	 * 获取默认权限组.
	 * TODO 此处未来需要改造成从数据库权限表中获取权限列表
	 * @return
	 */
	protected static List<GrantedAuthority> getDefaultAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add( new GrantedAuthorityImpl("ROLE_USER") );
		return  authorities;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
