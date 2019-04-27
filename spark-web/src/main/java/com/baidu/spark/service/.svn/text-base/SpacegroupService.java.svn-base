package com.baidu.spark.service;

import java.util.List;

import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.model.space.Spacegroup;

/**
 * 空间组服务接口.
 * 
 * @author 蹲子
 *
 */
public interface SpacegroupService {

	/**
	 * 查找指定用户的所有空间组.
	 * @param user 需要查找的用户
	 * @return 空间组的列表
	 */
	public List<Spacegroup> generateSpacegroupListWithSpace( User user );

	/**
	 * 根据组,获取组中所有的空间.
	 * @param spacegroupId 空间组的id.包含一些特殊组.特殊组的id在SpacegroupServiceImpl中的常量中定义
	 * @param user  需要查找的用户
	 * @return 组中的所有空间
	 */
	public List<Space> getSpacegroup( Long spacegroupId, User user );

	/**
	 * 修改一个用户对某个空间的 收藏/非收藏 信息.
	 * @param user 进行收藏操作的用户
	 * @param space 进行收藏操作的空间
	 * @return 修改后,此空间是否是被收藏状态
	 */
	public boolean changeFavoriteState(User user, Space space);
	
}
