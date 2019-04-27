package com.baidu.spark.web;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.baidu.spark.model.Space;
import com.baidu.spark.model.SpaceView;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.service.SpaceViewService;

/**
 * 视图收藏的前端控制器
 * 
 * @author shixiaolei
 */
@Controller
@RequestMapping("/spaces/{prefixCode}/spaceviews")
public class SpaceViewController {

	private SpaceService spaceService;

	private SpaceViewService viewService;

	@RequestMapping(value = "list", method = RequestMethod.GET)
	public ModelAndView listAllViewBySpace(@PathVariable String prefixCode) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		List<SpaceView> views = viewService.listAllInSpace(space);
		ModelAndView mav = new ModelAndView();
		mav.setViewName("spaceviews/list");
		mav.addObject("views", views);
		mav.addObject(space);
		return mav;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView create(@PathVariable String prefixCode,
			@RequestParam(value = "url", required = false) String url,
			@RequestParam(value = "name", required = false) String name) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		ModelAndView mav = new ModelAndView();
		mav.setViewName("spaceviews/new");
		SpaceView view = new SpaceView();
		if (StringUtils.hasText(url)) {
			view.setUrl(url);
		}
		if (StringUtils.hasText(name)) {
			view.setName(name);
		}
		mav.addObject("spaceView", view);
		mav.addObject(space);
		return mav;
	}

	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable String prefixCode, @PathVariable Long id) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		ModelAndView mav = new ModelAndView();
		mav.setViewName("spaceviews/edit");
		SpaceView view = (SpaceView) viewService.getById(id);
		mav.addObject("spaceView", view);
		mav.addObject(space);
		return mav;
	}

	@RequestMapping(method = RequestMethod.PUT)
	public String createSubmit(@PathVariable String prefixCode,
			@ModelAttribute("spaceView") @Valid SpaceView spaceView, 
			BindingResult result, ModelMap modelMap) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if ( result.hasErrors() ){
			modelMap.addAttribute("space", space );
			modelMap.addAttribute("spaceView", spaceView);
			return "/spaceviews/new";
		}
		spaceView.setSpace(space);
		viewService.saveOrCover(spaceView);
		return "redirect:/spaces/" + prefixCode + "/spaceviews/list";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public String updateSubmit(@PathVariable String prefixCode, @PathVariable Long id,
			@ModelAttribute("spaceView") @Valid SpaceView spaceView, 
			BindingResult result, ModelMap modelMap) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if ( result.hasErrors() ){
			modelMap.addAttribute("space", space );
			modelMap.addAttribute("spaceView", spaceView);
			return "/spaceviews/edit";
		}
		spaceView.setSpace(space);
		viewService.updateOrCover(spaceView);
		return "redirect:/spaces/" + prefixCode + "/spaceviews/list";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String deleteView(@PathVariable String prefixCode,
			@PathVariable Long id) {
		SpaceView view = viewService.getById(id);
		viewService.delete(view);
		return "redirect:/spaces/" + prefixCode + "/spaceviews/list";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String showView(@PathVariable String prefixCode,
			@PathVariable Long id, ModelMap map) {
		SpaceView spaceview = (SpaceView) viewService.getById(id);
		return "redirect:/spaces/" + prefixCode + exactUrl(spaceview);
	}

	private String exactUrl(SpaceView view) {
		String rawUrl = view.getUrl();
		Long id = view.getId();
		if (rawUrl.indexOf("#") < 0) {
			return rawUrl + "#t=" + id;
		} else {
			String hash = rawUrl.substring(rawUrl.indexOf("#") + 1);
			String prefix = rawUrl.substring(0, rawUrl.indexOf("#"));
			String[] params = hash.split("&");
			StringBuilder sb = new StringBuilder();
			int i = -1;
			for (String param : params) {
				if (!param.startsWith("t=")) {
					i++;
					if (i == 0) {
						sb.append(param);
					} else {
						sb.append("&").append(param);
					}
				}
			}
			if (i >= 0) {
				return prefix + "#" + sb.toString() + "&t=" + id;
			} else {
				return prefix + "#t=" + id;
			}
		}
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
