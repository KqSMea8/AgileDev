package com.baidu.spark.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 分页对象
 * 
 * @param <T> Page中的记录类型.
 * 
 * @author GuoLin
 */
public class Pagination<T> {
	
	/** 默认页面大小 */
	public static final int DEFAULT_SIZE = 10;

	/** 页内记录内容 */
	private List<T> results = null;

	/** 总记录数，初始值-1以便之后进行校验 */
	private int total = -1;

	/** 当前页面号 */
	private final int page;
	
	/** 每页大小 */
	private final int size;
	
	/** 正向排序的属性列表 */
	private List<String> ascOrderList = new LinkedList<String>();
	
	/** 逆向排序的属性列表 */
	private List<String> descOrderList = new LinkedList<String>();
	
	/**
	 * 空值构造器.
	 */
	public Pagination() {
		this(DEFAULT_SIZE, 1);
	}

	/**
	 * 构造器.
	 * @param page 当前页面号
	 */
	public Pagination(Integer page) {
		this(DEFAULT_SIZE, page);
	}

	/**
	 * 构造器.
	 * @param size 页面大小
	 * @param page 当前页面号
	 */
	public Pagination(Integer size, Integer page) {
		this.size = (size != null && size> 0) ? size : DEFAULT_SIZE ;
		this.page = (page != null && page > 0) ? page : 1;
	}

	/**
	 * 获取页内的数据列表.
	 * @return 数据列表
	 */
	public List<T> getResults() {
		return results;
	}

	/**
	 * 设置页内的数据列表.
	 * @param results 结果列表
	 */
	public void setResults(List<T> results) {
		this.results = results;
	}

	/**
	 * 获取总记录数.
	 * @return 总记录数
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * 设置总记录数.
	 * @param total 总记录数
	 */
	public void setTotal(int total) {
		this.total = total;
	}

	/**
	 * 获取总页数.
	 * @return 总页数
	 */
	public int getTotalPages() {
		// 校验total值是否已被设置
		if (total <= 0) {
			return 0;
		}

		int count = total / size;
		if (total % size > 0) {
			count++;
		}
		return count;
	}

	/**
	 * 获得每页的记录数量.
	 * @return 每页的记录数
	 */
	public int getSize() {
		return size;
	}

	/**
	 * 获得当前页的页号.
	 * @return 当前页号
	 */
	public int getPage() {
		int totalPages = getTotalPages();
		return page > totalPages ? totalPages : page;
	}

	/**
	 * 当前页第一条记录在总结果集中的位置,序号从0开始.
	 * @return 当前页第一条记录在结果集中的位置
	 */
	public int getFirst() {
		int first = (page - 1) * size;
		return first > total ? total : first;

	}

	/**
	 * 当前页最后一条记录在总结果集中的位置,序号从0开始.
	 * @return 当前页最后一条记录在结果集中的位置
	 */
	public int getLast() {
		int last = page * size;
		return last > total ? total : last;
	}
	
	/**
	 * 获得正向排序字段.
	 * @return 正向排序字段列表
	 */
	public List<String> getAscOrders() {
		return ascOrderList;
	}

	/**
	 * 加入一个正向排序字段.
	 * @param field 字段名称
	 */
	public void addAscOrder(String field) {
		ascOrderList.add(field);
	}

	/**
	 * 获得反向排序字段.
	 * @return 反向排序字段列表
	 */
	public List<String> getDescOrders() {
		return descOrderList;
	}

	/**
	 * 加入一个逆向排序字段.
	 * @param field 字段名称
	 */
	public void addDescOrder(String field) {
		descOrderList.add(field);
	}
	
	/**
	 * 翻转排序方向.
	 */
	public void getInverseOrder() {
		List<String> swapOrderList = descOrderList;
		ascOrderList = descOrderList;
		descOrderList = swapOrderList;
	}
	
	/**
	 * 根据全量结果集(未分页的)构造分页完成后的分页对象.
	 * 此方法主要应用于一些无法做服务器端查询分页的结果集.
	 * @param <T> 分页结果集元素类型
	 * @param completeResults 全量结果集
	 * @param page 当前页面
	 * @param pageSize 页面大小(每页显示的记录个数)
	 * @return 填充好的分页对象
	 */
	public static <T> Pagination<T> paging(Collection<T> completeResults, int page, int pageSize) {
		Pagination<T> pagination = new Pagination<T>(page, pageSize);
		pagination.setTotal(completeResults.size());

		int firstResult = pagination.getFirst();
		int maxResults = pagination.getLast();

		int i = 0;
		List<T> results = new ArrayList<T>(pageSize);
		for (Iterator<T> it = completeResults.iterator(); 
				it.hasNext() && (i < maxResults + firstResult); i++) {
			T element = it.next();
			if (i < firstResult) {
				continue;
			}
			results.add(element);
		}
		
		pagination.setResults(results);
		return pagination;
	}
	
	@Override
	public String toString() {
		return new StringBuilder("Pagination: [")
			.append("page=").append(page)
			.append("size=").append(size)
			.append("total=").append(total)
			.append("results=").append(getFirst()).append("-").append(getLast())
			.append("]")
			.toString();
	}

}
