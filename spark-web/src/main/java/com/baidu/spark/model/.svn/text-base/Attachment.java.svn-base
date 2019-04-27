package com.baidu.spark.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.baidu.spark.model.card.Card;

/**
 * 附件.
 * 
 * @author shixiaolei
 * 
 */
@Entity
public class Attachment {

	public static final Integer NORMAL_STATUS = 0, DELETED_STATUS = 1;

	/** 附件标识* */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 附件名称，即系统进行重名校验并自动改名之后的名称* */
	private String name;

	/** 附件原始名称，即上传时的文件名称* */
	private String originalName;

	/** 附件路径* */
	private String path;

	/** 附件文件类型* */
	private String type;

	/** 上传时间 * */
	private Date uploadTime;

	/** 上传者 * */
	@ManyToOne
	@JoinColumn(name = "upload_user")
	private User uploadUser;

	/** 所属卡片 * */
	@ManyToOne
	@JoinColumn(name = "card_id")
	private Card card;

	/** 附件状态.0表示正常状态,1表示删除. */
	private Integer status;

	/** 备注. * */
	private String note;

	/** 当重新上传附件时,被替换的附件. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "old_attach_id")
	private Attachment oldAttachment;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public User getUploadUser() {
		return uploadUser;
	}

	public void setUploadUser(User uploadUser) {
		this.uploadUser = uploadUser;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Attachment getOldAttachment() {
		return oldAttachment;
	}

	public void setOldAttachment(Attachment oldAttachment) {
		this.oldAttachment = oldAttachment;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Transient
	public boolean isValid() {
		return NORMAL_STATUS.equals(status);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Attachment) {
			Attachment attach = (Attachment) obj;
			return new EqualsBuilder().append(id, attach.getId()).append(name,
					attach.getName()).append(originalName,
					attach.getOriginalName()).append(uploadTime,
					attach.getUploadTime()).append(card, attach.getCard())
					.append(status, attach.getStatus()).isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(id).append(name).append(
				originalName).append(uploadTime).append(card).append(status)
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("name", name)
				.append("originalName", originalName).append("uploadTime",
						uploadTime).append("oldAttachment", oldAttachment)
				.append("card", card).append("status", status).toString();
	}

}
