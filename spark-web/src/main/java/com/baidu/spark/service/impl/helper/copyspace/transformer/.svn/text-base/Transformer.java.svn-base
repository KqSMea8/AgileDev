package com.baidu.spark.service.impl.helper.copyspace.transformer;

import java.util.List;

import com.baidu.spark.model.Space;
import com.baidu.spark.service.impl.helper.copyspace.metadata.Metadata;
import com.baidu.spark.service.impl.helper.copyspace.option.ImportOption;
import com.baidu.spark.service.impl.helper.copyspace.validation.ValidationResult;

/**原始数据转换器。用于导入和导出
 * @author zhangjing_pe
 * TODO 补充转换的json格式说明
 */
public interface Transformer<M extends Metadata>{
	/**
	 * 导出元数据对象
	 * @param space 所属空间
	 * @return
	 */
	public M exportMetadata(Space space);
	/**
	 * 导入元数据对象
	 * @param space 目标空间
	 * @param jsonData 空间定义的json数据描述
	 * @param importOptions 导入确认信息
	 */
	public void importMetadata(Space space,String jsonData,List<ImportOption<?>> importOptions);
	/**
	 * 将json数据转化为元数据对象
	 * @param jsonData
	 * @return
	 */
	public M getMetadata(String jsonData);
	/**
	 * 元数据是否与当前导入逻辑匹配
	 * @param metadata 元数据对象
	 * @return
	 */
	public boolean match(Metadata metadata);
	/**
	 * 验证json数据合法性
	 * @param jsonData json数据
	 * @return 验证错误结果
	 */
	public List<ValidationResult> validateImportData(String jsonData);
}
