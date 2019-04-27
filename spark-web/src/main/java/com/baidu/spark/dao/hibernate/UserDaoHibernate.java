package com.baidu.spark.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baidu.spark.dao.UserDao;
import com.baidu.spark.model.User;
/**
 * 用户数据存取
 * @author zhangjing_pe
 *
 */
@Repository
public class UserDaoHibernate extends GenericDaoHibernate<User, Long> implements
		UserDao {
	/** session factory*/
	@Autowired
	public UserDaoHibernate(SessionFactory sessionFactory) {
		super(sessionFactory, User.class);
	}

}
