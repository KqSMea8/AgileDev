package com.baidu.spark.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 空间级别的视图收藏
 * 
 * @author shixiaolei
 */
@Entity
@Table(name = "space_view")
public class SpaceView{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 名称 */
	@NotEmpty
	@Length(min = 1, max = 20)
	private String name;

	/** 对应URL */
	@Column(name = "view_url")
	@NotEmpty
	@Length(min = 1, max = 2048)
	private String url;

	/** 创建时间 */
	private Date createdTime;

	/** 显示顺序 */
	private Integer sort;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "space_id")
	protected Space space;

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof UserView) {
			UserView view = (UserView) obj;
			return new EqualsBuilder().append(id, view.getId()).append(name,
					view.getName()).append(url,
					view.getUrl()).append(createdTime,
					view.getCreatedTime()).append(user, view.getUser())
					.append(space, view.getSpace()).isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(id).append(name).append(
				url).append(createdTime).append(user).append(space)
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("name", name)
				.append("url", url).append("createdTime",
						createdTime).append("user", user.getUsername())
				.append("space", space.getPrefixCode()).toString();
	}

}