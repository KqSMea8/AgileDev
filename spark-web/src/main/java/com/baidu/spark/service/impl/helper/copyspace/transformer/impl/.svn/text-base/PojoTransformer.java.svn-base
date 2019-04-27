package com.baidu.spark.service.impl.helper.copyspace.transformer.impl;

import java.util.List;

import org.springframework.util.Assert;

import com.baidu.spark.model.Space;
import com.baidu.spark.service.impl.helper.copyspace.metadata.PojoMetadata;
import com.baidu.spark.service.impl.helper.copyspace.option.ImportOption;
import com.baidu.spark.service.impl.helper.copyspace.transformer.Transformer;

/**
 * 多个同类pojo 对象的transformer 基类
 * 
 * @author zhangjing_pe
 * 
 */
public abstract class PojoTransformer<M extends PojoMetadata<T>, T> implements
		Transformer<M> {
	/**
	 * 导入pojolist的metadata
	 */
	@Override
	public void importMetadata(Space space,String jsonData,List<ImportOption<?>> importOptions) {
		Assert.notNull(jsonData);
		PojoMetadata<T> metadata = getMetadata(jsonData);
		if (metadata != null) {
			importPojos(space,metadata.getPojos(),importOptions);
		}
	}
	/**
	 * 将对象列表转化为json串
	 * @param list
	 * @return
	 */
	public abstract String getJson(List<T> list);
	/**
	 * 导入pojo对象
	 * @param space 目标空间
	 * @param pojos pojo对象
	 * @param importOptions 导入选项值
	 */
	protected abstract void importPojos(Space space,List<T> pojos,List<ImportOption<?>> importOptions);

}
