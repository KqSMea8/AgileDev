package com.baidu.spark.security.integrate;

import static com.baidu.spark.TestUtils.initDatabase;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.spark.SparkTestUtils;
import com.baidu.spark.TestUtils;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.security.PermissionService;
import com.baidu.spark.security.SparkAclHandlerService;
import com.baidu.spark.security.SparkPermission;
import com.baidu.spark.security.SparkPermissionEnum;
import com.baidu.spark.security.SparkSystemResource;
import com.baidu.spark.security.principalsid.UserPrincipalSid;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml" ,"/applicationContext-security-test.xml"})
public class PermissionServiceTest {
	
	@Autowired
	PermissionService service;
	
	@Autowired
	SparkAclHandlerService handler;

	@Autowired
	private  DataSource dataSource;
	
	private SimpleJdbcTemplate jdbcTemplate;

	@Before
	public void initDb() throws Exception {
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		SparkTestUtils.initAclDatabase(dataSource);
		SparkTestUtils.clearAllTable(dataSource);
		initDatabase(dataSource,
		"com/baidu/spark/service/integrate/PermissionServiceTest.xml");
		jdbcTemplate.update("SET REFERENTIAL_INTEGRITY TRUE");
		User user = new User();
		user.setId(1L);
		user.setUsername("zhangjing_pe");
		TestUtils.setCurrentUser(user);
		
	}
	
	@Test
	public void createSpaceAcl_smoke(){
		Space space = new Space();
		space.setId(1234L);
		service.createAcl(space);
		int classId = getClassId(Space.class.getName());
		int ownerId = getOwnerSid("user_"+TestUtils.getCurrentUser().getId());
		int objectId = getObjectId(classId, space.getId().toString(), ownerId);
		Assert.assertNotNull(objectId);
	}
	
	@Test
	public void createSpaceAcl_创建已存在的资源(){
		Space space = new Space();
		space.setId(1234L);
		service.createAcl(space);
		service.createAcl(space);
	}
	
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void createSpaceAcl_参数为空(){
		Space space = null;
		service.createAcl(space);
	}
	
	@Test
	public void createCardAcl_smoke(){
		Card card = new Card();
		card.setId(1234L);
		service.createAcl(card);
		int classId = getClassId(Card.class.getName());
		int ownerId = getOwnerSid("user_"+TestUtils.getCurrentUser().getId());
		int objectId = getObjectId(classId, card.getId().toString(), ownerId);
		Assert.assertNotNull(objectId);
	}
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void createCardAcl_参数为空(){
		Card card = null;
		service.createAcl(card);
	}
	
	@Test
	public void createCardAcl_创建已存在的资源(){
		Card card = new Card();
		card.setId(1234L);
		service.createAcl(card);
		service.createAcl(card);
	}
	
	@Test
	public void getUserSpacePermission_smoke(){
		
		Space space = new Space();
		space.setId(2L);
		//避免owner权限的生效
		service.createAcl(space);
		
		for(SparkPermissionEnum pp : SparkPermissionEnum.values()){
			//清理权限
			UserPrincipalSid sid = new UserPrincipalSid(2L);
			handler.updatePermission(sid, Space.class, space.getId(), SparkPermission.NONE);
			
			Permission targetPermission = pp.getPermission();
			List<Permission> permissionList = new ArrayList<Permission>();
			permissionList.add(pp.getPermissionSet());
			handler.updatePermission(sid,Space.class,space.getId(),permissionList.toArray(new Permission[]{}));
			
			List<Permission> result = service.getUserPermission(2L, space);
			Assert.assertTrue(result.size()==splitPermission(pp.getPermissionSet()).size());
			assertEquals(getPermissionMask(result),pp.getPermissionSet().getMask());
		}
		//测试全局权限的情况
		for(SparkPermissionEnum pp : SparkPermissionEnum.values()){
			//清理权限
			UserPrincipalSid sid = new UserPrincipalSid(2L);
			handler.updatePermission(sid, Space.class, space.getId(), SparkPermission.NONE);
			
			Permission targetPermission = pp.getPermission();
			List<Permission> permissionList = new ArrayList<Permission>();
			permissionList.add(pp.getPermissionSet());
			handler.updatePermission(sid,SparkSystemResource.class,SparkSystemResource.getResource().getId(),permissionList.toArray(new Permission[]{}));
			
			List<Permission> result = service.getUserPermission(2L, space);
			Assert.assertTrue(result.size()==splitPermission(pp.getPermissionSet()).size());
			assertEquals(getPermissionMask(result),pp.getPermissionSet().getMask());
		}
		
	}
	
	private int getPermissionMask(List<Permission> permissionList){
		int mask = 0;
		if(permissionList!=null&&permissionList.size()>0){
			for(Permission pp : permissionList){
				mask |= pp.getMask();
			}
		}
		return mask;
	}
	/**
	 * 将权限数据划分成单个权限的集合
	 * 
	 * @param permission
	 * @return
	 */
	private Set<Permission> splitPermission(Permission permission) {
		Set<Permission> permissionList = new LinkedHashSet<Permission>();
		if (permission instanceof CumulativePermission) {
			for (SparkPermissionEnum it : SparkPermissionEnum.values()) {
				if ((permission.getMask() & it.getPermission().getMask()) == it
						.getPermission().getMask()) {
					permissionList.add(it.getPermission());
				}
			}
		} else {
			permissionList.add(permission);
		}
		return permissionList;
	}
	
	private int getClassId(String className){
		return jdbcTemplate.queryForInt("select id from acl_class where class=?",className);
	}
	
	private int getObjectId(int classId,String objectId,int ownerId){
		return jdbcTemplate.queryForInt("select id from acl_object_identity where object_id_class =? and object_id_identity = ? and owner_sid = ?",classId,objectId,ownerId);
	}
	
	private int getOwnerSid(String sid){
		return jdbcTemplate.queryForInt("select id from acl_sid where sid = ?", sid);
	}
}
