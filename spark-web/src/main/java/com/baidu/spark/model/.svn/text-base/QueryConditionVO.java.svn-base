package com.baidu.spark.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.baidu.spark.exception.SparkRuntimeException;
import com.baidu.spark.util.SparkConfig;
/**
 * 单个查询条件的vo
 * @author zhangjing_pe
 *
 */
public class QueryConditionVO {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	/**查询条件片段的模式*/
	static final String PARAM_FRAGMENT = "\\[([^\\r\\n\\[\\]]*)\\]";
	
	static final Pattern PARAM_FRAGMENT_PATTERN = Pattern.compile(PARAM_FRAGMENT);
	/**查询条件的模式*/
	private final static Pattern PARAM_PATTERN = Pattern
			.compile("^"+PARAM_FRAGMENT+PARAM_FRAGMENT+PARAM_FRAGMENT+"$");

	/** 查询字段名*/
	private String fieldName = null;
	/** 查询的操作类型*/
	private QueryOperationType operationType = null;
	/** 查询的值*/
	private String value = null;

	/**
	 * 构造函数，根据查询参数生成查询bean
	 * 要求传入的queryParamString满足格式[字段][操作符][值],
	 * 且操作符在QueryConditionVO.QueryOperationType定义的枚举值之中,
	 * 否则会抛出SparkRuntimeException
	 * @param queryParamString
	 */
	public QueryConditionVO(String queryParamString) {
		Assert.notNull(queryParamString);
		Matcher m = PARAM_PATTERN.matcher(queryParamString);
		if (m == null||!m.find()) {
			throw new SparkRuntimeException(
					"global.exception.queryParamFormatException");
		}
		m = PARAM_FRAGMENT_PATTERN.matcher(queryParamString);
		List<String> fragmentList = new ArrayList<String>();
		while (m.find()) {
			for (int i = 1; i <= m.groupCount(); i++) {
				fragmentList.add(m.group(i));
			}
		}
		
		if(fragmentList==null||fragmentList.size()!=3){
			logger.error("error query param:{}",queryParamString);
			throw new SparkRuntimeException(
				"global.exception.queryParamFormatException");
		}

		fieldName = fragmentList.get(0);
		operationType = QueryOperationType.getOperationType(fragmentList.get(1));
		if(operationType == null){
			throw new SparkRuntimeException(
			"global.exception.queryParamFormatException");
		}
		value = fragmentList.get(2);
		try {
			value = URLDecoder.decode(value,SparkConfig.getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			logger.error("encoding sortString error:{}",queryParamString);
			throw new SparkRuntimeException("global.exception.queryParamFormatException");
		}
	}

	/**
	 * 构造函数，构造查询条件
	 * @param fieldName 查询字段名
	 * @param operationType 操作类型
	 * @param value 查询字段值
	 */
	public QueryConditionVO( String fieldName, QueryOperationType operationType, String value){
		this.fieldName = fieldName;
		this.operationType = operationType;
		this.value = value;
	}
	
	public String getFieldName() {
		return fieldName;
	}

	public QueryOperationType getOperationType() {
		return operationType;
	}

	public String getValue() {
		return value;
	}
	
	public String toString(){
		return new StringBuilder("[").append(fieldName).append("][").append(
				operationType.getOperationTypeString()).append("][").append(
				(getValue() == null ? "" : getValue())).append("]").toString();
	}
	
	/**
	 * 查询操作串的可选值
	 * @author zhangjing_pe
	 * @author shixiaolei
	 */
	public enum QueryOperationType{
		
		EQUALS("equals"),LESSTHAN("lessthan"),MORETHAN("morethan"),LIKE("like"),BETWEEN("between"), NOTEQUALS("notequals", false, EQUALS);
		
		private String operationTypeString = null;
		private boolean isPositive = true ;
		private QueryOperationType oppositeQueryOperationType = null;
		
		QueryOperationType(String name, boolean isPositive , QueryOperationType oppositeQueryOperationType){
			this.operationTypeString = name;
			this.isPositive = isPositive;
			this.oppositeQueryOperationType = oppositeQueryOperationType;
		}
		
		QueryOperationType(String name){
			this.operationTypeString = name;
		}
		
		QueryOperationType(){
			
		}
		
		public String getOperationTypeString(){
			return operationTypeString;
		}
		
		
		public boolean isPositive() {
			return isPositive;
		}

		public QueryOperationType getOppositeQueryOperationType() {
			return oppositeQueryOperationType;
		}

		/**
		 * 根据字符串获得枚举对象
		 * @param name 字符串
		 * @return 枚举对象
		 */
		public static QueryOperationType getOperationType(String name){
			Assert.notNull(name);
			for(QueryOperationType type:QueryOperationType.values()){
				if(type.getOperationTypeString().equals(name)){
					return type;
				}
			}
			return null;
		}
		
	}

	
}
