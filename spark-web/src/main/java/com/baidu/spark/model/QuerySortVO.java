package com.baidu.spark.model;

import static com.baidu.spark.model.QueryConditionVO.PARAM_FRAGMENT;

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

/**
 * 卡片查询排序的vo
 * @author zhangjing_pe
 *
 */
public class QuerySortVO {
	
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	/**排序条件片段的模式*/
	private static final Pattern SORT_FRAGMENT_PATTERN = Pattern.compile(PARAM_FRAGMENT);
	/**排序条件的模式*/
	private static final Pattern SORT_FULL_PATTERN = Pattern
			.compile("^"+PARAM_FRAGMENT+PARAM_FRAGMENT+"$");
	/**排序条件简单的模式，不指定升序或降序，默认为升序*/
	private static final Pattern SORT_SHORT_PATTERN = Pattern
			.compile("^"+PARAM_FRAGMENT+"$");
	
	private static final String DESC = "desc";
	
	/** 查询字段名*/
	private String fieldName = null;
	/**是否倒序	 */
	private boolean desc = false;
	
	
	/**
	 * 构造函数
	 * @param fieldName
	 * @param desc
	 */
	public QuerySortVO(String fieldName, boolean desc){
		this.fieldName = fieldName;
		this.desc = desc;
	}

	/**
	 * 构造函数，根据查询参数生成查询bean
	 * @param sortString 格式为[字段][排序]或[字段]
	 * 如果写了[排序]部分，而且为desc(不区分大小写)，那么设置为降序
	 * 其它任何情况，都为升序
	 */
	public QuerySortVO(String sortString) {
		Assert.notNull(sortString);
		Matcher m = SORT_FULL_PATTERN.matcher(sortString);
		if (m == null ||! m.find()) {
			m = SORT_SHORT_PATTERN.matcher(sortString);
			if(m == null || !m.find()){
				throw new SparkRuntimeException(
				"global.exception.queryParamFormatException");
			}
		}
		m = SORT_FRAGMENT_PATTERN.matcher(sortString);
		List<String> fragmentList = new ArrayList<String>();
		while (m.find()) {
			for (int i = 1; i <= m.groupCount(); i++) {
				fragmentList.add(m.group(i));
			}
		}
		
		if(fragmentList==null||fragmentList.size()==0||fragmentList.size()>2){
			logger.error("error query param:{}",sortString);
			throw new SparkRuntimeException(
				"global.exception.queryParamFormatException");
		}

		fieldName = fragmentList.get(0);
		try {
			fieldName = URLDecoder.decode(fieldName,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("encoding sortString error:{}",sortString);
			throw new SparkRuntimeException("global.exception.queryParamFormatException");
		}
		if(fragmentList.size() == 2){
			String value = fragmentList.get(1).trim().toLowerCase();
			if(value.indexOf(DESC)==0){
				desc = true;
			}
		}
	}

	public String getFieldName() {
		return fieldName;
	}

	public boolean isDesc() {
		return desc;
	}

	public void setDesc(boolean desc) {
		this.desc = desc;
	}
	
	public String toString(){
		StringBuilder sb =  new StringBuilder("[").append(fieldName).append("]");
		if(desc){
			sb.append("[").append(DESC).append("]");
		}
		return sb.toString();
	
	}

}