package com.baidu.spark.service;

import java.util.List;

import org.springframework.security.access.annotation.Secured;

import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.model.UserView;
import com.baidu.spark.security.SparkPermissionEnum;
import com.baidu.spark.security.annotation.SecuredMethod;
import com.baidu.spark.security.annotation.SecuredObj;

/**
 * 用户自定义视图收藏的服务接口
 * 
 * @author shixiaolei
 */
public interface UserViewService {
	/**
	 * 保存一个用户基本的视图收藏.
	 * 
	 * @param view
	 *            待收藏的视图
	 */
	public void save(UserView view);

	/**
	 * 按ID返回指定视图收藏
	 * 
	 * @param id
	 *            视图ID
	 * @return 指定视图收藏
	 */
	public UserView getById(Long id);

	/**
	 * 修改一个视图收藏的名称
	 * 
	 * @param view
	 */
	public void updateName(UserView view);

	/**
	 * 按ID删除一个用户级别视图收藏
	 * 
	 * @param id
	 */
	public void deleteById(Long id);

	/**
	 * 删除指定空间下的所有<strong>用户自定义视图</strong>
	 * @param space 指定空间
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission = SparkPermissionEnum.ADMIN)
	public void deleteAllInSpace(@SecuredObj Space space);

	/**
	 * 判断同一空间内同一用户下，指定名称是否已存在
	 * 
	 * @param name
	 *            名称、
	 * @param space
	 *            空间
	 * @param user
	 *            用户
	 * @return
	 */
	public boolean isNameConflictBySpaceAndUser(String name, Space space,
			User user);

	/**
	 * 展示指定用户在指定空间下的全部用户自定义视图.  如果视图或空间不存在，抛出IllegalArgumentExcepion,
	 * 
	 * @param space
	 *            指定空间
	 * @param user
	 *            指定用户
	 * @return 展示指定用户在指定空间下的全部视图收藏
	 */
	public List<UserView> listAllUserViewBySpaceAndUser(Space space, User user);

}
