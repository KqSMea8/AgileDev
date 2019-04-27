package com.baidu.spark.model.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.baidu.spark.model.Attachment;
import com.baidu.spark.model.Project;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.model.card.history.CardHistory;
import com.baidu.spark.model.card.property.CardPropertyValue;

/**
 * 卡片.
 * 
 * @author chenhui
 * 
 */
@Entity
public class Card {

	/** 卡片标识. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 所属空间. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "space_id")
	@NotNull
	private Space space;

	/** 空间内的卡片顺序码. */
	private Long sequence;

	/** 卡片类型. */
	@ManyToOne
	@JoinColumn(name = "card_type")
	@NotNull
	private CardType type;

	/** 上级卡片. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "super_id")
	private Card parent;

	/** 下级卡片. */
	@OneToMany(mappedBy = "parent")
	@OrderBy("sequence DESC")
	private Set<Card> children;

	/** 卡片标题. */
	@NotNull
	private String title;

	/** 卡片内容. */
	private String detail;

	/** 卡片属性值. */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "card")
	private List<CardPropertyValue<?>> propertyValues;

	/** 创建人. */
	@ManyToOne
	private User createdUser;

	/** 创建时间. */
	private Date createdTime;

	/** 最后更新人. */
	@ManyToOne
	private User lastModifiedUser;

	/** 最后更新时间. */
	private Date lastModifiedTime;

	/** 关联到的iCafe项目. */
	@ManyToOne
	@JoinColumn(name = "project_id")
	private Project project;

	/** 卡片属性值. */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "card")
	@OrderBy("opTime DESC")
	private List<CardHistory> historyList;

	/** 子卡片个数 */
	@Transient
	private Integer childrenSize = null;

	/** 附件. */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "card")
	@OrderBy("uploadTime DESC")
	private List<Attachment> attachments;

	// TODO private History lastestHistory;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}

	public Long getSequence() {
		return sequence;
	}

	public void setSequence(Long sequence) {
		this.sequence = sequence;
	}

	public Card getParent() {
		return parent;
	}

	public void setParent(Card parent) {
		this.parent = parent;
	}

	public Set<Card> getChildren() {
		return children;
	}

	public void setChildren(Set<Card> children) {
		this.children = children;
	}

	public CardType getType() {
		return type;
	}

	public void setType(CardType type) {
		this.type = type;
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

	public User getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(User createdUser) {
		this.createdUser = createdUser;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public User getLastModifiedUser() {
		return lastModifiedUser;
	}

	public void setLastModifiedUser(User lastModifiedUser) {
		this.lastModifiedUser = lastModifiedUser;
	}

	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public void setPropertyValues(List<CardPropertyValue<?>> propertyValues) {
		this.propertyValues = propertyValues;
	}

	public List<CardPropertyValue<?>> getPropertyValues() {
		return propertyValues;
	}

	public void addPropertyValue(CardPropertyValue<?> cpv) {
		if (propertyValues == null) {
			propertyValues = new ArrayList<CardPropertyValue<?>>();
		}
		cpv.setCard(this);
		propertyValues.add(cpv);
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<CardHistory> getHistoryList() {
		return historyList;
	}

	public void setHistoryList(List<CardHistory> historyList) {
		this.historyList = historyList;
	}

	public void addHistory(CardHistory history) {
		if (historyList == null) {
			historyList = new ArrayList<CardHistory>();
		}
		historyList.add(history);
	}

	public String getCardCode() {
		if (space != null) {
			return space.getPrefixCode() + "-" + sequence;
		}
		return sequence + "";
	}

	public Integer getChildrenSize() {
		if (childrenSize == null) {
			childrenSize = children == null ? 0 : children.size();
		}
		return childrenSize;
	}

	public void setChildrenSize(Integer childrenSize) {
		this.childrenSize = childrenSize;
	}

	@Transient
	public List<Attachment> getValidAttachments() {
		if(attachments==null){
			return Collections.emptyList();
		}
		List<Attachment> ret = new ArrayList<Attachment>();
		for (Attachment attach : attachments) {
			if (attach.isValid()) {
				ret.add(attach);
			}
		}
		return ret;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachmentList) {
		this.attachments = attachmentList;
	}

	public void addAttachment(Attachment attach) {
		if (attachments == null) {
			attachments = new ArrayList<Attachment>(0);
		}
		attachments.add(attach);
	}
	
	/** 卡片的所有下级(子孙卡片). */
	@Transient
	public Set<Card> getDescendants(){
		if(children == null){
			return Collections.<Card>emptySet();
		}
		Set<Card> descendants = new HashSet<Card>(children);
		for(Card child : children){
			descendants.addAll(child.getDescendants());
		}
		return descendants;
	}

}
