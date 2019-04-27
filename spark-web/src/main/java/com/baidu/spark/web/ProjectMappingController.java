package com.baidu.spark.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.baidu.spark.model.Project;
import com.baidu.spark.model.Space;
import com.baidu.spark.service.ProjectService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.util.MessageHolder;

/**
 * 项目-空间映射控制器.
 * 
 * @author Adun
 * 
 */
@Controller
@RequestMapping("spaces/{prefixCode}/projects")
public class ProjectMappingController {
	
	private SpaceService spaceService;
	private ProjectService projectService;
	
	/**
	 * 在页面上显示项目-空间映射的列表
	 * @param prefixCode
	 * @return
	 */
	@RequestMapping("/list")
	public ModelAndView list(@PathVariable String prefixCode){
		ModelAndView mav = new ModelAndView();
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		List<Project> projectList = projectService.getAll(space);
		
		mav.addObject("projectList", projectList);
		mav.addObject("space", space);

		//新建用的bean
		Project project = new Project();
		project.setSpace(space);
		mav.addObject("project", project);
		
		mav.setViewName("/project/list");
		return mav;
	}
	
	/**
	 * 删除一个映射,并返回列表页
	 * @param id
	 * @return
	 */
	@RequestMapping(value="{id}", method=RequestMethod.DELETE)
	public String delete(@PathVariable Long id){
		Project project = projectService.get(id);
		if (null != project){
			projectService.delete(project);
		}
		return "redirect:list";
	}

	/**
	 * 新建一个映射
	 * @param id
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public String create(@ModelAttribute("project") Project project, BindingResult bindingResult, ModelMap modelMap){
		projectService.add(project);
		return "redirect:projects/list";
	}

	
	/**
	 * 删除确认页面
	 * @param prefixCode 空间名称
	 * @param id 项目-空间映射id
	 * @return
	 */
	@RequestMapping(value="/{id}/delete", method=RequestMethod.GET)
	public ModelAndView deleteConfirm(@PathVariable String prefixCode, @PathVariable Long id ) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		Project project = projectService.get(id);
		List<String> messages = new ArrayList<String>();
		messages.add(MessageHolder.get("project.deleteconfirm.cards", project.getCards().size()));
		ModelAndView mav = new ModelAndView();
		mav.addObject("messages", messages);
		mav.addObject("project", project);
		mav.addObject("space", space);
		mav.setViewName("project/delete");
		return mav;
	}
	
//	@RequestMapping(value="/{cardPropertyId}", method=RequestMethod.DELETE)
//	public String delete(@PathVariable String prefixCode, @PathVariable Long cardTypeId, 
//			@PathVariable Long cardPropertyId) {
//		CardType cardType = getCardTypeAndValidate(prefixCode, cardTypeId);
//		CardProperty cardProperty = getCardPropertyAndValidate(cardPropertyId, cardType);
//		cardTypeService.deleteCardProperty(cardProperty);
//		return "redirect:list";
//	}
	
	@Autowired
	public void setSpaceService(SpaceService spaceService) {
		this.spaceService = spaceService;
	}

	@Autowired
	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}
}
