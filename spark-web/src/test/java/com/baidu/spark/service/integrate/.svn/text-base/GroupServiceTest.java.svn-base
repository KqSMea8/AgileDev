package com.baidu.spark.service.integrate;

import static com.baidu.spark.SparkTestUtils.addUserPermission;
import static com.baidu.spark.SparkTestUtils.addUserSystemPermission;
import static com.baidu.spark.SparkTestUtils.initAclDatabase;
import static com.baidu.spark.TestUtils.assertReflectionEquals;
import static com.baidu.spark.TestUtils.clearTable;
import static com.baidu.spark.TestUtils.closeSessionInTest;
import static com.baidu.spark.TestUtils.initDatabase;
import static com.baidu.spark.TestUtils.openSessionInTest;
import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.spark.SparkTestUtils;
import com.baidu.spark.TestUtils;
import com.baidu.spark.model.Group;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.Space.SpaceType;
import com.baidu.spark.model.User;
import com.baidu.spark.security.SparkAclHandlerService;
import com.baidu.spark.security.SparkPermission;
import com.baidu.spark.security.SparkSystemResource;
import com.baidu.spark.security.principalsid.GroupPrincipalSid;
import com.baidu.spark.service.GroupService;
import com.baidu.spark.service.UserService;
import com.mchange.util.AssertException;

/**
 * 用户组接口测试
 * 
 * @author zhangjing_pe
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml","/applicationContext-security-test.xml" })
public class GroupServiceTest {

	@Autowired
	private GroupService groupService;

	@Autowired
	private UserService userService;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private SparkAclHandlerService permissionService;

	private static SimpleJdbcTemplate jdbcTemplate;

	@Before
	public void before() throws Exception {
		clearTable(dataSource, "groups");
		clearTable(dataSource, "card_type");
		clearTable(dataSource, "card_property");
		clearTable(dataSource, "card_property_value");
		clearTable(dataSource, "card");
		clearTable(dataSource, "card_history");
		clearTable(dataSource, "group_user");
		clearTable(dataSource, "group_space");
		clearTable(dataSource, "card_history");
		initAclDatabase(dataSource);
		initDatabase(dataSource,
				"com/baidu/spark/service/integrate/GroupServiceTest.xml");
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		SparkTestUtils.setCurrentUserAdmin(userService);
		openSessionInTest(sessionFactory);
		
	}

	@After
	public void after() throws Exception {
		closeSessionInTest(sessionFactory);
	}
	@Test
	public void saveGroup_空间不为空_保存方法smoke() {
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);group.setName("testGroup");
		groupService.saveGroup(group);
		List<Group> groupFromDB = jdbcTemplate.query("select * from groups", GroupRowMapper.INSTANCE);
		assertEquals(groupFromDB.get(groupFromDB.size()-1).getId(),group.getId());
		Integer groupSpaceNumber = getGroupSpaceMappingSize(group, space);
		assertEquals(groupSpaceNumber.intValue(),1);
	}
	
	@Test
	public void saveGroup_空间不为空_更新方法smoke() {
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);group.setName("testGroup");
		groupService.saveGroup(group);
		space = new Space();
		space.setId(2L);
		group.setOwner(space);
		groupService.saveGroup(group);
		List<Group> groupFromDB = jdbcTemplate.query("select * from groups", GroupRowMapper.INSTANCE);
		assertEquals(groupFromDB.get(groupFromDB.size()-1).getId(),group.getId());
		Integer groupSpaceNumber = getGroupSpaceMappingSize(group, space);
		assertEquals(1,groupSpaceNumber.intValue());
	}
	@Test
	public void saveGroup_emptySpace_smoke(){
		Group group = new Group();
		group.setName("testGroup");
		groupService.saveGroup(group);
		List<Group> groupFromDB = jdbcTemplate.query("select * from groups", GroupRowMapper.INSTANCE);
		assertEquals(groupFromDB.get(groupFromDB.size()-1).getId(),group.getId());
		assertEquals(1,getGroupSpaceMappingSize(group, null).intValue());
//		assertHasSid(jdbcTemplate, 1, GroupPrincipalSid.GROUP_PRINCIPAL_PREFIX+group.getId());
	}
	
	@Test
	public void saveGroupWithPermission_空间不为空_owner与映射一致_smoke(){
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);group.setName("testGroup");
		groupService.saveGroup(group,space,15);
		List<Group> groupFromDB = jdbcTemplate.query("select * from groups", GroupRowMapper.INSTANCE);
		assertEquals(groupFromDB.get(groupFromDB.size()-1).getId(),group.getId());
		Integer groupSpaceNumber = getGroupSpaceMappingSize(group, space);
		assertEquals(groupSpaceNumber.intValue(),1);
		assertEquals(15, getMask(group, Space.class, space.getId()));
	}
	
	@Test
	@Ignore("当前接口不支持这种情况")
	public void saveGroupWithPermission_空间不为空_owner与映射不一致_smoke(){
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		Space space2 = new Space();
		space2.setId(2L);
		group.setOwner(space);group.setName("testGroup");
		groupService.saveGroup(group,space2,15);
		List<Group> groupFromDB = jdbcTemplate.query("select * from groups", GroupRowMapper.INSTANCE);
		assertEquals(groupFromDB.get(groupFromDB.size()-1).getId(),group.getId());
		assertEquals(1,getGroupSpaceMappingSize(group, space).intValue());
		assertEquals(1,getGroupSpaceMappingSize(group, space2).intValue());
		assertEquals(0, getMask(group, Space.class, space.getId()));
		assertEquals(15, getMask(group, Space.class, space2.getId()));
	}
	
	@Test
	public void saveGroupWithPermission_空间为空_owner与映射一致_smoke(){
		Group group = new Group();
		group.setName("testGroup");
		groupService.saveGroup(group,null,15);
		List<Group> groupFromDB = jdbcTemplate.query("select * from groups", GroupRowMapper.INSTANCE);
		assertEquals(groupFromDB.get(groupFromDB.size()-1).getId(),group.getId());
		assertEquals(1,getGroupSpaceMappingSize(group, null).intValue());
		assertEquals(15, getMask(group, SparkSystemResource.class,SparkSystemResource.getResource().getId()));
	}
	
	@Test
	@Ignore("当前接口不支持这种情况")
	public void saveGroupWithPermission_空间为空_owner与映射不一致_smoke(){
		Group group = new Group();
		Space space = null;
		Space space2 = new Space();
		space2.setId(2L);
		group.setOwner(space);group.setName("testGroup");
		groupService.saveGroup(group,space2,15);
		List<Group> groupFromDB = jdbcTemplate.query("select * from groups", GroupRowMapper.INSTANCE);
		assertEquals(groupFromDB.get(groupFromDB.size()-1).getId(),group.getId());
		assertEquals(1,getGroupSpaceMappingSize(group, space).intValue());
		assertEquals(1,getGroupSpaceMappingSize(group, space2).intValue());
		assertEquals(0, getMask(group, SparkSystemResource.class,SparkSystemResource.getResource().getId()));
		assertEquals(15, getMask(group, Space.class, space2.getId()));
	}
	
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	@Test
	public void saveGroupPermission_空间不为空_smoke(){
		Group group = groupService.getGroup(1L);
		Space space = new Space();
		space.setId(2L);
		assertEquals(0,getMask(group, Space.class, space.getId()));
		groupService.saveGroupPermission(group, space, 13);
		assertEquals(13,getMask(group, Space.class, space.getId()));
	}
	
	@Test
	public void saveGroupPermission_空间为空_smoke(){
		Group group = groupService.getGroup(1L);
		assertEquals(0,getMask(group, SparkSystemResource.class, SparkSystemResource.getResource().getId()));
		groupService.saveGroupPermission(group, null, 13);
		assertEquals(13,getMask(group, SparkSystemResource.class, SparkSystemResource.getResource().getId()));
	}
	
	
	@Test
	public void saveGroupPermission_update_空间不为空_smoke(){
		Group group = groupService.getGroup(1L);
		Space space = new Space();
		space.setId(2L);
		assertEquals(0,getMask(group, Space.class, space.getId()));
		groupService.saveGroupPermission(group, space, 13);
		assertEquals(13,getMask(group, Space.class, space.getId()));
		groupService.saveGroupPermission(group, space, 6);
		assertEquals(6,getMask(group, Space.class, space.getId()));
	}
	
	@Test
	public void saveGroupPermission_update_空间为空_smoke(){
		Group group = groupService.getGroup(1L);
		assertEquals(0,getMask(group, SparkSystemResource.class, SparkSystemResource.getResource().getId()));
		groupService.saveGroupPermission(group, null, 13);
		assertEquals(13,getMask(group, SparkSystemResource.class, SparkSystemResource.getResource().getId()));
		groupService.saveGroupPermission(group, null, 6);
		assertEquals(6,getMask(group, SparkSystemResource.class, SparkSystemResource.getResource().getId()));
	}
	
	
	/**
	 * @param group
	 * @param space
	 * @return
	 */
	private Integer getGroupSpaceMappingSize(Group group, Space space) {
		if(space != null){
			Integer groupSpaceNumber = jdbcTemplate
					.queryForInt(
							"select count(*) from group_space where group_id = ? and space_id = ?",
							group.getId(), space.getId());
			return groupSpaceNumber;
		} else {
			Integer groupSpaceNumber = jdbcTemplate
					.queryForInt(
							"select count(*) from group_space where group_id = ? and space_id is null",
							group.getId());
			return groupSpaceNumber;
		}
	}
	@Test
	public void deleteGroupSpaceMapping_空间不为空smoke(){
		Group group = new Group();
		group.setId(1L);
		Space space = new Space();
		space.setId(1L);
		assertEquals(1,getGroupSpaceMappingSize(group, space).intValue());
		groupService.deleteGroupSpaceMapping(group, space);
		assertEquals(0,getGroupSpaceMappingSize(group, space).intValue());
	}
	
	@Test
	public void deleteGroupSpaceMapping_空间为空smoke(){
		Group group = new Group();
		group.setId(1L);
		group.setName("test123");
		groupService.saveGroup(group);
		assertEquals(1,getGroupSpaceMappingSize(group,null).intValue());
		groupService.deleteGroupSpaceMapping(group, null);
		assertEquals(0,getGroupSpaceMappingSize(group, null).intValue());
	}
	@Test
	public void deleteGroup_smoke(){
		Group group = new Group();
		group.setName("nihaoa");
		User user = new User();
		user.setId(1L);
		group.addUser(user);
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);
		groupService.saveGroup(group,space,13);
		groupService.saveGroupPermission(group,null,15);
		List<Group> groupFromDB = jdbcTemplate.query("select * from groups", GroupRowMapper.INSTANCE);
		assertEquals(groupFromDB.get(groupFromDB.size()-1).getId(),group.getId());
		assertEquals(1,jdbcTemplate.queryForInt("select count(*) from group_user where group_id = ?", group.getId()));
		assertEquals(2,jdbcTemplate.queryForInt("select count(*) from group_space where group_id = ?", group.getId()));
		assertEquals(13,getMask(group, Space.class, space.getId()));
		
		groupService.deleteGroup(group);;
		assertEquals(0,jdbcTemplate.queryForInt("select count(*) from groups where id = ?", group.getId()));
		assertEquals(0,jdbcTemplate.queryForInt("select count(*) from group_user where group_id = ?", group.getId()));
		assertEquals(0,jdbcTemplate.queryForInt("select count(*) from group_space where group_id = ?", group.getId()));
		assertEquals(0,getMask(group,  Space.class, space.getId()));
	}
	
	@Test
	public void getGroup_smoke(){
		Group group = new Group();
		group.setName("nihaoa");
		groupService.saveGroup(group);
		Group groupFromDB = jdbcTemplate.queryForObject("select * from groups where id = ?", GroupRowMapper.INSTANCE,group.getId());
		group.setUsers(null);
		groupFromDB.setUsers(null);
		assertReflectionEquals(group,groupFromDB);
	}
	
	@Test
	public void getGroups_smoke(){
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		group.setName("nihaoa");
		group.setOwner(space);
		groupService.saveGroup(group);
		groupService.saveGroupPermission(group,null,12);
		List<Group> groups = groupService.getGroups(space);
		assertEquals(2, groups.size());
		assertEquals(1L,groups.get(0).getId().longValue());
		assertEquals(group.getId().longValue(),groups.get(1).getId().longValue());
		groups = groupService.getGroups(null);
		assertEquals(1, groups.size());
		assertEquals(group.getId().longValue(),groups.get(0).getId().longValue());
	}
	
	
	@Test
	public void saveGroupUserFromGroup() {
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);
		group.setName("testGroup");
		User user1 = userService.getUserById(1L);
		User user2 = userService.getUserById(2L);
		group.addUser(user1);
		group.addUser(user2);
		groupService.saveGroup(group);
		
		List<User> userList = jdbcTemplate.query("select user_id from group_user where group_id = ?", GroupUserRowMapper.INSTANCE,group.getId());
		Assert.assertEquals(2,userList.size());
//		assertReflectionArrayEquals(userList.toArray(), group.getUsers().toArray());
	}

	@Test
	public void removeGroupUserFromGroup() {
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);
		group.setName("testGroup");
		User user1 = userService.getUserById(1L);
		User user2 = userService.getUserById(2L);
		group.addUser(user1);
		group.addUser(user2);
		groupService.saveGroup(group);
		
		List<User> userList = jdbcTemplate.query("select user_id from group_user where group_id = ?", GroupUserRowMapper.INSTANCE,group.getId());
		Assert.assertEquals(2,userList.size());
//		assertReflectionArrayEquals(userList.toArray(), group.getUsers().toArray());
		
		int oldSize = jdbcTemplate.queryForInt("select count(*) from group_user where group_id = ?", group.getId());
		group.removeUser(user2);
		groupService.saveGroup(group);
		int newSize = jdbcTemplate.queryForInt("select count(*) from group_user where group_id = ?", group.getId());
		Assert.assertEquals(newSize, oldSize-1);
		userList = jdbcTemplate.query("select user_id from group_user where group_id = ?", GroupUserRowMapper.INSTANCE,group.getId());
		Assert.assertEquals(1,userList.size());
		group = new Group();
		group.setOwner(space);
		group.setName("testGroup");
		groupService.saveGroup(group);
		userList = jdbcTemplate.query("select user_id from group_user where group_id = ?", GroupUserRowMapper.INSTANCE,group.getId());
		Assert.assertEquals(0,userList.size());
//		assertReflectionArrayEquals(userList.toArray(), group.getUsers().toArray());

	}
	
	////////////////////////////////////////权限测试////////////////////////////////////////////
	@Test
	public void saveGroup_granted(){
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);group.setName("testGroup");
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		SparkTestUtils.addUserPermission(permissionService, user, space, SparkPermission.ADMIN);
		groupService.saveGroup(group);
	}
	
	@Test
	public void saveGroup_notSetSpace_granted(){
		Group group = new Group();
		group.setName("testGroup");
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		SparkTestUtils.addUserSystemPermission(permissionService, user, SparkPermission.ADMIN);
		groupService.saveGroup(group);
	}
	@Test
	public void saveGroup_adminGranted(){
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);group.setName("testGroup");
		SparkTestUtils.addUserPermission(permissionService, TestUtils.getCurrentUser(), space, SparkPermission.ADMIN);
		groupService.saveGroup(group);
	}
	
	@Test
	public void saveGroup_notSetSpace_adminGranted(){
		Group group = new Group();
		group.setName("testGroup");
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		
		SparkTestUtils.addUserSystemPermission(permissionService, user, SparkPermission.ADMIN);
		groupService.saveGroup(group);
	}
	
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void saveGroup_noPermission(){
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);group.setName("testGroup");
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		groupService.saveGroup(group);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void saveGroup_noMatchPermission(){
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);group.setName("testGroup");
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		SparkTestUtils.addUserPermission(permissionService, user, space, SparkPermission.WRITE);
		groupService.saveGroup(group);
	}
	
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void saveGroup_notSetSpace_noPermission(){
		Group group = new Group();
		group.setName("testGroup");
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		groupService.saveGroup(group);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void saveGroup_notSetSpace_noMatchPermission(){
		Group group = new Group();
		group.setName("testGroup");
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		SparkTestUtils.addUserSystemPermission(permissionService, user, SparkPermission.WRITE);
		groupService.saveGroup(group);
	}
	
	@Test
	public void saveGroupWithPermission_granted() {
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);group.setName("testGroup");
		User user = userService.getUserById(1L);
		addUserPermission(permissionService, user, space, SparkPermission.ADMIN);
		groupService.saveGroup(group,space,15);
	}
	@Test
	public void saveGroupWithPermission_adminGranted() {
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);group.setName("testGroup");
		User user = userService.getUserById(1L);
		addUserSystemPermission(permissionService, user, SparkPermission.ADMIN);
		groupService.saveGroup(group,space,15);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void saveGroupWithPermission_denied() {
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		group.setOwner(space);group.setName("testGroup");
		groupService.saveGroup(group,space,15);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void saveGroupWithPermission_notMatchPermission() {
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);group.setName("testGroup");
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		addUserPermission(permissionService, user, space, SparkPermission.WRITE);
		groupService.saveGroup(group,space,15);
	}

	@Test
	public void saveGroupWithPermisson_notSetSpace_adminGranted(){
		Group group = new Group();
		group.setName("testGroup");
		groupService.saveGroup(group,null,15);
	}
	@Test
	public void saveGroupWithPermisson_notSetSpace_granted(){
		Group group = new Group();
		group.setName("testGroup");
		User user = userService.getUserById(1L);
		addUserSystemPermission(permissionService, user, SparkPermission.ADMIN);
		groupService.saveGroup(group,null,15);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void saveGroupWithPermisson_notSetSpace_noPermission(){
		Group group = new Group();
		group.setName("testGroup");
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		groupService.saveGroup(group,null,15);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void saveGroupWithPermisson_notSetSpace_noMatchPermission(){
		Group group = new Group();
		group.setName("testGroup");
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		addUserSystemPermission(permissionService, user, SparkPermission.WRITE);
		groupService.saveGroup(group,null,15);
	}
	
	@Test
	public void saveGroupPermission_granted() {
		Group group = groupService.getGroup(1L);
		Space space = new Space();
		space.setId(2L);
		User user = userService.getUserById(1L);
		addUserPermission(permissionService, user, space, SparkPermission.ADMIN);
		groupService.saveGroupPermission(group,space,15);
	}
	@Test
	public void saveGroupPermission_adminGranted() {
		Group group = groupService.getGroup(1L);
		Space space = new Space();
		space.setId(2L);
		User user = userService.getUserById(1L);
		addUserSystemPermission(permissionService, user, SparkPermission.ADMIN);
		groupService.saveGroupPermission(group,space,15);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void saveGroupPermission_denied() {
		Group group = groupService.getGroup(1L);
		Space space = new Space();
		space.setId(2L);
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		groupService.saveGroupPermission(group,space,15);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void saveGroupPermission_notMatchPermission() {
		Group group = groupService.getGroup(1L);
		Space space = new Space();
		space.setId(2L);
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		addUserPermission(permissionService, user, group.getOwner(), SparkPermission.WRITE);
		groupService.saveGroupPermission(group,space,15);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void saveGroupPermission_notMatchPermission2() {
		Group group = groupService.getGroup(1L);
		Space space = new Space();
		space.setId(2L);
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		addUserPermission(permissionService, user, group.getOwner(), SparkPermission.WRITE);
		groupService.saveGroupPermission(group,space,15);
	}

	@Test
	public void saveGroupPermisson_notSetSpace_adminGranted(){
		Group group = groupService.getGroup(1L);
		group.setName("testGroup");
		groupService.saveGroupPermission(group,null,15);
	}
	@Test
	public void saveGroupPermisson_notSetSpace_granted(){
		Group group = groupService.getGroup(1L);
		group.setName("testGroup");
		User user = userService.getUserById(1L);
		addUserSystemPermission(permissionService, user, SparkPermission.ADMIN);
		groupService.saveGroupPermission(group,null,15);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void saveGroupPermisson_notSetSpace_noPermission(){
		Group group = groupService.getGroup(1L);
		group.setName("testGroup");
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		groupService.saveGroupPermission(group,null,15);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void saveGroupPermisson_notSetSpace_noMatchPermission(){
		Group group = groupService.getGroup(1L);
		group.setName("testGroup");
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		addUserSystemPermission(permissionService, user, SparkPermission.WRITE);
		groupService.saveGroupPermission(group,null,15);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void saveGroupPermisson_notSetSpace_noMatchPermission2(){
		Group group = groupService.getGroup(1L);
		group.setName("testGroup");
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		addUserPermission(permissionService, user,group.getOwner(), SparkPermission.ADMIN);
		groupService.saveGroupPermission(group,null,15);
	}
	
	
	
	@Test
	public void deleteGroupPermission_granted() {
		Group group = groupService.getGroup(1L);
		Space space = new Space();
		space.setId(2L);
		groupService.saveGroupPermission(group,space,15);
		User user = userService.getUserById(1L);
		addUserPermission(permissionService, user, space, SparkPermission.ADMIN);
		groupService.deleteGroupSpaceMapping(group, space);
	}
	@Test
	public void deleteGroupPermission_adminGranted() {
		Group group = groupService.getGroup(1L);
		Space space = new Space();
		space.setId(2L);
		groupService.saveGroupPermission(group,space,15);
		
		User user = userService.getUserById(1L);
		addUserSystemPermission(permissionService, user, SparkPermission.ADMIN);
		groupService.deleteGroupSpaceMapping(group, space);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void deleteGroupPermission_denied() {
		Group group = groupService.getGroup(1L);
		Space space = new Space();
		space.setId(2L);
		groupService.saveGroupPermission(group,space,15);
		
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		groupService.deleteGroupSpaceMapping(group, space);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void deleteGroupPermission_notMatchPermission() {
		Group group = groupService.getGroup(1L);
		Space space = new Space();
		space.setId(2L);
		groupService.saveGroupPermission(group,space,15);
		
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		addUserPermission(permissionService, user, group.getOwner(), SparkPermission.WRITE);
		groupService.deleteGroupSpaceMapping(group, space);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void deleteGroupPermission_notMatchPermission2() {
		Group group = groupService.getGroup(1L);
		Space space = new Space();
		space.setId(2L);
		groupService.saveGroupPermission(group,space,15);
		
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		addUserPermission(permissionService, user, group.getOwner(), SparkPermission.WRITE);
		groupService.deleteGroupSpaceMapping(group, space);
	}

	@Test
	public void deleteGroupPermisson_notSetSpace_adminGranted(){
		Group group = groupService.getGroup(1L);
		group.setName("testGroup");
		groupService.saveGroupPermission(group,null,15);
		groupService.deleteGroupSpaceMapping(group, null);
	}
	@Test
	public void deleteGroupPermisson_notSetSpace_granted(){
		Group group = groupService.getGroup(1L);
		group.setName("testGroup");
		groupService.saveGroupPermission(group,null,15);
		
		User user = userService.getUserById(1L);
		addUserSystemPermission(permissionService, user, SparkPermission.ADMIN);
		groupService.deleteGroupSpaceMapping(group, null);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void deleteGroupPermisson_notSetSpace_noPermission(){
		Group group = groupService.getGroup(1L);
		group.setName("testGroup");
		groupService.saveGroupPermission(group,null,15);
		
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		groupService.deleteGroupSpaceMapping(group, null);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void deleteGroupPermisson_notSetSpace_noMatchPermission(){
		Group group = groupService.getGroup(1L);
		group.setName("testGroup");
		groupService.saveGroupPermission(group,null,15);
		
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		addUserSystemPermission(permissionService, user, SparkPermission.WRITE);
		groupService.deleteGroupSpaceMapping(group, null);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void deleteGroupPermisson_notSetSpace_noMatchPermission2(){
		Group group = groupService.getGroup(1L);
		group.setName("testGroup");
		groupService.saveGroupPermission(group,null,15);
		
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		addUserPermission(permissionService, user,group.getOwner(), SparkPermission.ADMIN);
		groupService.deleteGroupSpaceMapping(group, null);
	}
	
	
	
	@Test
	public void deleteGroup_granted(){
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);group.setName("testGroup");
		groupService.saveGroup(group);
		
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		SparkTestUtils.addUserPermission(permissionService, user, space, SparkPermission.ADMIN);
		groupService.deleteGroup(group);
	}
	
	@Test
	public void deleteGroup_notSetSpace_granted(){
		Group group = new Group();
		group.setName("testGroup");
		groupService.saveGroup(group);
		
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		SparkTestUtils.addUserSystemPermission(permissionService, user, SparkPermission.ADMIN);
		groupService.deleteGroup(group);
	}
	@Test
	public void deleteGroup_adminGranted(){
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);group.setName("testGroup");
		groupService.saveGroup(group);
		
		SparkTestUtils.addUserPermission(permissionService, TestUtils.getCurrentUser(), space, SparkPermission.ADMIN);
		groupService.deleteGroup(group);
	}
	
	@Test
	public void deleteGroup_notSetSpace_adminGranted(){
		Group group = new Group();
		group.setName("testGroup");
		groupService.saveGroup(group);
		
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		SparkTestUtils.addUserSystemPermission(permissionService, user, SparkPermission.ADMIN);
		groupService.deleteGroup(group);
	}
	
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void deleteGroup_noPermission(){
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);group.setName("testGroup");
		groupService.saveGroup(group);
		
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		groupService.deleteGroup(group);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void deleteGroup_noMatchPermission(){
		Group group = new Group();
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);group.setName("testGroup");
		groupService.saveGroup(group);
		
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		SparkTestUtils.addUserPermission(permissionService, user, space, SparkPermission.WRITE);
		groupService.deleteGroup(group);
	}
	
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void deleteGroup_notSetSpace_noPermission(){
		Group group = new Group();
		group.setName("testGroup");
		groupService.saveGroup(group);
		
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		groupService.deleteGroup(group);
	}
	@Test
	@ExpectedException(AccessDeniedException.class)
	public void deleteGroup_notSetSpace_noMatchPermission(){
		Group group = new Group();
		group.setName("testGroup");
		groupService.saveGroup(group);
		
		User user = userService.getUserById(1L);
		TestUtils.setCurrentUser(user);
		SparkTestUtils.addUserSystemPermission(permissionService, user, SparkPermission.WRITE);
		groupService.deleteGroup(group);
	}
	
	
	@Test
	public void deleteGroupsOfSpace_smoke(){
		Group group = new Group();
		group.setName("nihaoa");
		User user = new User();
		user.setId(1L);
		group.addUser(user);
		Space space = new Space();
		space.setId(1L);
		group.setOwner(space);
		groupService.saveGroup(group,space,13);
		groupService.saveGroupPermission(group,null,15);
		
		Group group2 = new Group();
		group2.setName("nihaoa");
		group2.addUser(user);
		group2.setOwner(space);
		groupService.saveGroup(group2,space,13);
		groupService.saveGroupPermission(group2,null,15);
		
		groupService.deleteGroups(space);;
		assertEquals(0,jdbcTemplate.queryForInt("select count(*) from groups where space_id = ?", space.getId()));
		assertEquals(0,jdbcTemplate.queryForInt("select count(*) from group_user where group_id = ?", group.getId()));
		assertEquals(0,jdbcTemplate.queryForInt("select count(*) from group_user where group_id = ?", group2.getId()));
		assertEquals(0,jdbcTemplate.queryForInt("select count(*) from group_space where space_id = ?", space.getId()));
		assertEquals(0,getMask(group,  Space.class, space.getId()));
		assertEquals(0,getMask(group2,  Space.class, space.getId()));
	}
	
	
	@SuppressWarnings("unchecked")
	private int getMask(Group group,Class objIdClass,Serializable objectIdentity){
		GroupPrincipalSid sid = new GroupPrincipalSid(group.getId());
		return SparkTestUtils.getMask(jdbcTemplate, sid, objIdClass, objectIdentity);
	}
	
	private static class GroupRowMapper implements RowMapper<Group> {
		private static final GroupRowMapper INSTANCE = new GroupRowMapper();
		@SuppressWarnings("unused")
		private static final RowMapper<Space> SPACE_MAPPER = new RowMapper<Space>() {
			@Override
			public Space mapRow(ResultSet rs, int rowNum) throws SQLException {
				Space space = new Space();
				space.setId(rs.getLong("id"));
				space.setName(rs.getString("name"));
				space.setPrefixCode(rs.getString("prefix_code"));
				space.setType(SpaceType.values()[rs.getInt("type")]);
				return space;
			}
		};

		@Override
		public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
			Group group = new Group();
			group.setId(rs.getLong("id"));
			group.setName(rs.getString("name"));
			Long spaceId = rs.getLong("space_id");
			if(spaceId!=null&&spaceId!=0){
				Space space = new Space();
				space.setId(spaceId);
				group.setOwner(space);
			}
			group.setLocked(rs.getBoolean("locked"));
			return group;
		}
	}
	private static class GroupUserRowMapper implements RowMapper<User> {
		private static final GroupUserRowMapper INSTANCE = new GroupUserRowMapper();
		private static final RowMapper<User> User_MAPPER = new RowMapper<User>() {
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();
				user.setId(rs.getLong("id"));
				user.setUsername(rs.getString("username"));
				user.setEmail(rs.getString("email"));
				user.setName(rs.getString("name"))
				;
				user.setLocked(rs.getBoolean("locked"));
				user.setUicId(rs.getLong("uic_id"));
				return user;
			}
		};

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			Long userId = rs.getLong("user_id");
			if(userId == null){
				throw new AssertException("null userId found in group_user");
			}
			List<User> user = jdbcTemplate.query("select * from users where id = ?", User_MAPPER,userId);
			if(user!=null){
				return user.get(0);
			}
			return null;
		}
	}
	
}

