package com.baidu.spark.util;

import java.util.List;
/**
 * 对list的处理工具类
 * @author Adun
 * 2010-06-03
 */
public class ListUtils {
	
	/**
	 * 判断一个列表是否为空(包括null和size为0)
	 * @param list
	 * @return boolean判断结果
	 */
	public static boolean isEmpty(List list){
		return null == list || list.size() == 0;
	}

	/**
	 * 判断一个列表是否非空,即不为null且size不为0
	 * @param list
	 * @return boolean判断结果
	 */
	public static boolean notEmpty(List list) {
		return !isEmpty(list);
	}
}
