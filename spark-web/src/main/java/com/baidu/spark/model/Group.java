package com.baidu.spark.model;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.Assert;

/**
 * Group model.
 * 用户组
 * 
 * @author zhangjing_pe
 *
 */
@Entity
@Table(name = "groups")
public class Group {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	/** 所属空间 */
	@ManyToOne
	@JoinColumn(name="space_id")
	private Space owner;
	
	/** 用户组名称 */
	@NotEmpty
	private String name;
	
	/** 是否公开*/
	private Boolean exposed = false;
	
	@ManyToMany(cascade=CascadeType.MERGE)
	@JoinTable(name = "group_user", 
			joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"), 
			inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
	@OrderBy("username ASC")
	private Set<User> users = new LinkedHashSet<User>();
	
	/** 是否可用 */
	private Boolean locked = false;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Space getOwner() {
		return owner;
	}

	public void setOwner(Space space) {
		this.owner = space;
	}

	public String getName() {
		return name;
	}

	public void setName(String groupName) {
		this.name = groupName;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}
	
	public Boolean getExposed() {
		return exposed;
	}

	public void setExposed(Boolean exposed) {
		this.exposed = exposed;
	}
	
	public void addUser(User user){
		if(user != null){
			user.addUserToGroup(this);
			if(users == null){
				users = new LinkedHashSet<User>();
			}
			users.add(user);
		}
	}
	
	public void removeUser(User user){
		Assert.notNull(user);
		Assert.notNull(user.getId());
		user.removeFromGroup(this);
		if(user != null&&users!=null){
			Iterator<User> userIt = users.iterator();
			while(userIt.hasNext()){
				if(user.getId().equals(userIt.next().getId())){
					userIt.remove();
					break;
				}
			}
		}
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}
	
	public boolean isGlobalGroup(){
		return getOwner() == null;
	}
	
	public boolean isSameOwner(Group group){
		return isSameOwner(group.getOwner());
	}
	
	public boolean isSameOwner(Space space){
		if(isGlobalGroup()){
			if(space == null){
				return true;	
			}else{
				return false;
			}
		}else {
			if(space == null || space.getId() == null){
				return false;
			}else if(space.getId().equals(owner.getId())){
				return true;
			}else {
				return false;
			}
		}
	}

}
