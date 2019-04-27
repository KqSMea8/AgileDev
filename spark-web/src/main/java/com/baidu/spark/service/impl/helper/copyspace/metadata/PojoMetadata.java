package com.baidu.spark.service.impl.helper.copyspace.metadata;

import java.util.ArrayList;
import java.util.List;
/**
 * 导出数据包装器
 * pojo类的实现。将bean（或list）作为参数转化为metadata
 * @author zhangjing_pe
 *
 */
public class PojoMetadata<T> extends Metadata{
	
	private List<T> pojos = new ArrayList<T>();

	public List<T> getPojos() {
		return pojos;
	}
	
	public void addPojo(T pojo){
		pojos.add(pojo);
	}
	
}
