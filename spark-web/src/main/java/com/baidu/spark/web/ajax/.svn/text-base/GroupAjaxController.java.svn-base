package com.baidu.spark.web.ajax;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baidu.spark.exception.UnhandledViewException;
import com.baidu.spark.model.Group;
import com.baidu.spark.model.User;
import com.baidu.spark.service.GroupService;
import com.baidu.spark.service.UserService;
import com.baidu.spark.util.mapper.IncludePathCallback;
import com.baidu.spark.util.mapper.SparkMapper;
import com.baidu.spark.util.mapper.SparkMapperSingletonWrapper;

/**
 * 用户对象前端控制器
 * @author zhangjing_pe
 *
 */
@Controller
@RequestMapping("ajax/groups")
public class GroupAjaxController {

	private static final String[] USER_INCLUDING_PATH = new String[] {
		"id",
		"username",
		"name",
		"mail"
	};
	
	private UserService userService;
	
	private GroupService groupService;
	
	private SparkMapper mapper = SparkMapperSingletonWrapper.getInstance();

	/**
	 * 根据父卡片的编号获取子卡片列表.
	 * @param prefixCode 空间标识
	 * @param sequence 编号
	 * @return 卡片列表
	 */
	@RequestMapping("/addGroupUser")
	@ResponseBody
	public User addGroupUser(@RequestParam String username,@RequestParam Long groupId) {
		User user = userService.getUserByUserName(username);
		if(user == null){
			return null;
		}
		Group group = getGroupAndValidate(groupId);
		group.addUser(user);
		groupService.saveGroup(group);	
		
		return mapper.clone(user, new IncludePathCallback(USER_INCLUDING_PATH));
	}
	
	@RequestMapping("/deleteGroupUser")
	@ResponseBody
	public User deleteGroupUser(@RequestParam String username,@RequestParam Long groupId) {
		User user = userService.getUserByUserName(username);
		if(user == null){
			return null;
		}
		Group group = getGroupAndValidate(groupId);
		if(group.getUsers()!=null){
			group.getUsers().remove(user);
		}
		groupService.saveGroup(group);
		return mapper.clone(user, new IncludePathCallback(USER_INCLUDING_PATH));
	}

	private Group getGroupAndValidate(Long groupId) {
		Group group = groupService.getGroup(groupId);
		if (group == null) {
			throw new UnhandledViewException("group.validate.notFound", groupId);
		}
		return group;
	}
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	@Autowired
	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}
}
