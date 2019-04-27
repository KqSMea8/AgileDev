package com.baidu.spark.service.impl.helper.copyspace.option;

import com.baidu.spark.service.impl.helper.copyspace.metadata.Metadata;

/**
 * 导入时的选项信息
 * @author zhangjing_pe
 *
 */
public abstract class ImportOption<M extends Metadata> {
	/**导入选项的页面输入的name前缀*/
	public static final String INPUT_NAME_PREFIX = "importOption_";
	/**导入选项的类型*/
	protected String type;
	/**导入选项的国际化资源key*/
	protected String messageKey;
	/**key*/
	protected String key;
	/**页面input的name*/
	protected String inputName;
	/**输入的值*/
	protected String value;
	/**对应的空间定义元数据的类型*/
	protected Class<M> metadataType;
	
	public ImportOption(Class<M> metadataType,String key,String messageKey){
		this.messageKey = messageKey;
		this.key = key;
		this.inputName = INPUT_NAME_PREFIX+key;
		this.metadataType = metadataType;
	}

	public String getKey() {
		return key;
	}

	public String getInputName() {
		return inputName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public Class<M> getMetadataType() {
		return metadataType;
	}
	
	

}
