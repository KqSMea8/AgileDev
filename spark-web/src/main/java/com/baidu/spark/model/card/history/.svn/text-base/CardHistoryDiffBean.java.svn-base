package com.baidu.spark.model.card.history;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnore;
import com.baidu.spark.util.ListUtils;
import com.baidu.spark.util.MessageHolder;

/**
 * 记录两个卡片之间的diff信息
 * @author Adun
 */
public class CardHistoryDiffBean {
	
	/**
	 * 是否记录的是一个新增操作
	 */
	private boolean isNew = false;
	
	/**
	 * 各个字段的diff信息列表
	 */
	private List<CardHistorySingleDiff> diffList;
	
	public boolean getIsNew() {
		return isNew;
	}
	public void setIsNew(boolean isNew) {
		this.isNew = isNew;
	}
	public List<CardHistorySingleDiff> getDiffList() {
		return diffList;
	}
	public void setDiffList(List<CardHistorySingleDiff> diffList) {
		this.diffList = diffList;
	}
	
	/**
	 * 获取该diffBean的diff文本列表
	 * @return 已经国际化的diff内容列表
	 */
	@JsonIgnore
	public List<String> getInfoList(){
		List<String> infoList = new ArrayList<String>();
		if (isNew){
			infoList.add(MessageHolder.get("history.new"));
		}else{
			if (ListUtils.isEmpty(diffList)){
				infoList.add(MessageHolder.get("history.no_change"));
			}
			if (null != diffList){
				for (CardHistorySingleDiff diff : diffList){
					infoList.add(diff.toString());
				}
			}
		}
		return infoList;
	}
}
