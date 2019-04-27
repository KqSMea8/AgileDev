package com.baidu.spark.service.integrate;

import static com.baidu.spark.SparkTestUtils.initAclDatabase;
import static com.baidu.spark.TestUtils.clearTable;
import static com.baidu.spark.TestUtils.closeSessionInTest;
import static com.baidu.spark.TestUtils.initDatabase;
import static com.baidu.spark.TestUtils.openSessionInTest;

import java.util.Collections;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.spark.SparkTestUtils;
import com.baidu.spark.model.Attachment;
import com.baidu.spark.model.Discussion;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.history.CardHistory;
import com.baidu.spark.service.CardHistoryService;
import com.baidu.spark.service.CardService;
import com.baidu.spark.service.NotificationService;
import com.baidu.spark.service.UserService;

/**
 * 通知服务集成测试用例.
 * 
 * @author GuoLin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml","/applicationContext-security-test.xml" })
public class NotificationServiceTest {

	@Autowired
	private NotificationService service;
	
	@Autowired
	private CardService cardService;

	@Autowired
	private CardHistoryService cardHistoryService;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Before
	public void before() throws Exception {
		clearTable(dataSource, "card_property_value");
		clearTable(dataSource, "card_property");
		clearTable(dataSource, "card_type");
		clearTable(dataSource, "card");
		clearTable(dataSource, "card_history");
		clearTable(dataSource, "users");
		clearTable(dataSource, "space_sequence");
		clearTable(dataSource, "spaces");
		initDatabase(dataSource, "com/baidu/spark/service/integrate/NotificationServiceTest.xml");
		initAclDatabase(dataSource);
		SparkTestUtils.setCurrentUserAdmin(userService);
		openSessionInTest(sessionFactory);
	}

	@After
	public void after() throws Exception {
		closeSessionInTest(sessionFactory);
	}
	
	//@Test
	//TODO: fix it
	public void discussionSmoke() {
		Discussion discussion = new Discussion();
		discussion.setContent("卡片内容");
		Card card = cardService.getCard(1L);
		service.send(discussion, "hello", card, "guolin@baidu.com", "chenhui@baidu.com");
	}
	
	//@Test
	//TODO: fix it
	public void cardSmoke() {
		CardHistory history = cardHistoryService.getHistory(2L);
		service.send(history, Collections.<Attachment>emptyList(), "guolin@baidu.com", "chenhui@baidu.com");
	}
	
	@Test
	//TODO delete this method
	//如果一个可用的测试用例也木有,junit会报错.
	public void smoke(){
		
	}
}
