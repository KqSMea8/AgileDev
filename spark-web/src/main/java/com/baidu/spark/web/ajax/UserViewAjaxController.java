package com.baidu.spark.web.ajax;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.model.UserView;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.service.UserViewService;
import com.baidu.spark.util.SpringSecurityUtils;
import com.baidu.spark.util.mapper.IncludePathCallback;
import com.baidu.spark.util.mapper.SparkMapper;
import com.baidu.spark.util.mapper.SparkMapperSingletonWrapper;

/**
 * 用户自定义视图收藏的前端控制器
 * 
 * @author shixiaolei
 */
@Controller
@RequestMapping("ajax/spaces/{prefixCode}/userviews")
public class UserViewAjaxController {

	private SpaceService spaceService;

	private UserViewService viewService;

	private static final String[] FAVORITE_INCLUDING_PATH = new String[] {
			"id", "name", "url" };

	private SparkMapper mapper = SparkMapperSingletonWrapper.getInstance();

	/**
	 * 相应AJAX请求，返回指定用户在指定空间内的全部收藏视图
	 * 
	 * @param prefixCode
	 *            空间前缀
	 * @return
	 */
	@RequestMapping(value = "list", method = RequestMethod.GET)
	@ResponseBody
	public List<UserView> listAllUserViewBySpace(@PathVariable String prefixCode) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		User user = SpringSecurityUtils.getCurrentUser();
		List<UserView> views = viewService.listAllUserViewBySpaceAndUser(space,
				user);
		return mapper.clone(views, new IncludePathCallback(
				FAVORITE_INCLUDING_PATH));
	}

	/**
	 * 相应AJAX请求，添加一个用户自定义查询视图收藏
	 * 
	 * @param prefixCode
	 *            空间前缀
	 * @param request
	 *            HttpServletRequest 从中读取name和url
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public List<UserView> addUserView(@PathVariable String prefixCode,
			@RequestBody UserView view) throws UnsupportedEncodingException {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		User user = SpringSecurityUtils.getCurrentUser();
		if (viewService.isNameConflictBySpaceAndUser(view.getName(), space, user)) {
			UserView badView = new UserView();
			badView.setId(-1L);
			List<UserView> vs = new ArrayList<UserView>(1);
			vs.add(badView);
			return vs;
		}
		view.setSpace(space);
		view.setUser(user);
		viewService.save(view);
		List<UserView> favorites = viewService.listAllUserViewBySpaceAndUser(space,
				user);
		return mapper.clone(favorites, new IncludePathCallback(
				FAVORITE_INCLUDING_PATH));
	}

	/**
	 * 相应AJAX请求，为指定视图收藏修改名称. 要求同一空间同一用户不重名，否则会返回一个空的List<View>，
	 * 
	 * @param prefixCode
	 *            空间前缀
	 * @param request
	 *            HttpServletRequest，从中读取ID和name
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public List<UserView> changeUserViewName(@PathVariable String prefixCode,
			@RequestBody UserView uv) throws UnsupportedEncodingException { 
		Long id = uv.getId();
		String name = URLDecoder.decode(uv.getName(), "UTF-8");
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		User user = SpringSecurityUtils.getCurrentUser();
		if (viewService.isNameConflictBySpaceAndUser(name, space, user)) {
			UserView badView = new UserView();
			badView.setId(-1L);
			List<UserView> vs = new ArrayList<UserView>(1);
			vs.add(badView);
			return vs;
		}
		UserView favoriteQuery = viewService.getById(id);
		favoriteQuery.setName(name);
		viewService.updateName(favoriteQuery);
		List<UserView> favorites = viewService.listAllUserViewBySpaceAndUser(space,
				user);
		return mapper.clone(favorites, new IncludePathCallback(
				FAVORITE_INCLUDING_PATH));
	}

	/**
	 * 相应AJAX请求，删除一个视图收藏
	 * 
	 * @param prefixCode
	 *            空间前缀
	 * @param request
	 *            HttpServletRequest，从中读取ID
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseBody
	public List<UserView> deleteView(@PathVariable String prefixCode,
			@RequestBody UserView uv) {
		Long id = uv.getId();
		viewService.deleteById(id);
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		User user = SpringSecurityUtils.getCurrentUser();
		List<UserView> favorites = viewService.listAllUserViewBySpaceAndUser(space,
				user);
		return mapper.clone(favorites, new IncludePathCallback(
				FAVORITE_INCLUDING_PATH));
	}

	@Autowired
	public void setViewService(UserViewService viewService) {
		this.viewService = viewService;
	}

	@Autowired
	public void setSpaceService(SpaceService spaceService) {
		this.spaceService = spaceService;
	}

}
