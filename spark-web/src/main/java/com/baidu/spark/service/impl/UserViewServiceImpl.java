package com.baidu.spark.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.baidu.spark.dao.UserViewDao;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.model.UserView;
import com.baidu.spark.service.UserViewService;
import com.baidu.spark.util.BeanMapperSingletonWrapper;

/**
 * 视图收藏服务接口的实现类
 * 
 * @author shixiaolei
 */
@Service
public class UserViewServiceImpl implements UserViewService {

	/** 数据操作类 */
	private UserViewDao dao;

	@Override
	public void save(UserView view) {
		Assert.notNull(view);
		view.setCreatedTime(new Date());
		dao.save(view);
	}

	@Override
	public List<UserView> listAllUserViewBySpaceAndUser(Space space, User user) {
		Assert.notNull(space);
		Assert.notNull(user);
		String hql = "from UserView where space = ? and user = ?";
		return dao.find(hql,space, user);
	}

	@Override
	public UserView getById(Long id) {
		return dao.get(id);
	}

	@Override
	public void updateName(UserView view) {
		Assert.notNull(view);
		Assert.notNull(view.getId());
		UserView dbView = getById(view.getId());
		BeanMapperSingletonWrapper.getInstance().map(view, dbView,
				"userview-updateView");
		dao.save(dbView);
	}

	@Override
	public void deleteById(Long id) {
		dao.delete(id);
	}

	@Override
	public void deleteAllInSpace(Space space) {
		Assert.notNull(space);
		String hql = " from UserView where space=?";
		List<UserView> views = dao.find(hql, space);
		for (UserView view : views) {
			dao.delete(view.getId());
		}
	}

	@Override
	public boolean isNameConflictBySpaceAndUser(String name, Space space,
			User user) {
		Assert.notNull(space);
		Assert.notNull(name);
		String hql = "from UserView where space = ? and name = ? and user = ?";
		List<UserView> views = dao.find(hql, space, name, user);
		return views != null && views.size() > 0;
	}

	@Autowired
	public void setDao(UserViewDao dao) {
		this.dao = dao;
	}
}
