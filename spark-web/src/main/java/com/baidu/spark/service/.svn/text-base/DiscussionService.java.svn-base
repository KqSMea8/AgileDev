package com.baidu.spark.service;

import java.util.List;

import com.baidu.spark.model.Discussion;
import com.baidu.spark.model.card.Card;

/**
 * 讨论区服务接口
 * 
 * @author shixiaolei
 */
public interface DiscussionService {

	/**
	 * 保存一个对卡片的讨论.
	 * <p>
	 * 它将远程调用ECMP接口，实现存储。 故如果存储过程中有问题，将抛出DiscussionException
	 * </p>
	 * 
	 * @param msg 讨论的内容
	 * @param card 回复的卡片
	 * @return
	 */
	public Discussion replyCard(String msg,  Card card);

	/**
	 * 保存一个对讨论的回复讨论.
	 * <p>
	 * 它将远程调用ECMP接口，实现存储。 故如果存储过程中有问题，将抛出DiscussionException
	 * </p>
	 * 
	 * @param msg 讨论的内容
	 * @param parentDiscussionId 父卡片（被回复的卡片）
	 * @param card 所在的卡片
	 * @return
	 */
	public Discussion replyDiscussion(String msg, Long parentDiscussionId, Card card);

	/**
	 * 按ID得到讨论.
	 * <p>
	 * 它将远程调用ECMP接口，实现存储。 故如果存储过程中有问题，将抛出DiscussionException
	 * </p>
	 * 
	 * @param id 卡片ID
	 * @param card 所在的卡片
	 * @return
	 */
	public Discussion getById(Long id, Card card);

	/**
	 * 修改一条讨论在内容本身.
	 * <p>
	 * 它将远程调用ECMP接口，实现存储。 故如果存储过程中有问题，将抛出DiscussionException
	 * </p>
	 * 
	 * @param msg 修改后讨论的内容
	 * @param discussionId 卡片ID
	 * @param card 所在的卡片
	 * @return
	 */
	public Discussion updateDiscussion(String msg, Long discussionId, Card card);

	/**
	 * 删除一条讨论.
	 * <p>
	 * 修改完后，该讨论<strong>仍将被存储</strong>，但是msg被改成"deleted"字样，而<strong>
	 * status被置为1</strong>. 它将远程调用ECMP接口，实现存储。
	 * 故如果存储过程中有问题，将抛出DiscussionException
	 * </p>
	 * 
	 * @param msg 修改后讨论的内容
	 * @param discussionId 卡片ID
	 * @param card 所在的卡片
	 * @return
	 */
	public Discussion deleteDiscussion(Long discussionId, Card card);

	/**
	 * 返回一张卡片的所有回复，它是一个<strong>树形结构</strong>.
	 * 故如果存储过程中有问题，将抛出DiscussionException
	 * 
	 * @param card 所在的卡片
	 * @return
	 */
	public List<Discussion> getAllDiscussionsInCard(Card card);

}
