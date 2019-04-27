package com.baidu.spark.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baidu.spark.dao.GroupDao;
import com.baidu.spark.model.Group;

/**
 * Group DAO Hibernate implementation.
 * 空间用户组的dao
 * 
 * @author zhangjing_pe
 *
 */
@Repository
public class GroupDaoHibernate extends GenericDaoHibernate<Group, Long> implements GroupDao {

	/**
	 * GroupDaoHibernate Constructor.
	 * @param sessionFactory Hibernate SessionFactory
	 */
	@Autowired
	public GroupDaoHibernate(SessionFactory sessionFactory) {
		super(sessionFactory, Group.class);
	}

}
