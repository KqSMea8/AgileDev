package com.baidu.spark.util;

import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.springframework.util.Assert;

import com.baidu.spark.exception.SparkRuntimeException;
/**
 * lucene查询工具类
 * @author zhangjing_pe
 *
 */
public abstract class LuceneUtils {
	

	/** lucene 版本 */
	private static final Version LUCENE_VERSION = Version.LUCENE_30;

	// 车东的二元分词
	// protected Analyzer analyzer = new
	// org.apache.lucene.analysis.cjk.CJKAnalyzer();

	// 刘欧修改的文档搜索采用的分词方法
	// protected Analyzer analyzer = new IcafeDocAnalyzer();

	// protected Analyzer analyzer = new StandardAnalyzer();
	// 标准分词
	public static final Analyzer analyzer = new StandardAnalyzer(LUCENE_VERSION);
	
	//未设置的数字在lucene中的值
	public static final Integer NOT_SET_NUMBER = Integer.MIN_VALUE;
	//未设置的日期在lucene中的值
	public static final Date NOT_SET_DATE = new Date(0);
	/**
	 * 获取指定字段的numericQuery，获取equals的操作
	 * @param fieldName
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static NumericRangeQuery getNumericEqualsQuery(String fieldName,Object value){
		Assert.notNull(fieldName);
		Assert.notNull(value);
		return getNumericRangeQuery(fieldName, value, value,  true, true);
	}
	
	/**
	 * 获取指定字段的numericQuery，获取Lessthan的操作
	 * @param fieldName
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static NumericRangeQuery getNumericLessThanQuery(String fieldName,Object value){
		Assert.notNull(fieldName);
		Assert.notNull(value);
		return getNumericRangeQuery(fieldName, null, value,  false, false);
	}
	
	/**
	 * 获取指定字段的numericQuery，获取equals的操作
	 * @param fieldName
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static NumericRangeQuery getNumericMoreThanQuery(String fieldName,Object value){
		Assert.notNull(fieldName);
		Assert.notNull(value);
		return getNumericRangeQuery(fieldName, value, null,  false, false);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> NumericRangeQuery getNumericRangeQuery(String fieldName,T minValue,T maxValue,boolean minInclude,boolean maxInclude){
		Assert.notNull(fieldName);
		Assert.isTrue(minValue!=null||maxValue!=null);
		NumericRangeQuery query = null;
		if(minValue instanceof Integer || maxValue instanceof Integer){
			query =  NumericRangeQuery.newIntRange(fieldName, (Integer)minValue, (Integer)maxValue,  minInclude, maxInclude);
		}else if(minValue instanceof Long || maxValue instanceof Long){
			query =  NumericRangeQuery.newLongRange(fieldName, (Long)minValue, (Long)maxValue,  minInclude, maxInclude);
		}else if(minValue instanceof Double || maxValue instanceof Double){
			query =  NumericRangeQuery.newDoubleRange(fieldName, (Double)minValue, (Double)maxValue,  minInclude, maxInclude);
		}else if(minValue instanceof Float || maxValue instanceof Float){
			query =  NumericRangeQuery.newFloatRange(fieldName, (Float)minValue, (Float)maxValue,  minInclude, maxInclude);
		}
		if(query==null){
			throw new SparkRuntimeException("global.exception.indexException");
		}
		query.setRewriteMethod(MultiTermQuery.CONSTANT_SCORE_FILTER_REWRITE);
		return query;
	}
	
	/**
	 * 获取指定字段的numericQuery，获取equals的操作
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public static Query getStringEqualsQuery(String fieldName,Object value){
		Assert.notNull(fieldName);
		Assert.notNull(value);
		String fieldValue = null;
		if(value instanceof Date){
			fieldValue = getLuceneDateString((Date)value);
		}else{
			fieldValue = value.toString();
		}
/*		lucene字符串查询的方法，由parser对查询串进行解析
 * 		QueryParser parser = new QueryParser(LUCENE_VERSION,fieldName,analyzer);
		Query query = null;
		try {
			query = parser.parse(fieldValue);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println(query.getClass());
		return query;*/
		Term term = new Term(fieldName, fieldValue);
		return new TermQuery(term);
	}
	
	
	/**
	 * 获取date类型的lucene存储字符串。使用秒单位来保存
	 * @param date 日期对象
	 * @return 实际存储
	 */
	public static String getLuceneDateString(Date date){
		Assert.notNull(date);
		return DateTools.dateToString(date, DateTools.Resolution.SECOND);
	}
	/**
	 * 根据字段类型，获取排序的方法
	 * @param fieldClazz
	 * @return
	 */
	public static int getLuceneSortType(Class<?> fieldClazz){
		Assert.notNull(fieldClazz);
		if(fieldClazz.equals(Long.class)){
			return SortField.LONG;
		}
		if(fieldClazz.equals(Integer.class)){
			return SortField.INT;
		}
		if(fieldClazz.equals(Double.class)){
			return SortField.DOUBLE;
		}
		if(fieldClazz.equals(Float.class)){
			return SortField.FLOAT;
		}
		//日期型，也用Long的比较
		if(Date.class.isAssignableFrom(fieldClazz)){
			return SortField.LONG;
		}
		return SortField.STRING;
	}
	
	
}
