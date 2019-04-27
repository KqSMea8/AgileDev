package com.baidu.spark.security.integrate;

import static com.baidu.spark.TestUtils.closeSessionInTest;
import static com.baidu.spark.TestUtils.initDatabase;
import static com.baidu.spark.TestUtils.openSessionInTest;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.security.acls.model.Permission;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.spark.SparkTestUtils;
import com.baidu.spark.TestUtils;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.security.SparkAclHandlerService;
import com.baidu.spark.security.SparkPermission;
import com.baidu.spark.security.SparkSystemResource;
import com.baidu.spark.security.voter.AccessPermissionBean;
import com.baidu.spark.security.voter.UserAccessResolver;
import com.baidu.spark.service.CardService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml",
		"/applicationContext-security-test.xml" })
public class UserAccessResolverTest {

	@Autowired
	SparkAclHandlerService service;
	@Autowired
	UserService userService;
	@Autowired
	SpaceService spaceService;

	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	CardService cardService;
	
	@Autowired
	UserAccessResolver resolver;

	@Autowired
	private DataSource dataSource;

	private SimpleJdbcTemplate jdbcTemplate;

	@Before
	public void initDb() throws Exception {
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		SparkTestUtils.clearAllTable(dataSource);
		SparkTestUtils.initAclDatabase(dataSource);
		initDatabase(dataSource,
				"com/baidu/spark/service/integrate/UserAccessResolverTest.xml");
		jdbcTemplate.update("SET REFERENTIAL_INTEGRITY TRUE");
		openSessionInTest(sessionFactory);
	}
	@After
	public void finish(){
		closeSessionInTest(sessionFactory);
	}

	private User setCurrentUser(String userName) {
		User user = userService.getUserByUserName(userName);
		if (user == null) {
			throw new IllegalArgumentException("username not found:" + userName);
		}
		TestUtils.setCurrentUser(user);
		return user;
	}

	private AccessPermissionBean getData(Object obj, Permission permission) {
		List<Permission> pList = new ArrayList<Permission>();
		pList.add(permission);
		AccessPermissionBean data;
		try {
			data = new AccessPermissionBean(true, pList,obj.getClass(),PropertyUtils.getProperty(obj,"id"));
		} catch (IllegalAccessException e) {
			data = new AccessPermissionBean(true, pList,obj.getClass(),null);
		} catch (InvocationTargetException e) {
			data = new AccessPermissionBean(true, pList,obj.getClass(),null);
		} catch (NoSuchMethodException e) {
			data = new AccessPermissionBean(true, pList,obj.getClass(),null);
		}
		return data;
	}

	@Test
	public void isUserIdGranted_DefaultAdminToSystem() {
		User user = setCurrentUser("chenhui");
		assertTrue(resolver.isGranted(user.getId(), getData(SparkSystemResource
				.getResource(), SparkPermission.ADMIN)));
		assertTrue(resolver.isGranted(user.getId(), getData(SparkSystemResource
				.getResource(), SparkPermission.WRITE)));
	}
	
	@Test
	public void isUserIdGranted_DefaultAdminToSpace() {
		User user = setCurrentUser("chenhui");
		Space space = spaceService.getSpace(1L);
		assertTrue(resolver.isGranted(user.getId(), getData(space,SparkPermission.ADMIN)));
		assertTrue(resolver.isGranted(user.getId(), getData(space,SparkPermission.WRITE)));
	}
	
	@Test
	public void isUserIdGranted_DefaultAdminToCard() {
		User user = setCurrentUser("chenhui");
		Card card = cardService.getCard(1L);
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.ADMIN)));
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.WRITE)));
	}

	@Test
	public void isUserIdGranted_SystemToSystemRights() {
		User user = setCurrentUser("systemAdmin");
		assertTrue(resolver.isGranted(user.getId(), getData(SparkSystemResource
				.getResource(), SparkPermission.ADMIN)));
		assertTrue(resolver.isGranted(user.getId(), getData(SparkSystemResource
				.getResource(), SparkPermission.WRITE)));
		user = setCurrentUser("systemWrite");
		assertTrue(resolver.isGranted(user.getId(), getData(SparkSystemResource
				.getResource(), SparkPermission.WRITE)));
		assertTrue(resolver.isGranted(user.getId(), getData(SparkSystemResource
				.getResource(), SparkPermission.READ)));
		user = setCurrentUser("systemRead");
		assertTrue(resolver.isGranted(user.getId(), getData(SparkSystemResource
				.getResource(), SparkPermission.READ)));
		
	}
	
	@Test
	public void isUserIdGranted_SystemToSpaceRights() {
		User user = setCurrentUser("systemAdmin");
		Space space = spaceService.getSpace(1L);
		assertTrue(resolver.isGranted(user.getId(), getData(space, SparkPermission.ADMIN)));
		assertTrue(resolver.isGranted(user.getId(), getData(space, SparkPermission.WRITE)));
		user = setCurrentUser("systemWrite");
		assertTrue(resolver.isGranted(user.getId(), getData(space, SparkPermission.WRITE)));
		assertTrue(resolver.isGranted(user.getId(), getData(space, SparkPermission.READ)));
		user = setCurrentUser("systemRead");
		assertTrue(resolver.isGranted(user.getId(), getData(space, SparkPermission.READ)));
	}

	@Test
	public void isUserIdGranted_SystemToCardRights() {
		User user = setCurrentUser("systemAdmin");
		Card card = cardService.getCard(1L);
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.ADMIN)));
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.WRITE)));
		user = setCurrentUser("systemWrite");
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.WRITE)));
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.READ)));
		user = setCurrentUser("systemRead");
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.READ)));
	}
	
	@Test
	public void isUserIdGranted_SystemOwner() {
		User user = setCurrentUser("systemOwner");
		assertFalse(resolver.isGranted(user.getId(), getData(SparkSystemResource
				.getResource(), SparkPermission.ADMIN)));
		assertFalse(resolver.isGranted(user.getId(), getData(SparkSystemResource
				.getResource(), SparkPermission.WRITE)));
	}
	
	
	@Test
	public void isUserIdGranted_SpaceToSpaceRights() {
		User user = setCurrentUser("space1Admin");
		Space space = spaceService.getSpace(1L);
		assertTrue(resolver.isGranted(user.getId(), getData(space, SparkPermission.ADMIN)));
		assertTrue(resolver.isGranted(user.getId(), getData(space, SparkPermission.WRITE)));
		user = setCurrentUser("space1Write");
		assertTrue(resolver.isGranted(user.getId(), getData(space, SparkPermission.WRITE)));
		assertTrue(resolver.isGranted(user.getId(), getData(space, SparkPermission.READ)));
		user = setCurrentUser("space1Read");
		assertTrue(resolver.isGranted(user.getId(), getData(space, SparkPermission.READ)));
	}
	@Test
	public void isUserIdGranted_SpaceToCardRights() {
		User user = setCurrentUser("space1Admin");
		Card card = cardService.getCard(1L);
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.ADMIN)));
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.WRITE)));
		user = setCurrentUser("space1Write");
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.WRITE)));
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.READ)));
		user = setCurrentUser("space1Read");
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.READ)));
	}
	
	@Test
	public void isUserIdGranted_CardToCardRights() {
		User user = setCurrentUser("card2Read");
		Card card = cardService.getCard(2L);
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.READ)));
	}

	@Test
	public void isUserIdGranted_ParentCardToChildCardRights() {
		User user = setCurrentUser("card1Read");
		Card card = cardService.getCard(2L);
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.READ)));
	}

	@Test
	public void isUserIdGranted_SpaceOwnerToCard() {
		User user = setCurrentUser("space1Owner");
		Card card = cardService.getCard(2L);
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.READ)));
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.ADMIN)));
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.WRITE)));
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.DELETE)));
	}

	@Test
	public void isUserIdGranted_CardOwner() {
		User user = setCurrentUser("card1Owner");
		Card card = cardService.getCard(1L);
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.ADMIN)));
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.WRITE)));
		assertTrue(resolver.isGranted(user.getId(), getData(card, SparkPermission.READ)));
	}

	@Test
	public void isUserIdGranted_CardToSpaceRights() {
		User user = setCurrentUser("card1Read");
		Space space = spaceService.getSpace(1L);
		assertFalse(resolver.isGranted(user.getId(), getData(space, SparkPermission.ADMIN)));
		assertFalse(resolver.isGranted(user.getId(), getData(space, SparkPermission.WRITE)));
		assertFalse(resolver.isGranted(user.getId(), getData(space, SparkPermission.READ)));
	}

	@Test
	public void isUserIdGranted_CardToSystemRights() {
		User user = setCurrentUser("card1Read");
		assertFalse(resolver.isGranted(user.getId(), getData(SparkSystemResource.getResource(), SparkPermission.ADMIN)));
		assertFalse(resolver.isGranted(user.getId(), getData(SparkSystemResource.getResource(), SparkPermission.WRITE)));
		assertFalse(resolver.isGranted(user.getId(), getData(SparkSystemResource.getResource(), SparkPermission.READ)));
	}

	@Test
	public void isUserIdGranted_SpaceToSystemRights() {
		User user = setCurrentUser("space1Admin");
		assertFalse(resolver.isGranted(user.getId(), getData(SparkSystemResource.getResource(), SparkPermission.ADMIN)));
		assertFalse(resolver.isGranted(user.getId(), getData(SparkSystemResource.getResource(), SparkPermission.WRITE)));
		assertFalse(resolver.isGranted(user.getId(), getData(SparkSystemResource.getResource(), SparkPermission.READ)));
	}

	@Test
	public void isUserIdGranted_NoToSystemRights() {
		User user = setCurrentUser("noright");
		assertFalse(resolver.isGranted(user.getId(), getData(SparkSystemResource.getResource(), SparkPermission.ADMIN)));
		assertFalse(resolver.isGranted(user.getId(), getData(SparkSystemResource.getResource(), SparkPermission.WRITE)));
		assertFalse(resolver.isGranted(user.getId(), getData(SparkSystemResource.getResource(), SparkPermission.READ)));

	}

	@Test
	public void isUserIdGranted_NoToSpaceRights() {
		User user = setCurrentUser("noright");
		Space space = new Space();
		space.setId(1L);
		assertFalse(resolver.isGranted(user.getId(), getData(space, SparkPermission.ADMIN)));
		assertFalse(resolver.isGranted(user.getId(), getData(space, SparkPermission.WRITE)));
		assertFalse(resolver.isGranted(user.getId(), getData(space, SparkPermission.READ)));
	}

	@Test
	public void isUserIdGranted_NoToCardRights() {
		User user = setCurrentUser("noright");
		Card card = new Card();
		card.setId(1L);
		assertFalse(resolver.isGranted(user.getId(), getData(card, SparkPermission.ADMIN)));
		assertFalse(resolver.isGranted(user.getId(), getData(card, SparkPermission.WRITE)));
		assertFalse(resolver.isGranted(user.getId(), getData(card, SparkPermission.READ)));
	}

}
