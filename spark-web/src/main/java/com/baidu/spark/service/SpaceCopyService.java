package com.baidu.spark.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.baidu.spark.model.Space;
import com.baidu.spark.service.impl.helper.copyspace.metadata.Metadata;
import com.baidu.spark.service.impl.helper.copyspace.option.ImportOption;
import com.baidu.spark.service.impl.helper.copyspace.validation.ValidationResult;

/**
 * 空间拷贝的service
 * @author zhangjing_pe
 *
 */
public interface SpaceCopyService {

	/**
	 * 获取空间定义的元数据
	 * @param space
	 * @return
	 */
	public List<Metadata> getSpaceMetadata(Space space);
	/**
	 * 将空间定义元数据导入目标空间中
	 * @param space 目标空间
	 * @param metadatas 源空间的元数据
	 */
	public void importMetadata(Space space, List<Metadata> metadatas);
	/**
	 * 验证元数据的合法性
	 * @param metadatas 元数据列表
	 * @return 错误列表
	 */
	public List<ValidationResult> validation(List<Metadata> metadatas);
	/**
	 * 根据空间元数据获取空间导入的选项
	 * @param metadatas 元数据列表
	 * @return 导入选项对象列表
	 */
	public List<ImportOption<?>> getImportOptions(List<Metadata> metadatas);
	/**
	 * 根据用户的选择，填入导入的metadata中，使用option定制化导入操作
	 * @param request request对象
	 * @param metadatas 空间元数据列表
	 * @return 填充option的元数据列表
	 */
	public List<Metadata> buildImportOption(HttpServletRequest request,List<Metadata> metadatas);

}