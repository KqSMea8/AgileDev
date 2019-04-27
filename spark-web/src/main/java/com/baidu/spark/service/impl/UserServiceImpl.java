package com.baidu.spark.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.baidu.spark.dao.Pagination;
import com.baidu.spark.dao.UserDao;
import com.baidu.spark.model.User;
import com.baidu.spark.service.UserService;

/**
 * 用户服务实现类.
 * 
 * @author zhangjing
 *
 */
@Service
public class UserServiceImpl implements UserService {
	
	private UserDao userDao;

	@Override
	public Pagination<User> getAllUsers(Pagination<User> page) {
		return userDao.findAll(page);
	}
	
	@Override
	public User getUserByUserName(String userName){
		return userDao.findUniqueByCriteria(
				Restrictions.or(Restrictions.eq("locked", false),Restrictions.isNull("locked")), 
				Restrictions.eq("username", userName));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getSuggestUser(String keyword,int limit){
		if(StringUtils.isEmpty(keyword)){
			return new ArrayList<User>();
		}
		String queryString = StringEscapeUtils.escapeSql(keyword)+"%";
		Criteria criteria = userDao.createCriteria();
		criteria.add(Restrictions.or(Restrictions.eq("locked", false),Restrictions.isNull("locked")));
		Criterion ex = Restrictions.or(Restrictions.like("name", queryString), Restrictions.like("username", queryString));
		criteria.add(ex);
		criteria.addOrder(Order.asc("username"));
		criteria.setMaxResults(limit);
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsersMatching(String keyword){
		Criteria criteria = userDao.createCriteria();
		criteria.add(Restrictions.or(Restrictions.like("username", keyword, MatchMode.START), Restrictions.like("name", keyword,MatchMode.START)));
		criteria.addOrder(Order.asc("username"));
		return criteria.list();
	}
	
	@Override
	public User getUserById(Long userId){
		Assert.notNull(userId);
		return userDao.get(userId);
	}
	
	@Override
	public User getUserByUicId(Long uicId){
		return userDao.findUniqueByProperty("uicId", uicId);
	}

	@Override
	public void saveUser(User user) {
		Assert.notNull(user);
		Assert.notNull(user.getUsername());
		userDao.save(user);
	}
	
	@Override
	public void updateUser(User user) {
		Assert.notNull(user);
		Assert.notNull(user.getUsername());
		userDao.update(user);
	}
	
	@Override
	public boolean checkConflicts(User user) {
		Assert.notNull(user);
		Assert.notNull(user.getUsername());
		Criteria criteria = userDao.createCriteria();
		criteria.add(Restrictions.eq("username", user.getUsername()));
		criteria.add(Restrictions.eq("locked", user.getLocked()));
		if( user.getId() != null ){
			criteria.add(Restrictions.ne("id", user.getId()));
		}
		return criteria.list().size() != 0;
	}
	
	@Override
	public boolean checkUicIdConflicts(User user) {
		Assert.notNull(user);
		Assert.notNull(user.getUicId());
		Criteria criteria = userDao.createCriteria();
		criteria.add(Restrictions.eq("uicId", user.getUicId()));
		if( user.getId() != null ){
			criteria.add(Restrictions.ne("id", user.getId()));
		}
		return criteria.list().size() != 0;
	}
	
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

}
