package com.baidu.spark.model.card.property;

import java.text.ParseException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.lang.StringUtils;

import com.baidu.spark.exception.PropertyValueValidationException;
import com.baidu.spark.util.DateUtils;



/**
 * 日期型属性值
 * @author chenhui
 *
 */
@Entity
@DiscriminatorValue(value=DatePropertyValue.TYPE)
public class DatePropertyValue extends CardPropertyValue<Date>{

	/** 日期类型常量. */
	public static final String TYPE = "date";
	
	@Column(name="timevalue")
	private Date value;

	@Override
	public Date getValue() {
		return value;
	}

	@Override
	public void setValue(Date value) {
		this.value = value;
	}

	@Override
	public void initValueWithString(String value) {
		if (StringUtils.isEmpty(value)) {
			this.value = null;
			return;
		}
		try {
			this.value = DateUtils.DATE_TIME_SHORT_PATTERN_FORMAT.parse(value);
		} catch (ParseException ex) {
			this.value = null;
			logger.warn("Date parse error", ex);
		}
	}
	
	@Override
	public String getDisplayValue() {
		Date value = getValue();
		return (value == null) ? "" : DateUtils.DATE_TIME_SHORT_PATTERN_FORMAT.format(value);
	}
	@Override
	protected void validateStringValue(String value) throws PropertyValueValidationException{
		super.validateStringValue(value);
		try {
			if(!StringUtils.isEmpty(value)){
				//TODO: 此处的校验是否过于严格？
				DateUtils.DATE_TIME_SHORT_PATTERN_FORMAT.parse(value);
			}
		} catch (ParseException ex) {
			throw new PropertyValueValidationException("cardpropertyvalue.validate.date.format",value);
		}
	}

	@Override
	public DatePropertyValue clone() {
		DatePropertyValue pv = new DatePropertyValue();
		pv.setCard(this.getCard());
		pv.setCardProperty(this.getCardProperty());
		pv.setValue(this.getValue());
		return pv;
	}
}
