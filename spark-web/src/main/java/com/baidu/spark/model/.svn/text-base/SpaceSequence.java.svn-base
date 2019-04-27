package com.baidu.spark.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 空间序号
 * 
 * @author zhangjing_pe
 * 
 */
@Entity
@Table(name = "space_sequence")
public class SpaceSequence {

	@Id
	private Long id;

	/** 下一个序号 */
	@Column(name="next_card_seq_num")
	private Long nextCardSeqNum;
	
	/** 下一个cardType的空间内id */
	private Long nextCardTypeLocalId;
	
	/** 下一个cardProperty的空间内id */
	private Long nextCardPropertyLocalId;
	
	/** 下一个listValue的空间内id */
	private Long nextListValueLocalId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getNextCardSeqNum() {
		return nextCardSeqNum;
	}

	public void setNextCardSeqNum(Long nextSeqNum) {
		this.nextCardSeqNum = nextSeqNum;
	}

	public long getCardSeqNumAndIncrement() {
		return nextCardSeqNum++;
	}

	public Long getNextCardTypeLocalId() {
		return nextCardTypeLocalId;
	}
	
	public Long getCardTypeLocalIdAndIncrement() {
		if(nextCardTypeLocalId == null){
			nextCardTypeLocalId = 1L;
		}
		return nextCardTypeLocalId++;
	}

	public void setNextCardTypeLocalId(Long nextCardTypeLocalId) {
		this.nextCardTypeLocalId = nextCardTypeLocalId;
	}

	public Long getNextCardPropertyLocalId() {
		return nextCardPropertyLocalId;
	}
	
	public Long getCardPropertyLocalIdAndIncrement() {
		if(nextCardPropertyLocalId == null){
			nextCardPropertyLocalId = 1L;
		}
		return nextCardPropertyLocalId++;
	}

	public void setNextCardPropertyLocalId(Long nextCardPropertyLocalId) {
		this.nextCardPropertyLocalId = nextCardPropertyLocalId;
	}

	public Long getNextListValueLocalId() {
		return nextListValueLocalId;
	}
	
	public Long getListValueLocalIdAndIncrement() {
		if(nextListValueLocalId == null){
			nextListValueLocalId = 1L;
		}
		return nextListValueLocalId++;
	}

	public void setNextListValueLocalId(Long nextListValueLocalId) {
		this.nextListValueLocalId = nextListValueLocalId;
	}

}
