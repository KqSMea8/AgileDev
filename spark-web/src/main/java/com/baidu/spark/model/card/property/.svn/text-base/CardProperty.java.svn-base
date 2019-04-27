package com.baidu.spark.model.card.property;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.spark.exception.PropertyValueValidationException;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.CardType;

/**
 * 
 * @author zhangjing_pe
 *
 * @param <T>卡片属性对应值的类型
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type")
public abstract class CardProperty  {
	
	@Transient
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**空间内cardtype标识*/
	private Long localId;
	
	/**属性名称*/
	@Column(name="name")
	@NotEmpty
	@Length(min = 1, max = 20)
	private String name;
	
	/**是否可隐藏*/
	private Boolean hidden = false;
	
	/**相关属性,如List的取值等*/
	private String info;
	
	/**卡片类型的所属空间*/
	@ManyToOne
	@JoinColumn(name="space_id")
	private Space space;
	
	/**所属卡片类型*/
	@ManyToMany
	@JoinTable(name = "card_type_property", 
			joinColumns = @JoinColumn(name = "card_property_id", referencedColumnName = "id"), 
			inverseJoinColumns = @JoinColumn(name = "card_type_id", referencedColumnName = "id"))
	private Set<CardType> cardTypes = new LinkedHashSet<CardType>();
	
	/** 卡片属性值列表. */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "cardProperty")
	private Set<CardPropertyValue<?>> values;
	
	/**显示顺序*/
	private Integer sort;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getLocalId() {
		return localId;
	}

	public void setLocalId(Long localId) {
		this.localId = localId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public Set<CardPropertyValue<?>> getValues() {
		return values;
	}

	public void setValues(Set<CardPropertyValue<?>> values) {
		this.values = values;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	/**
	 * 生成一个卡片属性对应的属性值实例.
	 * @param value 属性的初始化值
	 * @return 属性值对象
	 */
	public CardPropertyValue<?> generateValue(String value) {
		CardPropertyValue<?> ret = instantiateValue();
		ret.setCardProperty(this);
		ret.initValueWithString(value);
		return ret;
	}
	/**
	 * 验证输入值是否合法
	 * cardProperty可调用propertyValue的自身的验证方法，也可以根据每个property中的规则进行额外的验证
	 * XXX cardproperty可以定义部分验证规则
	 * @param value
	 */
	public void validatePropertyValue(String value) throws PropertyValueValidationException{
		CardPropertyValue<?> propertyValue  = instantiateValue();
		propertyValue.setCardProperty(this);
		propertyValue.validateStringValue(value);
	}
	
	/**
	 * 获取卡片属性对应的值的对象.
	 * @return 卡片属性值对象
	 */
	protected abstract CardPropertyValue<?> instantiateValue();
	
	/**
	 * 获取卡片类型的字符串标识.
	 * @return 类型标识字符串
	 */
	public abstract String getType();

	public Set<CardType> getCardTypes() {
		return cardTypes;
	}

	public void setCardTypes(Set<CardType> cardTypes) {
		this.cardTypes = cardTypes;
	}

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}
	
	public void addCardType(CardType cardType){
		if(cardTypes == null){
			cardTypes = new HashSet<CardType>();
		}
		cardTypes.add(cardType);
	}
	
}
