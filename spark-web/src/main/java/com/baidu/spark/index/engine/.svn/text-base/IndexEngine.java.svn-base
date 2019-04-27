package com.baidu.spark.index.engine;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.baidu.spark.dao.Pagination;
import com.baidu.spark.exception.IndexException;
import com.baidu.spark.model.QueryConditionVO;
import com.baidu.spark.model.QuerySortVO;
/**
 * 索引服务接口
 * @author zhangjing_pe
 *
 * @param <I> 被索引的对象
 */
public interface IndexEngine<I> {

	/**
	 * 添加索引一条记录
	 * 
	 * @param obj
	 *            I 被索引对象
	 * @throws IndexException
	 */
	public abstract void addIndex(I obj) throws IndexException;

	/**
	 * 批量创建索引
	 * 
	 * @param objs
	 *            Collection 对象集合
	 * @param isCreate
	 *            boolean 是否是新建.新建则不会删除原有索引
	 * @param isOptimize
	 *            boolean 是否需要优化
	 * @throws IndexException
	 */
	public abstract void batchAddIndex(Collection<I> objs, boolean isCreate,
			boolean isOptimize) throws IndexException;

	/**
	 * 根据对象删除索引
	 * 
	 * @param obj
	 *            I 被索引对象
	 * @throws IndexException
	 */
	public abstract void deleteIndex(I obj) throws IndexException;

	/**
	 * 删除某个特定字段的索引信息
	 * 
	 * @param fieldName
	 *            String lucene字段名
	 * @param fieldValue
	 *            String lucene字段值
	 * @throws IndexException
	 */
	public abstract void deleteIndexByField(String fieldName, Object fieldValue)
			throws IndexException;

	/**
	 * 修改索引信息
	 * 
	 * @param obj
	 *            I 被索引对象
	 * @throws IndexException
	 */
	public abstract void updateIndex(I obj) throws IndexException;

	/**
	 * 清除所有索引
	 * 
	 * @throws IndexException
	 */
	public abstract void clearAllIndex() throws IndexException;

	/**
	 * 根据查询条件，获取bean list
	 * @param queryConditionVOList 查询规则
	 * @param sortList 排序器
	 * @param pageBean 分页对象
	 * @return
	 * @throws IndexException
	 */
	public abstract List<I> findIndexByQueryVO(List<QueryConditionVO> queryConditionVOList,List<QuerySortVO> sortList, Pagination<I> pageBean) throws IndexException;
	
	/**
	 * 根据查询条件，获取bean list
	 * @param queryConditionVOList 查询规则
	 * @param sortList 排序器
	 * @return
	 * @throws IndexException
	 */
	public abstract List<I> findIndexByQueryVO(List<QueryConditionVO> queryConditionVOList,List<QuerySortVO> sortList) throws IndexException;

	/**
	 * 根据字段名和字段值进行查询
	 * 使用相等的方法进行判断
	 * @param mapValue 
	 * @param pageBean 
	 * @return
	 * @throws IndexException
	 */
	public abstract List<I> findIndexByFieldValues(Map<String,Object> mapValue, Pagination<I> pageBean) throws IndexException;
	
	/**
	 * 根据字段名和字段值进行查询
	 * 使用相等的方法进行判断
	 * @param mapValue 
	 * @return
	 * @throws IndexException
	 */
	public abstract List<I> findIndexByFieldValues(Map<String,Object> mapValue) throws IndexException;

}