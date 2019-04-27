package com.baidu.spark.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baidu.spark.dao.GroupSpaceDao;
import com.baidu.spark.model.Group;
import com.baidu.spark.model.GroupSpace;
import com.baidu.spark.model.Space;

/**
 * GroupSpace DAO Hibernate implementation.
 * TODO Change ME.
 * 
 * @author zhangjing_pe
 *
 */
@Repository
public class GroupSpaceDaoHibernate extends GenericDaoHibernate<GroupSpace, Long> implements GroupSpaceDao {

	/**
	 * GroupSpaceDaoHibernate Constructor.
	 * @param sessionFactory Hibernate SessionFactory
	 */
	@Autowired
	public GroupSpaceDaoHibernate(SessionFactory sessionFactory) {
		super(sessionFactory, GroupSpace.class);
	}
	
	@Override
	public void merge(Group group,Space space){
		if (getGroupSpace(group, space).size() == 0) {
			GroupSpace groupSpace = new GroupSpace();
			groupSpace.setGroup(group);
			groupSpace.setSpace(space);
			save(groupSpace);
		}
	}
	
	@Override
	public void delete(Group group,Space space){
		List<GroupSpace> groupSpaces = getGroupSpace(group, space);
		if(groupSpaces!=null&&!groupSpaces.isEmpty()){
			for(GroupSpace gs: groupSpaces){
				delete(gs);
			}
		}
	}
	
	@Override
	@SuppressWarnings( "unchecked" )
	public List<Group> getGroups(Space space){
		Criteria criteria = createCriteria();
		if(space == null){
			criteria.add(Restrictions.isNull("space"));	
		}else{
			criteria.add(Restrictions.eq("space", space));
		}
		criteria.setProjection(Projections.property("group"));
		return criteria.list();
	}
	
	@Override
	@SuppressWarnings( "unchecked" )
	public List<Space> getSpaces(Group group){
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq("group", group));
		criteria.setProjection(Projections.property("space"));
		return criteria.list();
	}

	/**根据space和group获取对象
	 * @param group
	 * @param space
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	private List<GroupSpace> getGroupSpace(Group group, Space space) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq("group.id", group.getId()));
		if(space == null){
			criteria.add(Restrictions.isNull("space"));
		}else{
			criteria.add(Restrictions.eq("space.id", space.getId()));
		}
		return criteria.list();
	}
	
	@Override
	public void deleteAll(Group group){
		Map<String,Object> values = new HashMap<String,Object>();
		values.put("group", group);
		String historyHql = "delete GroupSpace gs where gs.group = :group";
		executeUpdate(historyHql, values);
	}
	
	@Override
	public void deleteAll(Space space){
		Map<String,Object> values = new HashMap<String,Object>();
		values.put("space", space);
		String historyHql = "delete GroupSpace gs where gs.space = :space";
		executeUpdate(historyHql, values);	
	}
	
}
