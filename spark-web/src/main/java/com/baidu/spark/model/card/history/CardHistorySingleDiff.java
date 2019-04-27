package com.baidu.spark.model.card.history;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import com.baidu.spark.util.MessageHolder;

/**
 * 记录两个卡片间,某字段的diff信息.
 * @author Adun
 */
public class CardHistorySingleDiff {
	
	public enum CardHistoryDiffType {
		CHANGE_FIELD, ADD_FIELD, DEL_FIELD,CHANGE_ATTACHMENT, ADD_ATTACHMENT, DEL_ATTACHMENT;
	}
	
	private CardHistoryDiffType diffType;
	private String fieldName;
	private String previousValue;
	private String newValue;
	
	private CardHistorySingleDiff(){}
	
	/**
	 * 生成一个修改字段的diff信息
	 * @param fieldName 字段名称
	 * @param previousValue 字段原有值
	 * @param newValue 字段新值
	 * @return 该字段diff的Bean
	 */
	public static CardHistorySingleDiff createChangeFieldDiff(String fieldName, String previousValue, String newValue){
		Assert.notNull(fieldName);
		if (previousValue == null){
			previousValue = "";
		}
		if (newValue == null){
			newValue = "";
		}
		
		CardHistorySingleDiff diff = new CardHistorySingleDiff();
		diff.setDiffType(CardHistoryDiffType.CHANGE_FIELD);
		diff.setFieldName(fieldName);
		diff.setPreviousValue(previousValue);
		diff.setNewValue(newValue);
		return diff;
	}
	
	/**
	 * 生成一个新增字段的diff信息
	 * @param fieldName 字段名称
	 * @param newValue 字段新值
	 * @return 该字段diff的Bean
	 */
	public static CardHistorySingleDiff createAddFieldDiff(String fieldName, String newValue){
		Assert.notNull(fieldName);
		if (newValue == null){
			newValue = "";
		}
		
		CardHistorySingleDiff diff = new CardHistorySingleDiff();
		diff.setDiffType(CardHistoryDiffType.ADD_FIELD);
		diff.setFieldName(fieldName);
		diff.setNewValue(newValue);
		return diff;
	}
	
	/**
	 * 生成一个删除字段的diff信息
	 * @param fieldName 字段名称
	 * @param previousValue 字段原有值
	 * @return 该字段diff的Bean
	 */
	public static CardHistorySingleDiff createDelFieldDiff(String fieldName, String previousValue){
		Assert.notNull(fieldName);
		if (previousValue == null){
			previousValue = "";
		}
		
		CardHistorySingleDiff diff = new CardHistorySingleDiff();
		diff.setDiffType(CardHistoryDiffType.DEL_FIELD);
		diff.setFieldName(fieldName);
		diff.setPreviousValue(previousValue);
		return diff;
	}
	
	/**
	 * 生成一个修改附件的diff信息
	 * @param fieldName 字段名称
	 * @param previousValue 字段原有值
	 * @param newValue 字段新值
	 * @return 该字段diff的Bean
	 */
	public static CardHistorySingleDiff createChangeAttachmentDiff(String fieldName, String previousValue, String newValue){
		Assert.notNull(fieldName);
		if (previousValue == null){
			previousValue = "";
		}
		if (newValue == null){
			newValue = "";
		}
		
		CardHistorySingleDiff diff = new CardHistorySingleDiff();
		diff.setDiffType(CardHistoryDiffType.CHANGE_ATTACHMENT);
		diff.setFieldName(fieldName);
		diff.setPreviousValue(previousValue);
		diff.setNewValue(newValue);
		return diff;
	}
	
	/**
	 * 生成一个新增附件的diff信息
	 * @param fieldName 字段名称
	 * @param newValue 字段新值
	 * @return 该字段diff的Bean
	 */
	public static CardHistorySingleDiff createAddAttachmentDiff(String fieldName, String newValue){
		Assert.notNull(fieldName);
		if (newValue == null){
			newValue = "";
		}
		
		CardHistorySingleDiff diff = new CardHistorySingleDiff();
		diff.setDiffType(CardHistoryDiffType.ADD_ATTACHMENT);
		diff.setFieldName(fieldName);
		diff.setNewValue(newValue);
		return diff;
	}
	
	/**
	 * 生成一个删除附件的diff信息
	 * @param fieldName 字段名称
	 * @param previousValue 字段原有值
	 * @return 该字段diff的Bean
	 */
	public static CardHistorySingleDiff createDelAttachmentDiff(String fieldName, String previousValue){
		Assert.notNull(fieldName);
		if (previousValue == null){
			previousValue = "";
		}
		
		CardHistorySingleDiff diff = new CardHistorySingleDiff();
		diff.setDiffType(CardHistoryDiffType.DEL_ATTACHMENT);
		diff.setFieldName(fieldName);
		diff.setPreviousValue(previousValue);
		return diff;
	}

	private void setDiffType(CardHistoryDiffType diffType) {
		this.diffType = diffType;
	}
	public String getFieldName() {
		return fieldName;
	}
	private void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getPreviousValue() {
		return previousValue;
	}
	private void setPreviousValue(String previousValue) {
		this.previousValue = previousValue;
	}
	public String getNewValue() {
		return newValue;
	}
	private void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	
	/**
	 * 获取此条diff信息的页面显示文本
	 * @return 显示已经经过国际化处理的diff内容字符串.
	 */
	@ Override
	public String toString(){
		if (StringUtils.isEmpty(previousValue)){
			previousValue = "[" + MessageHolder.get("history.empty_value") + "]";
		}
		if (StringUtils.isEmpty(newValue)){
			newValue = "[" + MessageHolder.get("history.empty_value") + "]";
		}
		if (diffType == CardHistoryDiffType.CHANGE_FIELD){
			if (fieldName.equals("detail")){
				return MessageHolder.get("history.change_detail");
			}else{
				String i18nName = "card." + fieldName;
				String tmp = MessageHolder.get("card." + fieldName);
				if (!i18nName.equals(tmp)){
					fieldName = tmp;
				}
				return MessageHolder.get("history.change_field", fieldName, previousValue, newValue);
			}
		}else if (diffType == CardHistoryDiffType.ADD_FIELD){
			return MessageHolder.get("history.add_field", fieldName, newValue);
		}else if (diffType == CardHistoryDiffType.DEL_FIELD){
			return MessageHolder.get("history.delete_field", fieldName, previousValue);
		}else if (diffType == CardHistoryDiffType.CHANGE_ATTACHMENT){
			return MessageHolder.get("history.change_attachment", previousValue, newValue);
		}else if (diffType == CardHistoryDiffType.ADD_ATTACHMENT){
			return MessageHolder.get("history.add_attachment",  newValue);
		}else if (diffType == CardHistoryDiffType.DEL_ATTACHMENT){
			return MessageHolder.get("history.delete_attachment",  previousValue);
		}else{
			return "";
		}
	}

	public CardHistoryDiffType getDiffType() {
		return diffType;
	}
}
