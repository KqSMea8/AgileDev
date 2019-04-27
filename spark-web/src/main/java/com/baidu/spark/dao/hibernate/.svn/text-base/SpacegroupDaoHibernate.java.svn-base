package com.baidu.spark.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.baidu.spark.dao.SpacegroupDao;
import com.baidu.spark.model.space.Spacegroup;

/**
 * 空间组数据存取对象Hibernate实现类.
 * 
 * @author 阿蹲
 *
 */
@Repository
public class SpacegroupDaoHibernate extends GenericDaoHibernate<Spacegroup, Long> implements SpacegroupDao {

	@Autowired
	public SpacegroupDaoHibernate(SessionFactory sessionFactory) {
		super(sessionFactory, Spacegroup.class);
	}
}
