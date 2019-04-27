package com.baidu.spark.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import com.baidu.spark.model.space.Spacegroup;



/**
 * 用户实体
 *
 * @author zhangjing
 */
@Entity
@Table(name="users")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User implements UserDetails, Emailable {

	private static final long serialVersionUID = 9001897594863807041L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/** UIC用户ID. */
	private Long uicId;
	
	/** 用户姓名. */
	@NotEmpty
	private String name;
	
	/** 用户登录名. */
	@NotEmpty
	private String username;
	
	/** email. */
	private String email;
	
	/** 是否被锁定. */
	private Boolean locked = false;
	
	/** 用户所属的用户组 */
	@ManyToMany(mappedBy="users")
	private Set<Group> groups;
	
	/** 用户所具有的自定义空间组 */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="user")
	private Set<Spacegroup> spacegroups;
	
	/** 用户权限. */
	@Transient
	private List<GrantedAuthority> authorities;
	
	public User(){}
	
	public User(String username, String name){
		this.username = username;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUicId() {
		return uicId;
	}

	public void setUicId(Long uicId) {
		this.uicId = uicId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	@Override
	@Deprecated
	public String getPassword() {
		return "[PROTECTED]";
	}

	@Override
	@Deprecated
	public boolean isAccountNonExpired() {
		return isEnabled();
	}

	@Override
	@Deprecated
	public boolean isAccountNonLocked() {
		return isEnabled();
	}

	@Override
	@Deprecated
	public boolean isCredentialsNonExpired() {
		return isEnabled();
	}

	@Override
	public boolean isEnabled() {
		return !Boolean.TRUE.equals(locked);
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}
	/**
	 * <p>将用户添加到组中
	 * <b>Caution，单纯维护对象关系，关系实际在group端维护，更新后无法持久化.group端的维护已调用此方法，不要在程序其他位置中调用此方法
	 * @param group
	 */
	void addUserToGroup(Group group){
		if(group!=null){
			if(groups == null){
				groups = new LinkedHashSet<Group>();
			}
			groups.add(group);
		}
	}
	/**
	 * <p>从用户信息中删除此组
	 * <b>Caution，纯维护对象关系，关系实际在group端维护，更新后无法持久化.group端的维护已调用此方法，不要在程序其他位置中调用此方法
	 * @param group
	 */
	void removeFromGroup(Group group){
		Assert.notNull(group);
		Assert.notNull(group.getId());
		if(groups != null&&groups!=null){
			Iterator<Group> groupIt = groups.iterator();
			while(groupIt.hasNext()){
				if(group.getId().equals(groupIt.next().getId())){
					groupIt.remove();
					break;
				}
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof User) {
			User target = (User) obj;
			return new EqualsBuilder()
	            .append(id, target.getId())
	            .append(name, target.getName())
	            .append(username, target.getUsername())
	            .append(email,target.getEmail())
	            .append(locked, target.locked)
	            .append(uicId, target.getUicId())
	            .isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(id)
			.append(name)
			.append(username)
			.append(email)
			.append(locked)
			.append(locked)
			.append(uicId)
			.toHashCode();
	}

	public Set<Spacegroup> getSpacegroups() {
		return spacegroups;
	}

	public void setSpacegroups(Set<Spacegroup> spacegroups) {
		this.spacegroups = spacegroups;
	}
	

}