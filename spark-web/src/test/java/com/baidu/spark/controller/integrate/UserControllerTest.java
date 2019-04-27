package com.baidu.spark.controller.integrate;

import static com.baidu.spark.TestUtils.initDatabase;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.baidu.spark.TestUtils;
import com.baidu.spark.model.User;
import com.baidu.spark.service.UserService;
import com.baidu.spark.web.UserController;


/**
 * 
 * @author chenhui
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class UserControllerTest {

	@Autowired
	private UserService userService;
	
	@Autowired
	private DataSource dataSource;
	@Autowired
	private SessionFactory sessionFactory;
	
    private UserController controller;

	
	@Before
	public void before() throws Exception{
		initDatabase(dataSource, "com/baidu/spark/service/integrate/UserServiceTest.xml");
		controller = new UserController();
		controller.setUserService(userService);
		TestUtils.openSessionInTest(sessionFactory);
	}
	
	@After
	public void after() throws Exception {
		TestUtils.closeSessionInTest(sessionFactory);
	}
	
	@Test
	public void testGetCreateForm(){
		ModelMap modelMap = new ModelMap();
		assertEquals("users/userform", controller.getCreateForm(modelMap));
		User user = (User)modelMap.get("user");
		assertNotNull(user);
		assertNull(user.getId());
	}
	
	@Test
	public void testGetEditForm(){
		ModelMap modelMap = new ModelMap();
		Long userId = 1L;
		assertEquals("users/userform", controller.getEditForm(userId, modelMap));
		User user = (User)modelMap.get("user");
		assertNotNull(user);
		assertEquals(userId, user.getId());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testListDefault_默认不展示任何用户(){
		ModelMap modelMap = new ModelMap();
		assertEquals("users/userlist",controller.list(null, modelMap));
		List<User> users = (List<User>) modelMap.get("users");
		assertNull(users);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testListAllUsers_当关键字为空时(){
		ModelMap modelMap = new ModelMap();
		assertEquals("users/userlist",controller.list("", modelMap));
		List<User> users = (List<User>) modelMap.get("users");
		assertNotNull(users);
		assertEquals(3, users.size());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testListUsersWithKeyword(){
		ModelMap modelMap = new ModelMap();
		assertEquals("users/userlist",controller.list("chenhui", modelMap));
		List<User> users = (List<User>) modelMap.get("users");
		assertNotNull(users);
		assertEquals(1, users.size());
	}
	
	@Test
	public void testUserFormSubmit_保存新用户正常到编辑页面(){
		ModelMap modelMap = new ModelMap();
		User user = new User ();
		user.setUsername("new user");
		user.setName("陈惠");
		user.setEmail("newuser@baidu.com");
		user.setLocked(false);
		user.setUicId(100L);
		BindingResult result = new BeanPropertyBindingResult(user, "user");
		String view = controller.createFormSubmit(user, result, modelMap);
		assertEquals(0,result.getErrorCount());
		assertNotNull(user.getId());
		assertEquals("redirect:/users/" + user.getId() + "/edit", view);
	}
	
	@Test
	public void testCreateFormSubmit_在职用户名冲突异常(){
		ModelMap modelMap = new ModelMap();
		User user = new User ();
		user.setUsername("chenhui");
		user.setName("新用户");
		user.setEmail("newuser@baidu.com");
		user.setLocked(false);
		user.setUicId(100L);
		BindingResult result = new BeanPropertyBindingResult(user, "user");
		String view = controller.createFormSubmit(user, result, modelMap);
		assertTrue("result has errors",result.hasErrors());
		assertNotNull("username has error",result.getFieldError("username"));
		assertEquals("Duplicate.user.username", result.getFieldError("username").getCode());
		assertEquals("users/userform", view);
	}
	
	@Test
	public void testCreateFormSubmit_与离职用户名冲突正藏(){
		ModelMap modelMap = new ModelMap();
		User user = new User ();
		user.setUsername("lizhi");
		user.setName("离职");
		user.setEmail("lizhi@baidu.com");
		user.setLocked(false);
		user.setUicId(100L);
		BindingResult result = new BeanPropertyBindingResult(user, "user");
		String view = controller.createFormSubmit(user, result, modelMap);
		assertTrue("result no errors",!result.hasErrors());
		assertEquals("redirect:/users/" + user.getId() + "/edit", view);
	}
	
	@Test
	public void testCreateFormSubmit_其他属性为空正常(){
		ModelMap modelMap = new ModelMap();
		User user = new User ();
		user.setUsername("newuser");
		user.setName("新用户");
		BindingResult result = new BeanPropertyBindingResult(user, "user");
		String view = controller.createFormSubmit(user, result, modelMap);
		assertTrue("result no errors",!result.hasErrors());
		assertEquals("redirect:/users/" + user.getId() + "/edit", view);
	}
	
	@Test
	public void testCreateFormSubmit_UICID冲突(){
		ModelMap modelMap = new ModelMap();
		User user = new User ();
		user.setUsername("chenhui");
		user.setName("新用户");
		user.setEmail("newuser@baidu.com");
		user.setLocked(false);
		user.setUicId(8888L);
		BindingResult result = new BeanPropertyBindingResult(user, "user");
		String view = controller.createFormSubmit(user, result, modelMap);
		assertTrue("result has errors",result.hasErrors());
		assertNotNull("uicId has error",result.getFieldError("uicId"));
		assertEquals("Duplicate.user.uicid", result.getFieldError("uicId").getCode());
		assertEquals("users/userform", view);
	}
	
	@Test
	public void testEditUserSubmit_用户名冲突(){
		User user = userService.getUserById(1L);
		user.setUsername("guolin");
		BindingResult result = new BeanPropertyBindingResult(user, "user");
		ModelMap modelMap = new ModelMap();
		String view = controller.editFormSubmit(user, result, modelMap);
		assertTrue("result has errors",result.hasErrors());
		assertNotNull("username has error",result.getFieldError("username"));
		assertEquals("Duplicate.user.username", result.getFieldError("username").getCode());
		assertEquals("users/userform", view);
	}
	
	@Test
	public void testEditUserSubmit_UIC冲突(){
		User user = new User();
		user.setId(1L);
		user.setUsername("chenhui");
		user.setUicId(8888L);
		BindingResult result = new BeanPropertyBindingResult(user, "user");
		ModelMap modelMap = new ModelMap();
		String view = controller.editFormSubmit(user, result, modelMap);
		assertTrue("result has errors",result.hasErrors());
		assertNotNull("uicId has error",result.getFieldError("uicId"));
		assertEquals("Duplicate.user.uicid", result.getFieldError("uicId").getCode());
		assertEquals("users/userform", view);
	}
	
	@Test
	public void testEditUserSubmit_用户锁定(){
		User user = new User();
		user.setUsername("chenhui");
		user.setId(1L);
		user.setLocked(false);
		BindingResult result = new BeanPropertyBindingResult(user, "user");
		ModelMap modelMap = new ModelMap();
		String view = controller.editFormSubmit(user, result, modelMap);
		assertFalse(result.hasErrors());
		assertEquals("redirect:/users/list", view);
	}
	
	@Test
	public void testEditUserSubmit_smoke(){
		User user = userService.getUserById(1L);
		BindingResult result = new BeanPropertyBindingResult(user, "user");
		ModelMap modelMap = new ModelMap();
		String view = controller.editFormSubmit(user, result, modelMap);
		assertFalse(result.hasErrors());
		assertEquals("redirect:/users/list", view);
	}
	
}
