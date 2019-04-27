package com.baidu.spark;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.EntityEntry;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.Status;
import org.hibernate.event.EventSource;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.jmock.Expectations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import com.baidu.spark.model.User;
import com.baidu.spark.util.ReflectionUtils;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * 测试工具类.
 * 
 * @author GuoLin
 * 
 */
public abstract class TestUtils {

	private static final Logger logger = LoggerFactory.getLogger(TestUtils.class);
	
	private static final ThreadLocal<SessionFactory> currentSessionFactory = new ThreadLocal<SessionFactory>();

	/** 基本类型列表. */
	private static final Class<?>[] BASIC_TYPES = new Class<?>[] {
			// primitives
			Boolean.class, Byte.class, Character.class, Double.class, 
			Float.class, Integer.class, Long.class, Short.class,
			// objects
			String.class, BigDecimal.class, BigInteger.class, Class.class, 
			File.class, Date.class, java.sql.Date.class, java.sql.Time.class, 
			java.sql.Timestamp.class, URL.class,
			// enumeration
			Enum.class
	};

	/**
	 * 初始化库表.
	 * @param dataSource 数据源
	 * @param datafile 数据文件路径，相对CLASSPATH
	 * @return 数据集合，大多数情况无需处理
	 * @throws Exception 任何可能的异常
	 */
	public static IDataSet initDatabase(DataSource dataSource, String datafile) throws Exception {
		final IDatabaseConnection conn = new DatabaseConnection(dataSource.getConnection());
		final IDataSet data = getDataSet(datafile);
		try {
			DatabaseOperation.CLEAN_INSERT.execute(conn, data);
		} finally {
			conn.close();
		}
		return data;
	}
	
	/**
	 * 清理表.
	 * @param dataSource 数据源
	 * @param tableName 表名
	 * @throws Exception 任何可能的异常
	 */
	public static void clearTable(DataSource dataSource, String tableName) throws Exception {
		String setForeignKeyConstraintFalseHsql = "SET REFERENTIAL_INTEGRITY FALSE";
		String setForeignKeyConstraintTrueHsql = "SET REFERENTIAL_INTEGRITY TRUE";
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			IDatabaseConnection dbunitConn = new DatabaseConnection(conn);
			ITableFilter filter = new DatabaseSequenceFilter(dbunitConn);
	
			final IDataSet data = new FilteredDataSet(filter,new DefaultDataSet(new DefaultTable(tableName)));
			
			// 处理前先关闭外键检查
			Statement stmt = conn.createStatement();
			stmt.execute(setForeignKeyConstraintFalseHsql);
			
			DatabaseOperation.DELETE_ALL.execute(dbunitConn, data);
			
			// 处理后重新开启
			stmt.execute(setForeignKeyConstraintTrueHsql);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	/**
	 * 获取文件中的数据集.数据文件中使用"[NULL]"表示null
	 * @param datafile 数据文件路径，相对CLASSPATH
	 * @return 数据文件映射的数据集
	 * @throws IOException 任何IO异常
	 * @throws DataSetException 数据集处理异常
	 */
	private static IDataSet getDataSet(String datafile) throws IOException, DataSetException {
		ReplacementDataSet rds = new ReplacementDataSet(new FlatXmlDataSet(Thread.currentThread().getContextClassLoader().getResource(datafile)));
		rds.addReplacementObject("[NULL]", null);
		return rds;
	}
	
	/**
	 * 仿造OpenSessionInView编写的OpenSessionInTest的打开方法.
	 * <p>
	 * 打开一个Hibernate Session，一般在JUnit的Before方法中使用.
	 * </p>
	 * @param sessionFactory Hibernate SessionFactory
	 */
	public static void openSessionInTest(SessionFactory sessionFactory) {
		currentSessionFactory.set(sessionFactory);
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
	}
	
	/**
	 * 仿造OpenSessionInView编写的OpenSessionInTest的关闭方法.
	 * <p>
	 * 关闭一个Hibernate Session，一般在JUnit的After方法中使用.
	 * </p>
	 * @param sessionFactory Hibernate SessionFactory
	 */
	public static void closeSessionInTest(SessionFactory sessionFactory) {
		currentSessionFactory.remove();
		SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
		SessionFactoryUtils.closeSession(sessionHolder.getSession());
	}
	
	/**
	 * 设置已通过Spring Sercurity验证的当前用户
	 * <p>
	 * 可以通过SpringSecurityUtils.getCurrentUser()获取用户
	 * </p>
	 * @param user 需要为数据库存在用户
	 */
	public static void setCurrentUser(User user){
		Assert.notNull(user);
		Assert.notNull(user.getId());
		SecurityContext context = new SecurityContextImpl();
		context.setAuthentication(new TestingAuthenticationToken(user, "XXX"));
		SecurityContextHolder.setContext(context);
	}
	
	/**
	 * 获取Spring Sercurity验证的当前用户
	 */
	public static User getCurrentUser(){
		return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	/**
	 * 判断数组中是否存在指定属性propertyName且值为propertyValue的元素.
	 * @param array 数组
	 * @param propertyName 数组元素的属性名
	 * @param propertyValues 数组元素属性的值，可以为多个值
	 * @return 必须propertyValues中的每个值都找到匹配对象才返回true，否则返回false
	 */
	public static boolean isPropertyValuesInArray(Object[] array, String propertyName, Object... propertyValues) {
		Assert.notNull(array);
		Assert.hasLength(propertyName);
		Assert.notEmpty(propertyValues);
		int matched = 0;
		for (Object object : array) {
			Object value = ReflectionUtils.getPropertyValue(object, propertyName);
			for (Object propertyValue : propertyValues) {
				if (value == propertyValue || (value != null && value.equals(propertyValue))) {
					matched++;
					break;
				}
			}
		}
		return matched == propertyValues.length;
	}
	
	/**
	 * 从数组中查找出第一个propertyName=propertyValue的对象
	 * @param <T> 数组类型
	 * @param array 数组
	 * @param propertyName 属性名称
	 * @param propertyValue 属性值
	 * @return 数组中查找出第一个propertyName=propertyValue的对象
	 */
	public static <T> T getObjectByPropertyFromArray(T[] array, String propertyName, Object propertyValue) {
		Assert.notNull(array);
		Assert.hasLength(propertyName);
		for (T object : array) {
			Object value = ReflectionUtils.getPropertyValue(object, propertyName);
			if (value == propertyValue || (value != null && value.equals(propertyValue))) {
				return object;
			}
		}
		return null;
	}
	
	/**
	 * 检验数组中每个元素是否都<strong>反射相等</strong>.
	 * <p>
	 * 忽略数组顺序.
	 * 如果存在Hibernate的Proxy对象或是PersistentCollection，则仅比较其id值
	 * </p>
	 * @param lhs 待比较的左数组
	 * @param rhs 待比较的右数组
	 */
	public static void assertReflectionArrayEquals(Object lhs, Object rhs) {
		assertReflectionArrayEquals(lhs, rhs, new HashMap<RoadNode, RoadNode>());
	}
	
	/**
	 * 检验数组中每个元素是否都<strong>反射相等</strong>.
	 * <p>
	 * 忽略数组顺序.
	 * 如果存在Hibernate的Proxy对象或是PersistentCollection，则仅比较其id值
	 * </p>
	 * @param lhs 待比较的左数组
	 * @param rhs 待比较的右数组
	 * @param road 已处理过的对象Map，必须是{@link java.util.HashMap}
	 */
	private static void assertReflectionArrayEquals(Object lhs, Object rhs, HashMap<RoadNode, RoadNode> road) {
		if (lhs == rhs) {
			return;
		} 
		else if (lhs == null || rhs == null) {
			throw new AssertionError("'" + lhs + "' not equals '" + rhs + "'");
		}
		else if (!lhs.getClass().isArray() || !rhs.getClass().isArray()) {
			throw new AssertionError("Specified argument(s) is not array: '" + lhs.getClass() + "', '" + rhs.getClass() + "'");
		} 
		else if (!lhs.getClass().isAssignableFrom(rhs.getClass())) {
			throw new AssertionError("Type '" + lhs.getClass() + "' is not match type '" + rhs.getClass() + "'");
		}
		
		// 加入road，避免循环判断
		RoadNode roadNode = new RoadNode(lhs, rhs);
		if (road.containsKey(roadNode)) {
			AssertionError error = road.get(roadNode).getError();
			if (error != null) {
				throw error;
			} else {
				return;
			}
		}
		else {
			road.put(roadNode, roadNode);
		}
		
		int lsize = Array.getLength(lhs);
		int rsize = Array.getLength(rhs);
		if (lsize != rsize) {
			throw new AssertionError("Length of value '" + lhs + "'(length:" + lsize + ") is not equals another '" + rhs + "'(length:" + rsize + ")");
		}
		
		// 开始比较
		List<Object> list = new LinkedList<Object>();
		for (int i = 0; i < lsize; i++) {
			Object l = Array.get(lhs, i);
			list.add(l);
		}
		
		for (int i = 0; i < rsize; i++) {
			Object r = Array.get(rhs, i);
			boolean equals = false;
			Map<Object, AssertionError> errors = new LinkedHashMap<Object, AssertionError>(); 
			Iterator<?> iter = list.iterator();
			while (iter.hasNext()) {
				Object l = iter.next();
				try {
					assertReflectionEquals(l, r, road);
					equals = true;
					iter.remove();
					break;
				} catch (AssertionError err) {
					errors.put(l, err);
				}
			}
			if (!equals) {
				StringBuilder errorMessage = new StringBuilder();
				errorMessage.append("Element '").append(r).append("' did not match any of '").append(arrayToString(lhs)).append("'")
						.append("\nThe nested AssertionError is: \n");
				for (Map.Entry<Object, AssertionError> entry : errors.entrySet()) {
					errorMessage.append("* ").append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
				}
				throw new AssertionError(errorMessage.toString());
			}
		}
	}
	
	/**
	 * 通过反射比较两个对象是否相等.
	 * <p>
	 * 如果存在Hibernate的Proxy对象或是PersistentCollection，则仅比较其id值
	 * </p>
	 * @param lhs 待比较左对象
	 * @param rhs 待比较右对象
	 */
	public static void assertReflectionEquals(Object lhs, Object rhs) {
		assertReflectionEquals(lhs, rhs, new HashMap<RoadNode, RoadNode>());
	}
	
	/**
	 * 通过反射比较两个对象是否相等.
	 * <p>
	 * 如果存在Hibernate的Proxy对象或是PersistentCollection，则仅比较其id值
	 * </p>
	 * @param lhs 待比较左对象
	 * @param rhs 待比较右对象
	 * @param road 已处理过的对象Map，必须是{@link java.util.HashMap}
	 */
	private static void assertReflectionEquals(Object lhs, Object rhs, HashMap<RoadNode, RoadNode> road) {
		
		if (lhs == rhs) {
			return;
		}
		else if (lhs == null || rhs == null) {
			throw new AssertionError("'" + lhs + "' not equals '" + rhs + "'");
		}
		else if (!lhs.getClass().equals(rhs.getClass())) {
			throw new AssertionError("Type '" + lhs.getClass() + "' is not match type '" + rhs.getClass() + "'");
		}
		
		// 加入road，避免循环判断
		RoadNode roadNode = new RoadNode(lhs, rhs);
		if (road.containsKey(roadNode)) {
			AssertionError error = road.get(roadNode).getError();
			if (error != null) {
				throw error;
			} else {
				return;
			}
		}
		else {
			logger.debug("Comparing l: {}, r: {}, node: {}", new Object[] { lhs, rhs, roadNode });
			road.put(roadNode, roadNode);
		}
		
		AssertionError error = null;
		Class<?> clazz = lhs.getClass();
		if (isTypeMatches(clazz, BASIC_TYPES)) {
			// Basic类型的情况
			if (!lhs.equals(rhs)) {
				error = new AssertionError("'" + lhs + "' not equals '" + rhs + "'");
				roadNode.setError(error);
				throw error;
			}
		} 
		else if (Collection.class.isAssignableFrom(clazz)) {
			// Collection的情况
			assertReflectionArrayEquals(((Collection<?>)lhs).toArray(), ((Collection<?>)rhs).toArray(), road);
			
		} 
		else if (clazz.isArray()) {
			// Array的情况
			assertReflectionArrayEquals(lhs, rhs, road);
			
		} 
		else {
			// 是Bean的情况
			PropertyDescriptor[] descriptors = ReflectionUtils.getPropertyDescriptors(clazz);
			for (PropertyDescriptor descriptor : descriptors) {
				try {
					Method readMethod = descriptor.getReadMethod();
					Method writeMethod = descriptor.getWriteMethod();
					if (writeMethod == null || readMethod == null) {
						continue;
					}
						
					Object lhsValue = ReflectionUtils.invokeMethod(lhs, readMethod);
					Object rhsValue = ReflectionUtils.invokeMethod(rhs, readMethod);
					
					// 处理Hibernate代理对象，如果Session不存在则打印warnning并忽略
					try {
						lhsValue = convertProxyToPojo(lhsValue);
						rhsValue = convertProxyToPojo(rhsValue);
					} catch (HibernateException ex) {
						logger.warn("Ignored a hibernate proxy exception: {}", ex.getMessage());
						continue;
					}
					
					assertReflectionEquals(lhsValue, rhsValue, road);
				} catch (AssertionError err) {
					error = new AssertionError("Property '" + descriptor.getName() + "' compare failed: " + err.getMessage());
					roadNode.setError(error);
					throw error;
				}
			}
		}
	}
	
	/**
	 * 用于标识已处理过的对象对的节点.
	 * 
	 * @author GuoLin
	 *
	 */
	private static class RoadNode {
		
		private int hashCode;
		
		private AssertionError error;
		
		RoadNode(Object l, Object r) {
			// XXX 加法冲突几率较大，也有可能溢出
			this.hashCode = System.identityHashCode(l) + System.identityHashCode(r);
		}
		
		public AssertionError getError() {
			return error;
		}

		public void setError(AssertionError error) {
			this.error = error;
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof RoadNode) ||o == null) {
				return false;
			}
			return hashCode == ((RoadNode)o).hashCode;
		}
		
		@Override
		public int hashCode() {
			return hashCode;
		}

		@Override
		public String toString() {
			return new StringBuilder("[RoadNode: hashCode=").append(hashCode)
				.append(", error=").append(error != null ? true : false)
				.append("]").toString();
		}
	}
	
	/**
	 * 初始化Hibernate Proxy，并将之转化为一个新的对象，新的对象中仅包含ID字段.
	 * @param object HibernateProxy对象
	 * @return 如果为单个HibernateProxy对象返回一个仅包含ID字段内容的POJO对象，
	 * 如果为PersistentCollection对象则返回POJO对象列表，每个POJO对象中包含ID字段内容，
	 * 否则返回对象本身
	 */
	@SuppressWarnings("unchecked")
	private static Object convertProxyToPojo(Object object) {
		if (object == null) {
			return null;
		} 
		else if (object instanceof PersistentCollection) {
			Hibernate.initialize(object);
			if (object instanceof Collection) {
				Collection<?> persistentCollection = (Collection<?>)object;
				@SuppressWarnings("rawtypes")
				Collection results = instantiateCollection(((Collection)object).getClass());
				for (Object element : persistentCollection) {
					results.add(cloneEntityWithIdOnly(element));
				}
				return results;
			} else {
				throw new UnsupportedOperationException("Type " + object.getClass().toString() + " not supported yet.");
			}
		} 
		else if (object instanceof HibernateProxy || isManagedEntity(object)) {
			return cloneEntityWithIdOnly(object);
		}
		else {
			return object;
		}
	}
	
	/**
	 * 检测一个对象是否是Hibernate的受管状态实体.
	 * <p>
	 * 此检测有效的前提是必须通过{@link #openSessionInTest(SessionFactory)}方法开启Session,
	 * 否则总是返回false.
	 * </p>
	 * @param object 待检测对象
	 * @return 如果是受管状态返回true，否则返回false
	 */
	private static boolean isManagedEntity(Object object) {
		EventSource eventSource = (EventSource) getCurrentSession();
		if (eventSource == null) {
			return false;
		}
		EntityEntry entry = eventSource.getPersistenceContext().getEntry(object);
		return entry != null && Status.MANAGED.equals(entry.getStatus());
	}

	/**
	 * 根据提供的类型实例化一个集合类.
	 * @param <T> 类型
	 * @param clazz 类型
	 * @return 实例化完成后的集合类
	 * @throws UnsupportedOperationException 如果提供的类型不是集合类型
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <T> T instantiateCollection(Class<T> clazz) throws UnsupportedOperationException {
		if (SortedSet.class.isAssignableFrom(clazz)) {
			return (T) new TreeSet();
		} else if (Set.class.isAssignableFrom(clazz)) {
			return (T) new HashSet();
		} else if (List.class.isAssignableFrom(clazz)) {
			return (T) new ArrayList();
		} else {
			throw new UnsupportedOperationException("Can't convert object to any supported Collection.");
		}
	}
	
	/**
	 * 初始化Hibernate Proxy，并将之转化为一个新的对象，新的对象中仅包含ID字段.
	 * @param object HibernateProxy对象
	 * @return 新的POJO，仅包含ID属性
	 */
	private static Object cloneEntityWithIdOnly(Object object) {
		Class<?> persistentClass;
		String idPropertyName;
		Serializable id;
		
		if (object instanceof HibernateProxy) {
			LazyInitializer initializer = ((HibernateProxy)object).getHibernateLazyInitializer();
			persistentClass = initializer.getPersistentClass();
			ClassMetadata metadata = initializer.getSession().getFactory().getClassMetadata(persistentClass);
			idPropertyName = metadata.getIdentifierPropertyName();
			id = initializer.getIdentifier();
		} 
		else if (isManagedEntity(object)) {
			SessionImplementor sessionImpl = (SessionImplementor)getCurrentSession();
			if (sessionImpl == null) {
				throw new HibernateException("Session not open.");
			}
			EntityEntry entry = sessionImpl.getPersistenceContext().getEntry(object);
			ClassMetadata metadata = entry.getPersister().getClassMetadata();
			idPropertyName = metadata.getIdentifierPropertyName();
			id = entry.getId();
			persistentClass = metadata.getMappedClass(EntityMode.POJO);
		} 
		else {
			return object;
		}
		
		// 新建POJO对象
		Object result = ReflectionUtils.instantiate(persistentClass);
		ReflectionUtils.setFieldValue(result, idPropertyName, id);
		
		return result;
	}
	
	private static Session getCurrentSession() {
		SessionFactory sessionFactory = currentSessionFactory.get();
		if (sessionFactory == null) {
			return null;
		}
		SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
		if (sessionHolder == null) {
			return null;
		}
		return sessionHolder.getSession();
	}
	
	/**
	 * 检测类型是否属于类型数组中的任意类型
	 * @param clazz 待检测类型
	 * @param types 类型数组
	 * @return 如果匹配则返回true，否则返回false
	 */
	private static boolean isTypeMatches(Class<?> clazz, Class<?>[] types) {
		for (Class<?> type : types) {
			if (type.isAssignableFrom(clazz)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 检查一个对象是否是Hibernate的Proxy.
	 * @param obj 任意对象
	 * @return 如果是HibernateProxy则返回true，否则返回false
	 */
	public static boolean isHibernateProxy(Object obj) {
		return (obj instanceof HibernateProxy || obj instanceof PersistentCollection);
	}
	
	/**
	 * 新建一个集合.
	 * @param <E> 元素类型
	 * @param <C> 集合类型
	 * @param clazz 集合类型
	 * @param elements 元素列表
	 * @return 新建后的集合
	 */
	public static <E, C extends Collection<E>> C newCollection(Class<C> clazz, E... elements) {
		C result = (C)ReflectionUtils.instantiate(clazz);
		for (E element : elements) {
			result.add(element);
		}
		return result;
	}
	
	/**
	 * 数组转换到字符串.
	 * @param array 待转换数组
	 * @return 转换后的字符串，如果非数组，则直接调用其toString()方法
	 */
	public static String arrayToString(Object array) {
		if (array == null) {
			return "null";
		} 
		else if (Object[].class.isAssignableFrom(array.getClass())) {
			return Arrays.toString((Object[]) array);
		}
		else if (array.getClass().isArray()) {
			int length = Array.getLength(array);
	        if (length == 0) { 
	        	return "[]";
	        }
	        StringBuilder buf = new StringBuilder();
	        buf.append('[').append(Array.get(array, 0));
	        for (int i = 1; i < length; i++) {
	        	buf.append(", ").append(Array.get(array, i));
	        }
	        return buf.append(']').toString();
		} 
		else {
			return array.toString();
		}
	}
	
	/**
	 * 检查jMock中传入参数的<code>toString()</code>方法结果
	 * 是否与预期对象的<code>toString()</code>方法相同.
	 * <p>
	 * XXX 未来应该考虑切换成{@link #assertReflectionEquals(Object, Object)}的代理</p>
	 * @param <T> 传入参数的类型
	 * @param object 传入参数
	 * @return 用于{@link Expectations#with(Matcher)}方法
	 */
    public static <T> Matcher<T> sameString(T object) {
        return new IsToStringEqualsMatcher<T>(object);
    }
    
    private static class IsToStringEqualsMatcher<T> extends BaseMatcher<T> {
        
        private final T object;

        private IsToStringEqualsMatcher(T object) {
            this.object = object;
        }

        @Override
        public boolean matches(Object object) {
            return this.object.toString().equals(object.toString());
        }

        @Override
        public void describeTo(Description description) {
            // TODO 
        }
        
    }
    
}
