package com.baidu.spark.web;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.baidu.spark.model.Space;
import com.baidu.spark.security.PermissionService;
import com.baidu.spark.security.SparkPermissionEnum;
import com.baidu.spark.service.GroupService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.util.BeanMapperSingletonWrapper;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Group Controller.
 * 
 * @author zhangjing_pe
 *
 */
@Controller
@RequestMapping("/spaces/{prefixCode}/groups")
public class GroupController {
	
	private GroupService groupService;
	
	private SpaceService spaceService;
	
	private PermissionService permissionService;
	
	public static final int GROUP_NAME_LENGTH = 100;
	
	/**
	 * 用户组列表页面
	 * @param prefixCode
	 * @param modelMap
	 */
	@RequestMapping("/list")
	public String list(@PathVariable String prefixCode, ModelMap modelMap) {
		Space space = getSpaceAndValidate(prefixCode);
		List<Group> groups = groupService.getGroups(space);
		
		SparkPermissionEnum[] spacePermissions = SparkPermissionEnum.values();
 		final Map<Long,Integer> authorityMap = new HashMap<Long,Integer>();
 		for(Group group : groups){
 			Permission permission = permissionService.getMergedPermission(group, space);
			authorityMap.put(group.getId(), permission.getMask());
 		}
		
 		//对组进行排序,按照权限大小的逆序排列.
 		Collections.sort(groups, new Comparator<Group>(){
			@Override
			public int compare(Group o1, Group o2) {
				return authorityMap.get(o2.getId()) - authorityMap.get(o1.getId());
			}
 		});
 		
		modelMap.addAttribute("permissions", spacePermissions);
		modelMap.addAttribute("authorityMap", authorityMap);
		
		modelMap.addAttribute(space);
		modelMap.addAttribute("groupList", groups);
		return "/groups/list";
	}
	
	/**
	 * 新建用户组信息
	 * @param prefixCode
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/new")
	public String addGroup(@PathVariable String prefixCode,ModelMap modelMap){
		Space space = getSpaceAndValidate(prefixCode);
		Group group = new Group();
		SparkPermissionEnum[] spacePermissions = SparkPermissionEnum.values();
		modelMap.addAttribute("permissions",spacePermissions);
		modelMap.addAttribute(space);
		modelMap.addAttribute(group);
		return "/groups/edit";
	}
	
	/**
	 * 编辑用户组信息
	 * @param prefixCode
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public String addGroupSubmit(@PathVariable String prefixCode, @RequestParam(required=false) Integer permissionSet,
			@ModelAttribute("group") Group group,BindingResult bindingResult,ModelMap modelMap){
		Space space = getSpaceAndValidate(prefixCode);
		group.setOwner(space);
		if(group.getName() == null||group.getName().isEmpty()){
			bindingResult.addError(new ObjectError("group.required.name", new String[]{"required.group.name"},null,null));
		}else if(group.getName().length()>GROUP_NAME_LENGTH){
			bindingResult.addError(new ObjectError("group.toolong.name", new String[]{"group.toolong.name"},new String[]{group.getName()},null));
		}
		if (groupService.checkConflicts(group)){
			bindingResult.rejectValue("name", "group.validate.duplicateOfName");
		}
		if(bindingResult.hasErrors()){
			modelMap.addAttribute("permissions",SparkPermissionEnum.values());
			modelMap.addAttribute(space);
			modelMap.put("group", group);
			modelMap.addAttribute("permissionSet",permissionSet);
			return "/groups/edit";
		}
		
		groupService.saveGroup(group, space, permissionSet);
		
		return "redirect:/spaces/" +prefixCode+
		"/groups/list";
	}
	
	/**
	 * 编辑用户组信息
	 * @param prefixCode
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/{groupId}/edit")
	public String editGroup(@PathVariable String prefixCode, @PathVariable Long groupId,ModelMap modelMap){
		Space space = getSpaceAndValidate(prefixCode);
		Group group = getGroupAndValidate(groupId);
		
		Permission permissions = permissionService.getMergedPermission(group, space);
		SparkPermissionEnum[] spacePermissions = SparkPermissionEnum.values();

		modelMap.addAttribute("permissionSet",permissions.getMask());
		modelMap.addAttribute(space);
		modelMap.addAttribute("group", group);
		modelMap.addAttribute("permissions",spacePermissions);
		return "/groups/edit";
	}
	
	/**
	 * 编辑用户组信息
	 * @param prefixCode
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/{groupId}",method=RequestMethod.PUT)
	public String editGroupSubmit(@PathVariable String prefixCode, @RequestParam(required=false) Integer permissionSet,@ModelAttribute("group") Group group,
			BindingResult bindingResult,ModelMap modelMap){
		if(group.getId() == null){
			bindingResult.addError(new ObjectError("group.required.id", new String[]{"required.group.id"},null,null));
		}
		if(group.getName() == null||group.getName().isEmpty()){
			bindingResult.addError(new ObjectError("group.required.name", new String[]{"required.group.name"},null,null));
		}else if(group.getName().length()>GROUP_NAME_LENGTH){
			bindingResult.addError(new ObjectError("group.toolong.name", new String[]{"group.toolong.name"},new String[]{group.getName()},null));
		}
		if (groupService.checkConflicts(group)){
			bindingResult.rejectValue("name", "group.validate.duplicateOfName");
		}
		Space space = getSpaceAndValidate(prefixCode);
		
		if(bindingResult.hasErrors()){
			modelMap.addAttribute(space);
			modelMap.put("group", group);
			modelMap.addAttribute("permissions",SparkPermissionEnum.values());
			modelMap.addAttribute("permissionSet",permissionSet);
			return "/groups/edit";
		}
		Group groupdb = getGroupAndValidate(group.getId());
		
		if(group.isSameOwner(groupdb)){
			//copy group信息
			Mapper mapper = BeanMapperSingletonWrapper.getInstance();
			mapper.map(group, groupdb,"group-updateGroup");
			groupService.saveGroup(groupdb, space, permissionSet);
		}else{
			groupService.saveGroupPermission(group, space, permissionSet);
		}
		return "redirect:/spaces/" +prefixCode+
		"/groups/list";
	}

	/**
	 * 用户组修改成员页面
	 * @param prefixCode
	 * @param modelMap
	 */
	@RequestMapping("/{groupId}/groupuser")
	public String getGroupUser(@PathVariable String prefixCode,@PathVariable Long groupId, ModelMap modelMap) {
		Space space = getSpaceAndValidate(prefixCode);
		Group group = getGroupAndValidate(groupId,space.getId());
		modelMap.addAttribute(space);
		modelMap.addAttribute("group", group);
		return "/groups/groupuser";
	}

	/**
	 * 删除用户组
	 * @param prefixCode
	 * @param sequence
	 * @return
	 */
	@RequestMapping(value="/{groupId}", method=RequestMethod.DELETE)  
	public String delete(@PathVariable String prefixCode, @PathVariable Long groupId){
		Space space = getSpaceAndValidate(prefixCode);
		Group group = getGroupAndValidate(groupId,space.getId());
		
		groupService.deleteGroup(group);
		return "redirect:list";
	}  
	
	private Space getSpaceAndValidate(String prefixCode) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new UnhandledViewException("space.validate.notFound", prefixCode);
		}
		return space;
	}
	/**
	 * 验证groupid是否存在，且groupId的owner是否与spaceId是否相同
	 * @param groupId
	 * @param spaceId
	 * @return
	 */
	private Group getGroupAndValidate(Long groupId,Long spaceId){
		Group group = getGroupAndValidate(groupId);
		if(group == null || group.getOwner() == null || !spaceId.equals(group.getOwner().getId())){
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
	public void setSpaceService(SpaceService spaceService) {
		this.spaceService = spaceService;
	}
	@Autowired
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	
	
}
