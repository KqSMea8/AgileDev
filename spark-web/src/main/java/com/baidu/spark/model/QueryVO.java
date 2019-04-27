package com.baidu.spark.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.baidu.spark.model.QueryConditionVO.QueryOperationType;

/**
 * 卡片查询的view object,用于传输查询条件
 * @author zhangjing_pe
 *
 */
public class QueryVO {
	
	/**
	 * 查询条件的参数
	 */
	private List<QueryConditionVO> queryConditionList = null;
	/**
	 * 排序对象
	 */
	private List<QuerySortVO> querySortList = null;
	
	public QueryVO(){
		queryConditionList = new ArrayList<QueryConditionVO>();
		querySortList = new ArrayList<QuerySortVO>();
	}
	/**
	 * 构造器，生成queryVO
	 * @param conditionParams
	 * @param sortParams
	 */
	public QueryVO(String[] conditionParams,String[] sortParams){
		this();
		if(conditionParams!=null&&conditionParams.length>0){
			for(String param:conditionParams){
				addQueryCondition(new QueryConditionVO(param));
			}
		}
		if(sortParams!=null&&sortParams.length>0){
			for(String param:sortParams){
				addQuerySort(new QuerySortVO(param));
			}
		}
	}
	
	public QueryVO(List<QueryConditionVO> conditionParams,List<QuerySortVO> sortParams){
		queryConditionList = conditionParams;
		querySortList = sortParams;
	}
	
	private void addQuerySort(QuerySortVO sortVo){
		querySortList.add(sortVo);
	}
	
	private void addQueryCondition(QueryConditionVO paramVo){
		queryConditionList.add(paramVo);
	}

	/**
	 * 添加查询条件
	 * @param fieldName 字段名称
	 * @param operationType 操作类型
	 * @param value 字段值
	 */
	public void addQueryCondition(String fieldName, QueryOperationType operationType, Object value){
		queryConditionList.add(new QueryConditionVO(fieldName, operationType, String.valueOf(value)));
	}
	
	/**
	 * 删除字段查询条件
	 * @param fieldName
	 */
	public void removeQueryCondition(String fieldName){
		Iterator<QueryConditionVO> it = queryConditionList.iterator();
		while(it.hasNext()){
			if(it.next().getFieldName().equals(fieldName)){
				it.remove();
			}
		}
	}
	
	/**
	 * 添加排序条件
	 * @param fieldName 排序字段名称
	 * @param desc 是否倒序
	 */
	public void addQuerySort(String fieldName, boolean desc){
		querySortList.add(new QuerySortVO(fieldName, desc));
	}
	
	/**
	 * 添加字段排序（默认asc）
	 * @param fieldName
	 */
	public void addQuerySort(String fieldName){
		addQuerySort(fieldName, false);
	}
	
	public List<QueryConditionVO> getQueryConditionList() {
		return queryConditionList;
	}

	public void setQueryConditionList(List<QueryConditionVO> q) {
		this.queryConditionList = q;
	}
	
	public List<QuerySortVO> getQuerySortList() {
		return querySortList;
	}

	public void setQuerySortList(List<QuerySortVO> sort) {
		this.querySortList = sort;
	}
	
	//页面传递参数使用
	public void setQ(List<QueryConditionVO> q) {
		this.queryConditionList = q;
	}
	//页面传递参数使用
	public void setSort(List<QuerySortVO> sort) {
		this.querySortList = sort;
	}
	
	
	
	
}
