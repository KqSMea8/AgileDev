package com.baidu.spark.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baidu.spark.dao.UserViewDao;
import com.baidu.spark.model.UserView;

/**
 * 视图收藏数据操作实现类
 * 
 * @author shixiaolei
 */
@Repository
public class UserViewDaoHibernate extends
		GenericDaoHibernate<UserView, Long> implements UserViewDao {

	@Autowired
	public UserViewDaoHibernate(SessionFactory sessionFactory) {
		super(sessionFactory, UserView.class);
	}

}
