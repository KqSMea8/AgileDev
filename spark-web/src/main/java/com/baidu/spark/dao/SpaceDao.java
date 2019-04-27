package com.baidu.spark.dao;

import com.baidu.spark.model.Space;

/**
 * 空间数据存取对象接口.
 * 
 * @author GuoLin
 *
 */
public interface SpaceDao extends GenericDao<Space, Long> {
	
	/**
	 * 批量删除空间下所有卡片
	 * 也会级联批量删除卡片属性值,卡片历史
	 * @param space
	 * @return 删除的卡片数量
	 */
	public int bulkDeleteCards(Space space);

}
