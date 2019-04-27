package com.baidu.spark.model.card.property;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.spark.exception.PropertyValueValidationException;
import com.baidu.spark.model.card.Card;


/**
 * 
 * 卡片属性值
 * @author chenhui
 *
 */
@Entity
@Table(name="card_property_value")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type")
public abstract class CardPropertyValue<T> implements Cloneable{
	
	/** 保持原有值的标志 */
	public static final String RETAIN_FLAG = "-99#@$%99";
	
	@Transient
	protected final Logger logger = LoggerFactory.getLogger(CardPropertyValue.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/** 所属卡片*/
	@ManyToOne
	@JoinColumn(name="card_id")
	private Card card;
	
	/** 卡片属性定义*/
	@ManyToOne(targetEntity=CardProperty.class)
	@JoinColumn(name="card_property_id")
	protected CardProperty cardProperty;
	
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

	public CardProperty getCardProperty() {
		return cardProperty;
	}

	public void setCardProperty(CardProperty cardProperty) {
		this.cardProperty = cardProperty;
	}
	
	/**
	 * 获取实际的属性值
	 * @return
	 */
	public abstract T getValue();
	
	/**
	 * 设置实际的属性值
	 * @param value
	 */
	public abstract void setValue(T value);
	
	/**
	 * 使用string来设这实际的属性值
	 * @param value
	 */
	public abstract void initValueWithString(String value);
	/**
	 * 验证字符串输入值是否合法
	 * @param value
	 * @throws PropertyValueValidationException
	 */
	protected void validateStringValue(String value) throws PropertyValueValidationException{
		if(RETAIN_FLAG.equals(value)) return;
	}
	
	/**
	 * 获取显示值
	 * @return
	 */
	public String getDisplayValue(){
		T value = getValue();
		return (value == null) ? "" : value.toString();
	}
	
	/**
	 * 目前是用于导出数据时,提供直观的,具有区分度的数据
	 * @return 导出时所显示的数据
	 * 暂时还没使用
	 */
	@Deprecated
	public String getText(){
		return getDisplayValue();
	}
	
	/**
	 * 获取导出数据时使用的显示值.需要有一定的区分度和简洁度
	 * @return 导出时的显示值
	 */
	public String getValueString(){
		String valueString;
		Object value = getValue();
		if (value instanceof Timestamp){
			value = new Date(((Timestamp)value).getTime());
		}
		if (null == value){
			return "";
		}else{
			valueString = value.toString();
		}
		if(null == valueString){
			valueString = "";
		}
		return valueString;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public CardPropertyValue<T> clone()  {
		try {
			return (CardPropertyValue<T>)super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
}
