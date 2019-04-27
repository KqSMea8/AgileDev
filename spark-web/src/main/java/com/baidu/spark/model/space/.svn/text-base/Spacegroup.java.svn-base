package com.baidu.spark.model.space;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;

/**
 * 空间组 bean
 * @author 阿蹲
 */

@Entity
@Table(name="spacegroups")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Spacegroup {
	
	/**
	 * 自增的空间组ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 空间组名称.用于页面上显示的.
	 */
	private String name;
	
	/**
	 * 用户组描述.目前没啥用
	 */
	private String description;
	
	/**
	 * 空间组所包含空间
	 */
	@ManyToMany(cascade=CascadeType.MERGE)
	@JoinTable(name = "spacegroup_space", 
			joinColumns = @JoinColumn(name = "spacegroup_id", referencedColumnName = "id"), 
			inverseJoinColumns = @JoinColumn(name = "space_id", referencedColumnName = "id"))
	@OrderBy("id ASC")
	private Set<Space> spaces = new LinkedHashSet<Space>();
	
	/**
	 * 用户组所属用户.
	 */
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Space> getSpaces() {
		return spaces;
	}

	public void setSpaces(Set<Space> spaces) {
		this.spaces = spaces;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
