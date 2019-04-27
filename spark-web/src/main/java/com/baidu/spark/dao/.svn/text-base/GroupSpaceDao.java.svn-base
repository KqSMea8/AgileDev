package com.baidu.spark.dao;

import java.util.List;

import com.baidu.spark.model.Group;
import com.baidu.spark.model.GroupSpace;
import com.baidu.spark.model.Space;

/**
 * GroupSpace DAO interface.
 * TODO Change ME.
 * 
 * @author zhangjing_pe
 *
 */
public interface GroupSpaceDao extends GenericDao<GroupSpace, Long> {

	/**
	 * 根据space获取映射表中的所有space
	 * @param space
	 * @return
	 */
	public List<Group> getGroups(Space space);
	
	/**
	 * 根据group获取映射表中的所有space
	 * @param group
	 * @return
	 */
	public List<Space> getSpaces(Group group);
	/**
	 * 保存group和space的映射关系。如果映射关系已存在，就不保存
	 * @param group
	 * @param space
	 */
	public void merge(Group group,Space space);
	
	/**
	 * 删除grouphe space的所有映射关系
	 * @param group
	 * @param space
	 */
	public void delete(Group group,Space space);
	
	/**
	 * 根据group删除所有关系
	 * @param group
	 */
	public void deleteAll(Group group);
	
	/**
	 * 根据space删除所有关系
	 * @param group
	 */
	public void deleteAll(Space space);
	
}
