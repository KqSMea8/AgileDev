package com.baidu.spark.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baidu.spark.dao.SpaceSequenceDao;
import com.baidu.spark.model.SpaceSequence;
/**
 * 空间卡片序号dao
 * @author zhangjing_pe
 *
 */
@Repository
public class SpaceSequenceDaoHibernate extends GenericDaoHibernate<SpaceSequence, Long>
		implements SpaceSequenceDao {
	
	@Autowired
	public SpaceSequenceDaoHibernate(SessionFactory sessionFactory) {
		super(sessionFactory,SpaceSequence.class);
	}

	public Long getCardSeqAndIncrease(Long spaceId){
		synchronized (this) {
			SpaceSequence ss = get(spaceId);
			if(ss == null){
				return null;
			}
			flush();
			Long ret = ss.getCardSeqNumAndIncrement();
			save(ss);
			flush();
			return ret;
		}
	}
	
	@Override
	public Long getCardTypeLocalIdAndIncrese(Long spaceId){
		synchronized (this) {
			SpaceSequence ss = get(spaceId);
			if(ss == null){
				return null;
			}
			flush();
			Long ret = ss.getCardTypeLocalIdAndIncrement();
			save(ss);
			flush();
			return ret;
		}
	}
	
	@Override
	public Long getCardPropertyLocalIdAndIncrese(Long spaceId){
		synchronized (this) {
			SpaceSequence ss = get(spaceId);
			if(ss == null){
				return null;
			}
			flush();
			Long ret = ss.getCardPropertyLocalIdAndIncrement();
			save(ss);
			flush();
			return ret;
		}
	}
	
	@Override
	public Long getListPropertyValueLocalIdAndIncrease(Long spaceId){
		synchronized (this) {
			SpaceSequence ss = get(spaceId);
			if(ss == null){
				return null;
			}
			flush();
			Long ret = ss.getListValueLocalIdAndIncrement();
			save(ss);
			flush();
			return ret;
		}
	}

}
