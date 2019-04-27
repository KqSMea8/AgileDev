package com.baidu.spark.util.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/**
 * 往一个JsonAppender里面增加一个list
 * @author Adun
 * @param <T>
 */
public abstract class CollectionAppender<T> {
	
	protected Collection<T> list;
	
	public CollectionAppender(Collection<T> list){
		this.list = list;
	}
	
	/**
	 * 读取每个对象所要生成的json的kv对中,所有k的名称
	 * @return
	 */
	public abstract String[] getNames();
	
	/**
	 * 读取每个对象所要生成的json的kv对中,所有v的名称
	 * @param obj
	 * @return
	 */
	protected abstract Object[] getValues(T obj);
	
	/**
	 * 读取所有对象所要生成的json的kv对中,所有v的名称
	 * @return
	 */
	public List<Object[]> getValuesList(){
		List<Object[]> valuesList = new ArrayList<Object[]>();
		if (null != list){
			for (T t:list){
				valuesList.add(getValues(t));
			}
		}
		return valuesList;
	}
}
