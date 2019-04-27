package com.baidu.spark.model;

import java.util.Date;
import java.util.List;

/**
 * 讨论贴的模型.
 * <p>
 * 它并非持久化Bean，而是存储在ECMP处，在本系统中只负责远程交互。
 * </p>
 * 
 * @author shixiaolei
 */
public class Discussion  {

	/** ID */
	private Long id;
	/** 回复的父Id. */
	private Long replyId;
	/** 发帖人姓名*/
	private String userName;
	/** 发帖人Id*/
	private Long userId;
	/** 最后修改时间*/
	private Date lastModifyTime;
	/** 内容*/
	private String content;
	/** 状态. 0为删除, 1为正常.*/
	private Integer status;
	/** 本回复的回复*/
	private List<Discussion> replyList;

	public static final Integer STATE_REMOVE = 0, STATE_NORMAL = 1;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getReplyId() {
		return replyId;
	}

	public void setReplyId(Long replyId) {
		this.replyId = replyId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(Date lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<Discussion> getReplyList() {
		return replyList;
	}

	public void setReplyList(List<Discussion> replyList) {
		this.replyList = replyList;
	}

}
