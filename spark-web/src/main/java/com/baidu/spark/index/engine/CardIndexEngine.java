package com.baidu.spark.index.engine;

import java.util.List;

import com.baidu.spark.dao.Pagination;
import com.baidu.spark.exception.IndexException;
import com.baidu.spark.model.QueryVO;
import com.baidu.spark.model.card.Card;
/**
 * 卡片索引服务
 * @author zhangjing_pe
 *
 */
public interface CardIndexEngine extends IndexEngine<Card>{

	/**
	 * 根据queryString查询卡片列表
	 * @param queryString
	 * @param cardPage 卡片分页对象
	 * @return
	 * @throws IndexException 
	 */
	public List<Card> queryByCardQueryVO(final QueryVO queryVo,Pagination<Card> cardPage) throws IndexException;
	
	/**
	 * 层级视图页面的查询
	 * @param queryVo
	 * @param parentId
	 * @return
	 * @throws IndexException
	 */
	public List<Card> hierarchyQuery(final QueryVO queryVo,Long parentId) throws IndexException;
	
}
