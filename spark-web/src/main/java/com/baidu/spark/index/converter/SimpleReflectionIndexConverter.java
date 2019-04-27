package com.baidu.spark.index.converter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.springframework.util.Assert;

import com.baidu.spark.exception.SparkRuntimeException;
import com.baidu.spark.model.QueryConditionVO;
import com.baidu.spark.model.QuerySortVO;
import com.baidu.spark.model.QueryVO;
import com.baidu.spark.model.QueryConditionVO.QueryOperationType;
import com.baidu.spark.util.LuceneUtils;
import com.baidu.spark.util.ReflectionUtils;


/**
 * 由反射来进行对象与索引之间的转化。
 * 使用时，只需要指定需要进行反射的字段，由ReflectionIndexConverter自动进行索引和对象之间的转换
 * @author zhangjing_pe
 * @author shixiaolei
 *
 * @param <T> 被索引的类型
 */
public abstract class SimpleReflectionIndexConverter<T> extends AbstractIndexConverter<T> {
	public SimpleReflectionIndexConverter(){};
	/**
	 * 设置需要进行反射的属性名
	 * @return 属性名的数组
	 */
	protected abstract String[] getIndexFields();

	/**
	 * 根据对象，及定义的对象字段，反射获取lucene的document
	 * Date类型字段通过特定的格式进行存储
	 */
	@Override
	public Document getIndexableDocument(T t) {
		Assert.notNull(getIndexFields());
		Assert.notNull(t);
		Document document = new Document();
		for (String fieldName : getIndexFields()) {
            Object fieldValue = ReflectionUtils.getPropertyValue(t, fieldName);
            Class<?> clazz = getReflectFieldType(t, fieldName);
            document.add(getFieldFromObject(fieldName, fieldValue,clazz));
        }
		return document;
	}
	/**
	 * 根据lucene document及对象实例，反射出对象的属性
	 * 注意：date的字符串通过特定的格式转为属性。其他类型的属性通过String类型的构造器进行构造。若没有String类型的构造器，则会设置失败
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T getOriginObj(Document document) {
		Assert.notNull(document);
		Assert.notNull(getIndexFields());
		T t = generatePojo();
		String[] indexFields = getIndexFields();
        for (String fieldName : indexFields) {
        	Fieldable field = document.getFieldable(fieldName);
        	if(field instanceof NumericField){
        		return (T)((NumericField)field).getNumericValue();
        	}
        	String fieldValue = document.get(fieldName);
        	Object value = getObjectFromFieldValue(t, fieldName, fieldValue);
            ReflectionUtils.setFieldValue(t, fieldName, value);
        }
        return t;
	}
	
	/**
	 * 将索引值转化为实际的对象
	 * 使用特定的日期格式对日期索引值做转换（yyyyMMddHHmm）
	 * @param t 业务对象
	 * @param fieldName 索引字段名
	 * @param fieldValue 索引值
	 * @return 索引值对应的对象
	 */
	protected Object getObjectFromFieldValue(T t,String fieldName,String fieldValue){
		Assert.notNull(t);
		Assert.notNull(fieldName);
		Assert.notNull(fieldValue);
		Object value = null;
		Class<?> fieldType = getReflectFieldType(t, fieldName);
		if (Date.class.isAssignableFrom(fieldType)) {
			try {
				if(StringUtils.isNotEmpty(fieldValue)){
					value = DateTools.stringToDate(fieldValue);
				}
			} catch (ParseException e) {
				logger.error(fieldValue, e);
				throw new SparkRuntimeException("global.exception.indexException");
			}
		} else {
			value = getFieldObjectByQueryString(fieldValue, fieldType);
		}
		return value;
	}

	/**反射获取类中字段的类型
	 * @param t
	 * @param fieldName
	 * @return
	 */
	protected Class<?> getReflectFieldType(Object t, String fieldName) {
		Assert.notNull(t);
		Assert.notNull(fieldName);
		java.lang.reflect.Field field = ReflectionUtils.getDeclaredField(t,
				fieldName);
		Class<?> fieldType = field.getType();
		return fieldType;
	}
	
	/**
	 * 将查询中的值转化为目标类的对象
	 * 如果参数中有“,”，则转为对象的数组
	 * @param queryValue
	 * @param targetClass
	 * @return
	 */
	protected Object getObjectFromQueryValue(String queryValue,Class<?> targetClass){
		Assert.notNull(targetClass);
		boolean isArray = false;
		if(queryValue!=null&&queryValue.indexOf(',')>0){
			isArray = true;
			
		}
		if (!isArray) {
			return getFieldObjectByQueryString(queryValue, targetClass);
		} else {
			List<Object> retList = new ArrayList<Object>();
			String[] values = StringUtils.split(queryValue,",");
			for(String value : values){
				retList.add(getFieldObjectByQueryString(value, targetClass));
			}
			return retList;
		}
	}

	
	@Override
	public Query getQueryFromVo(final QueryVO queryVo){
		Assert.notNull(getIndexFields());
		List<QueryConditionVO> queryParamList = null;
		if(queryVo != null){
			queryParamList = queryVo.getQueryConditionList();
		}
		if(queryParamList == null || queryParamList.isEmpty()){
			return new BooleanQuery();
		}
		T t = generatePojo();
		Map<String, BooleanQuery> positiveQueryMap = new HashMap<String, BooleanQuery>();
		Map<String, BooleanQuery> negativeQueryMap = new HashMap<String, BooleanQuery>();
		for (QueryConditionVO queryParam : queryParamList) {
			if(ArrayUtils.contains(getIndexFields(), queryParam.getFieldName())){
				String field = queryParam.getFieldName();
				Class<?> fieldClazz = getReflectFieldType(t, field);
				Object value = getObjectFromQueryValue(queryParam.getValue(), fieldClazz);
				groupQueryByField(field, queryParam.getOperationType(), value, positiveQueryMap, negativeQueryMap);
			}
		}
		return generateBooleanQueryWithFieldGroups(positiveQueryMap, negativeQueryMap);
	}
	
	/**
	 * 根据查询属性名,将查询条件分组.
	 * <p>
	 * 规则是：
	 * 不同属性分组之间，是"与"的关系;
	 * 同一属性的不同条件之间, "肯定型条件(如：是/包含/大于/小于/介于)"之间,是"或"的关系; 
	 * "否定型条件(如：不是)"之间,是"与"的关系;
	 * 肯定与否定条件之间,也是"与"的关系 
	 * </p>
	 * @param field 查询属性名
	 * @param type 查询条件按类型
	 * @param value 查询值
	 * @param positiveQueryMap 肯定型查询条件的Map,其键为属性名，值为该属性的查询条件按上述规则的拼接
	 * @param negativeQueryMap 否定型查询条件的Map,其键为属性名，值为该属性的查询条件按上述规则的拼接
	 */
	protected void groupQueryByField(String field,  QueryOperationType type, Object value, 
			Map<String, BooleanQuery> positiveQueryMap, Map<String, BooleanQuery> negativeQueryMap){
		BooleanQuery fieldQuery = null;
		Query singleQuery = getLuceneQuery(type, field, value);
		if(type.isPositive()){
			fieldQuery = positiveQueryMap.get(field);
			if(fieldQuery==null){
				fieldQuery = new BooleanQuery();
				positiveQueryMap.put(field, fieldQuery);
			} 
			fieldQuery.add(singleQuery, BooleanClause.Occur.SHOULD);
			 
		}else{
			fieldQuery = negativeQueryMap.get(field);
			if(fieldQuery==null){
				fieldQuery = new BooleanQuery();
				negativeQueryMap.put(field, fieldQuery);
			}
			fieldQuery.add(singleQuery, BooleanClause.Occur.MUST);
		}
	}
	
	/**
	 * 根据按属性名分组的查询条件，拼接成整体查询条件
	 * @param positiveQueryMap 肯定型查询条件的Map,其键为属性名，值为该属性的查询条件按上述规则的拼接
	 * @param negativeQueryMap 否定型查询条件的Map,其键为属性名，值为该属性的查询条件按上述规则的拼接
	 * @return 整体查询条件
	 */
	protected BooleanQuery generateBooleanQueryWithFieldGroups(
			Map<String, BooleanQuery> positiveQueryMap, Map<String, BooleanQuery> negativeQueryMap) {
		BooleanQuery retQuery = new BooleanQuery();
		for(BooleanQuery fieldQuery : positiveQueryMap.values()){
			if(fieldQuery.clauses()!=null && !fieldQuery.clauses().isEmpty()){
				retQuery.add(fieldQuery, BooleanClause.Occur.MUST);
			}
		}
		for(BooleanQuery fieldQuery : negativeQueryMap.values()){
			if(fieldQuery.clauses()!=null && !fieldQuery.clauses().isEmpty()){
				retQuery.add(fieldQuery, BooleanClause.Occur.MUST);
			}
		}
		return retQuery;
	}
	
	@Override
	public List<SortField> getSortFromVo(final QueryVO queryVo) {
		Assert.notNull(getIndexFields());
		List<QuerySortVO> sorts = null;
		if(queryVo != null){
			sorts = queryVo.getQuerySortList();
		}
		List<SortField> sortFields = new ArrayList<SortField>();
		if(sorts == null || sorts.isEmpty()){
			return sortFields;
		}
		T pojo = generatePojo();
		for(QuerySortVO sort:sorts){
			if(ArrayUtils.contains(getIndexFields(), sort.getFieldName())) {
				SortField field = getSortByReflection(pojo, sort);
				sortFields.add(field);
			}
		}
		return sortFields;
	}

	/**根据sort中定义的字段名，用反射的方法获取字段的类型，生成需要的索引排序字段对象
	 * @param pojo
	 * @param sort
	 * @return
	 */
	protected SortField getSortByReflection(T pojo, QuerySortVO sort) {
		return new SortField(sort.getFieldName(),
				LuceneUtils.getLuceneSortType(getReflectFieldType(
						pojo, sort.getFieldName())),sort.isDesc());
	}

	@Override
	public Object getKeyFieldValue(T obj){
		Assert.notNull(obj);
		Object o = ReflectionUtils.getPropertyValue(obj, getKeyFieldName());
		if(o == null){
			throw new IllegalArgumentException("Object Key value expected!");
		}
		return o;
	}
	
}
