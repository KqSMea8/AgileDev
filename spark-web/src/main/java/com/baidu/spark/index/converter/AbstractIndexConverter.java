package com.baidu.spark.index.converter;

import java.lang.reflect.Constructor;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.WildcardQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.baidu.spark.exception.SparkRuntimeException;
import com.baidu.spark.model.QueryConditionVO;
import com.baidu.spark.util.DateUtils;
import com.baidu.spark.util.LuceneUtils;

/**
 * 由反射来进行对象与索引之间的转化。
 * 使用时，只需要指定需要进行反射的字段，由ReflectionIndexConverter自动进行索引和对象之间的转换
 * @author zhangjing_pe
 * @author shixiaolei
 *
 * @param <T> 被索引的类型
 */
public abstract class AbstractIndexConverter<T> implements IndexConverter<T> {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 根据操作类型和对应的值，返回lucene查询串。方法中根据字段的值，判断进行数字字段query或字符串字段query
	 * @param queryOperationType 操作类型
	 * @param fieldName 字段名
	 * @param fieldValue 字段的对象值
	 * @return Query lucene query对象
	 */
	protected Query getLuceneQuery(QueryConditionVO.QueryOperationType queryOperationType,String fieldName,Object fieldValue){
		Assert.notNull(queryOperationType);
		Assert.notNull(fieldName);
		Assert.notNull(fieldValue);
		if(fieldValue instanceof Number){
			return getNumericQuery(queryOperationType, fieldName, fieldValue);
		}
		return getStringQuery(queryOperationType, fieldName, fieldValue);
	}
	
	/**
	 * 根据类型字段名称、值和操作类型，构造数字类型的查询。根据查询类型，构造不同的数字range查询
	 * @param queryOperationType 操作类型
	 * @param fieldName 字段名
	 * @param fieldValue 字段的对象值
	 * @return Query 对象
	 */
	protected Query getNumericQuery(QueryConditionVO.QueryOperationType queryOperationType,String fieldName,Object fieldValue){
		Assert.notNull(queryOperationType);
		Assert.notNull(fieldName);
		Assert.notNull(fieldValue);
		switch(queryOperationType){
		case EQUALS:
			return LuceneUtils.getNumericEqualsQuery(fieldName,fieldValue);
		case MORETHAN:
			return LuceneUtils.getNumericMoreThanQuery(fieldName,fieldValue);
		case LESSTHAN:
			return LuceneUtils.getNumericLessThanQuery(fieldName,fieldValue);
		case NOTEQUALS:
			BooleanQuery bq = new BooleanQuery();
			bq.add(LuceneUtils.getNumericMoreThanQuery(fieldName,fieldValue), BooleanClause.Occur.SHOULD) ;
			bq.add(LuceneUtils.getNumericLessThanQuery(fieldName,fieldValue), BooleanClause.Occur.SHOULD) ;
			return bq;
		}
		throw new SparkRuntimeException("global.exception.unsupportQueryOperationException");
	}
	/**
	 * 根据类型字段名称、值和操作类型，构造字符串类型的查询。根据查询类型，构造不同的字符串查询。
	 * 传入的date类型，会转化为对当天的周期的区间查询。
	 * @param queryOperationType 操作类型
	 * @param fieldName 字段名
	 * @param fieldValue 字段的对象值
	 * @return Query lucnen query对象。若没有进行匹配，则返回空
	 */
	@SuppressWarnings("unchecked")
	protected Query getStringQuery(QueryConditionVO.QueryOperationType queryOperationType,String fieldName,Object value){
		Assert.notNull(queryOperationType);
		Assert.notNull(fieldName);
		Assert.notNull(value);
		String fieldStringUpperValue = null;
		String fieldStringLowerValue = null;
		boolean isArray = value instanceof List<?>;
		boolean isDate = isArray?((List<?>)value).get(0) instanceof Date:value instanceof Date;
		if(isDate){
			if(queryOperationType.equals(QueryConditionVO.QueryOperationType.BETWEEN)){
				if(!isArray || ((List<?>)value).size() < 2){
					throw new SparkRuntimeException("global.exception.queryParamValueError");
				}
				List<Date> values = (List<Date>)value;
				Date lowerTime = DateUtils.clearTimeOfDate(values.get(0));
				Calendar c = Calendar.getInstance();
				c.setTime(DateUtils.clearTimeOfDate(values.get(1)));
				c.add(Calendar.DAY_OF_YEAR, 1);
				c.add(Calendar.SECOND, -1);
				fieldStringLowerValue = LuceneUtils.getLuceneDateString((Date)lowerTime);
				fieldStringUpperValue = LuceneUtils.getLuceneDateString(c.getTime());
				TermRangeQuery query = new TermRangeQuery(fieldName,fieldStringLowerValue,fieldStringUpperValue,true,true);
				query.setRewriteMethod(MultiTermQuery.CONSTANT_SCORE_FILTER_REWRITE);
				return query;
			}else{
				//对日期range进行特殊处理
				Date dateWithoutTime = DateUtils.clearTimeOfDate((Date)value);
				Calendar c = Calendar.getInstance();
				c.setTime(dateWithoutTime);
				c.add(Calendar.DAY_OF_YEAR, 1);
				c.add(Calendar.SECOND, -1);
				fieldStringLowerValue = LuceneUtils.getLuceneDateString((Date)dateWithoutTime);
				fieldStringUpperValue = LuceneUtils.getLuceneDateString(c.getTime());
				if(queryOperationType.equals(QueryConditionVO.QueryOperationType.EQUALS)){
					TermRangeQuery query = new TermRangeQuery(fieldName,fieldStringLowerValue,fieldStringUpperValue,true,true);
					query.setRewriteMethod(MultiTermQuery.CONSTANT_SCORE_FILTER_REWRITE);
					return query;
				}
			}
		}else{
			fieldStringLowerValue = value.toString();
			fieldStringUpperValue = value.toString();
		}
		
		switch (queryOperationType) {
		case EQUALS:
			return LuceneUtils.getStringEqualsQuery(fieldName,
					fieldStringLowerValue);
		case MORETHAN:
			TermRangeQuery rangeQuery = new TermRangeQuery(fieldName,fieldStringUpperValue,null,(isDate?true:false),false);
			rangeQuery.setRewriteMethod(MultiTermQuery.CONSTANT_SCORE_FILTER_REWRITE);
			return rangeQuery;
		case LESSTHAN:
			rangeQuery = new TermRangeQuery(fieldName,null,fieldStringLowerValue,false,(isDate?true:false));
			rangeQuery.setRewriteMethod(MultiTermQuery.CONSTANT_SCORE_FILTER_REWRITE);
			return rangeQuery;
		case LIKE:
			BooleanQuery query = new BooleanQuery();
			String[] queryKeyWords = fieldStringLowerValue.split(" ");
			if(queryKeyWords != null&& queryKeyWords.length>0){
				for(String keyWord:queryKeyWords){
					Term term = new Term(fieldName, "*"+keyWord+"*");
					WildcardQuery wildCardQuery = new WildcardQuery(term);
					wildCardQuery.setRewriteMethod(MultiTermQuery.CONSTANT_SCORE_FILTER_REWRITE);
					query.add(wildCardQuery, BooleanClause.Occur.MUST);
				}
			}
			if(query.getClauses().length>0){
				return query;
			}else{
				//避免like时查到空结果，全部进行匹配
				Term term = new Term(fieldName, "*");
				WildcardQuery wildCardQuery = new WildcardQuery(term);
				wildCardQuery.setRewriteMethod(MultiTermQuery.CONSTANT_SCORE_FILTER_REWRITE);
				query.add(wildCardQuery, BooleanClause.Occur.MUST);
				return query;
			}
		case NOTEQUALS:
			BooleanQuery bq = new BooleanQuery();
			TermRangeQuery rangeQuery1 = new TermRangeQuery(fieldName,fieldStringUpperValue,null,false,false);
			rangeQuery1.setRewriteMethod(MultiTermQuery.CONSTANT_SCORE_FILTER_REWRITE);
			TermRangeQuery rangeQuery2 = new TermRangeQuery(fieldName,null,fieldStringUpperValue,false,false);
			rangeQuery2.setRewriteMethod(MultiTermQuery.CONSTANT_SCORE_FILTER_REWRITE);
			bq.add(rangeQuery1,BooleanClause.Occur.SHOULD);
			bq.add(rangeQuery2,BooleanClause.Occur.SHOULD);
			return bq;
			
		}
		throw new SparkRuntimeException("global.exception.unsupportQueryOperationException");
	}
	
	/**
	 * 根据字段名和值的类型获得lucene document的field对象
	 * 若字段类型为string，则进行分词索引。否则，不进行分词
	 * 使用特定的日期格式对日期对象进行索引
	 * 对数字类型和日期类型进行空值的特殊处理。若数字为空，则使用最小值，方便进行排序；若日期为空，则使用字符串类型进行构建
	 * @param fieldName索引的字段名
	 * @param fieldValue被索引的值
	 * @param fieldType 被索引的类型
	 * @return lucene的field对象
	 */
	protected Fieldable getFieldFromObject(String fieldName,Object fieldValue,Class<?> fieldType){
		Assert.notNull(fieldType);
		Assert.notNull(fieldName);
		fieldValue = getDocumentFieldObj(fieldValue, fieldType);
		if(Number.class.isAssignableFrom(fieldType)){
			NumericField numberField = new NumericField(fieldName,Field.Store.YES,true);
			if(fieldValue instanceof Long){
				numberField.setLongValue((Long)fieldValue);
			}else if(fieldValue instanceof Integer){
				numberField.setIntValue((Integer)fieldValue);
			}else if(fieldValue instanceof Double){
				numberField.setDoubleValue((Double)fieldValue);
			}else if(fieldValue instanceof Float){
				numberField.setFloatValue((Float)fieldValue);
			}
			return numberField;
		}

        return new Field(fieldName, fieldValue.toString(),
                                Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS);
        //暂时对字符串不做分词索引，使用wildcard拼装查询条件，进行模糊查询
//						fieldValue instanceof String ? Field.Index.ANALYZED_NO_NORMS: Field.Index.NOT_ANALYZED_NO_NORMS);
	}
	
	/**
	 * 根据字段名和值的类型获得lucene document的field对象
	 * 若字段类型为string，则进行分词索引。否则，不进行分词
	 * 使用特定的日期格式对日期对象进行索引
	 * <b>若fieldValue为空，会当作“”来处理，按照字符串来处理，可能会影响到实际的值的获取</b>
	 * @param fieldName 被索引的字段名
	 * @param fieldValue被索引的值
	 * @return lucene的field对象
	 */
	protected Fieldable getFieldFromObject(String fieldName,Object fieldValue){
		if(fieldValue == null){
			fieldValue = "";
		}
		return getFieldFromObject(fieldName, fieldValue,fieldValue.getClass());
	}
	/**
	 * 获取document对象的索引值。对被索引的对象数据进行处理，主要处理number和date类型的空值
	 * @param fieldValue 被索引的值
	 * @param fieldType 被索引值的类型
	 * @return 处理过后的被索引值
	 */
	protected Object getDocumentFieldObj(Object fieldValue,Class<?> fieldType){
		Assert.notNull(fieldType);
		Object retValue = fieldValue;
		if(Number.class.isAssignableFrom(fieldType)){
			if(fieldValue == null){
				retValue = getFieldObjectByQueryString(LuceneUtils.NOT_SET_NUMBER.toString(), fieldType);
			}
		}else if (Date.class.isAssignableFrom(fieldType)){
			if(fieldValue == null){
				retValue = DateTools.dateToString(LuceneUtils.NOT_SET_DATE, DateTools.Resolution.SECOND);
			}else{
				retValue = DateTools.dateToString((Date)fieldValue, DateTools.Resolution.SECOND);
			}
        }
		return retValue==null?"":retValue;
	}
	
	/**根据类型和查询串的string值构造查询的数据对象
	 * @param fieldValue
	 * @param value
	 * @param fieldType
	 * @return
	 */
	protected Object getFieldObjectByQueryString(String fieldValue, 
			Class<?> fieldType) {
		Assert.notNull(fieldType);
		Object value = null;
		//处理数字类型的“未选择”，以及排序时的异常。对于未设置的情况，使用最小值来设置查询条件。构建索引时，将未设置的数字也构建为未选择
		if(StringUtils.isEmpty(fieldValue)){
			if(Number.class.isAssignableFrom(fieldType)){
				fieldValue = ""+LuceneUtils.NOT_SET_NUMBER;
			}else if(Date.class.isAssignableFrom(fieldType)){
				fieldValue = DateUtils.DATE_PATTERN_FORMAT.format(LuceneUtils.NOT_SET_DATE);
			}
		}else if(Date.class.isAssignableFrom(fieldType)){
			return DateUtils.parseFormatDate(fieldValue);	
		}
		// 使用string类型的构造函数
		Constructor<?> stringCons = null;
		try {
			stringCons = fieldType.getConstructor(String.class);
		} catch (Exception ex) { // do noting
		}
		if (stringCons != null){
			try {
				value = stringCons.newInstance(fieldValue);
			} catch (Exception e) {
				logger.error("String argument Constructor error!", e);
				throw new SparkRuntimeException("global.exception.indexException");
			}
		}
		return value;
	}
}
