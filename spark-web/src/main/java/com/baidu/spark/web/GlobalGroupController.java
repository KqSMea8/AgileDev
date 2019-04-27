package com.baidu.spark.web;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.baidu.spark.exception.UnhandledViewException;
import com.baidu.spark.model.Group;
import com.baidu.spark.model.User;
import com.baidu.spark.security.PermissionService;
import com.baidu.spark.security.SparkPermissionEnum;
import com.baidu.spark.service.GroupService;
import com.baidu.spark.util.BeanMapperSingletonWrapper;

/**
 * Group Controller.
 * 全局用户组配置
 * 
 * @author zhangjing_pe
 *
 */
@Controller
@RequestMapping("/globalgroups")
public class GlobalGroupController {
	
	private GroupService groupService;
	
	private PermissionService permissionService;
	
	/**
	 * 用户组列表页面
	 * @param prefixCode
	 * @param modelMap
	 */
	@RequestMapping("/list")
	public String list(ModelMap modelMap) {
		List<Group> groups = groupService.getGroups(null);
		
		SparkPermissionEnum[] spacePermissions = SparkPermissionEnum.values();
 		Map<Long,Integer> authorityMap = new HashMap<Long,Integer>();
 		for(Group group : groups){
 			Permission permission = permissionService.getSystemMergedPermission(group);
			authorityMap.put(group.getId(), permission.getMask());
 		}
		
		modelMap.addAttribute("permissions", spacePermissions);
		modelMap.addAttribute("authorityMap", authorityMap);
		
		modelMap.addAttribute("groupList", groups);
		return "/globalgroups/list";
	}
	
	/**
	 * 新建用户组信息
	 * @param prefixCode
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/new")
	public String addGroup(ModelMap modelMap){
		Group group = new Group();
		SparkPermissionEnum[] spacePermissions = SparkPermissionEnum.values();
		modelMap.addAttribute("permissions",spacePermissions);
		modelMap.addAttribute(group);
		return "/globalgroups/edit";
	}
	
	/**
	 * 编辑用户组信息
	 * @param prefixCode
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public String addGroupSubmit(@RequestParam(required=false) Integer permissionSet,
			@ModelAttribute("group") Group group,BindingResult bindingResult,ModelMap modelMap){
		if(group.getName() == null||group.getName().isEmpty()){
			bindingResult.addError(new ObjectError("group.required.name", new String[]{"required.group.name"},null,null));
		}else if(group.getName().length()>GroupController.GROUP_NAME_LENGTH){
			bindingResult.addError(new ObjectError("group.toolong.name", new String[]{"group.toolong.name"},new String[]{group.getName()},null));
		}
		if (groupService.checkConflicts(group)){
			bindingResult.rejectValue("name", "group.validate.duplicateOfName");
		}
		if(bindingResult.hasErrors()){
			modelMap.addAttribute("permissions",SparkPermissionEnum.values());
			modelMap.put("group", group);
			modelMap.addAttribute("permissionSet",permissionSet);
			return "/globalgroups/edit";
		}
		group.setOwner(null);
		groupService.saveGroup(group,null,permissionSet);
		return "redirect:/globalgroups/list";
	}
	
	/**
	 * 编辑用户组信息
	 * @param prefixCode
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/{groupId}/edit")
	public String editGroup(@PathVariable Long groupId,ModelMap modelMap){
		Group group = getGroupAndValidate(groupId);
		
		Permission permissions = permissionService.getSystemMergedPermission(group);
		SparkPermissionEnum[] spacePermissions = SparkPermissionEnum.values();

		modelMap.addAttribute("permissionSet",permissions.getMask());
		modelMap.addAttribute("group", group);
		modelMap.addAttribute("permissions",spacePermissions);
		return "/globalgroups/edit";
	}
	
	/**
	 * 编辑用户组信息
	 * @param prefixCode
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/{groupId}",method=RequestMethod.PUT)
	public String editGroupSubmit(@RequestParam(required=false) Integer permissionSet,@ModelAttribute("group") Group group,
			BindingResult bindingResult,ModelMap modelMap){
		if(group.getId() == null){
			bindingResult.addError(new ObjectError("group.required.id", new String[]{"required.group.id"},null,null));
		}
		if(group.getName() == null||group.getName().isEmpty()){
			bindingResult.addError(new ObjectError("group.required.name", new String[]{"required.group.name"},null,null));
		}else if(group.getName().length()>GroupController.GROUP_NAME_LENGTH){
			bindingResult.addError(new ObjectError("group.toolong.name", new String[]{"group.toolong.name"},new String[]{group.getName()},null));
		}
		if (groupService.checkConflicts(group)){
			bindingResult.rejectValue("name", "group.validate.duplicateOfName");
		}
		if(bindingResult.hasErrors()){
			modelMap.put("group", group);
			modelMap.addAttribute("permissions",SparkPermissionEnum.values());
			modelMap.addAttribute("permissionSet",permissionSet);
			return "/globalgroups/edit";
		}
		Group groupdb = getGroupAndValidate(group.getId());
		
		if(group.isSameOwner(groupdb)){
			//copy group信息
			Mapper mapper = BeanMapperSingletonWrapper.getInstance();
			mapper.map(group, groupdb,"group-updateGroup");
			groupService.saveGroup(groupdb, null, permissionSet);
		}else{
			groupService.saveGroupPermission(group, null, permissionSet);
		}
		return "redirect:/globalgroups/list";
	}

	/**
	 * 用户组修改成员页面
	 * @param prefixCode
	 * @param modelMap
	 */
	@RequestMapping("/{groupId}/groupuser")
	public String getGroupUser(@PathVariable Long groupId, ModelMap modelMap) {
		Group group = getGroupAndValidateOwner(groupId);
		modelMap.addAttribute("group", group);
		return "/globalgroups/groupuser";
	}
	
	/**
	 * 用户组修改成员页面
	 * @param prefixCode
	 * @param modelMap
	 */
	@RequestMapping(value="/{groupId}/groupuser",method=RequestMethod.POST)
	public String addGroupUser(@PathVariable String prefixCode,@PathVariable Long groupId,@RequestParam(required=false) List<Long> ids, ModelMap modelMap) {
		Group group = getGroupAndValidateOwner(groupId);
		Set<User> users = new LinkedHashSet<User>();
		if(ids!=null&&!ids.isEmpty()){
			for(Long userId:ids){
				User user = new User();
				user.setId(userId);
				users.add(user);
			}
		}
		group.setUsers(users);
		groupService.saveGroup(group);
		modelMap.addAttribute("group", group);
		return "redirect:/globalgroups/list";
	}
	
	/**
	 * 删除用户组
	 * @param prefixCode
	 * @param sequence
	 * @return
	 */
	@RequestMapping(value="/{groupId}", method=RequestMethod.DELETE)  
	public String delete(@PathVariable Long groupId){
		Group group = getGroupAndValidateOwner(groupId);
	     if( group == null ){
	    	 throw new UnhandledViewException("global.exception.noSuchGroup");
	     }
	     groupService.deleteGroup(group);
	     return "redirect:list";  
	}  
	
	private Group getGroupAndValidateOwner(Long groupId){
		Group group = getGroupAndValidate(groupId);
		if(group.getOwner() != null){
			throw new UnhandledViewException("group.validate.mismatchWithSpace",groupId);
		}
		return group;
	}
	
	private Group getGroupAndValidate(Long groupId) {
		Group group = groupService.getGroup(groupId);
		if (group == null) {
			throw new UnhandledViewException("group.validate.notFound", groupId);
		}
		return group;
	}
	
	@Autowired
	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}
	@Autowired
	public void setPermissionService(PermissionService permissionService){
		this.permissionService = permissionService;
	}
}
