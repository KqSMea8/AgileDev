package com.baidu.spark.web.ajax;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baidu.spark.exception.ResponseStatusException;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.SpaceView;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.service.SpaceViewService;
import com.baidu.spark.util.mapper.IncludePathCallback;
import com.baidu.spark.util.mapper.SparkMapper;
import com.baidu.spark.util.mapper.SparkMapperSingletonWrapper;

/**
 * 空间级别的视图Ajax前端控制器
 * 
 * @author shixiaolei
 */
@Controller
@RequestMapping("ajax/spaces/{prefixCode}/spaceviews")
public class SpaceViewAjaxController {
	private SpaceService spaceService;

	private SpaceViewService viewService;

	private static final String[] VIEW_INCLUDING_PATH = new String[] { "id",
			"name", "url" };

	private SparkMapper mapper = SparkMapperSingletonWrapper.getInstance();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 相应AJAX请求，返回指定空间内的全部空间级别视图
	 * 
	 * @param prefixCode
	 *            空间前缀
	 * @return
	 */
	@RequestMapping(value = "list", method = RequestMethod.GET)
	@ResponseBody
	public List<SpaceView> listAllSpaceView(@PathVariable String prefixCode) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		List<SpaceView> results;
		try {
			List<SpaceView> views = viewService.listAllInSpace(space);
			results = mapper.clone(views, new IncludePathCallback(
					VIEW_INCLUDING_PATH));
		} catch (Exception e) {
			results = mapper.clone(new LinkedList<SpaceView>(),
					new IncludePathCallback(VIEW_INCLUDING_PATH));
		}
		return results;
	}

	/**
	 * 对空间视图进行重新排序.
	 * 
	 * @param prefixCode
	 *            空间别名
	 * @param sortedIds
	 *            按顺序存放的卡片属性ID列表
	 */
	@RequestMapping(value = "/resort", method = RequestMethod.POST)
	@ResponseBody
	public void resort(@PathVariable("prefixCode") String prefixCode,
			@RequestBody Long[] sortedIds) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		List<SpaceView> views = viewService.listAllInSpace(space);
		Map<Long, SpaceView> idMap = new HashMap<Long, SpaceView>();
		for (SpaceView view : views) {
			idMap.put(view.getId(), view);
		}
		for (int i = 0; i < sortedIds.length; i++) {
			SpaceView view = (SpaceView) idMap.get(sortedIds[i]);
			view.setSort(i);
			try {
				viewService.update(view);
			} catch (Exception e) {
				logger.warn("save spaceview {} failed when change sort",
						sortedIds[i]);
			}
		}
	}
	
	@RequestMapping(value = "/{name}/isConflict")
	@ResponseBody
	public Boolean checkConflict(@PathVariable String prefixCode,@PathVariable String name){
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return viewService.isNameConflictInSpace(name, space);
	}

	@Autowired
	public void setViewService(SpaceViewService viewService) {
		this.viewService = viewService;
	}

	@Autowired
	public void setSpaceService(SpaceService spaceService) {
		this.spaceService = spaceService;
	}
}
