package com.baidu.spark.service;

import java.util.List;

import org.springframework.security.access.annotation.Secured;

import com.baidu.spark.model.Space;
import com.baidu.spark.model.SpaceView;
import com.baidu.spark.security.SparkPermissionEnum;
import com.baidu.spark.security.annotation.SecuredMethod;
import com.baidu.spark.security.annotation.SecuredObj;

/**
 * 空间级别视图收藏的服务接口
 * 
 * @author shixiaolei
 */
public interface SpaceViewService {

	/**
	 * 保存一个空间级别的视图.
	 * @param view 待收藏的视图
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission = SparkPermissionEnum.ADMIN)
	public void save(@SecuredObj(clazz = Space.class, idPropertyName = "space.id") SpaceView view);
	
	/**
	 * 修改一个空间级别的视图.
	 * @param view 待收藏的视图
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission = SparkPermissionEnum.ADMIN)
	public void update(@SecuredObj(clazz = Space.class, idPropertyName = "space.id") SpaceView view);
	/**
	 * 保存一个空间级别的视图,如果系统中存在一个同名视图,就覆盖它;否则,则新建一个.
	 * @param view 待收藏的视图
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission = SparkPermissionEnum.ADMIN)
	public void saveOrCover(@SecuredObj(clazz = Space.class, idPropertyName = "space.id") SpaceView view);

	/**
	 * 保存一个空间级别的视图,如果系统中存在一个同名视图,就覆盖它;否则,则修改原来的视图.
	 * @param view 待修改的视图
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission = SparkPermissionEnum.ADMIN)
	public void updateOrCover(@SecuredObj(clazz = Space.class, idPropertyName = "space.id") SpaceView view) ;
	
	/**
	 * 按ID返回指定视图收藏
	 * @param id  视图ID
	 * @return 指定视图收藏
	 */
	public SpaceView getById(Long id);


	/**
	 * 按ID删除一个用户级别视图收藏
	 * @param id
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission = SparkPermissionEnum.ADMIN)
	public void delete(@SecuredObj(clazz = Space.class, idPropertyName = "space.id") SpaceView id);


	/**
	 * 判断同一空间内，指定名称是否已存在
	 * @param name 名称 
	 * @param space 空间
	 * @return
	 */
	public boolean isNameConflictInSpace(String name, Space space);
	
 

	/**
	 * 展示指定空间下的全部空间级别的视图. <BR/>如果视图或空间不存在，抛出IllegalArgumentExcepion,
	 * @param space 指定空间
	 * @return 展示指定用户在指定空间下的全部视图收藏
	 */
	public List<SpaceView> listAllInSpace(Space space);

}
