package com.baidu.spark.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baidu.spark.dao.SpaceViewDao;
import com.baidu.spark.model.SpaceView;

/**
 * 视图收藏数据操作实现类
 * 
 * @author shixiaolei
 */
@Repository
public class SpaceViewDaoHibernate extends
		GenericDaoHibernate<SpaceView, Long> implements SpaceViewDao {

	@Autowired
	public SpaceViewDaoHibernate(SessionFactory sessionFactory) {
		super(sessionFactory, SpaceView.class);
	}

}
