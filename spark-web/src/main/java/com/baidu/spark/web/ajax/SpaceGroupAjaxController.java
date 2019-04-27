package com.baidu.spark.web.ajax;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.baidu.spark.exception.UnhandledViewException;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.security.PermissionService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.service.SpacegroupService;
import com.baidu.spark.util.SpringSecurityUtils;
import com.baidu.spark.util.mapper.IncludePathCallback;
import com.baidu.spark.util.mapper.SparkMapper;
import com.baidu.spark.util.mapper.SparkMapperSingletonWrapper;

/**
 * 空间相关AJAX前端控制器.
 * 
 * @author GuoLin
 *
 */
@Controller
@RequestMapping("ajax/spacegroup")
public class SpaceGroupAjaxController {
	
	private SpaceService spaceService;
	
	private SpacegroupService spacegroupService;
	
	private PermissionService permissionService;
	
	private SparkMapper mapper = SparkMapperSingletonWrapper.getInstance();
	
	private static final String[] SPACE_INCLUDING_PATH = new String[] {"id", "name", "prefixCode", "description", "isFavorite"};
	
	/**
	 * 根据空间组的Id,获取空间组中所有的空间
	 * @param spacegroupId 空间组的id.包含一些特殊组.特殊组的id在SpacegroupServiceImpl中的常量中定义
	 * @return 空间组中的所有空间
	 */
	@RequestMapping("/list/{spacegroupId}")
	@ResponseBody
	public List<Space> getSpace(@PathVariable("spacegroupId") Long spacegroupId) {
		User user =  SpringSecurityUtils.getCurrentUser();
		
		// 空间数据
		List<Space> spaceList = spacegroupService.getSpacegroup( spacegroupId, user );
		if (spaceList == null) {
			spaceList = new ArrayList<Space>();
		}
		
		// 空间权限
		return mapper.clone(spaceList, new IncludePathCallback( SPACE_INCLUDING_PATH ));
	}

	/**
	 * 更改某个空间"是否被收藏"属性
	 * @param prefixCode 需要修改的空间
	 * @return 修改后,空间的状态为"被收藏"还是"没有被收藏"
	 */
	@RequestMapping("/favorite/{prefixCode}")
	@ResponseBody
	public boolean changeFavoriteState(@PathVariable("prefixCode") String prefixCode){
		User user =  SpringSecurityUtils.getCurrentUser();
		Space space = spaceService.getSpaceByPrefixCode( prefixCode );
		if( null == space ){
			throw new UnhandledViewException("space.validate.notFound");
		}
		
		return spacegroupService.changeFavoriteState(user, space);
	}
	
	
	@Autowired
	public void setSpacegroupService(SpacegroupService spacegroupService) {
		this.spacegroupService = spacegroupService;
	}

	@Autowired
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	@Autowired
	public void setSpaceService(SpaceService spaceService) {
		this.spaceService = spaceService;
	}
}
