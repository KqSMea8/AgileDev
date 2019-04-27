package com.baidu.spark.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * GroupSpace model.
 * group与space的映射
 * 
 * @author zhangjing_pe
 *
 */
@Entity
@Table(name = "group_space")
public class GroupSpace {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "map_id")
	private Long id;
	
	/** 所属空间 */
	@ManyToOne
	@JoinColumn(name="group_id")
	private Group group;
	
	/** 所属空间 */
	@ManyToOne
	@JoinColumn(name="space_id")
	private Space space;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}


}
