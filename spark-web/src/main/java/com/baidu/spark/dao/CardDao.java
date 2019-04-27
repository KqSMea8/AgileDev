package com.baidu.spark.dao;

import com.baidu.spark.model.card.Card;
/**
 * 卡片数据接口.
 * @author zhangjing_pe
 *
 */
public interface CardDao extends GenericDao<Card, Long> {

	/**
	 * 获取某空间下的所有卡片
	 * @param spaceId
	 * @param page
	 * @return
	 */
	public Pagination<Card> getCardsBySpaceId(Long spaceId,Pagination<Card> page);
}
