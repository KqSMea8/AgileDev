package com.baidu.spark.web.ajax;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.baidu.spark.exception.ResponseStatusException;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.SpaceView;
import com.baidu.spark.model.User;
import com.baidu.spark.security.PermissionService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.service.SpaceViewService;
import com.baidu.spark.util.SpringSecurityUtils;

/**
 * 空间相关AJAX前端控制器.
 * 
 * @author GuoLin
 *
 */
@Controller
@RequestMapping("ajax/spaces/{prefixCode}")
public class SpaceAjaxController {
	
	private SpaceService spaceService;
	
	private PermissionService permissionService;
	
	private SpaceViewService spaceViewService;
	
	@RequestMapping
	public ModelAndView getSpace(@PathVariable("prefixCode") String prefixCode) {
		
		ModelAndView mav = new ModelAndView();
		
		// 空间数据
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		// 空间权限
		User user = SpringSecurityUtils.getCurrentUser();
		List<Permission> permissions = permissionService.getUserPermission(user.getId(), space);
		List<SpaceView> spaceViews = spaceViewService.listAllInSpace(space);
		
		// 存入request
		mav.addObject("space", space);
		mav.addObject("spaceViews", spaceViews);
		mav.addObject("permissions", permissions);
		
		mav.setViewName("ajax/spaces/metadata");
		
		return mav;
	}

	@Autowired
	public void setSpaceService(SpaceService spaceService) {
		this.spaceService = spaceService;
	}

	@Autowired
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	@Autowired
	public void setSpaceViewService(SpaceViewService spaceViewService) {
		this.spaceViewService = spaceViewService;
	}
	
}
