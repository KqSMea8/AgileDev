package com.baidu.spark.web;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.baidu.spark.model.User;
import com.baidu.spark.service.UserService;
import com.baidu.spark.service.UserSynchronizeService;

/**
 * 用户前端控制器.
 * 
 * @author zhangjing
 *
 */
@Controller
@RequestMapping("/users")
public class UserController {
	
	private UserService userService;
	private UserSynchronizeService userSyncService;
	
	@RequestMapping("/sync")
	public void sync(ModelMap modelMap){
		int count = userSyncService.syncToLatest();
		modelMap.put("total", count);
	}
	
	@RequestMapping("/list")
	public String list(@RequestParam(required=false) String keyword, ModelMap modelMap){
		if( keyword != null ){
			modelMap.addAttribute("keyword", keyword);
			modelMap.addAttribute("users", userService.getUsersMatching(keyword));
		}
		return "users/userlist";
	}
	
	@RequestMapping("/new")
	public String getCreateForm(ModelMap modelMap){
		modelMap.addAttribute("user", new User());
		return "users/userform";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String createFormSubmit(@Valid User user, BindingResult result, ModelMap modelMap){
		String message =  validateUser(user, result, modelMap); 
		if( message != null ){
			return message;
		}else{
			userService.saveUser(user);
			return "redirect:/users/" + user.getId() + "/edit";
		}
	}
	
	@RequestMapping(value="/{userId}", method= RequestMethod.PUT)
	public String editFormSubmit(@Valid User user, BindingResult result, ModelMap modelMap){
		String message =  validateUser(user, result, modelMap); 
		if( message != null ){
			return message;
		}else{
			userService.saveUser(user);
			return "redirect:/users/list";
		}
	}
	
	@RequestMapping("/{userId}/edit")
	public String getEditForm(@PathVariable Long userId, ModelMap modelMap){
		modelMap.addAttribute("user", userService.getUserById(userId));
		return "users/userform";
	}
	
	private String validateUser(User user, BindingResult result, ModelMap modelMap){
		//验证用户名是否冲突
		if (!StringUtils.isEmpty(user.getUsername()) && userService.checkConflicts(user)){
			result.rejectValue("username", "Duplicate.user.username");
		}
		//验证UICID是否冲突
		if( user.getUicId() != null && userService.checkUicIdConflicts(user)){
			User exist = userService.getUserByUicId(user.getUicId());
			result.rejectValue("uicId", "Duplicate.user.uicid",new Object[]{exist.getUsername()},"");
		}
		if ( result.hasErrors() ){
			modelMap.addAttribute("user", user );
			return "users/userform";
		}else{
			return null;
		}
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setUserSyncService(UserSynchronizeService userSyncService) {
		this.userSyncService = userSyncService;
	}

}
