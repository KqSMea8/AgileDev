package com.baidu.spark.security.integrate;

import static com.baidu.spark.TestUtils.assertReflectionArrayEquals;
import static com.baidu.spark.TestUtils.initDatabase;
import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.spark.SparkTestUtils;
import com.baidu.spark.TestUtils;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.security.SparkAclHandlerService;
import com.baidu.spark.security.SparkPermission;
import com.baidu.spark.security.SparkPermissionEnum;
import com.baidu.spark.security.SparkSystemResource;
import com.baidu.spark.security.principalsid.GroupPrincipalSid;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml" ,"/applicationContext-security-test.xml"})
public class SparkAclHandlerTest {
	
	@Autowired
	SparkAclHandlerService service;

	@Autowired
	private  DataSource dataSource;
	
	private SimpleJdbcTemplate jdbcTemplate;

	@Before
	public void initDb() throws Exception {
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		SparkTestUtils.clearAllTable(dataSource);
		SparkTestUtils.initAclDatabase(dataSource);
		initDatabase(dataSource,
		"com/baidu/spark/service/integrate/PermissionServiceTest.xml");
		jdbcTemplate.update("SET REFERENTIAL_INTEGRITY TRUE");
		User user = new User();
		user.setId(1L);
		user.setUsername("zhangjing_pe");
		TestUtils.setCurrentUser(user);
		
	}
	
	@Test
	public void loadSplitPermission_smoke(){
		Sid sid = new GroupPrincipalSid(1L);
		List<Permission> pp = service.loadSplitPermission(sid, SparkSystemResource.class, SparkSystemResource.getResource().getId());
		assertEquals(1,pp.size());
		assertEquals(pp.get(0).getMask(),SparkPermission.DELETE.getMask());
		pp = service.loadSplitPermission(sid, Space.class, 1L);
		assertEquals(2,pp.size());
		List<Permission> expectResults = new ArrayList<Permission>();
		expectResults.add(SparkPermission.READ);
		expectResults.add(SparkPermission.WRITE);
		assertReflectionArrayEquals(expectResults.toArray(), pp.toArray());
		pp = service.loadSplitPermission(sid, Space.class, 2L);
		assertEquals(0,pp.size());
	}
	
	@Test
	public void loadSplitPermission_sid不存在(){
		Sid sid = new GroupPrincipalSid(12345L);
		List<Permission> pp = service.loadSplitPermission(sid, SparkSystemResource.class, SparkSystemResource.getResource().getId());
		assertEquals(0,pp.size());
	}
	
	@Test
	public void loadSplitPermission_资源不存在(){
		Sid sid = new GroupPrincipalSid(1L);
		List<Permission> pp = service.loadSplitPermission(sid, Space.class, 12345L);
		assertEquals(0,pp.size());
	}
	@Test
	public void loadSplitPermission_关系不存在(){
		Sid sid = new GroupPrincipalSid(1L);
		List<Permission> pp = service.loadSplitPermission(sid, Space.class, 2L);
		assertEquals(0,pp.size());
	}

	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void loadSplitPermission_参数一为空() {
		List<Permission> pp = service.loadSplitPermission(null, SparkSystemResource.class, SparkSystemResource.getResource().getId());
	}

	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void loadSplitPermission_参数二为空() {
		Sid sid = new GroupPrincipalSid(1L);
		List<Permission> pp = service.loadSplitPermission(sid, null, SparkSystemResource.getResource().getId());
	}

	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void loadSplitPermission_参数三为空() {
		Sid sid = new GroupPrincipalSid(1L);
		List<Permission> pp = service.loadSplitPermission(sid, SparkSystemResource.class, null);
	}
	
	@Test
	public void loadMergedPermission(){
		Sid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, SparkSystemResource.class, SparkSystemResource.getResource().getId());
		assertEquals(pp.getMask(), 8);
		pp = service.loadMergedPermission(sid, Space.class, 1L);
		assertEquals(pp.getMask(), 5);
	}
	@Test
	public void loadMergedPermission_nopermission(){
		Sid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
	}
	
	@Test
	public void loadMergedPermission_sid不存在(){
		Sid sid = new GroupPrincipalSid(1234L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
	}
	
	@Test
	public void loadMergedPermission_资源不存在(){
		Sid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2345L);
		assertEquals(pp.getMask(),0);
	}
	
	
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void loadMergedPermissionn_参数一为空() {
		service.loadMergedPermission(null, SparkSystemResource.class, SparkSystemResource.getResource().getId());
	}

	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void loadMergedPermission_参数二为空() {
		Sid sid = new GroupPrincipalSid(1L);
		service.loadMergedPermission(sid, null, SparkSystemResource.getResource().getId());
	}

	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void loadMergedPermission_参数三为空() {
		Sid sid = new GroupPrincipalSid(1L);
		service.loadMergedPermission(sid, SparkSystemResource.class, null);
	}
	
	@Test
	public void updatePermissionList_test(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
		List<Permission> permissionList = new ArrayList<Permission>();
		permissionList.add(SparkPermission.ADMIN);
		permissionList.add(SparkPermission.DELETE);
		
		service.updatePermission(sid,Space.class,2L,permissionList.toArray(new Permission[]{}));
		int mergedMask = getPermissionMask(permissionList);
		assertEquals(mergedMask,service.loadMergedPermission(sid, Space.class, 2L).getMask());
		assertEquals(mergedMask,getPermission(sid, Space.class, 2L));
		
		service.updatePermission(sid,Space.class,3L,permissionList.toArray(new Permission[]{}));
		mergedMask = getPermissionMask(permissionList);
		assertEquals(mergedMask,service.loadMergedPermission(sid, Space.class, 3L).getMask());
		assertEquals(mergedMask,getPermission(sid, Space.class, 3L));
		
		permissionList.add(SparkPermission.CREATE_CHILDREN);
		service.updatePermission(sid,Space.class,3L,permissionList.toArray(new Permission[]{}));
		mergedMask = getPermissionMask(permissionList);
		assertEquals(mergedMask,service.loadMergedPermission(sid, Space.class, 3L).getMask());
		assertEquals(mergedMask,getPermission(sid, Space.class, 3L));
		
	}
	
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void updatePermissionList_参数一为空(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
		List<Permission> permissionList = new ArrayList<Permission>();
		permissionList.add(SparkPermission.ADMIN);
		permissionList.add(SparkPermission.DELETE);
		
		service.updatePermission(null,Space.class,2L,permissionList.toArray(new Permission[]{}));
	}
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void updatePermissionList_参数二为空(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
		List<Permission> permissionList = new ArrayList<Permission>();
		permissionList.add(SparkPermission.ADMIN);
		permissionList.add(SparkPermission.DELETE);
		
		service.updatePermission(sid,null,2L,permissionList.toArray(new Permission[]{}));
	}
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void updatePermissionList_参数三为空(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
		List<Permission> permissionList = new ArrayList<Permission>();
		permissionList.add(SparkPermission.ADMIN);
		permissionList.add(SparkPermission.DELETE);
		
		service.updatePermission(sid,Space.class,null,permissionList.toArray(new Permission[]{}));
	}
	
	@Test
	public void updatePermissionList_参数四为空数组(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
		List<Permission> permissionList = new ArrayList<Permission>();
		service.updatePermission(sid,Space.class,2L,permissionList.toArray(new Permission[]{}));
		
		pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
	}
	@Test
	public void updateMaskList_test(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
		List<Integer> permissionList = new ArrayList<Integer>();
		permissionList.add(SparkPermission.ADMIN.getMask());
		permissionList.add(SparkPermission.DELETE.getMask());
		
		service.updatePermission(sid,Space.class,2L,permissionList);
		int mergedMask = getPermissionMask(permissionList.toArray(new Integer[]{}));
		assertEquals(mergedMask,service.loadMergedPermission(sid, Space.class, 2L).getMask());
		assertEquals(mergedMask,getPermission(sid, Space.class, 2L));
		
		service.updatePermission(sid,Space.class,3L,permissionList);
		mergedMask = getPermissionMask(permissionList.toArray(new Integer[]{}));
		assertEquals(mergedMask,service.loadMergedPermission(sid, Space.class, 3L).getMask());
		assertEquals(mergedMask,getPermission(sid, Space.class, 3L));
		
		permissionList.add(SparkPermission.CREATE_CHILDREN.getMask());
		service.updatePermission(sid,Space.class,3L,permissionList);
		mergedMask = getPermissionMask(permissionList.toArray(new Integer[]{}));
		assertEquals(mergedMask,service.loadMergedPermission(sid, Space.class, 3L).getMask());
		assertEquals(mergedMask,getPermission(sid, Space.class, 3L));
	}
	
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void updateMaskList_参数一为空(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
		List<Integer> permissionList = new ArrayList<Integer>();
		permissionList.add(SparkPermission.ADMIN.getMask());
		permissionList.add(SparkPermission.DELETE.getMask());
		
		service.updatePermission(null,Space.class,2L,permissionList);
	}
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void updateMaskList_参数二为空(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
		List<Integer> permissionList = new ArrayList<Integer>();
		permissionList.add(SparkPermission.ADMIN.getMask());
		permissionList.add(SparkPermission.DELETE.getMask());
		
		service.updatePermission(sid,null,2L,permissionList);
	}
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void updateMaskList_参数三为空(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
		List<Integer> permissionList = new ArrayList<Integer>();
		permissionList.add(SparkPermission.ADMIN.getMask());
		permissionList.add(SparkPermission.DELETE.getMask());
		
		service.updatePermission(sid,Space.class,null,permissionList);
	}
	
	@Test
	public void updateMaskList_参数四为空(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
		List<Integer> permissionList = null;
		
		service.updatePermission(sid,Space.class,2L,permissionList);
		pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
	}
	
	@Test
	public void updateMaskList_参数四为空数组(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
		List<Integer> permissionList = new ArrayList<Integer>();
		
		service.updatePermission(sid,Space.class,2L,permissionList);
		pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
	}
	
	@Test
	public void updateMergedPermission_Smoke(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
		List<Permission> permissionList = new ArrayList<Permission>();
		permissionList.add(SparkPermission.ADMIN);
		permissionList.add(SparkPermission.DELETE);
		
		service.updatePermission(sid,Space.class,2L,getMergedPermission(permissionList));
		int mergedMask = getPermissionMask(permissionList);
		assertEquals(mergedMask,service.loadMergedPermission(sid, Space.class, 2L).getMask());
		assertEquals(mergedMask,getPermission(sid, Space.class, 2L));
		
		service.updatePermission(sid,Space.class,3L,getMergedPermission(permissionList));
		mergedMask = getPermissionMask(permissionList);
		assertEquals(mergedMask,service.loadMergedPermission(sid, Space.class, 3L).getMask());
		assertEquals(mergedMask,getPermission(sid, Space.class, 3L));
		
		permissionList.add(SparkPermission.CREATE_CHILDREN);
		service.updatePermission(sid,Space.class,3L,getMergedPermission(permissionList));
		mergedMask = getPermissionMask(permissionList);
		assertEquals(mergedMask,service.loadMergedPermission(sid, Space.class, 3L).getMask());
		assertEquals(mergedMask,getPermission(sid, Space.class, 3L));
	}
	
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void updateMergedPermission_参数一为空(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
		List<Permission> permissionList = new ArrayList<Permission>();
		permissionList.add(SparkPermission.ADMIN);
		permissionList.add(SparkPermission.DELETE);
		
		service.updatePermission(null,Space.class,2L,getMergedPermission(permissionList));
	}
	
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void updateMergedPermission_参数二为空(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
		List<Permission> permissionList = new ArrayList<Permission>();
		permissionList.add(SparkPermission.ADMIN);
		permissionList.add(SparkPermission.DELETE);
		
		service.updatePermission(sid,null,2L,getMergedPermission(permissionList));
	}
	
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void updateMergedPermission_参数三为空(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		Permission pp = service.loadMergedPermission(sid, Space.class, 2L);
		assertEquals(pp.getMask(),0);
		List<Permission> permissionList = new ArrayList<Permission>();
		permissionList.add(SparkPermission.ADMIN);
		permissionList.add(SparkPermission.DELETE);
		
		service.updatePermission(sid,Space.class,null,getMergedPermission(permissionList));
	}
	
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void updateMergedPermission_参数四为空(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		Permission pp = null;
		service.updatePermission(sid,Space.class,2L,pp);
	}
	
	
	
	@Test
	public void deletePermissionWithSid_smoke(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		int total = jdbcTemplate.queryForInt("select count(*) from acl_entry");
		service.deletePermission(sid, SparkSystemResource.class, SparkSystemResource.getResource().getId());
		int current = jdbcTemplate.queryForInt("select count(*) from acl_entry");
		assertEquals(current,total-1);
		assertEquals(0,getPermission(sid, SparkSystemResource.class, SparkSystemResource.getResource().getId()));
		
		service.deletePermission(sid, Space.class, 1L);
		total = current;
		current = jdbcTemplate.queryForInt("select count(*) from acl_entry");
		assertEquals(current,total-1);
		assertEquals(0,getPermission(sid, Space.class, 1L));
	}
	
	@Test
	public void deletePermissionWithSid_删除sid不存在(){
		GroupPrincipalSid sid = new GroupPrincipalSid(12344L);
		service.deletePermission(sid, Space.class, 1L);
	}
	
	@Test
	public void deletePermissionWithSid_删除资源不存在(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		service.deletePermission(sid, Space.class, 123456L);
	}
	
	@Test
	public void deletePermissionWithSid_删除关系不存在(){
		GroupPrincipalSid sid = new GroupPrincipalSid(5L);
		service.deletePermission(sid, SparkSystemResource.class, SparkSystemResource.getResource().getId());
	}
	
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void deletePermissionWithSid_参数一为空(){
		service.deletePermission(null, SparkSystemResource.class, SparkSystemResource.getResource().getId());
	}
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void deletePermissionWithSid_参数二为空(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		service.deletePermission(sid, null, SparkSystemResource.getResource().getId());
	}
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void deletePermissionWithSid_参数三为空(){
		GroupPrincipalSid sid = new GroupPrincipalSid(1L);
		service.deletePermission(sid, SparkSystemResource.class, null);
	}
	
	@Test
	public void deletePermissionOfResource_smoke(){
		int total = jdbcTemplate.queryForInt("select count(*) from acl_entry");
		service.deletePermission(SparkSystemResource.class, SparkSystemResource.getResource().getId());
		int current = jdbcTemplate.queryForInt("select count(*) from acl_entry");
		assertEquals(current,total-1);
		
		service.deletePermission(Space.class, 1L);
		total = current;
		current = jdbcTemplate.queryForInt("select count(*) from acl_entry");
		assertEquals(current,total-1);
	}
	
	@Test
	public void deletePermissionOfResource_删除资源不存在(){
		service.deletePermission(Space.class, 1L);
		service.deletePermission(Space.class, 1L);
	}
	
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void deletePermissionOfResource_参数一为空(){
		service.deletePermission(null, SparkSystemResource.getResource().getId());
	}
	
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void deletePermissionOfResource_参数二为空(){
		service.deletePermission(SparkSystemResource.class, null);
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
	
	private int getPermissionMask(Integer[] permissionList){
		int mask = 0;
		if(permissionList!=null&&permissionList.length >0){
			for(Integer pp : permissionList){
				mask |= pp;
			}
		}
		return mask;
	}
	
	private Permission getMergedPermission(List<Permission> permissionList){
		CumulativePermission permission = new CumulativePermission();
		if(permissionList!=null&&permissionList.size()>0){
			for(Permission pp: permissionList){
				permission.set(pp);
			}
		}
		return permission;
	}
	
	private int getPermission(PrincipalSid sid,Class<?> objectClass,Serializable objectIdentity){
		return SparkTestUtils.getMask(jdbcTemplate, sid, objectClass, objectIdentity);
	}
	
	private int getClassId(String className){
		return jdbcTemplate.queryForInt("select id from acl_class where class=?",className);
	}
	
	private int getObjectId(int classId,String objectId){
		return jdbcTemplate.queryForInt("select id from acl_object_identity where object_id_class =? and object_id_identity = ?",classId,objectId);
	}
	
	private int getObjectId(int classId,String objectId,int ownerId){
		return jdbcTemplate.queryForInt("select id from acl_object_identity where object_id_class =? and object_id_identity = ? and owner_sid = ?",classId,objectId,ownerId);
	}
	
	private int getOwnerSid(String sid){
		return jdbcTemplate.queryForInt("select id from acl_sid where sid = ?", sid);
	}
}
