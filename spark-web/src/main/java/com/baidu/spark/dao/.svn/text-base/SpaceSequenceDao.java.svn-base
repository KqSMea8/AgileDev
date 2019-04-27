package com.baidu.spark.dao;

import com.baidu.spark.model.SpaceSequence;

/**
 * 空间顺序数据存取对象接口.
 * 
 * @author zhangjing_pe
 *
 */
public interface SpaceSequenceDao extends GenericDao<SpaceSequence, Long> {
	/**
	 * 获取指定空间的spaceSequence
	 * 由事务隔离来保证立刻提交
	 * @param spaceId
	 * @return
	 */
	public Long getCardSeqAndIncrease(Long spaceId);
	
	/**
	 * 获取指定空间的card type localid
	 * 由事务隔离来保证立刻提交
	 * @param spaceId
	 * @return
	 */
	public Long getCardTypeLocalIdAndIncrese(Long spaceId);
	
	/**
	 * 获取指定空间的card property localid
	 * 由事务隔离来保证立刻提交
	 * @param spaceId
	 * @return
	 */
	public Long getCardPropertyLocalIdAndIncrese(Long spaceId);
	
	/**
	 * 获取指定空间的list option value localid
	 * 由事务隔离来保证立刻提交
	 * @param spaceId
	 * @return
	 */
	public Long getListPropertyValueLocalIdAndIncrease(Long spaceId);
}