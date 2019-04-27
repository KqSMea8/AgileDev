package com.baidu.spark.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baidu.spark.dao.ProjectDao;
import com.baidu.spark.model.Project;

/**
 * Icafe Project DAO Hibernate implementation.
 * icafe项目的dao
 * 
 * @author Adun
 *
 */
@Repository
public class ProjectDaoHibernate extends GenericDaoHibernate<Project, Long> implements ProjectDao {
	
	/**
	 * Icafe Project DaoHibernate Constructor.
	 * @param sessionFactory Hibernate SessionFactory
	 */
	@Autowired
	public ProjectDaoHibernate(SessionFactory sessionFactory) {
		super(sessionFactory, Project.class);
	}

}
