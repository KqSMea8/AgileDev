package com.baidu.spark.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baidu.spark.dao.CardTypeDao;
import com.baidu.spark.model.card.CardType;
/**
 * 卡片类型数据存取对象Hibernate实现类.
 * 
 * @author zhangjing_pe
 *
 */
@Repository
public class CardTypeDaoHibernate extends GenericDaoHibernate<CardType, Long> implements
		CardTypeDao {
	/** session factory */
	@Autowired
	public CardTypeDaoHibernate(SessionFactory sessionFactory) {
		super(sessionFactory, CardType.class);
	}
	
}
