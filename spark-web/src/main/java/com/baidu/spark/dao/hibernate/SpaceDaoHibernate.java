package com.baidu.spark.dao.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baidu.spark.dao.SpaceDao;
import com.baidu.spark.model.Space;

/**
 * 空间数据存取对象Hibernate实现类.
 * 
 * @author GuoLin
 *
 */
@Repository
public class SpaceDaoHibernate extends GenericDaoHibernate<Space, Long> implements SpaceDao {

	@Autowired
	public SpaceDaoHibernate(SessionFactory sessionFactory) {
		super(sessionFactory, Space.class);
	}

	@Override
	public int bulkDeleteCards(Space space){
		Map<String,Object> values = new HashMap<String,Object>();
		values.put("space", space);
		// delete all the history of cards
		String historyHql = "delete CardHistory history where history.card in " +
			" (from Card card where card.space = :space )";
		executeUpdate(historyHql, values);
		//delete all the property values of cards 
		String valuesHql = " delete CardPropertyValue propValue where propValue.card in "
			+ " (from Card card where card.space = :space )";
		executeUpdate( valuesHql, values );
		//delete all the attachments of cards 
		String attachmentHql = " delete Attachment attachment where attachment.card in "
			+ " (from Card card where card.space = :space )";
		executeUpdate( attachmentHql, values );
		//delete the relationship between cards
		String parentSetNull = "update Card card set card.parent = null where card.space = :space ";
		executeUpdate(parentSetNull, values);
		//delete all the cards 
		String cardsHql = "delete Card card where card.space = :space" ;
		return executeUpdate( cardsHql, values );
	}
}
