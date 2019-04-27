package com.baidu.spark.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.baidu.spark.dao.CardHistoryDao;
import com.baidu.spark.model.card.history.CardHistory;

/**
 * 卡片历史的Dao
 * @author tianyusong
 * 2010-06-01
 */
@Repository
public class CardHistoryDaoHibernate extends GenericDaoHibernate<CardHistory, Long> implements CardHistoryDao {

	@Autowired
	public CardHistoryDaoHibernate(SessionFactory sessionFactory) {
		super(sessionFactory, CardHistory.class);
	}

}
