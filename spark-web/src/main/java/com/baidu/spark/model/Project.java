package com.baidu.spark.model;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.baidu.spark.model.card.Card;

/**
 * iCafe项目.
 * 
 * @author GuoLin
 * 
 */
@Entity
@Table(name = "icafe_project")
public class Project {

	/** 项目ID. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** iCafe项目ID. */
	private Long icafeProjectId;

	/** iCafe项目名称. */
	private String name;

	/** 项目关联到的空间. */
	@ManyToOne
	@JoinColumn(name = "space_id")
	private Space space;

	/** 关联到项目上的卡片 */
	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
	private List<Card> cards;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIcafeProjectId() {
		return icafeProjectId;
	}

	public void setIcafeProjectId(Long icafeProjectId) {
		this.icafeProjectId = icafeProjectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	
	public void addCard(Card card) {
		if (this.cards == null) {
			this.cards = new LinkedList<Card>();
		}
		this.cards.add(card);
		card.setProject(this);
	}

}