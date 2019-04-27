package com.baidu.spark.util;

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.util.DozerConstants;

/**
 * DozerBeanMapper的单例.
 * 
 * @author GuoLin
 *
 */
public class BeanMapperSingletonWrapper {

	/**
	 * 查询时,获取cardType以获取该cardType中所有的property信息,来生成可用的查询条件.
	 */
	public static String GET_AVAILABLE_FIELD_4_SEARCH_MAP_ID = "cardType-availableField4Search";
	
	
	/** DozerBeanMapper单例. */
	private static DozerBeanMapper mapper;
	
	/**
	 * 私有构造器.
	 */
	private BeanMapperSingletonWrapper() {
		
	}
	
	/**
	 * 获取Mapper实例.
	 * @return Mapper实例
	 */
	public static synchronized Mapper getInstance() {
		if (mapper == null) {
			List<String> mappingFiles = new ArrayList<String>();
			mappingFiles.add(DozerConstants.DEFAULT_MAPPING_FILE);
			mapper = new DozerBeanMapper(mappingFiles);
			//mapper.setCustomFieldMapper(new ExcludeFieldMapper());
		}
		return mapper;
	}
	
	
}
