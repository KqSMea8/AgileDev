package com.baidu.spark.model;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.history.CardHistory;

/**
 * 操作记录
 * @author Adun
 * 2010-05-31
 */
@Entity
public class OpLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@JoinColumn(name="target_type")
	private Integer targetType;
	
	@JoinColumn(name="op_type")
	@Enumerated
	private OpType opType;
	
	@JoinColumn(name="space_id")
	@ManyToOne
	private Space space;
	
	@JoinColumn(name="card_id")
	@ManyToOne
	private Card card;
	
	@JoinColumn(name="history_id")
	@ManyToOne
	private CardHistory cardHistory;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getTargetType() {
		return targetType;
	}

	public void setTargetType(Integer targetType) {
		this.targetType = targetType;
	}

	public OpType getOpType() {
		return opType;
	}

	public void setOpType(OpType opType) {
		this.opType = opType;
	}

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public CardHistory getCardHistory() {
		return cardHistory;
	}

	public void setCardHistory(CardHistory cardHistory) {
		this.cardHistory = cardHistory;
	}
	
}
