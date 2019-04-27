package com.baidu.spark.service;

import java.util.List;
import org.springframework.security.access.annotation.Secured;

import com.baidu.spark.dao.Pagination;
import com.baidu.spark.model.Space;
import com.baidu.spark.security.SparkPermissionEnum;
import com.baidu.spark.security.annotation.SecuredMethod;
import com.baidu.spark.security.annotation.SecuredObj;
import com.baidu.spark.security.annotation.objevaluator.SparkSystemResourceEvaluator;

/**
 * 空间服务接口.
 * 
 * @author GuoLin
 *
 */
public interface SpaceService {

	/**
	 * 获取所有空间列表.
	 * @param page 包含分页信息的分页对象
	 * @return 分页后的空间列表分页对象
	 */
	public Pagination<Space> getAllSpaces(Pagination<Space> page);
	
	/**
	 * 获取所有空间列表
	 * @return 所有空间
	 */
	public List<Space> getAllSpaces();
	
	/**
	 * 根据ID获取空间
	 * @param spaceId 空间ID
	 * @return ID对应空间 
	 */
	public Space getSpace(Long spaceId);
	
	/**
	 * 根据prefixCode获取空间
	 * @param prefixCode 空间标识
	 * @return 对应空间
	 */
	public Space getSpaceByPrefixCode(String prefixCode);
	
	/**
	 * 获取指定空间的下一个sequence
	 * @param spaceId 空间ID
	 * @return 
	 */
	public Long getNextSpaceSeq(Long spaceId);
	
	/**
	 * 创建空间
	 * @param space 待保存的空间
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission=SparkPermissionEnum.CREATE_CHILDREN,objEvaluator=SparkSystemResourceEvaluator.class)
	public void saveSpace(Space space);

	/**
	 * 修改空间
	 * @param space 修改后的空间
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission=SparkPermissionEnum.WRITE)
	public void updateSpace(@SecuredObj Space space);
	
	/**
	 * 删除空间
	 * 当前会删除掉空间所有相关的数据
	 * @param space 待删除空间
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission=SparkPermissionEnum.ADMIN)
	public void deteleSpace(@SecuredObj Space space);
	
	/**
	 * 判断是否已存在同名的空间
	 * 用于校验空间的prefixCode
	 * @param space 待验证空间
	 * @return 如有同名冲突则返回true,否则返回false
	 */
	public boolean checkConflicts(Space space);

	/**
	 * 判断是否已存在相同prefixCode的空间.
	 * @param prefixCode 空间别名
	 * @return 如有同名冲突则返回true,否则返回false
	 */
	public boolean checkConflicts(String prefixCode);

}
