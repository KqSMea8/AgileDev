package com.baidu.spark.service;

import com.baidu.spark.model.OpType;
import com.baidu.spark.model.User;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.history.CardHistory;

public interface CardHistoryService {
	
	/**
	 * 根据当前卡片,操作类型,操作用户,生成一条历史记录
	 * 暂时先不支持删除卡片时候保存历史记录.
	 * 删除/增加自定义字段时,使用OpType.edit.传入的卡片为修改后的卡片内容
	 * 保存历史与保存卡片无所谓前后顺序.程序会找卡片保存的上一条历史记录去diff
	 * @param card 当前操作的卡片
	 * @param opType 操作类型,如编辑卡片,新建卡片等
	 * @param user 操作人
	 * @return 生成的历史信息的对象(持久化对象).如果没有任何diff信息,则直接返回null
	 * @author Adun
	 * 2010-06-12
	 */
	public CardHistory saveHistory(Card card, OpType opType, User user);
	
	/**
	 * 根据id获取一个历史记录
	 * @param historyId 历史记录的id
	 * @return 卡片历史信息对象
	 */
	public CardHistory getHistory(Long historyId);
	
	/**
	 * 从卡片历史信息的序列化内容中,读取卡片当时的状态信息
	 * @param historyJsonString 卡片的json序列化文本(此信息记录在history的data字段中)
	 * @return 从序列化文本中恢复的卡片信息
	 */
	public Card deserializeHistory(String historyJsonString);
}
