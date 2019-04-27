package com.baidu.spark.model.card.history;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.baidu.spark.model.OpType;
import com.baidu.spark.model.User;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.util.json.JsonUtils;


/**
 * 卡片历史记录
 * @author tianyusong
 */
@Entity
public class CardHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**所属卡片*/
	@ManyToOne
	@JoinColumn(name="card_id")
	private Card card;
	
	/**操作用户*/
	@ManyToOne
	@JoinColumn(name="op_user_id")
	private User user;
	
	/**操作时间*/
	@Temporal(TemporalType.TIMESTAMP)
	private Date opTime;
	
	/**操作类型*/
	@Enumerated
	private OpType opType;

	/**标题*/
	private String title;
	 
	/**详细内容*/
	private String detail;
	
	/**当前状态的序列化文本*/
	private String data;
	
	/**与上个版本的diff信息*/
	private String diffData;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getOpTime() {
		return opTime;
	}

	public void setOpTime(Date opTime) {
		this.opTime = opTime;
	}

	public OpType getOpType() {
		return opType;
	}

	public void setOpType(OpType opType) {
		this.opType = opType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getDiffData() {
		return diffData;
	}

	public void setDiffData(String diffData) {
		this.diffData = diffData;
	}
	
	public CardHistoryDiffBean getDiffBean(){
		return JsonUtils.getObjectByJsonString(diffData, CardHistoryDiffBean.class);
	}
}
