package com.baidu.spark.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baidu.spark.dao.ConfigurationDao;
import com.baidu.spark.model.Configuration;

/**
 * 系统变量数据对象Hibernate实现类.
 * 
 * @author shixiaolei
 * 
 */
@Repository
public class ConfigurationDaoHibernate extends
		GenericDaoHibernate<Configuration, String> implements ConfigurationDao {
	/** session factory */
	@Autowired
	public ConfigurationDaoHibernate(SessionFactory sessionFactory) {
		super(sessionFactory, Configuration.class);
	}

}
