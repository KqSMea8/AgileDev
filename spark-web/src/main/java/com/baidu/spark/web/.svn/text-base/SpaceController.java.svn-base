package com.baidu.spark.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.collections.CollectionUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.baidu.spark.exception.UnhandledViewException;
import com.baidu.spark.model.Group;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.security.PermissionService;
import com.baidu.spark.security.SparkPermissionEnum;
import com.baidu.spark.service.CardService;
import com.baidu.spark.service.GroupService;
import com.baidu.spark.service.SpaceCopyService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.service.SpacegroupService;
import com.baidu.spark.service.UserService;
import com.baidu.spark.service.impl.helper.copyspace.metadata.Metadata;
import com.baidu.spark.service.impl.helper.copyspace.option.ImportOption;
import com.baidu.spark.service.impl.helper.copyspace.validation.ValidationResult;
import com.baidu.spark.util.BeanMapperSingletonWrapper;
import com.baidu.spark.util.MessageHolder;
import com.baidu.spark.util.SparkConfig;
import com.baidu.spark.util.SpringSecurityUtils;
import com.baidu.spark.util.WebUtils;

/**
 * 空间列表前端控制器.
 * 
 * @author GuoLin
 * @author chenhui
 *
 */
@Controller
@RequestMapping("/spaces")
public class SpaceController {
	
	private SpaceService spaceService;
	
	private CardService cardService;
	
	private GroupService groupService;
	
	private SpacegroupService spacegroupService;
	
	private UserService userService;
	
	private SpaceCopyService spaceCopyService;
	
	private PermissionService permissionService;

	/**
	 * 空间列表 
	 * @param modelMap 页面Model
	 */
	@RequestMapping("/list")
	public void list(ModelMap modelMap) {
		User user = SpringSecurityUtils.getCurrentUser();
		user = userService.getUserById(user.getId());
		modelMap.addAttribute("spacegroupList", spacegroupService.generateSpacegroupListWithSpace( user ));
		modelMap.addAttribute("cards", cardService.getRecentUpdateCards(user, 5));
	}
	
	/**
	 * 新建空间
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/new",method=RequestMethod.GET)
	public String create(Model model){
		model.addAttribute(new Space());
		model.addAttribute("permissions",getEveryonePermissonList());
		//TODO 查询用户所有可见的空间
		model.addAttribute("spaceList", spaceService.getAllSpaces());
		return "spaces/new";
	}
	
	
	/**
	 * 新建空间提交
	 * @param space
	 * @param result
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public String newSpace(@RequestParam(value="createFromSpaceId",required=false) Long createFromSpaceId,@Valid Space space, BindingResult result, 
			@RequestParam(required=false) Long isPublic,@RequestParam Integer permission,ModelMap modelMap,HttpServletRequest request){
		if (spaceService.checkConflicts(space)){
			result.rejectValue("prefixCode", "Duplicate.space.prefixCode");
		}
		if (result.hasErrors()) {
			modelMap.addAttribute("spaceList", spaceService.getAllSpaces());
			modelMap.addAttribute("createFromSpaceId",createFromSpaceId);
			modelMap.addAttribute("permissions",getEveryonePermissonList());
			if(isPublic!=null&&isPublic>0){
				modelMap.addAttribute("publicPermission",permission);
			}
			modelMap.addAttribute("space", space);
		    return "spaces/new";
		}
		
		if (createFromSpaceId != null && createFromSpaceId > 0) {
			// 从已有空间拷贝
			Space sourceSpace = spaceService.getSpace(createFromSpaceId);
			List<Metadata> metadataList = spaceCopyService.getSpaceMetadata(sourceSpace);
			List<ValidationResult> validationResult = spaceCopyService.validation(metadataList);
			if(CollectionUtils.isNotEmpty(validationResult)){
				modelMap.addAttribute("importValidationResult", validationResult);
				modelMap.addAttribute("spaceList", spaceService.getAllSpaces());
				modelMap.addAttribute("createFromSpaceId",createFromSpaceId);
				modelMap.addAttribute("space", space);
				modelMap.addAttribute("permissions",getEveryonePermissonList());
				if(isPublic!=null&&isPublic>0){
					modelMap.addAttribute("publicPermission",permission);
				}
			    return "spaces/new";
			}else{
				//验证通过，先保存空间定义
				spaceService.saveSpace(space);
				processPermissions(space, isPublic, permission);
				List<ImportOption<?>> importOptions = spaceCopyService.getImportOptions(metadataList);
				if(CollectionUtils.isEmpty(importOptions)){
					//无导入前选项，直接保存并返回
					spaceCopyService.importMetadata(space, metadataList);
					return "redirect:/spaces/" + space.getPrefixCode() + "/edit";	
				}
				//进入导入前的选项
				return importOption(sourceSpace, space, importOptions, modelMap);
			}
		} else {
			spaceService.saveSpace(space);
			processPermissions(space, isPublic, permission);
			WebUtils.setInfoMessage(request, MessageHolder.get("info.success.createspace"));
			return "redirect:/spaces/" + space.getPrefixCode() + "/edit";
		}
	}

	private void processPermissions(Space space, Long isPublic, Integer permission) {
		// 添加默认用户组
		User user = SpringSecurityUtils.getCurrentUser();
		user = userService.getUserById(user.getId());
		saveDefaultGroup(space, user);
		//添加全局的用户组
		Group everyOneGroup = SparkConfig.getEveryOneGroup();
		if(isPublic!=null&&isPublic>0){
			permissionService.updatePermission(everyOneGroup, space,permission);
			space.setIsPublic(true);
		}else{
			permissionService.deletePermission(everyOneGroup, space);
			space.setIsPublic(false);
		}
		spaceService.updateSpace(space);
	}
	
	/**
	 * 获取所有用户的用户组所能获得的权限列表
	 * @return
	 */
	private List<SparkPermissionEnum> getEveryonePermissonList(){
		List<SparkPermissionEnum> everyOnePermissionList = new ArrayList<SparkPermissionEnum>();
		everyOnePermissionList.add(SparkPermissionEnum.READ);
		everyOnePermissionList.add(SparkPermissionEnum.CREATE_CHILDREN);
		everyOnePermissionList.add(SparkPermissionEnum.WRITE);
		everyOnePermissionList.add(SparkPermissionEnum.DELETE);
		everyOnePermissionList.add(SparkPermissionEnum.ADMIN);
		return everyOnePermissionList;
	}
	/**
	 * 进入导入前的选项选择页面
	 * @param space
	 * @param result
	 * @param modelMap
	 * @return
	 */
	private String importOption(Space sourceSpace,Space targetSpace,List<ImportOption<?>> options, ModelMap modelMap){
		modelMap.addAttribute("options", options);
		modelMap.addAttribute("sourceSpace", sourceSpace);
		modelMap.addAttribute("targetSpace",targetSpace);
		return "spaces/importOption";
	}
	
	/**
	 * 执行导入
	 * @param space
	 * @param result
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/import",method=RequestMethod.POST)
	public String importOptionSubmit(@RequestParam Long sourceSpaceId,@RequestParam Long targetSpaceId,HttpServletRequest request){
		Space source = spaceService.getSpace(sourceSpaceId);
		Space target = spaceService.getSpace(targetSpaceId);
		if( source == null || target == null){
			throw new UnhandledViewException("space.validate.notFound");
		}
		List<Metadata> metadatas = spaceCopyService.getSpaceMetadata(source);
		metadatas = spaceCopyService.buildImportOption(request, metadatas);
		spaceCopyService.importMetadata(target, metadatas);
		return "redirect:/spaces/" + target.getPrefixCode() + "/edit";
	}
	
	private void saveDefaultGroup(Space space,User adminUser){
		Group group = new Group();
		group.setName("空间管理员组");
		group.setOwner(space);
		group.addUser(adminUser);
		groupService.saveGroup(group,space,SparkPermissionEnum.ADMIN.getPermissionSet().getMask());
		
		group = new Group();
		group.setName("删除组");
		group.setOwner(space);
		groupService.saveGroup(group,space,SparkPermissionEnum.DELETE.getPermissionSet().getMask());
		
		group = new Group();
		group.setName("编辑组");
		group.setOwner(space);
		groupService.saveGroup(group,space,SparkPermissionEnum.WRITE.getPermissionSet().getMask());
		
		group = new Group();
		group.setName("新建组");
		group.setOwner(space);
		groupService.saveGroup(group,space,SparkPermissionEnum.CREATE_CHILDREN.getPermissionSet().getMask());
		
		group = new Group();
		group.setName("只读组");
		group.setOwner(space);
		groupService.saveGroup(group,space,SparkPermissionEnum.READ.getPermissionSet().getMask());
	}
	
	/**
	 * 编辑空间
	 * @param prefixCode 空间标识
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/{prefixCode}/edit", method=RequestMethod.GET)
	public String edit(@PathVariable String prefixCode, ModelMap modelMap){
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if( space == null ){
			throw new UnhandledViewException("space.validate.notFound", prefixCode);
		}
		//查询公共空间的权限设置
		Group everyOneGroup = SparkConfig.getEveryOneGroup();
		Permission permission = permissionService.getMergedPermission(everyOneGroup, space);
		if(permission.getMask()>0){
			modelMap.addAttribute("publicPermission",permission.getMask());
		}
		//权限类型列表
		modelMap.addAttribute("permissions",getEveryonePermissonList());
		modelMap.addAttribute("space", space);
		modelMap.addAttribute("spaceModel", space);
		return "spaces/edit";
	}
	
	/**
	 * 编辑空间表单提交
	 * @param space 空间数据
	 * @param result 校验结果
	 * @return 空间的卡片列表 
	 */
	@RequestMapping(value = "/{prefixCode}",method = RequestMethod.PUT)
	public String editSubmit(@PathVariable String prefixCode, 
			@Valid @ModelAttribute("spaceModel")Space space,BindingResult result, 
			@RequestParam(required=false) Long isPublic,@RequestParam Integer permission, ModelMap modelMap,HttpServletRequest request){
		Space dbSpace = spaceService.getSpaceByPrefixCode(prefixCode);
		if( dbSpace == null ){
			throw new UnhandledViewException("space.validate.notFound", prefixCode);
		}
		if (spaceService.checkConflicts(space)){
			result.rejectValue("prefixCode", "Duplicate.space.prefixCode");
		}
		if ( result.hasErrors() ){
			modelMap.addAttribute("permissions",getEveryonePermissonList());
			if(isPublic!=null&&isPublic>0){
				modelMap.addAttribute("publicPermission",permission);
			}
			modelMap.addAttribute("space",dbSpace);
			return "spaces/edit";
		}
		Mapper mapper = BeanMapperSingletonWrapper.getInstance();
		mapper.map(space, dbSpace );
		
		if(isPublic!=null&&isPublic>0){
			dbSpace.setIsPublic(true);
		}else{
			dbSpace.setIsPublic(false);
		}
		//先设置权限，避免删除权限后无法更新
		spaceService.updateSpace(dbSpace);
		//设置公共空间权限
		Group everyOneGroup = SparkConfig.getEveryOneGroup();
		if(isPublic!=null&&isPublic>0){
			permissionService.updatePermission(everyOneGroup, space,permission);
		}else{
			permissionService.deletePermission(everyOneGroup, space);
		}
		
		WebUtils.setInfoMessage(request, MessageHolder.get("info.success.editspace"));
		return "redirect:/spaces/" +space.getPrefixCode() +"/edit";
	}
	
	/**
	 * 删除空间
	 * @param prefixCode
	 * @param modelMap
	 * @return
	 */
	@RequestMapping( value=" /{prefixCode}/delete", method = RequestMethod.GET)
	public String delete(@PathVariable String prefixCode, ModelMap modelMap){
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		WebUtils.unhandledViewIfNull(space, "space.validate.notFound", prefixCode);
		List<String> messages = new ArrayList<String>(8);
		messages.add(MessageHolder.get("space.deleteconfirm.cards"));
		messages.add(MessageHolder.get("space.deleteconfirm.histories"));
		messages.add(MessageHolder.get("space.deleteconfirm.cardtypes"));
		messages.add(MessageHolder.get("space.deleteconfirm.cardproperties"));
		messages.add(MessageHolder.get("space.deleteconfirm.groups"));
		messages.add(MessageHolder.get("space.deleteconfirm.views"));
		messages.add(MessageHolder.get("space.deleteconfirm.spacegroups"));
		modelMap.addAttribute("messages",messages);
		modelMap.addAttribute("space", space);
		return "spaces/delete";
	}
	
	
	@RequestMapping( value="/{prefixCode}", method = RequestMethod.DELETE)
	public String delete(@PathVariable String prefixCode){
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		WebUtils.unhandledViewIfNull(space, "space.validate.notFound", prefixCode);
		spaceService.deteleSpace(space);
		return "redirect:/spaces/list";
	}
	
	
	@RequestMapping( value="/{prefixCode}", method = RequestMethod.GET)
	public String enterSpace(@PathVariable String prefixCode){
		return "redirect:/spaces/" + prefixCode + "/cards/view";
	}
	
	@RequestMapping(value="/initCardIndex")
	public void initCardIndex(HttpServletResponse response){
		Integer total = cardService.initAllCardIndex(true,true);
		try {
			response.getWriter().append("finished!Total cards:").append(total.toString());
		} catch (IOException e) {
		} finally{
			try {
				response.getWriter().flush();
				response.getWriter().close();
			} catch (IOException e) {
			}
		}
	}
	
	@Autowired
	public void setSpaceService(SpaceService spaceService) {
		this.spaceService = spaceService;
	}
	
	@Autowired
	public void setCardService(CardService cardService) {
		this.cardService = cardService;
	}

	@Autowired
	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setSpaceCopyService(SpaceCopyService spaceCopyService) {
		this.spaceCopyService = spaceCopyService;
	}

	@Autowired
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	@Autowired
	public void setSpacegroupService(SpacegroupService spacegroupService) {
		this.spacegroupService = spacegroupService;
	}
	
	
}
