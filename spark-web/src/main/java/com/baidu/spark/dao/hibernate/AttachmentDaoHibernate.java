package com.baidu.spark.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baidu.spark.dao.AttachmentDao;
import com.baidu.spark.model.Attachment;

/**
 * 附件数据存取对象Hibernate实现类.
 * 
 * @author shixiaolei
 * 
 */
@Repository
public class AttachmentDaoHibernate extends
		GenericDaoHibernate<Attachment, Long> implements AttachmentDao {
	/** session factory */
	@Autowired
	public AttachmentDaoHibernate(SessionFactory sessionFactory) {
		super(sessionFactory, Attachment.class);
	}

}
