package com.baidu.spark.web.ajax;

import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baidu.iit.icafe.outservice.spark.ISparkService;
import com.baidu.iit.icafe.outservice.spark.bean.ProjectInfoBean;
import com.baidu.spark.exception.ResponseStatusException;
import com.baidu.spark.model.Project;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.service.CardService;
import com.baidu.spark.service.ProjectService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.util.ApplicationContextHolder;
import com.baidu.spark.util.SpringSecurityUtils;
import com.baidu.spark.util.StringUtils;
import com.baidu.spark.util.mapper.IncludePathCallback;
import com.baidu.spark.util.mapper.SparkMapper;
import com.baidu.spark.util.mapper.SparkMapperSingletonWrapper;

/**
 * 与项目有关的ajax调用controller
 * 
 * @author Adun
 */
@Controller
@RequestMapping("ajax/spaces/{prefixCode}/projects")
public class ProjectAjaxController {

	private static final String[] PROJECT_INCLUDING_PATH_WITH_CARDS = new String[] {
			"id", "icafeProjectId", "name", "space.id", "space.prefixCode",
			"space.name", "space.type", "cards.id" };

	private static final String[] CARD_INCLUDING_PATH = new String[] { "id",
			"title", "sequence", "type.id", "type.name", "type.color",
			"space.prefixCode", "parent.id" };

	private ProjectService projectService;
	private SpaceService spaceService;
	private CardService cardService;
	private ISparkService icafeProjectService;
	
	private SparkMapper mapper = SparkMapperSingletonWrapper.getInstance();

	/**
	 * 根据projectName获取匹配的ProjectInfo列表
	 * @param prefixCode 空间名称
	 * @param projectName 模糊匹配的字符串
	 * @return
	 */
	@RequestMapping("/suggestList")
	@ResponseBody
	public List<ProjectInfoBean> getSuggestList(@PathVariable String prefixCode, @RequestParam String projectName) {
		this.icafeProjectService = ApplicationContextHolder.getBean("icafeProjectWebService");

		projectName = com.baidu.spark.util.StringUtils.toUnicode(projectName);
		
		List<ProjectInfoBean> icafeProjectList = icafeProjectService.queryProjectInfoByName(SpringSecurityUtils.getCurrentUserName(), projectName, 1, 300);
		
		List<Project> existList = projectService.getAll();
		Set<Long> existIdSet = new HashSet<Long>();
		if (existList != null){
			for (Project p : existList){
				existIdSet.add(p.getIcafeProjectId());
			}
		}
		
		for (Iterator<ProjectInfoBean> iterator = icafeProjectList.iterator(); iterator.hasNext();) {
			ProjectInfoBean icafeProject = iterator.next();
			if (existIdSet.contains(icafeProject.getProjectId().longValue())) {
				iterator.remove();
			}
		}
		return icafeProjectList;
	}
	
	/**
	 * 根据项目信息字符串建立空间-项目映射.
	 * @param prefixCode 空间名称
	 * @param projectInfos 一组项目信息,中间用逗号隔开.
	 * 项目信息的格式为: 项目id + ":" + URLEncode(项目全名)
	 * @return
	 */
	@RequestMapping("add")
	@ResponseBody
	public String create(@PathVariable String prefixCode, @RequestParam String projectInfos){
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		List<String> projectInfoList = StringUtils.split(projectInfos, ',');
		for (String projectInfo : projectInfoList){
			List<String> paramList = com.baidu.spark.util.StringUtils.split(projectInfo, ':');
			if (paramList.size() == 2){
				Long icafeProjectId = StringUtils.parseLong(paramList.get(0));
				String name = null;
				try{
					name = URLDecoder.decode(paramList.get(1), "UTF-8");
				}catch(Exception e){}
				
				if (icafeProjectId != 0L && name != null){
					Project project = new Project();
					project.setSpace(space);
					project.setIcafeProjectId(icafeProjectId);
					project.setName(name);
					projectService.add(project);
				}
			}
		}
		return "success";
	}

	@RequestMapping("/list")
	@ResponseBody
	public List<Project> getAllInSpace(@PathVariable String prefixCode) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		List<Project> projects = projectService.getAll(space);
		return mapper.clone(projects, new IncludePathCallback(
				PROJECT_INCLUDING_PATH_WITH_CARDS));
	}

	@RequestMapping("/{id}/cards")
	@ResponseBody
	public List<Card> getAllCardsInSpace(@PathVariable String prefixCode,
			@PathVariable Long id) {
		Project project = projectService.get(id);
		if (project == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		List<Card> cards = cardService.getCardsInProject(project);
		return mapper
				.clone(cards, new IncludePathCallback(CARD_INCLUDING_PATH));
	}
	
	@RequestMapping(value = "/{id}/cards", method = RequestMethod.POST)
	@ResponseBody
	public void assignCardsToProject(@PathVariable String prefixCode, @PathVariable Long id, @RequestBody Long[] sequences) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		Project project = projectService.get(id);
		if (project == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		
		for (Long sequence : sequences) {
			Card card = cardService.getCardBySpaceAndSeq(space, sequence);
			project.addCard(card);
			cardService.updateCard(card);
		}
	}

	@RequestMapping(value = "/none/cards", method = RequestMethod.POST)
	@ResponseBody
	public void breakRelationshipBetweenCardAndProject(@PathVariable String prefixCode, @RequestBody Long[] sequences) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		
		for (Long sequence : sequences) {
			Card card = cardService.getCardBySpaceAndSeq(space, sequence);
			card.setProject(null);
			cardService.updateCard(card);
		}
	}

	@Autowired
	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}

	@Autowired
	public void setSpaceService(SpaceService spaceService) {
		this.spaceService = spaceService;
	}

	@Autowired
	public void setCardService(CardService cardService) {
		this.cardService = cardService;
	}

//	@Autowired
	public void setIcafeProjectService(ISparkService icafeProjectService) {
		this.icafeProjectService = icafeProjectService;
	}

}
