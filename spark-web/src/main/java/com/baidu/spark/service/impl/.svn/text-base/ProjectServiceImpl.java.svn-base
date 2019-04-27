package com.baidu.spark.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baidu.spark.dao.CardDao;
import com.baidu.spark.dao.ProjectDao;
import com.baidu.spark.model.Project;
import com.baidu.spark.model.Space;
import com.baidu.spark.service.ProjectService;

/**
 * Icafe项目相关操作的Service实现
 * @author Adun
 */
@Service
public class ProjectServiceImpl implements ProjectService{
	
	private CardDao cardDao;
	private ProjectDao projectDao;
	
	@Override
	public List<Project> getAll(Space space){
		return projectDao.find("from Project where space = ?", space);
	}

	@Override
	public List<Project> getAll(){
		return projectDao.findAll();
	}
	
	@Override
	public void add(Project project) {
		projectDao.save(project);
	}

	@Override
	public void delete(Project project) {
		if (null != project){
			if (null != project.getSpace()){
				Map<String, Object> valueMap = new HashMap<String, Object>();
				valueMap.put("space", project.getSpace());
				valueMap.put("project", project);
				cardDao.executeUpdate("update Card card set card.project = null where card.space = :space and card.project = :project", valueMap);
			}
			projectDao.delete(project);
		}
	}
	
	@Override
	public Project get(Long id) {
		return projectDao.get(id);
	}
	
	@Override
	public Project getByIcafeId(Long id) {
		List<Project> projects = projectDao.findByProperty("icafeProjectId", id);
		if(CollectionUtils.isNotEmpty(projects)){
			return projects.get(0);
		}
		return null;
	}
	
	@Autowired
	public void setProjectDao(ProjectDao projectDao) {
		this.projectDao = projectDao;
	}

	@Autowired
	public void setCardDao(CardDao cardDao) {
		this.cardDao = cardDao;
	}
}
