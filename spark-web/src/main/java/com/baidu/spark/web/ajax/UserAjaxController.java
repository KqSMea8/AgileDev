package com.baidu.spark.web.ajax;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baidu.spark.model.Emailable;
import com.baidu.spark.model.Emailgroup;
import com.baidu.spark.model.User;
import com.baidu.spark.service.UserService;
import com.baidu.spark.util.SpringSecurityUtils;
import com.baidu.spark.util.mapper.IncludePathCallback;
import com.baidu.spark.util.mapper.SparkMapper;
import com.baidu.spark.util.mapper.SparkMapperSingletonWrapper;

/**
 * 用户对象前端控制器
 * 
 * @author zhangjing_pe
 * 
 */
@Controller
@RequestMapping("ajax/users")
public class UserAjaxController {

	private static final String SEPERATOR = ",";

	private static final String[] USER_INCLUDING_PATH = new String[] { "id",
			"username", "name", "email" };
	
	private static final String[] EMAILABLE_INCLUDING_PATH = new String[] { "name", "email" };

	private UserService userService;

	private SparkMapper mapper = SparkMapperSingletonWrapper.getInstance();

	/**
	 * 根据父卡片的编号获取子卡片列表.
	 * 
	 * @param prefixCode
	 *            空间标识
	 * @param sequence
	 *            编号
	 * @return 卡片列表
	 */
	@RequestMapping("/autocomplete/{multiple}")
	@ResponseBody
	public List<User> getAllCardsByParent(@RequestParam String q,
			@RequestParam Integer limit, @PathVariable String multiple) {
		if (multiple.equals("true")) {
			q = q.substring(q.lastIndexOf( UserAjaxController.SEPERATOR ) + 1);
		}
		List<User> userList = userService.getSuggestUser(q, limit);
		return mapper.clone(userList, new IncludePathCallback( USER_INCLUDING_PATH ));
	}
	
	/**
	 * 根据邮件获取邮箱.
	 * @param q 查询内容
	 * @param limit 限制数量
	 * @return 可发送邮件的对象列表
	 */
	@RequestMapping("/emails/query")
	@ResponseBody
	public List<? extends Emailable> getAllEmailableByEmail(@RequestParam String q, @RequestParam Integer limit) {
		
		// 如果有存在的用户则直接响应用户
		List<? extends Emailable> users = userService.getSuggestUser(q, limit);
		if (!users.isEmpty()) {
			return mapper.clone(users, new IncludePathCallback(EMAILABLE_INCLUDING_PATH));
		}
		else {
			// TODO 暂不支持邮件组查询
			List<Emailable> emailgroups = new ArrayList<Emailable>(6);
			String prefix = q.contains("@") ? q.substring(0, q.indexOf("@")) : q;
			String[] expectations = new String[] { prefix + "@baidu.com", prefix + "@bj.baidu.com", prefix + "@sh.baidu.com", 
					prefix + "@gz.baidu.com", prefix + "@sz.baidu.com", prefix + "@dg.baidu.com" };
			for (int i = 0; i < expectations.length; i++) {
				String expectation = expectations[i];
				if (expectation.contains(q)) {
					emailgroups.add(new Emailgroup(prefix, expectation));
				}
			}
			return mapper.clone(emailgroups, new IncludePathCallback(EMAILABLE_INCLUDING_PATH));
		}
	}

	/**
	 * 获取当前用户.
	 * 
	 * @return 当前用户
	 */
	@RequestMapping("/currentUser")
	@ResponseBody
	public User getCurrentUser() {
		User user = SpringSecurityUtils.getCurrentUser();
		return mapper.clone(user, new IncludePathCallback(USER_INCLUDING_PATH));
	}
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
