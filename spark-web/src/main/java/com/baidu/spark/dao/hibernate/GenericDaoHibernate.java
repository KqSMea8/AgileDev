package com.baidu.spark.dao.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.transform.ResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.baidu.spark.dao.GenericDao;
import com.baidu.spark.dao.Pagination;
import com.baidu.spark.util.ReflectionUtils;

/**
 * Hibernate的范型基类.
 * 
 * 可以在service类中直接创建使用.也可以继承出DAO子类,在多个Service类中共享DAO操作.
 * 参考Spring2.5自带的Petlinc例子,取消了HibernateTemplate.
 * 通过Hibernate的sessionFactory.getCurrentSession()获得session,直接使用Hibernate原生API.
 *
 * @param <T> DAO操作的对象类型
 * @param <PK> 主键类型
 * 
 * @author calvin
 * @author GuoLin
 */
public class GenericDaoHibernate<T, PK extends Serializable> implements GenericDao<T, PK> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/** Hibernate Session工厂 */
	protected SessionFactory sessionFactory;
	
	/** 是否允许 */
	private boolean cachable;

	/** 实体类型 */
	protected Class<T> entityClass;

	/**
	 * 构造器.
	 * @param sessionFactory Hibernate Session工厂
	 * @param entityClass 实体类型
	 */
	public GenericDaoHibernate(SessionFactory sessionFactory, Class<T> entityClass) {
		this.sessionFactory = sessionFactory;
		this.entityClass = entityClass;
	}

	/**
	 * 构造器.
	 * @param sessionFactory Hibernate Session工厂
	 * @param entityClass 实体类型
	 * @param cachable 是否允许缓存
	 */
	public GenericDaoHibernate(SessionFactory sessionFactory, Class<T> entityClass, boolean cachable) {
		this.sessionFactory = sessionFactory;
		this.entityClass = entityClass;
		this.cachable = cachable;
	}

	/**
	 * 获取Session.
	 * @return Session Hibernate Session
	 */
	public Session getSession() {
		Session session = sessionFactory.getCurrentSession();
		return session;
	}

	/**
	 * 获取SessionFactory.
	 * @return SessionFactory Hibernate Session工厂
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@SuppressWarnings("unchecked")
	public T get(final PK id) {
		return (T) getSession().get(entityClass, id);
	}

	public void save(T entity) {
		Assert.notNull(entity);
		getSession().saveOrUpdate(entity);
		logger.info("save entity: {}", entity);
	}
	
	public void update(T entity) {
		Assert.notNull(entity);
		Assert.notNull(getId(entity));
		getSession().update(entity);
		logger.info("update entity: {}", entity);
	}

	public void delete(T entity) {
		Assert.notNull(entity);
		getSession().delete(entity);
		logger.info("delete entity: {}", entity);
	}

	public void delete(PK id) {
		Assert.notNull(id);
		delete(get(id));
	}

	public List<T> findAll() {
		return findByCriteria();
	}

	public Pagination<T> findAll(Pagination<T> page) {
		return findByCriteria(page);
	}

	@SuppressWarnings("unchecked")
	public List<T> find(String hql, Object... values) {
		return createQuery(hql, values).list();
	}
	
	@SuppressWarnings("unchecked")
	public Pagination<T> findByLimit(String hql, int first, int size, Object... values) {
		Assert.hasText(hql);
		
		Pagination<T> page = new Pagination<T>();
		
		String countHql = getCountHql(hql);
		Query countQuery = createQuery(countHql, values);
		page.setTotal(((Number)countQuery.uniqueResult()).intValue());
		
		// 查询结果集
		Query query = createQuery(hql, values);
		List<T> users = query.setFirstResult(first).setMaxResults(size).list();
		page.setResults(users);
		
		return page;
	}

	@SuppressWarnings("unchecked")
	public Pagination<T> find(Pagination<T> page, String hql, Object... values) {
		Assert.notNull(page);

		String countHql = getCountHql(hql);
		page.setTotal(findLong(countHql, values).intValue());
		
		Query q = createQuery(hql, values)
			.setFirstResult(page.getFirst())
			.setMaxResults(page.getSize());
		page.setResults(q.list());
		return page;
	}

	/**将hql转化为获取count的语句
	 * 如果hql出现fetch，且select中未出现fetch后的对象，会出现org.hibernate.QueryException: query specified join fetching, but the owner of the fetched association was not present in the select list
	 * 在hql中将fetch滤掉
	 * 使用hsqldb进行测试的时候，order by 子句会报错。这里把order by 删除掉
	 * @param hql
	 * @return
	 */
	private String getCountHql(String hql) {
		Assert.notNull(hql);
		String countHql = "select count(*) " + hql.substring(StringUtils.indexOfIgnoreCase(hql, "from"));
		countHql = StringUtils.replace(countHql, " fetch ", " ");
		if(StringUtils.indexOfIgnoreCase(countHql, " order ")>=0){
			countHql = StringUtils.substring(countHql, 0,StringUtils.indexOfIgnoreCase(countHql, " order ")+1);			
		}
		return countHql;
	}

	@SuppressWarnings("unchecked")
	public <R> R findUnique(String hql, Object... values) {
		return (R) createQuery(hql, values).uniqueResult();
	}

	public Integer findInt(String hql, Object... values) {
		return (Integer)findUnique(hql, values);
	}

	public Long findLong(String hql, Object... values) {
		return (Long)findUnique(hql, values);
	}
	
	@SuppressWarnings("unchecked")
	public T findUniqueByCriteria(Criterion...criterions) {
		List<T> list = createCriteria(criterions).list();
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(Criterion... criterions) {
		return createCriteria(criterions).list();
	}

	public Pagination<T> findByCriteria(Pagination<T> page, Criterion... criterions) {
		Assert.notNull(page);
		Criteria criteria = createCriteria(criterions);
		return findByCriteria(page, criteria);
	}

	@SuppressWarnings("unchecked")
	public Pagination<T> findByCriteria(Pagination<T> page, Criteria criteria) {
		Assert.notNull(page);
		
		CriteriaImpl impl = (CriteriaImpl) criteria;
		
		Integer firstResult = (Integer)ReflectionUtils.getFieldValue(impl, "firstResult");
		Integer maxResult = (Integer)ReflectionUtils.getFieldValue(impl, "maxResults");
		
		Integer pageNo = (Integer)ReflectionUtils.getFieldValue(page, "page");
		impl.setFirstResult((pageNo - 1) * page.getSize())
			.setMaxResults(page.getSize());
		for (String ascOrder : page.getAscOrders()) {
			impl.addOrder(Order.asc(ascOrder));
		}
		for (String descOrder : page.getDescOrders()) {
			impl.addOrder(Order.desc(descOrder));
		}
		page.setResults(criteria.list());
		//查询总数
		ReflectionUtils.setFieldValue(impl,"firstResult",firstResult);
		ReflectionUtils.setFieldValue(impl,"maxResults",maxResult);
		
		int total = countQueryResult(impl);
		page.setTotal(total);
		
		return page;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findByProperty(String propertyName, Object value) {
		Assert.hasText(propertyName);
		return createCriteria(Restrictions.eq(propertyName, value)).list();
	}

	@SuppressWarnings("unchecked")
	public T findUniqueByProperty(String propertyName, Object value) {
		Assert.hasText(propertyName);
		return (T)createCriteria(Restrictions.eq(propertyName, value)).setCacheable(true).uniqueResult();
	}

	/**
	 * 根据查询函数与参数列表创建Query对象,后续可进行更多处理,辅助函数.
	 * @return Hibernate Query对象
	 */
	public Query createQuery(String queryString, Object... values) {
		Assert.hasText(queryString);
		Query queryObject = getSession().createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i, values[i]);
			}
		}
		queryObject.setCacheable(cachable);  // 设置允许开启查询缓存
		return queryObject;
	}

	public Criteria createCriteria(Criterion... criterions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		criteria.setCacheable(cachable);  // 设置允许开启查询缓存
		return criteria;
	}

	public boolean isPropertyUnique(String propertyName, Object newValue, Object orgValue) {
		if (newValue == null || newValue.equals(orgValue)) {
			return true;
		}

		Object object = findUniqueByProperty(propertyName, newValue);
		return (object == null);
	}

	/**
	 * 通过count查询获得本次查询所能获得的对象总数.
	 * @param c Hibernate Criteria对象
	 * @return page对象中的totalCount属性
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected int countQueryResult(Criteria c) {
		CriteriaImpl impl = (CriteriaImpl) c;

		// 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
		Projection projection = impl.getProjection();
		ResultTransformer transformer = impl.getResultTransformer();

		List<CriteriaImpl.OrderEntry> orderEntries = null;
		try {
			orderEntries = (List<CriteriaImpl.OrderEntry>)ReflectionUtils.getFieldValue(impl, "orderEntries");
			ReflectionUtils.setFieldValue(impl, "orderEntries", new ArrayList());
		} catch (Exception ex) {
			logger.error("Impossible exception: {}", ex);
		}

		// 执行Count查询
		int totalCount = (Integer)c.setProjection(Projections.rowCount()).uniqueResult();
		if (totalCount < 1) {
			return 0;
		}

		// 将之前的Projection和OrderBy条件重新设回去
		c.setProjection(projection);

		if (projection == null) {
			c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}

		if (transformer != null) {
			c.setResultTransformer(transformer);
		}

		try {
			ReflectionUtils.setFieldValue(impl, "orderEntries", orderEntries);
		} catch (Exception ex) {
			logger.error("Impossible exception: {}", ex);
		}

		return totalCount;
	}

	public int executeUpdate(String hql, Map<String, Object> valueMap) {
		return getSession().createQuery(hql).setProperties(valueMap).executeUpdate();
	}
	
	/**
	 * 取得对象的主键名,辅助函数.
	 * @return 获取对象主键名称
	 */
	public String getIdName() {
		ClassMetadata meta = getSessionFactory().getClassMetadata(entityClass);
		Assert.notNull(meta, "Class " + entityClass.getSimpleName() + " not define in hibernate session factory.");
		return meta.getIdentifierPropertyName();
	}
	
	/**
	 * 获得对象的主键值
	 * @param entity 实体对象
	 * @return 主键值
	 */
	private Object getId(Object entity){
		Assert.notNull(entity);
		ClassMetadata meta = getSessionFactory().getClassMetadata(entityClass);
		Assert.notNull(meta, "Class " + entityClass.getSimpleName() + " not define in hibernate session factory.");
		return meta.getIdentifier(entity, EntityMode.POJO);
	}
	
	public void clear(){
		getSession().flush();
		getSession().clear();
	}
	
	public void flush(){
		getSession().flush();
	}

}