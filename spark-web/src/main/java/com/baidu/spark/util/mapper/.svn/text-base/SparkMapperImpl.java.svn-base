package com.baidu.spark.util.mapper;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.collection.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.spark.util.IdentityHashSet;
import com.baidu.spark.util.ReflectionUtils;

import edu.emory.mathcs.backport.java.util.TreeMap;

/**
 * Spark映射器实现.
 * TODO 尚未完成destination存在的情况，尚不支持Map映射
 * TODO 允许设置参数，是的分别按照getter/setter来存取还是按照field存取
 * XXX 代码有待优化
 * 
 * @author GuoLin
 *
 */
public class SparkMapperImpl implements SparkMapper {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	/** 基本类型数组. */
	protected static final Class<?>[] BASIC_TYPES = new Class<?>[] {
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
	
	protected static final Class<?>[] COLLECTION_TYPES = new Class<?>[] {
			ArrayList.class
	};
	
	/** 是否映射null值. XXX 对于当前来说此配置没有意义，未来将会把所有配置都移动到MappingConfig中 */
	private static boolean MAP_NULL = false;
	
	public <T> T clone(T source) {
		return clone(source, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T clone(T source, MappingCallback callback) {
		// 初始化配置实例
		MappingConfig config = new MappingConfig(callback);
		
		// 克隆根对象
		T destination = (T) clone(null, null, source, null, config);
		
		// 根据待处理队列内容完成关联对象的克隆
		Queue<MappingInvoker> queue = config.getProcessingQueue();
		while (queue.size() > 0) {
			MappingInvoker invoker = queue.poll();
			invoker.invoke();
		}
		return destination;
	}
	
	@SuppressWarnings("unchecked")
	protected Object clone(Object sourceParent, Object destinationParent, final Object source, Object destination, final MappingConfig config) {
		if (config.getCallback() != null) {
			Object result = config.getCallback().callback(sourceParent, destinationParent, source, destination, config);
			if (result != MappingCallback.CONTINUE) {
				return result;
			}
		}
		
		if (source == null) {
			return null;
		}
		
		if (config.existsOnRoad(source)) {
			return config.getExistsDestinationOnRoad(source);
		}
		
		Class<?> clazz = getRealType(source);
		
		logger.debug("Current path '{}'", config.getPath());
		
		// 处理basic type
		if (isBasicType(clazz) || source == null) {
			return source;
			
		} 
		// 处理array
		else if (clazz.isArray()) {
			Object array = instantiate(source, destination);
			config.addToRoad(source, array);
			for (int i = 0; i < Array.getLength(array); i++) {
				Object destIndexValue = clone(source, destination, Array.get(source, i), null, config);
				Array.set(array, i, destIndexValue);
			}
			return array;
			
		} 
		// 处理collection
		else if (Collection.class.isAssignableFrom(clazz)) {
			Collection collection = (Collection)instantiate(source, destination);
			config.addToRoad(source, collection);
			for (Object element : (Collection<?>)source) {
				Object destIndexValue = clone(source, destination, element, null, config);
				collection.add(destIndexValue);
			}
			return collection;

		} 
		// 处理简单bean
		else {
			final Object bean = instantiate(source, destination);
			config.addToRoad(source, bean);
			
			// 反射拿到所有的属性并遍历
			PropertyDescriptor[] descriptors = ReflectionUtils.getPropertyDescriptors(clazz);
			for (final PropertyDescriptor descriptor : descriptors) {
				
				// 从source取出值
				final Method readMethod = descriptor.getReadMethod();
				final Method writeMethod = descriptor.getWriteMethod();
				if (readMethod == null || writeMethod == null) {
					continue;
				}
				final Object sourceFieldValue = ReflectionUtils.invokeMethod(source, readMethod);
				
				// 处理map null逻辑
				if (sourceFieldValue == null && !MAP_NULL) {
					continue;
				}
				
				// 获取类，如果值为null则根据property类型取出
				Class<?> fieldClass = sourceFieldValue == null ? descriptor.getPropertyType() : sourceFieldValue.getClass();
				if (logger.isTraceEnabled()) {
					logger.trace("Got value '{}'({}) from property '{}'", 
							new Object[] { sourceFieldValue, fieldClass.getName(), descriptor.getName() });
				}
				
				// 将待处理的属性入队列，以实现广度优先
				MappingInvoker invoker = new MappingInvoker() {
					@Override
					public void invoke() {
						Object destFieldValue = SparkMapperImpl.this.clone(source, bean, sourceFieldValue, null, new MappingConfig(descriptor, config));
						ReflectionUtils.invokeMethod(bean, writeMethod, destFieldValue);
					}
				};
				config.addToProcessingQueue(invoker);
			}
			return bean;
		}
	}
	
	/**
	 * 实例化一个和source类型相同的对象并返回.
	 * @param source 源对象
	 * @param destination 目标对象，如果不存在则输入null
	 * @return 如果不为null返回目标对象本身，否则返回实例化完成后的新对象
	 */
	private static Object instantiate(Object source, Object destination) {
		if (destination == null) {
			Class<?> type = getRealType(source);
			if (type.isArray()) {
				return Array.newInstance(type.getComponentType(), Array.getLength(source));
			}
			else {
				return ReflectionUtils.instantiate(type);
			}
		} else {
			return destination;
		}
	}
	
	/**
	 * 获取真实类型.
	 * <p>
	 * 这里会自动识别Hibernate代理，并返回其实体原始类型.
	 * </p>
	 * @param object 待检测对象
	 * @return 如果是Hibernate代理对象则返回实际实体类型，否则返回对象{@link #getClass()}方法
	 */
	private static Class<?> getRealType(Object object) {
		if (object instanceof HibernateProxy) {
			return ((HibernateProxy) object).getHibernateLazyInitializer().getPersistentClass();
		}
		else if (object instanceof PersistentCollection) {
			if (object instanceof List) {
				return ArrayList.class;
			} else if (object instanceof SortedSet) {
				return TreeSet.class;
			} else if (object instanceof Set) {
				// XXX 解决当复写了hashCode方法后，因为广度优先第一次映射到的对象因为所有属性都为null所以hashCode也将相等
				return IdentityHashSet.class;
			} else if (object instanceof SortedMap) {
				return TreeMap.class;
			} else if (object instanceof Map) {
				return HashMap.class;
			} else {
				throw new UnsupportedOperationException("Specified type '" + object.getClass().getName() + "' not supported yet.");
			}
		}
		else {
			return object.getClass();
		}
	}

	/**
	 * 判断给出的类型是否为基本类型.
	 * @param type 类型
	 * @return 如果是基本类型则返回true，否则返回false
	 */
	protected static boolean isBasicType(Class<?> type) {
		return isTypeMatches(type, BASIC_TYPES);
	}
	
	/**
	 * 判断一个类型是否是类型列表中的任一类型的子类.
	 * @param clazz 待检测类型
	 * @param types 类型列表
	 * @return 如果待检测类型是类型列表子类返回true，否则返回false
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
	 * 映射触发器.
	 * <p>
	 * 用于包装待执行逻辑的抽象类.
	 * 待执行逻辑将被加入到待处理队列中.
	 * </p>
	 * 
	 * @author GuoLin
	 *
	 */
	protected static abstract class MappingInvoker {
		public abstract void invoke();
	}
	
	/**
	 * 映射配置类.
	 * <p>
	 * 用于存储与映射相关的一些配置信息.
	 * </p>
	 * 
	 * @author GuoLin
	 *
	 */
	protected static class MappingConfig {
		
		/** 映射路径. 反映出当前映射对象层级关系，例如：user.group.name */
		private final String path;
		
		/** 回调类. 用于存储客户定制的回调逻辑. */
		private final MappingCallback callback;
		
		/** 待处理逻辑序列，实现广度优先. */
		private final Queue<MappingInvoker> processingQueue;
		
		/** 当前映射属性描述. */
		private final PropertyDescriptor propertyDescriptor;
		
		/** 已处理对象序列，记录已经映射过的对象Map. */
		private final Map<Object, Object> road;
		
		/**
		 * 根据父配置创建子配置的构造器.
		 * @param propertyDescriptor 当前待映射的属性描述
		 * @param parent 父配置实例
		 */
		public MappingConfig(PropertyDescriptor propertyDescriptor, MappingConfig parent) {
			if (parent == null) {
				throw new IllegalArgumentException("Argument parent must not be null.");
			}
			this.propertyDescriptor = propertyDescriptor;
			this.callback = parent.callback;
			this.processingQueue = parent.processingQueue;
			if (parent.path != null && parent.path.length() > 0) {
				this.path = parent.path + "." + propertyDescriptor.getName();
			} else {
				this.path = propertyDescriptor.getName();
			}
			this.road = parent.road;
		}
		
		/**
		 * 用于创建根配置的构造器.
		 * @param callback 回调函数
		 */
		public MappingConfig(MappingCallback callback) {
			super();
			this.path = "";
			this.callback = callback;
			this.processingQueue = new LinkedList<MappingInvoker>();
			this.propertyDescriptor = null;
			this.road = new IdentityHashMap<Object, Object>();
		}
		
		/**
		 * 检测一个对象是否已经处理过.
		 * @param roadObject 待检测对象
		 * @return 如果已经处理过则返回true，否则返回false
		 */
		public boolean existsOnRoad(Object roadObject) {
			return road.containsKey(roadObject);
		}
		
		/**
		 * 添加一个已处理对象.
		 * @param roadObject 处理完成的源对象
		 * @param destination  处理完成后的目标对象
		 * @return 如果源对象已经被处理过则返回false，否则返回true
		 */
		public boolean addToRoad(Object roadObject, Object destination) {
			if (road.containsKey(roadObject)) {
				return false;
			}
			road.put(roadObject, destination);
			return true;
		}
		
		/**
		 * 根据已处理完成的源对象获取到对应的处理完成的目标对象.
		 * @param roadObject 已处理过的源对象
		 * @return 上次处理完成的目标对象，如果源对象未处理过则返回null
		 */
		public Object getExistsDestinationOnRoad(Object roadObject) {
			return road.get(roadObject);
		}
		
		/**
		 * 获取当前处理路径.
		 * @return 当前处理路径
		 */
		public String getPath() {
			return path;
		}

		/**
		 * 获取用户设定的回调逻辑实例.
		 * @return 回调逻辑实例
		 */
		public MappingCallback getCallback() {
			return callback;
		}

		/**
		 * 获取待处理逻辑队列.
		 * @return 待处理逻辑队列
		 */
		public Queue<MappingInvoker> getProcessingQueue() {
			return processingQueue;
		}
		
		/**
		 * 添加一个待处理逻辑进入队列.
		 * @param mappingInvoker 待处理逻辑实例
		 */
		public void addToProcessingQueue(MappingInvoker mappingInvoker) {
			processingQueue.add(mappingInvoker);
		}
		
		/**
		 * 获取当前待处理属性的类型.
		 * @return 待处理属性类型
		 */
		public Class<?> getPropertyType() {
			return propertyDescriptor.getPropertyType();
		}
		
		/**
		 * 获取当前待处理属性的名称.
		 * @return 待处理属性名称
		 */
		public String getPropertyName() {
			return propertyDescriptor.getName();
		}
		
		/**
		 * 获取当前待处理属性的读方法(getter).
		 * @return 待处理属性读方法
		 */
		public Method getReadMethod() {
			return propertyDescriptor.getReadMethod();
		}
		
		/**
		 * 获取当前待处理属性的写方法(setter).
		 * @return 待处理属性写方法
		 */
		public Method getWriteMethod() {
			return propertyDescriptor.getWriteMethod();
		}

		/**
		 * 获取当前待处理属性.
		 * @return 待处理属性
		 */
		public PropertyDescriptor getPropertyDescriptor() {
			return propertyDescriptor;
		}
		
	}
	
}
