package com.baidu.spark.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baidu.spark.dao.CardPropertyValueDao;
import com.baidu.spark.model.card.property.CardPropertyValue;

/**
 * 用户数据存取
 * 
 * @author zhangjing_pe
 * 
 */
@SuppressWarnings("unchecked")
@Repository
public class CardPropertyValueDaoHibernate extends
		GenericDaoHibernate<CardPropertyValue, Long> implements
		CardPropertyValueDao {
	/** session factory */
	@Autowired
	public CardPropertyValueDaoHibernate(SessionFactory sessionFactory) {
		super(sessionFactory, CardPropertyValue.class);
	}

}
