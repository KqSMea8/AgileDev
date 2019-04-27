package com.baidu.spark.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.baidu.spark.dao.SpacegroupDao;
import com.baidu.spark.model.Group;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.model.space.Spacegroup;
import com.baidu.spark.security.PermissionService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.service.SpacegroupService;
import com.baidu.spark.service.UserService;
import com.baidu.spark.util.ListUtils;
import com.baidu.spark.util.MessageHolder;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 空间服务实现类.
 * 
 * @author GuoLin
 *
 */
@Service
public class SpacegroupServiceImpl implements SpacegroupService {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private SpacegroupDao spacegroupDao;
	
	private PermissionService permissionService;
	
	private UserService userService;
	
	private SpaceService spaceService;
	
	@SuppressWarnings("unused")
	private static final Long VERTUAL_SPACEGROUP = 0L;
	
	private static final Long WORKING_SPACEGROUP = -1L;
	
	private static final Long ATTENDING_SPACEGROUP = -2L;

	@Override
	public List<Spacegroup> generateSpacegroupListWithSpace( User user ){

		List<Spacegroup> spacegroupList =new ArrayList<Spacegroup>();
		user = userService.getUserById( user.getId() );
		//该用户的组
		Set<Spacegroup> userSpacegroup = user.getSpacegroups();
		if ( null == userSpacegroup || 0 == userSpacegroup.size()){	//如果该用户还没有组,则给他新建一个.
			if (null == userSpacegroup){
				userSpacegroup = new LinkedHashSet<Spacegroup>();
				user.setSpacegroups( userSpacegroup );
			}
			Spacegroup favoritSpacegroup;
			favoritSpacegroup = new Spacegroup();
			favoritSpacegroup.setName( MessageHolder.get( "label.favoriteSpace" )  );
			favoritSpacegroup.setUser(user);
			spacegroupDao.save( favoritSpacegroup );
			userSpacegroup.add( favoritSpacegroup );
		}
		spacegroupList.addAll( userSpacegroup );
		
		//特殊的组: 工作的空间/参与的空间
		{
			Spacegroup workingSpacegroup;
			workingSpacegroup = new Spacegroup();
			workingSpacegroup.setId( WORKING_SPACEGROUP );
			workingSpacegroup.setName( MessageHolder.get( "label.workingSpace" ) );
			
			//为其填充入空间列表,在页面中会使用: 展现空间列表页的时候会首先判断有没有收藏的空间,如果有则显示收藏空间,否则再判断有没有工作空间,如果没有的话则跳转到公开空间列表.这里就是保证页面能够拿到用户的工作空间
			//added by tianyusong 2010-11-07
			//注释讲不明白..实在看不懂的话,直接来问我吧.
			List<Space> workingSpaceList = getSpacegroup(workingSpacegroup.getId(), user);
			workingSpacegroup.setSpaces( new LinkedHashSet<Space>() );
			if ( null != workingSpaceList ){
				workingSpacegroup.getSpaces().addAll( workingSpaceList );
			}
			
			spacegroupList.add( workingSpacegroup );
		}
		{
			Spacegroup workingSpacegroup;
			workingSpacegroup = new Spacegroup();
			workingSpacegroup.setId( ATTENDING_SPACEGROUP );
			workingSpacegroup.setName( MessageHolder.get( "label.attendingSpace" ) );
			spacegroupList.add( workingSpacegroup );
		}
		
		//管理员定义的组
		Criterion c = Restrictions.isNull("user");
		List<Spacegroup> commonSpacegroup = spacegroupDao.findByCriteria(c);
		if ( null != commonSpacegroup ){
			spacegroupList.addAll( commonSpacegroup );
		}
		
		return spacegroupList;
	}

	@Override
	public List<Space> getSpacegroup( Long spacegroupId, User user ) {
		Assert.notNull( spacegroupId );
		Assert.notNull( user );
		user = userService.getUserById( user.getId() );
		
		//获取spacegroupId对应的space列表
		List<Space> spaceList = new ArrayList<Space>();
		if ( WORKING_SPACEGROUP.equals( spacegroupId ) || ATTENDING_SPACEGROUP.equals( spacegroupId ) ){
			//获取用户有角色的空间
			Set<Space> userSpaceSet = getUserSpaceSetInGroup( user );
			if ( WORKING_SPACEGROUP.equals( spacegroupId ) ){
				spaceList.addAll( userSpaceSet );
			}else{
				//获取所有空间的列表,并过滤用户没有权限的和用户有角色的.剩余的就是用户"参与"的空间, 即由于"开放给所有人"而使当前用户可以看到的空间
				List<Space> allSpaceList = spaceService.getAllSpaces();
				for ( Space space : allSpaceList ){
					List<Permission> permissionList = permissionService.getUserPermission( user.getId(), space );
					if ( ListUtils.notEmpty(permissionList) ){
						if ( !userSpaceSet.contains( space ) ){
							spaceList.add( space );
						}
					}
				}
			}
		}else {
			//获取用户空间组中定义的空间
			Spacegroup spacegroup = spacegroupDao.get(spacegroupId);
			if (null != spacegroup){
				spaceList.addAll(spacegroup.getSpaces());
			}
		}
		
		//在获取的空间中,标记该所有用户收藏的空间,用于页面上"收藏" 的标示
		Set<Long> userSpaceIdSet = new LinkedHashSet<Long>();
		Set<Spacegroup> userSpacegroupSet = user.getSpacegroups();
		if ( null != userSpacegroupSet ){
			for ( Spacegroup currentSpacegroup : userSpacegroupSet ){
				if ( null == currentSpacegroup || null == currentSpacegroup.getSpaces() ){
					continue;
				}
				for ( Space space : currentSpacegroup.getSpaces() ){
					if ( null != space ){
						userSpaceIdSet.add( space.getId() );
					}
				}
			}
		}
		
		for ( Space space : spaceList ){
			if ( userSpaceIdSet.contains( space.getId() ) ){
				space.setIsFavorite( true );
			}
		}
		
		//显示的排序
		Collections.sort(spaceList, new Comparator<Space>(){
			@Override
			public int compare(Space o1, Space o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
		
		return spaceList;
	}
	
	@Override
	public boolean changeFavoriteState(User user, Space space) {
		Assert.notNull( user );
		Assert.notNull( space );
		
		user = userService.getUserById( user.getId() );
		
		Set<Space> userSpaceSet = getUserSpacegoupSpaceSet ( user );
		Set<Spacegroup> userSpacegroupSet = user.getSpacegroups();
		if ( null != userSpaceSet && userSpaceSet.contains( space ) ){
			//如果用户当前已经收藏了此空间,则在收藏中去掉此空间
			if (null != userSpacegroupSet){
				for ( Spacegroup spacegroup : userSpacegroupSet ){
					if ( null != spacegroup && null != spacegroup.getSpaces() && spacegroup.getSpaces().contains( space ) ){
						spacegroup.getSpaces().remove( space );
						spacegroupDao.save( spacegroup );
					}
				}
			}
			return false;
		}else{
			//如果用户没有收藏此空间,则在用户自定义空间组中增加此空间
			if ( null == userSpacegroupSet ){
				userSpacegroupSet = new LinkedHashSet<Spacegroup>();
			}
			Spacegroup spacegroup = userSpacegroupSet.iterator().next();
			if ( null == spacegroup.getSpaces() ){
				spacegroup.setSpaces( new LinkedHashSet<Space>() );
			}
			spacegroup.getSpaces().add( space );
			spacegroupDao.save( spacegroup );
			return true;
		}
	}

	/**
	 * 根据用户,获得所有该用户在自定义空间组中定义的空间
	 * @param user 需要查询的用户
	 * @return 用户自定义组中的所有空间
	 */
	private Set<Space> getUserSpaceSetInGroup ( User user ){
		Set<Group> userGroupSet = user.getGroups();
		Set<Space> userSpaceSet = new LinkedHashSet<Space>();
		if ( null != userGroupSet ){
			for ( Group group : userGroupSet ){
				if ( null == group || null == group.getOwner() ){
					continue;
				}
				userSpaceSet.add( group.getOwner() );
			}
		}
		return userSpaceSet;
	}
	
	/**
	 * 根据用户,查找用户具有角色的空间.即,在空间人员配置中存在此人员的所有空间
	 * @param user 需要查找的用户
	 * @return 用户具有角色的所有空间
	 */
	private Set<Space> getUserSpacegoupSpaceSet ( User user ){
		Set<Spacegroup> userSpacegroupSet = user.getSpacegroups();
		Set<Space> userSpaceSet = new LinkedHashSet<Space>();
		if ( null != userSpacegroupSet ){
			for ( Spacegroup spacegroup : userSpacegroupSet ){
				if ( null == spacegroup || null == spacegroup.getSpaces() ){
					continue;
				}
				userSpaceSet.addAll( spacegroup.getSpaces() );
			}
		}
		return userSpaceSet;
	}
	
	@Autowired
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
	@Autowired
	public void setSpacegroupDao(SpacegroupDao spacegroupDao) {
		this.spacegroupDao = spacegroupDao;
	}
	@Autowired
	public void setSpaceService(SpaceService spaceService) {
		this.spaceService = spaceService;
	}
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
