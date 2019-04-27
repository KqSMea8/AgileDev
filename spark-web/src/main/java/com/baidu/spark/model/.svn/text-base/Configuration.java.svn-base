package com.baidu.spark.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 系统环境变量对象，以键值对的方式存储系统常用变量.
 * 
 * @author shixiaolei
 * 
 */
@Entity
@Table(name = "configurations")
public class Configuration {
	/** 键 */
	@Id
	@Column(name = "config_key")
	private String key;
	/** 值 */
	@Column(name = "config_value")
	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Configuration(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	public Configuration() {
		super();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Configuration) {
			Configuration conf = (Configuration) obj;
			return new EqualsBuilder().append(key, conf.getKey()).append(value,
					conf.getValue()).isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(key).append(value)
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("key", key).append("value",
				value).toString();
	}

}
