package com.baidu.spark.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baidu.spark.dao.CardPropertyDao;
import com.baidu.spark.model.card.property.CardProperty;
/**
 * cardpropertyDaoHibernate
 * @author zhangjing_pe
 *
 */
@Repository
public class CardPropertyDaoHibernate extends GenericDaoHibernate<CardProperty,Long>
		implements CardPropertyDao {

	/** session factory */
	@Autowired
	public CardPropertyDaoHibernate(SessionFactory sessionFactory) {
		super(sessionFactory, CardProperty.class);
	}


}
