package com.baidu.spark.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baidu.spark.dao.CardDao;
import com.baidu.spark.dao.Pagination;
import com.baidu.spark.model.card.Card;
/**
 * cardDao
 * @author zhangjing_pe
 *
 */
@Repository
public class CardDaoHibernate extends GenericDaoHibernate<Card, Long> implements
		CardDao {
	
	/** 卡片的fetch join*/
	private static final String CARD_FETCH_JOIN = "from Card card " +
			"left outer join fetch card.createdUser " +
			"left outer join fetch card.lastModifiedUser ";
	
	/**根据空间id获取卡片列表*/
	private static final String GET_CARD_BY_SPACE_ID = CARD_FETCH_JOIN +" where card.space.id = ? order by card.sequence asc";
	
	
	/**
	 * 获取某空间下的所有卡片
	 * @param spaceId
	 * @param page
	 * @return
	 */
	public Pagination<Card> getCardsBySpaceId(Long spaceId,Pagination<Card> page){
		return find(page, GET_CARD_BY_SPACE_ID, spaceId);
	}
	
	/** session factory */
	@Autowired
	public CardDaoHibernate(SessionFactory sessionFactory) {
		super(sessionFactory, Card.class);
	}

}
