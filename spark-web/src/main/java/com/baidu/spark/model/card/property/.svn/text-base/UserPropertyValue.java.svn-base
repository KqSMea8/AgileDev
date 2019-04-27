package com.baidu.spark.model.card.property;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import com.baidu.spark.model.User;
import com.baidu.spark.service.UserService;
import com.baidu.spark.util.ApplicationContextHolder;
import com.baidu.spark.util.StringUtils;

/**
 * 保存人员的属性值
 * 允许保存多个人员.人员ID间用逗号隔开.
 * @author Adun
 */
@Entity
@DiscriminatorValue(value=UserPropertyValue.TYPE)
public class UserPropertyValue extends CardPropertyValue<Long> {

	public static final String TYPE = "user";
	
	private static UserService userService;
	
	private UserService getUserService(){
		if ( null == userService ){
			userService = ApplicationContextHolder.getBean(UserService.class);
		}
		return userService;
	}
	
	@Column(name="intvalue")
	private Long value;
	
	@Transient
	private User user;
	
	/**
	 * 如果user为空,则根据value对其进行初始化
	 */
	private void initUser (boolean forcedInit){
		if ( null == user || forcedInit ){
			if ( null != value ){
				user = getUserService().getUserById(value);
			}
			if ( null == user ){
				user = new User();
			}
		}
	}
	
	@Override
	public Long getValue() {
		return value;
	}

	/**
	 * 获取用户英文名字符串
	 * @return 用户中文名
	 */
	public String getEditValue() {
		initUser(false);
		return user.getUsername()==null?"":user.getUsername();
	}
	
	/**
	 * 获取用户中文名字符串
	 * @return 用户中文名
	 */
	@Override
	public String getDisplayValue(){
		initUser(false);
		return user.getName()==null?"":user.getName();
	}
	
	@Override
	public void initValueWithString(String userIdString) {
		Long userId = StringUtils.parseLong(userIdString);
		this.value = 0L==userId?null:userId;
		initUser(true);
	}

	@Override
	public void setValue(Long value) {
		this.value = value;
		initUser(true);
	}
	
	@Override
	public UserPropertyValue clone()  {
		UserPropertyValue pv = new UserPropertyValue();
		pv.setCard(this.getCard());
		pv.setCardProperty(this.getCardProperty());
		pv.setValue(this.getValue());
		return pv;
	}
}