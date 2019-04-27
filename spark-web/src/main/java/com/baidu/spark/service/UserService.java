package com.baidu.spark.service;

import java.util.List;

import com.baidu.spark.dao.Pagination;
import com.baidu.spark.model.User;

/**
 * 用户服务接口.
 * 
 * @author 张晶
 *
 */
public interface UserService {

	/**
	 * 获取所有空间列表.
	 * @param page 包含分页信息的分页对象
	 * @return 分页后的空间列表分页对象
	 */
	public Pagination<User> getAllUsers(Pagination<User> page);
	
	/**
	 * 根据登录名获取用户信息
	 * @param userName
	 * @return
	 */
	public User getUserByUserName(String userName);
	/**
	 * 根据用户id获取用户对象
	 * @param userId 用户主键id
	 * @return 用户对象。未查到则返回空
	 */
	public User getUserById(Long userId);
	
	
	/**
	 * 根据用户UICID获取用户对象
	 * @param uicId UICID
	 * @return 用户对象
	 */
	public User getUserByUicId(Long uicId);
	
	/**
	 * 获取suggest用户信息。根据用户名、姓名、邮箱来过滤可用的用户，按username升序排列
	 * 若keyword为空，则返回空数组，而不是所有用户
	 * @param keyword 筛选的关键字
	 * @param limit 数量限制
	 * @return
	 */
	public List<User> getSuggestUser(String keyword,int limit);
	
	/**
	 * 根据用户名或姓名来查询匹配的用户
	 * @param keyword 以keyword开头匹配,若keyword为空则返回全部
	 * @return
	 */
	public List<User> getUsersMatching(String keyword);
	
	/**
	 * 保存系统用户
	 * @param user <strong>游离态</strong>的用户对象，待保存
	 */
	public void saveUser(User user);
	
	/**
	 * 修改用户
	 * @param user <strong>持久态</strong>的用户对象，待修改
	 */
	public void updateUser(User user);

	/**
	 * 校验登录名是否与已有在职用户冲突
	 * @param user
	 * @return
	 */
	public boolean checkConflicts(User user);
	
	/**
	 * 校验是否有跟user冲突的UICID存在
	 * @param user
	 * @return
	 */
	public boolean checkUicIdConflicts(User user);
}
