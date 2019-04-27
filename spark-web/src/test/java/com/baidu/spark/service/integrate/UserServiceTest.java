package com.baidu.spark.service.integrate;

import static com.baidu.spark.TestUtils.initDatabase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.spark.dao.Pagination;
import com.baidu.spark.model.User;
import com.baidu.spark.service.UserService;

/**
 * 用户服务类测试用例
 * 
 * @author chenhui
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml","/applicationContext-security-test.xml" })
public class UserServiceTest {

	@Autowired
	private UserService userService;
	
	@Autowired
	private DataSource dataSource;
	
	@Before
	public void before() throws Exception {
		initDatabase(dataSource, "com/baidu/spark/service/integrate/UserServiceTest.xml");
	}
	
	@Test
	public void getAllUsers_smoke(){
		Pagination<User> page = new Pagination<User>();
		page = userService.getAllUsers(page);
		assertNotNull(page.getResults());
		assertEquals( 3, page.getResults().size());
	}
	
	@Test
	public void getUserByUserName_smoke(){
		assertNotNull(userService.getUserByUserName("chenhui"));
	}
	
	@Test
	public void getUserByUserName_Null(){
		assertNull(userService.getUserByUserName(null));
	}
	
	@Test
	public void getUsreByUserName_userNotExist(){
		assertNull(userService.getUserByUserName("xxxx"));
	}
	
	@Test
	public void getSuggestUser_smoke(){
		List<User> users = userService.getSuggestUser("ch", 5);
		assertNotNull(users);
		assertEquals(1, users.size());
	}
	
	@Test
	public void getSuggestUser_EmptyKeyword(){
		List<User> users = userService.getSuggestUser(null, 5);
		assertNotNull(users);
		assertEquals(0, users.size());
	}
	
	@Test
	public void getUsersMatching_smoke(){
		List<User> users = userService.getUsersMatching("");
		assertNotNull(users);
		assertEquals(3, users.size());
	}
	
	@Test
	public void getUserById_smoke(){
		User user = userService.getUserById(1L);
		assertNotNull(user);
		assertEquals("chenhui", user.getUsername());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getUserById_Null(){
		userService.getUserById(null);
	}
	
	@Test
	public void saveUser_smoke(){
		User user = new User("test","test");
		userService.saveUser(user);
		assertNotNull(user.getId());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveUser_NullObject(){
		userService.saveUser(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveUser_NullUsername(){
		userService.saveUser( new User());
	}
	
	
	@Test
	public void checkConflicts_smoke(){
		User user = new User();
		user.setUsername("chenhui");
		user.setLocked(false);
		Assert.assertTrue(userService.checkConflicts(user));
	}
	
	@Test
	public void checkConflicts_smoke_离职(){
		User user = new User();
		user.setUsername("chenhui");
		user.setLocked(true);
		assertTrue(!userService.checkConflicts(user));
	}
	
	@Test
	public void checkConflicts_existUser_sameUser(){
		User user = new User();
		user.setId(1L);
		user.setUsername("chenhui");
		assertTrue(!userService.checkConflicts(user));
	}
	
	@Test
	public void checkConflicts_existUser_differentUser(){
		User user = new User();
		user.setId(2L);
		user.setUsername("chenhui");
		assertTrue(userService.checkConflicts(user));
	}
	
	@Test
	public void getUsersByUicId_smoke(){
		User user = userService.getUserByUicId(8428L);
		assertNotNull(user);
		assertEquals("chenhui", user.getUsername());
	}
	
	
	@Test
	public void checkUidIdConflicts_smoke_exist(){
		User user = new User();
		user.setUsername("chenhui");
		user.setUicId(8888L);
		assertTrue(userService.checkUicIdConflicts(user));
	}
	
	@Test
	public void checkUidIdConflicts_smoke_notexist(){
		User user = new User();
		user.setUsername("chenhui");
		user.setUicId(666L);
		assertTrue(!userService.checkUicIdConflicts(user));
	}
	
	@Test
	public void checkUidIdConflicts_existUser_sameUser(){
		User user = new User();
		user.setId(3L);
		user.setUsername("chenhui");
		user.setUicId(8888L);
		assertTrue(!userService.checkUicIdConflicts(user));
	}
	
	@Test
	public void checkUidIdConflicts_existUser_differentUser(){
		User user = new User();
		user.setId(1L);
		user.setUsername("chenhui");
		user.setUicId(8888L);
		assertTrue(userService.checkUicIdConflicts(user));
	}
	
}

