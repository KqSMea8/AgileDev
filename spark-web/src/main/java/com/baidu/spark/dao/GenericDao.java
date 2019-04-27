package com.baidu.spark.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;

/**
 * DAO基类接口.
 * 
 * @author GuoLin
 *
 * @param <T>
 * @param <PK>
 */
public interface GenericDao<T, PK extends Serializable> {

	/**
	 * 保存一个对象.
	 * @param entity 待保存的实体
	 */
	public void save(T entity);

	/**
	 * 更新一个实体
	 * 该实体对象的ID不能为null
	 * @param entity 待更新的实体
	 */
	public void update(T entity);
	/**
	 * 根据ID删除一个对象.
	 * @param id 待删除的ID
	 */
	public void delete(PK id);
	
	/**
	 * 删除一个对象.
	 * @param entity 待删除实体
	 */
	public void delete(T entity);

	/**
	 * 按id获取对象.
	 * @param id 待获取对象的ID
	 * @return 一个对象
	 */
	public T get(final PK id);

	/**
	 * 获取所有对象列表.
	 * @return 对象列表
	 */
	public List<T> findAll();

	/**
	 * 根据分页对象返回一页对象.
	 * @param page 分页对象
	 * @return 一页对象
	 */
	public Pagination<T> findAll(Pagination<T> page);

	/**
	 * 按HQL查询对象列表.
	 * @param hql hql语句
	 * @param values 数量可变的参数
	 * @return 对象列表
	 */
	public List<T> find(String hql, Object... values);

	/**
	 * 根据HQL查找，返回其中[first,first+size)的记录列表.
	 * @param hql HQL查询语句
	 * @param first 第一个记录号
	 * @param size 需求的记录数量
	 * @param values HQL的参数
	 * @return 用户分页对象
	 */
	public Pagination<T> findByLimit(String hql, int first, int size, Object... values);
	
	/**
	 * 按HQL分页查询.
	 * 暂不支持自动获取总结果数,需对象另行执行查询.
	 * @param page 分页参数.包括pageSize 和firstResult.
	 * @param hql hql语句.
	 * @param values 数量可变的参数.
	 * @return 分页查询结果,附带结果列表及所有查询时的参数.
	 */
	public Pagination<T> find(Pagination<T> page, String hql, Object... values);

	/**
	 * 按HQL查询唯一对象.
	 * @param hql hql语句
	 * @param values 数量可变的参数
	 * @return 对象
	 */
	public <R> R findUnique(String hql, Object... values);

	/**
	 * 按HQL查询Intger类形结果. 
	 * @param hql hql语句
	 * @param values 数量可变的参数
	 * @return 整数
	 */
	public Integer findInt(String hql, Object... values);

	/**
	 * 按HQL查询Long类型结果.
	 * @param hql hql语句
	 * @param values 数量可变的参数
	 * @return 长整型数
	 */
	public Long findLong(String hql, Object... values);
	
	/**
	 * 按Criterion查询唯一对象.
	 * @param criterions 数量可变的查询约束
	 * @return 对象
	 */
	public T findUniqueByCriteria(Criterion...criterions);

	/**
	 * 按Criterion查询对象列表.
	 * @param criterions 数量可变的查询约束
	 * @return 对象列表
	 */
	public List<T> findByCriteria(Criterion... criterions);

	/**
	 * 按Criterion分页查询.
	 * @param page 分页参数.包括pageSize、firstResult、orderBy、asc、autoCount.
	 *             其中firstResult可直接指定,也可以指定pageNo.
	 *             autoCount指定是否动态获取总结果数.
	 * @param criterions 数量可变的Criterion.
	 * @return 分页查询结果.附带结果列表及所有查询时的参数.
	 */
	public Pagination<T> findByCriteria(Pagination<T> page, Criterion... criterions);

	/**
	 * 按属性查找对象列表.
	 * @param propertyName 查询属性名称
	 * @param value 查询值
	 * @return 对象列表
	 */
	public List<T> findByProperty(String propertyName, Object value);
	
	/**
	 * 按属性查找唯一对象.
	 * @param propertyName 查询属性名称
	 * @param value 查询值
	 * @return 对象
	 */
	public T findUniqueByProperty(String propertyName, Object value);

	/**
	 * 判断对象的属性值在数据库内是否唯一.
	 * <p>
	 * 在修改对象的情景下,如果属性新修改的值(value)等于属性原值(orgValue)则不作比较.
	 * 传回orgValue的设计侧重于从页面上发出Ajax判断请求的场景.
	 * 否则需要SS2里那种以对象ID作为第3个参数的isUnique函数.
	 * </p>
	 * @param propertyName 属性名称
	 * @param newValue 新值
	 * @param orgValue 原始值
	 * @return 如果属性值唯一则返回true，否则返回false
	 */
	public boolean isPropertyUnique(String propertyName, Object newValue, Object orgValue);
	
	/**
	 * 执行一条更新语句.
	 * 可以是批量更新或者批量删除.
	 * @param hql HQL语句
	 * @param valueMap 命名参数的映射
	 * @return 作用条数
	 */
	public int executeUpdate(String hql, Map<String, Object> valueMap);

	/**
	 * 根据Criterion条件创建Criteria,后续可进行更多处理,辅助函数.
	 * @param criterions Criterion查询条件
	 * @return Hibernate Criteria对象
	 */
	public Criteria createCriteria(Criterion... criterions) ;
	
	/**
	 * 根据criteria进行分页查询.
	 * XXX 一对多的分页查询会有问题
	 * @param page
	 * @param criteria
	 * @return
	 */
	public Pagination<T> findByCriteria(Pagination<T> page, Criteria criteria) ;
	/**
	 * flush&clear hibernate cache
	 */
	public void clear();
	
	public void flush();
	
}
