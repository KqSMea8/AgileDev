package com.baidu.spark.service;

import java.util.List;
import org.springframework.security.access.annotation.Secured;
import com.baidu.spark.model.Project;
import com.baidu.spark.model.Space;
import com.baidu.spark.security.SparkPermissionEnum;
import com.baidu.spark.security.annotation.SecuredMethod;
import com.baidu.spark.security.annotation.SecuredObj;

/**
 * Icafe项目相关操作的Service接口
 * @author Adun
 */
public interface ProjectService {

	/**
	 * 获取某个空间中所有对应的project列表
	 * @param spaceId 所查询的空间的id
	 * @return 指定空间中所有project的列表
	 */
	public List<Project> getAll(Space space);
	
	/**
	 * 获取所有对应的project列表
	 * @return 所有project的列表
	 */
	public List<Project> getAll();
	
	/**
	 * 增加一条项目-空间对应信息
	 * @param project 待保存的bean
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission=SparkPermissionEnum.ADMIN)
	public void add(@SecuredObj(clazz=Space.class,idPropertyName="space.id") Project project);
	
	/**
	 * 删除一条项目-空间对应信息
	 * @param id 待删除的mapping的id
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission=SparkPermissionEnum.ADMIN)
	public void delete(@SecuredObj(clazz=Space.class,idPropertyName="space.id") Project project);

	/**
	 * 根据Id获取一条项目-空间对应信息
	 * @param id 项目和空间的mappingId
	 * @return Project对象
	 */
	public Project get(Long id);
	
	/**
	 * 根据icafe id获取一个项目对象
	 * @param id
	 * @return
	 */
	public Project getByIcafeId(Long id);
}
