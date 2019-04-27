package com.baidu.spark.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.baidu.spark.dao.SpaceViewDao;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.SpaceView;
import com.baidu.spark.model.User;
import com.baidu.spark.service.SpaceViewService;
import com.baidu.spark.util.SpringSecurityUtils;

/**
 * 视图收藏服务接口的实现类
 * 
 * @author shixiaolei
 */
@Service
public class SpaceViewServiceImpl implements SpaceViewService {

	/** 数据操作类 */
	private SpaceViewDao dao;

	@Override
	public void save(SpaceView view) {
		Assert.notNull(view);
		Assert.notNull(view.getSpace());
		view.setCreatedTime(new Date());
		view.setUser((User)SpringSecurityUtils.getCurrentUser());
		view.setSort(getNextSortIndex(view.getSpace()));
		dao.save(view);
	}
	
	@Override
	public void update(SpaceView view) {
		Assert.notNull(view);
		Assert.notNull(view.getSpace());
		Assert.notNull(view.getId());
		SpaceView dbView = dao.get(view.getId());
		dbView.setUrl(view.getUrl());
		dbView.setName(view.getName());
		dao.update(dbView);
	}
	
	@Override
	public synchronized void saveOrCover(SpaceView view) {
		Assert.notNull(view);
		Assert.notNull(view.getSpace());
		String name = view.getName();
		Space space = view.getSpace();
		SpaceView oldView = dao.findUnique("from SpaceView where space = ? and name = ?  ", space, name);
		if(oldView!= null){
			oldView.setUrl(view.getUrl());
			update(oldView);
		}else{
			save(view);
		}
	}
	
	@Override
	public synchronized void updateOrCover(SpaceView view) {
		Assert.notNull(view);
		Assert.notNull(view.getSpace());
		String name = view.getName();
		Space space = view.getSpace();
		SpaceView oldView = dao.findUnique("from SpaceView where space = ? and name = ? and id <> ? ", space, name, view.getId());
		if(oldView != null){
			oldView.setUrl(view.getUrl());
			update(oldView);
			delete(view);
		}else{
			update(view);
		}
	}

	@Override
	public List<SpaceView> listAllInSpace(Space space) {
		Assert.notNull(space);
		String hql = "from SpaceView where space = ? order by sort asc ";
		return dao.find(hql, space);
	}

	@Override
	public SpaceView getById(Long id) {
		return dao.get(id);
	}

	@Override
	public void delete(SpaceView view) {
		dao.delete(view);
	}

	@Override
	public boolean isNameConflictInSpace(String name, Space space) {
		Assert.notNull(space);
		Assert.notNull(name);
		String hql = "from SpaceView where space = ? and name = ?  ";
		List<SpaceView> views = dao.find(hql, space, name);
		return views != null && views.size() > 0;
	}
	
	private Integer getNextSortIndex(Space space) {
		String hql = "select max(v.sort) from SpaceView v where space = ? ";
		Integer max = dao.findUnique(hql, space);
		return max == null ? 0 : (max + 1);
	}

	@Autowired
	public void setDao(SpaceViewDao dao) {
		this.dao = dao;
	}
}
