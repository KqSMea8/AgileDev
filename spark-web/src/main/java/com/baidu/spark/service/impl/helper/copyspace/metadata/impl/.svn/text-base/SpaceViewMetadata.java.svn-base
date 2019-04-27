package com.baidu.spark.service.impl.helper.copyspace.metadata.impl;

import java.util.ArrayList;

import com.baidu.spark.model.SpaceView;
import com.baidu.spark.service.impl.helper.copyspace.metadata.PojoMetadata;
import com.baidu.spark.service.impl.helper.copyspace.option.CheckboxImportOption;
import com.baidu.spark.service.impl.helper.copyspace.option.ImportOption;
/**
 * 空间视图的空间定义的元数据对象
 * @author zhangjing_pe
 *
 */
public class SpaceViewMetadata extends PojoMetadata<SpaceView> {

	public static final String IMPORT_SPACE_VIEW_KEY = "importSpaceViewInfo";
	
	public SpaceViewMetadata(){
		importOptions = new ArrayList<ImportOption<?>>();
		//添加导入选项：是否导入用户组信息
		ImportOption<SpaceViewMetadata> option = new CheckboxImportOption<SpaceViewMetadata>(SpaceViewMetadata.class, IMPORT_SPACE_VIEW_KEY, "space.copyFromExist.importSpaceView","1");
		importOptions.add(option);
	}
}
