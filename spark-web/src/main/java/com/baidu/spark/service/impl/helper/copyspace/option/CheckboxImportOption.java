package com.baidu.spark.service.impl.helper.copyspace.option;

import com.baidu.spark.service.impl.helper.copyspace.metadata.Metadata;

/**
 * checkbox类型的导入选项
 * @author zhangjing_pe
 *
 */
public class CheckboxImportOption<M extends Metadata> extends ImportOption<M>{
	/**checkbox的类型字符串*/
	public static final String TYPE = "checkbox";
	/**默认值显示*/
	private String defaultValue = null;
	/**是否被选中的值*/
	private static String CHECKED = "1";
	
	public CheckboxImportOption(Class<M> metadataType,String key,String messageKey,String defaultValue){
		super(metadataType, key, messageKey);
		type = TYPE;
		this.defaultValue = defaultValue;
	}
	
	public String getCheckedValue(){
		return CHECKED;
	}
	
	public String getDefaultValue(){
		return defaultValue;
	}
	
	public boolean checked(){
		return value!=null&&value.equals(CHECKED);
	}
}
