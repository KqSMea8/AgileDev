package com.baidu.spark.model.card;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.property.CardProperty;

/**
 * 卡片类型模型
 * 
 * @author chenhui
 *
 */
@Entity
public class CardType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/** 卡片名称 */
	@NotEmpty
	@Length(min = 1, max = 15)
	private String name;
	
	/**卡片类型的所属空间*/
	@ManyToOne
	@JoinColumn(name="space_id")
	private Space space;
	
	/**空间内cardtype标识*/
	private Long localId;

	/**卡片类型*/
	@ManyToMany
	@JoinTable(name = "card_type_property", 
			joinColumns = @JoinColumn(name = "card_type_id", referencedColumnName = "id"), 
			inverseJoinColumns = @JoinColumn(name = "card_property_id", referencedColumnName = "id"))
	@OrderBy("sort ASC")
	private Set<CardProperty> cardProperties = new LinkedHashSet<CardProperty>();
	
	/** 卡片类型上级*/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="parent_card_type_id")
	private CardType parent;
	
	/**卡片类型的下级类型*/
	@OneToMany(mappedBy = "parent")
	private Set<CardType> children;
	
	/** 是否允许从属本类型*/
	private Boolean recursive = false;
	
	/** 显示颜色 */
	private String color;
	
	public CardType(){}
	
	public CardType(Space space){
		this.space = space;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Long getLocalId() {
		return localId;
	}

	public void setLocalId(Long localId) {
		this.localId = localId;
	}

	public void setParent(CardType parent) {
		this.parent = parent;
	}

	public CardType getParent() {
		return parent;
	}

	public void setRecursive(Boolean recursive) {
		this.recursive = recursive;
	}

	public Boolean getRecursive() {
		return recursive;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getColor() {
		return color;
	}
	
	public void setSpace(Space space) {
		this.space = space;
	}

	public Space getSpace() {
		return space;
	}
	
	public void setChildren(Set<CardType> children) {
		this.children = children;
	}

	public Set<CardType> getChildren() {
		return children;
	}

	public Set<CardProperty> getCardProperties() {
		return cardProperties;
	}

	public void setCardProperties(Set<CardProperty> cardProperties) {
		this.cardProperties = cardProperties;
	}
	
	public void addCardProperty(CardProperty cardProperty) {
		if (cardProperties == null) {
			cardProperties = new LinkedHashSet<CardProperty>();
		}
		cardProperties.add(cardProperty);
	}
	
	public void addChildType(CardType cardType){
		if( children == null ){
			children = new LinkedHashSet<CardType>();
		}
		children.add(cardType);
		cardType.setParent(this);
	}
	
	public void removeChildType(CardType cardType){
		if( children != null ){
			children.remove(cardType);
			cardType.setParent(null);
		}
	}
	
	/**
	 * 获取卡片类型所有的级联的下级
	 * @return 卡片类型集合
	 */
	public Set<CardType> getAllChildrenTypes(){
		Set<CardType> childrenTypes = new LinkedHashSet<CardType>();
		if( !CollectionUtils.isEmpty(children) ){
			for(CardType type : children){
				childrenTypes.add(type);
				childrenTypes.addAll(type.getAllChildrenTypes());
			}
		}
		return childrenTypes;
	}
	/**
	 * 判断cardType是否包含指定的property
	 * @param property
	 * @return
	 */
	public boolean containsProperty(CardProperty property){
		if(property != null && CollectionUtils.isNotEmpty(cardProperties)){
			for(CardProperty cp:cardProperties){
				if(cp.getId().equals(property.getId())){
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof CardType) {
			CardType target = (CardType) obj;
			return new EqualsBuilder()
            .append(id, target.getId())
            .append(name, target.getName())
            .append(localId,target.getLocalId())
            .append(recursive, target.getRecursive())
            .append(space == null ? null :space.getId(), target.getSpace() == null ? null: target.getSpace().getId())
            .isEquals();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(id)
			.append(name)
			.append(localId)
			.append(recursive)
			.append(space == null ? null :space.getId())
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
		.append("id",id)
		.append("name",name)
		.append("recursive",recursive)
		.append("space",space == null ? null :space.getId())
		.append("cardpropery[...]")
		.append("children[...]")
		.toString();
	}

}
