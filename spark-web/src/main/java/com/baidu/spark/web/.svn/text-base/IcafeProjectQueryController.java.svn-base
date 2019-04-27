package com.baidu.spark.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.baidu.spark.model.Project;
import com.baidu.spark.service.ProjectService;

/**
 * icafe查询需求列表.
 * 
 * @author zhangjing_pe
 *
 */
@Controller
@RequestMapping("/icafeproject")
public class IcafeProjectQueryController {
	
	private ProjectService projectService;
	
	
	/**
	 * 需求列表 
	 * @param modelMap 页面Model
	 */
	@RequestMapping(value = "/{projectId}", method=RequestMethod.GET)
	public String list(@PathVariable Long projectId,HttpServletRequest request) {
		Project project = projectService.getByIcafeId(projectId);
		if(project != null){
			request.setAttribute("project", project);			
		}
		return "/plugin/icafe/cardlist";
	}

	@Autowired
	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}

	
}
