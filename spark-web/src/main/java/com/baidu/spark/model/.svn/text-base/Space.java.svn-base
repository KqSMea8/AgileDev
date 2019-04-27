package com.baidu.spark.model;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.model.space.Spacegroup;

/**
 * 空间模型.
 * 
 * @author GuoLin
 *
 */
@Entity
@Table(name = "spaces")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Space {

	public enum SpaceType {

		/** 普通空间类型. */
		NORMAL, 
		
		/** 模板空间类型. */
		TEMPLATE
		
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/** 空间名. */
	@NotEmpty
	private String name;
	
	/** 缩写. */
	@NotEmpty
	@Length(min=2, max=15)
	@Pattern(regexp="^[A-Za-z0-9][A-Za-z0-9_]+")
	private String prefixCode;
	
	/** 描述. */
	private String description;
	
	/** 类型. */
	@Enumerated
	private SpaceType type = SpaceType.NORMAL;
	
	/** 卡片类型. */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="space")
	private Set<CardType> cardTypes;
	
	/** 卡片属性. */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="space")
	@OrderBy("sort ASC")
	private Set<CardProperty> cardProperties;
	
	/** 空间上的项目列表. */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="space")
	private Set<Project> projects;
	
	/** 空间TAB列表. */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="space")
	@OrderBy("sort ASC")
	private List<SpaceView> views;
	
	/**
	 * 空间组所包含空间
	 */
	@ManyToMany(mappedBy="spaces")
	private Set<Spacegroup> spaceGroups = new LinkedHashSet<Spacegroup>();
	
	/**是否公共空间*/
	private Boolean isPublic = false;
	
	/** 是否是当前用户收藏的需求. 仅在空间列表页使用的显示字段 */
	@Transient
	private Boolean isFavorite = false;

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

	public String getPrefixCode() {
		return prefixCode;
	}

	public void setPrefixCode(String prefixCode) {
		this.prefixCode = prefixCode;
	}

	public SpaceType getType() {
		return type;
	}

	public void setType(SpaceType type) {
		this.type = type;
	}

	public void setCardTypes(Set<CardType> cardTypes) {
		this.cardTypes = cardTypes;
	}
	
	public Set<CardType> getCardTypes() {
		return cardTypes;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public Set<Project> getProjects() {
		return projects;
	}
	
	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}
	
	public void addProjects(Project project) {
		if (projects == null) {
			projects = new HashSet<Project>();
		}
		projects.add(project);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Space) {
			Space target = (Space) obj;
			return new EqualsBuilder()
	            .append(id, target.getId())
	            .append(name, target.getName())
	            .append(prefixCode, target.getPrefixCode())
	            .append(type,target.getType())
	            .append(getIsPublic(), target.getIsPublic())
	            .isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(id)
			.append(name)
			.append(prefixCode)
			.append(type)
			.append(isPublic)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id", id)
			.append("name", name)
			.append("prefixCode", prefixCode)
			.append("type", type)
			.append("cardTypes[...]")
			.toString();
	}

	public Set<CardProperty> getCardProperties() {
		return cardProperties;
	}

	public void setCardProperties(Set<CardProperty> cardProperties) {
		this.cardProperties = cardProperties;
	}

	public Boolean getIsPublic() {
		return isPublic==null?false:isPublic;
	}

	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}

	public Boolean getIsFavorite() {
		return isFavorite;
	}

	public void setIsFavorite(Boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public List<SpaceView> getViews() {
		return views;
	}

	public void setViews(List<SpaceView> views) {
		this.views = views;
	}

	public Set<Spacegroup> getSpaceGroups() {
		return spaceGroups;
	}

	public void setSpaceGroups(Set<Spacegroup> spaceGroups) {
		this.spaceGroups = spaceGroups;
	}
	
}
