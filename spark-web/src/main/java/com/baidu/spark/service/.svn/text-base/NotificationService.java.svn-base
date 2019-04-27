package com.baidu.spark.service;

import java.util.List;

import com.baidu.spark.model.Attachment;
import com.baidu.spark.model.Discussion;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.history.CardHistory;

/**
 * 通知服务接口.
 * <p>
 * 当用户对某些内容(如卡片内容、评论等)产生变更的时候，
 * 通知其他用户知晓.
 * </p>
 * 
 * @author GuoLin
 *
 */
public interface NotificationService {
	
	/**
	 * 将卡片变更通知给指定邮件.
	 * @param history 卡片历史对象
	 * @param attachments 此次修改卡片时修改的附件
	 * @param notifyMessage 用户填写的补充文本内容
	 * @param receivers 收件人列表
	 */
	public void send(CardHistory history, List<Attachment> attachments, String notifyMessage, String... receivers);
	
	/**
	 * 将评论通知给指定收件人.
	 * @param discussion 评论对象
	 * @param receivers 收件人列表
	 */
	public void send(Discussion discussion, String notifyMessage, Card card, String... receivers);
	
	/**
	 * 将附件通知给指定收件人.
	 * @param attachment 附件对象
	 * @param notifyMessage 用户填写的文本信息
	 * @param receivers 收件人列表
	 */
	public void send(Attachment attachment, String notifyMessage, String... receivers);
	
	/**
	 * 将人员同步的失败记录信息通知给收件人.
	 * @param infos 人员同步中失败的信息列表,其中每条信息是从UIC中同步而来的数据，以键值对形式展现
	 * @param receivers 收件人列表,如果为空，则会发给系统管理员邮箱
	 */
	public void sendUserSynchronizeErrors(List<String> infos, String... receivers);
	
}
