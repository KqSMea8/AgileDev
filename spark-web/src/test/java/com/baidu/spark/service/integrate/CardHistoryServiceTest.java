package com.baidu.spark.service.integrate;

import static com.baidu.spark.TestUtils.assertReflectionEquals;
import static com.baidu.spark.TestUtils.clearTable;
import static com.baidu.spark.TestUtils.closeSessionInTest;
import static com.baidu.spark.TestUtils.initDatabase;
import static com.baidu.spark.TestUtils.openSessionInTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.ObjectUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.spark.SparkTestUtils;
import com.baidu.spark.dao.CardDao;
import com.baidu.spark.dao.CardHistoryDao;
import com.baidu.spark.model.Attachment;
import com.baidu.spark.model.OpType;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.history.CardHistory;
import com.baidu.spark.model.card.history.CardHistorySingleDiff;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.model.card.property.CardPropertyValue;
import com.baidu.spark.model.card.property.DateProperty;
import com.baidu.spark.model.card.property.DatePropertyValue;
import com.baidu.spark.model.card.property.ListProperty;
import com.baidu.spark.model.card.property.ListPropertyValue;
import com.baidu.spark.model.card.property.NumberProperty;
import com.baidu.spark.model.card.property.NumberPropertyValue;
import com.baidu.spark.model.card.property.TextProperty;
import com.baidu.spark.model.card.property.TextPropertyValue;
import com.baidu.spark.service.CardHistoryService;
import com.baidu.spark.service.CardService;
import com.baidu.spark.service.CardTypeService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.service.UserService;
import com.baidu.spark.service.impl.helper.CardHistoryDiffHelper;
import com.baidu.spark.util.DateUtils;
import com.baidu.spark.util.ListUtils;
import com.baidu.spark.util.ReflectionUtils;

/**
 * 卡片历史记录service的测试类
 * @author Adun
 * 2010-06-10
 * @author GuoLin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext-test.xml","/applicationContext-security-test.xml"})
public class CardHistoryServiceTest {

	@Autowired
	private CardHistoryService cardHistoryService;
	
	@Autowired
	private CardHistoryDao historyDao;
	
	@Autowired
	private CardDao cardDao;
	
	@Autowired
	private CardService cardService;
	
	@Autowired
	private CardTypeService cardTypeService;
	
	@Autowired
	private SpaceService spaceService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private SimpleJdbcTemplate jdbcTemplate;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Before
	public void before() throws Exception {
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		SparkTestUtils.initAclDatabase(dataSource);
		clearTable(dataSource, "card_property_value");
		clearTable(dataSource, "card_property");
		clearTable(dataSource, "card_type");
		clearTable(dataSource, "card");
		clearTable(dataSource, "users");
		clearTable(dataSource, "space_sequence");
		clearTable(dataSource, "spaces");
		clearTable(dataSource, "card_history");
		initDatabase(dataSource, "com/baidu/spark/service/integrate/CardHistoryServiceTest.xml");
		SparkTestUtils.setCurrentUserAdmin(userService);
		
		openSessionInTest(sessionFactory);
	}
	
	@After
	public void after() throws Exception {
		closeSessionInTest(sessionFactory);
	}
	
	@Test
	public void saveHistory_edit_smoke() throws Exception {
		Space space = spaceService.getSpaceByPrefixCode("spark");
		Card card = cardService.getCardBySpaceAndSeq(space, 1L);
		User user = userService.getUserById(1L);
		card.setTitle(card.getTitle() + "!");
		cardService.updateCard(card);
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		
		// TODO 测试diff_data和data(类型可能会变化)
		System.out.println("****** data: " + actual.getData());
		System.out.println("****** diff_data: " + actual.getDiffData());
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_edit_nochange_smoke() throws Exception {
		Space space = spaceService.getSpaceByPrefixCode("spark");
		Card card = cardService.getCardBySpaceAndSeq(space, 2L);
		User user = userService.getUserById(1L);
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		assertTrue(expected == null);
	}
	
	@Test
	public void saveHistory_add_smoke() throws Exception {
		Card card = createTransientCard();
		User user = userService.getUserById(1L);
		cardDao.save(card);
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Add_Card, user);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		
		// TODO 测试diff_data和data(类型可能会变化)
		System.out.println("****** data: " + actual.getData());
		System.out.println("****** diff_data: " + actual.getDiffData());
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveHistory_nullOpType() {
		User user = userService.getUserById(1L);
		Space space = spaceService.getSpaceByPrefixCode("spark");
		Card card = cardService.getCardBySpaceAndSeq(space, 2L);
		cardHistoryService.saveHistory(card, null, user);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveHistory_add_nullCard() {
		User user = userService.getUserById(1L);
		cardHistoryService.saveHistory(null, OpType.Add_Card, user);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void saveHistory_add_beforeCardSaved() {
		User user = userService.getUserById(1L);
		Card card = createTransientCard();
		List<CardPropertyValue<?>> values = new ArrayList<CardPropertyValue<?>>();
		for (CardProperty property : card.getType().getCardProperties()) {
			values.add(property.generateValue(null));
		}
		
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Add_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_add_cardWithoutSequence() {
		User user = userService.getUserById(1L);
		Card card = createTransientCard();
		card.setSequence(null);
		cardDao.save(card);

		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Add_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_add_cardWithZeroSequence() {
		User user = userService.getUserById(1L);
		Card card = createTransientCard();
		card.setSequence(0L);
		cardDao.save(card);

		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Add_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_add_cardWithNegativeSequence() {
		User user = userService.getUserById(1L);
		Card card = createTransientCard();
		card.setSequence(-1000L);
		cardDao.save(card);

		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Add_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_add_cardWithoutParent() {
		User user = userService.getUserById(1L);
		Card card = createTransientCard();
		card.setParent(null);
		cardDao.save(card);
		
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Add_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_add_cardWithoutDetail() {
		User user = userService.getUserById(1L);
		Card card = createTransientCard();
		card.setDetail(null);
		cardDao.save(card);
		
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Add_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_add_cardWithoutPropertyValues() {
		User user = userService.getUserById(1L);
		Card card = createTransientCardWithoutPropertyValues();
		card.setPropertyValues(null);
		cardDao.save(card);
		
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Add_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_add_cardWithEmptyPropertyValues() {
		User user = userService.getUserById(1L);
		Card card = createTransientCardWithoutPropertyValues();
		card.setPropertyValues(new ArrayList<CardPropertyValue<?>>());
		cardDao.save(card);

		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Add_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_add_cardWithNumberPropertyValue() {
		User user = userService.getUserById(1L);
		Card card = createTransientCardWithoutPropertyValues();
		CardProperty property = cardTypeService.getCardProperty(2L);
		card.addPropertyValue(property.generateValue("100"));
		cardDao.save(card);
		
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Add_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_add_cardWithDatePropertyValue() {
		User user = userService.getUserById(1L);
		Card card = createTransientCardWithoutPropertyValues();
		CardProperty property = cardTypeService.getCardProperty(3L);
		String date = DateUtils.DATE_TIME_PATTERN_FORMAT.format(new Date());
		card.addPropertyValue(property.generateValue(date));
		cardDao.save(card);
		
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Add_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_add_cardWithTextPropertyValue() {
		User user = userService.getUserById(1L);
		Card card = createTransientCardWithoutPropertyValues();
		CardProperty property = cardTypeService.getCardProperty(3L);
		card.addPropertyValue(property.generateValue("so so text!"));
		cardDao.save(card);
		
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Add_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_add_cardWithListPropertyValue() {
		User user = userService.getUserById(1L);
		Card card = createTransientCardWithoutPropertyValues();
		CardProperty property = cardTypeService.getCardProperty(2L);
		card.addPropertyValue(property.generateValue("2"));
		cardDao.save(card);
		
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Add_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_add_cardWithNumberNullValue() {
		User user = userService.getUserById(1L);
		Card card = createTransientCardWithoutPropertyValues();
		for (CardProperty property : card.getType().getCardProperties()) {
			if (NumberProperty.TYPE.equals(property.getType())) {
				card.addPropertyValue(property.generateValue(null));
			}
		}
		cardDao.save(card);

		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Add_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_add_cardWithDateNullValue() {
		User user = userService.getUserById(1L);
		Card card = createTransientCardWithoutPropertyValues();
		for (CardProperty property : card.getType().getCardProperties()) {
			if (DateProperty.TYPE.equals(property.getType())) {
				card.addPropertyValue(property.generateValue(null));
			}
		}
		cardDao.save(card);

		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Add_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_add_cardWithTextNullValue() {
		User user = userService.getUserById(1L);
		Card card = createTransientCardWithoutPropertyValues();
		for (CardProperty property : card.getType().getCardProperties()) {
			if (TextProperty.TYPE.equals(property.getType())) {
				card.addPropertyValue(property.generateValue(null));
			}
		}
		cardDao.save(card);

		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Add_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_add_cardWithListNullValue() {
		User user = userService.getUserById(1L);
		Card card = createTransientCardWithoutPropertyValues();
		for (CardProperty property : card.getType().getCardProperties()) {
			if (ListProperty.TYPE.equals(property.getType())) {
				card.addPropertyValue(property.generateValue(null));
			}
		}
		cardDao.save(card);

		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Add_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveHistory_add_nullUser() {
		Space space = spaceService.getSpaceByPrefixCode("spark");
		Card card = cardService.getCardBySpaceAndSeq(space, 2L);
		cardHistoryService.saveHistory(card, OpType.Add_Card, null);
	}
	
	@Test(expected = IllegalStateException.class)
	public void saveHistory_add_historyAlreadyExists() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(1L);
		cardHistoryService.saveHistory(card, OpType.Add_Card, user);
	}
	
	@Test
	public void saveHistory_add_noModifyOnOriginalCard() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(3L);
		cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		card.setHistoryList(null); // TODO 检测不完整
		
		Card expected = jdbcTemplate.queryForObject("select * from card where id=?", CardRowMapper.INSTANCE, card.getId());
		List<CardPropertyValue<?>> propertyValues = jdbcTemplate.query("select * from card_property_value where card_id = ?", CardPropertyValueRowMapper.INSTANCE, card.getId());
		expected.setPropertyValues(propertyValues);

		assertReflectionEquals(expected, card);
	}
	
	@Ignore("尚未实现删除历史功能")
	@Test(expected = IllegalStateException.class)
	public void saveHistory_delete_historyNotExists() {
//		User user = userService.getUserById(1L);
//		Card card = createTransientCard();
//		cardDao.save(card);
//		cardHistoryService.saveHistory(card, OpType.Delete, user);
	}
	
	@Test
	@Ignore("尚未实现删除历史功能")
	public void saveHistory_delete_beforeCardDelete() {
//		User user = userService.getUserById(1L);
//		Card card = cardDao.get(1L);
//		cardHistoryService.saveHistory(card, OpType.Delete, user);
//		cardDao.delete(1L);
	}
	
	@Ignore("尚未实现删除历史功能")
	@Test(expected = IllegalStateException.class)
	public void saveHistory_delete_afterCardDelete() {
//		User user = userService.getUserById(1L);
//		Card card = cardDao.get(1L);
//		cardDao.delete(1L);
//		cardHistoryService.saveHistory(card, OpType.Delete, user);
	}
	
	@Test(expected = IllegalStateException.class)
	@Ignore
	public void saveHistory_edit_beforeCardSaved() {
		User user = userService.getUserById(1L);
		Card card = createTransientCard();
		cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveHistory_edit_nullCard() {
		User user = userService.getUserById(1L);
		cardHistoryService.saveHistory(null, OpType.Add_Card, user);
	}
	
	//由于Sequence不能修改,故对此字段不作处理
	@Test
	@Ignore
	public void saveHistory_edit_cardWithoutSequence() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(1L);
		card.setSequence(null);
		cardDao.save(card);

		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	//由于Sequence不能修改,故对此字段不作处理
	@Test
	@Ignore
	public void saveHistory_edit_cardWithZeroSequence() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(1L);
		card.setSequence(0L);
		cardDao.save(card);

		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	//由于Sequence不能修改,故对此字段不作处理
	@Test
	@Ignore
	public void saveHistory_edit_cardWithNegativeSequence() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(1L);
		card.setSequence(-1000L);
		cardDao.save(card);

		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_edit_cardWithoutParent() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(2L);
		card.setParent(null);
		cardDao.save(card);
		
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_edit_cardWithoutDetail() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(1L);
		card.setDetail(null);
		cardDao.save(card);
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_edit_cardWithoutPropertyValues() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(1L);
		card.setPropertyValues(null);
		
		//added by Adun 删除字段不记历史了...多修改个其他东西才记. TODO guo
		card.setTitle(card.getTitle() + "1");
		
		cardDao.save(card);
		
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_edit_cardWithEmptyPropertyValues() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(1L);
		card.setPropertyValues(new ArrayList<CardPropertyValue<?>>());
		
		//added by Adun 删除字段不记历史了...多修改个其他东西才记. TODO guo
		card.setTitle(card.getTitle() + "1");
		
		cardDao.save(card);

		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_edit_cardWithNumberPropertyValue() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(1L);
		CardProperty property = cardTypeService.getCardProperty(2L);
		card.addPropertyValue(property.generateValue("100"));
		cardDao.save(card);
		
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_edit_cardWithDatePropertyValue() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(1L);
		CardProperty property = cardTypeService.getCardProperty(3L);
		String date = DateUtils.DATE_TIME_PATTERN_FORMAT.format(new Date());
		card.addPropertyValue(property.generateValue(date));
		cardDao.save(card);
		
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_edit_cardWithTextPropertyValue() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(1L);
		CardProperty property = cardTypeService.getCardProperty(4L);
		card.addPropertyValue(property.generateValue("so so text!"));
		cardDao.save(card);
		
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_edit_cardWithListPropertyValue() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(1L);
		CardProperty property = cardTypeService.getCardProperty(2L);
		card.addPropertyValue(property.generateValue("2"));
		cardDao.save(card);
		
		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_edit_cardWithNumberNullValue() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(1L);
		for (CardProperty property : card.getType().getCardProperties()) {
			if (NumberProperty.TYPE.equals(property.getType())) {
				card.addPropertyValue(property.generateValue(null));
			}
		}
		
		//added by Adun 删除字段不记历史了...多修改个其他东西才记. TODO guo
		card.setTitle(card.getTitle() + "1");
		
		cardDao.save(card);

		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_edit_cardWithDateNullValue() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(1L);
		for (CardProperty property : card.getType().getCardProperties()) {
			if (DateProperty.TYPE.equals(property.getType())) {
				card.addPropertyValue(property.generateValue(null));
			}
		}
		
		//added by Adun 删除字段不记历史了...多修改个其他东西才记. TODO guo
		card.setTitle(card.getTitle() + "1");
		
		cardDao.save(card);

		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_edit_cardWithTextNullValue() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(1L);
		for (CardProperty property : card.getType().getCardProperties()) {
			if (TextProperty.TYPE.equals(property.getType())) {
				card.addPropertyValue(property.generateValue(null));
			}
		}
		
		//added by Adun 删除字段不记历史了...多修改个其他东西才记. TODO guo
		card.setTitle(card.getTitle() + "1");
		
		cardDao.save(card);

		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test
	public void saveHistory_edit_cardWithListNullValue() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(1L);
		for (CardProperty property : card.getType().getCardProperties()) {
			if (ListProperty.TYPE.equals(property.getType())) {
				card.addPropertyValue(property.generateValue(null));
			}
		}
		
		//added by Adun 删除字段不记历史了...多修改个其他东西才记. TODO guo
		card.setTitle(card.getTitle() + "1");
		
		cardDao.save(card);

		CardHistory expected = cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		assertNotNull(expected);
		CardHistory actual = jdbcTemplate.queryForObject("select * from card_history where id=?", CardHistoryRowMapper.INSTANCE, expected.getId());
		assertNotNull(actual);
		assertReflectionEquals(actual, expected);
		compareJsonAndCard(actual.getData(), card);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveHistory_edit_nullUser() {
		Card card = cardDao.get(2L);
		cardHistoryService.saveHistory(card, OpType.Edit_Card, null);
	}
	
	@Test(expected = IllegalStateException.class)
	public void saveHistory_edit_historyNotExists() {
		User user = userService.getUserById(1L);
		Card card = createTransientCard();
		cardDao.save(card);
		
		cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
	}
	
	@Test
	public void saveHistory_edit_noModifyOnOriginalCard() {
		User user = userService.getUserById(1L);
		Card card = cardDao.get(3L);
		cardHistoryService.saveHistory(card, OpType.Edit_Card, user);
		card.setHistoryList(null); // TODO 检测不完整
		
		Card expected = jdbcTemplate.queryForObject("select * from card where id=?", CardRowMapper.INSTANCE, card.getId());
		List<CardPropertyValue<?>> propertyValues = jdbcTemplate.query("select * from card_property_value where card_id = ?", CardPropertyValueRowMapper.INSTANCE, card.getId());
		expected.setPropertyValues(propertyValues);

		assertReflectionEquals(expected, card);
	}

	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeSpace() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeSpace_name() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeSpace_toNull() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeSpace_idToNull() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeSequence() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeSequence_toNull() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeSequence_toZero() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeSequence_toNegative() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType_name() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType_space() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType_properties() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType_properties_toNull() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType_properties_toEmpty() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType_properties_remove() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType_properties_add() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType_properties_removeAndAddSame() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType_properties_removeAndAddDifferent() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType_properties_name() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType_properties_hidden() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType_properties_info() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType_properties_sort() {
		
	}
		
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType_parent() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType_recursive() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType_toNull() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeType_idToNull() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeParent() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeParent_space() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeParent_title() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeParent_type() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeParent_parent() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeParent_otherProperties() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeParent_toNull() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeParent_idToNull() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeChildren_toEmpty() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeChildren_toNull() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeChildren_remove() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeChildren_add() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeChildren_removeAndAddDifferent() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeChildren_removeAndAddSame() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeChildren_properties() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeTitle() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeTitle_toEmpty() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeTitle_toNull() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeDetail() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeDetail_toEmpty() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeDetail_toNull() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changePropertyValues_toEmpty() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changePropertyValues_toNull() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changePropertyValues_remove() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changePropertyValues_add() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changePropertyValues_removeAndAddSame() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changePropertyValues_removeAndAddDifferent() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changePropertyValues_value() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeCreatedUser() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeCreatedTime() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeModifiedUser() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_changeModifiedTime() {
		
	}
	
	@Test
	@Ignore("尚未完成")
	public void saveHistory_edit_concurrent() {

	}
	
	@Test
	public void getAllHistory_smoke(){
		historyDao.save(new CardHistory());
		List<CardHistory> historyList = historyDao.findAll();
		Assert.assertTrue(ListUtils.notEmpty(historyList));
	}
	
	@Test
	public void getCardByHistory_smoke(){
		Space space = spaceService.getSpaceByPrefixCode("spark");
		Card card = cardService.getCardBySpaceAndSeq(space, 2L);
		card.setTitle(card.getTitle() + "!");
		cardService.updateCard(card);
		CardHistory history = cardHistoryService.saveHistory(card, OpType.Edit_Card, userService.getUserByUserName("zhangjing"));
		Card newCard = cardHistoryService.deserializeHistory(history.getData());
		Assert.assertNotNull(newCard);
	}
	
	@Test
	public void diffCard_smoke(){
		Space space = spaceService.getSpaceByPrefixCode("spark");
		Card card2 = cardService.getCardBySpaceAndSeq(space, 2L);
		Card card1 = cardService.getCardBySpaceAndSeq(space, 1L);
		List<CardHistorySingleDiff> diffList = CardHistoryDiffHelper.diffCard(card2, card1);
		Assert.assertNotNull(diffList);
	}
	
	private void compareJsonAndCard(String json, Card card) {
		JsonNode root;
		try {
			root = mapper.readTree(json);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		assertEquals(card.getId(), new Long(root.path("id").getLongValue()));
		assertEquals(null==card.getSpace()?null:card.getSpace().getId(), root.path("space").isNull()?null:new Long(root.path("space").getLongValue()));
		assertEquals(card.getParent() == null ? null : card.getParent().getId(), root.path("parent").isNull()?null:root.path("parent").getLongValue());
		assertEquals(card.getType()==null? null:card.getType().getId(), root.path("type").isNull()?null:new Long(root.path("type").getLongValue()));
		assertEquals(card.getTitle(), root.path("title").isNull()?null:root.path("title").getTextValue());
		assertEquals(card.getDetail(), root.path("detail").isNull()?null:root.path("detail").getTextValue());
		assertEquals(card.getCreatedUser()==null?null:card.getCreatedUser().getId(), root.path("createdUser").isNull()?null:new Long(root.path("createdUser").getLongValue()));
		// TODO assertEquals(card.getCreatedTime().getTime(), root.path("createTime").getTextValue());
		assertEquals(card.getSequence(), root.path("sequence").isNull()?null:new Long(root.path("sequence").getLongValue()));
		
		if (null != card.getPropertyValues()){
			for (int i = 0; i < card.getPropertyValues().size(); i++) {
				CardPropertyValue<?> value = card.getPropertyValues().get(i);
				JsonNode element = root.path("properties").path(i);
				assertEquals(value.getCardProperty().getId(), new Long(element.path("id").getLongValue()));
				assertEquals(value.getCardProperty().getName(), element.path("name").getTextValue());
				
				CardPropertyValue<?> converter = ReflectionUtils.instantiate(value.getClass());
				
				if (value.getCardProperty().getType().equals("text") || value.getCardProperty().getType().equals("list")){
					converter.initValueWithString(element.path("value").isNull()?null:element.path("value").getTextValue());
				}else if(value.getCardProperty().getType().equals("number")){
					converter.initValueWithString(element.path("value").isNull()?null:new Long(element.path("value").getLongValue()).toString());
				}else if(value.getCardProperty().getType().equals("date")){
					converter.initValueWithString(element.path("value").isNull()?null:element.path("value").getTextValue());
				}
				// 使用equals方法的原因：日期格式中，convert.getValue()出来的是个java.util.Date，但value.getValue()出来的是java.sql.Timestamp
				assertTrue(ObjectUtils.equals(converter.getValue(), value.getValue()));
			}
		}
		// TODO 尚未完成
	}
	
	private Card createTransientCard() {
		Card card = createTransientCardWithoutPropertyValues();
		for (CardProperty property : card.getType().getCardProperties()) {
			if (TextProperty.TYPE.equals(property.getType())) {
				card.addPropertyValue(property.generateValue("some text " + Math.random()));
			} else if (NumberProperty.TYPE.equals(property.getType())) {
				card.addPropertyValue(property.generateValue( String.valueOf((int)(Math.random() * 10000)) ));
			} else if (ListProperty.TYPE.equals(property.getType())) {
				card.addPropertyValue(property.generateValue( String.valueOf((int)(Math.random() * 10)) ));
			} else if (DateProperty.TYPE.equals(property.getType())) {
				card.addPropertyValue(property.generateValue(DateUtils.DATE_TIME_PATTERN_FORMAT.format(new Date())));
			}
		}
		return card;
	}
	
	private Card createTransientCardWithoutPropertyValues() {
		User user = userService.getUserById(1L);
		
		Card parent = cardService.getCard(1L);
		CardType type = cardTypeService.getCardType(1L);
		Space space = spaceService.getSpace(1L);
		
		Card card = new Card();
		card.setId(null);
		card.setCreatedTime(new Date());
		card.setCreatedUser(user);
		card.setDetail("some detail");
		card.setLastModifiedTime(new Date());
		card.setLastModifiedUser(user);
		card.setParent(parent);
		// WITHOUT card.setPropertyValues(propertyValues);
		card.setSequence(333L);
		card.setSpace(space);
		card.setTitle("some title");
		card.setType(type);
		
		return card;
	}
	
	private static class CardHistoryRowMapper implements RowMapper<CardHistory> {
		static CardHistoryRowMapper INSTANCE = new CardHistoryRowMapper();
		private CardHistoryRowMapper() {
			
		}
		@Override
		public CardHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
			Card card = new Card();
			card.setId(rs.getLong("card_id"));
			
			User user = new User();
			user.setId(rs.getLong("op_user_id"));
			
			CardHistory history = new CardHistory();
			history.setId(rs.getLong("id"));
			history.setCard(card);
			history.setDetail(rs.getString("detail"));
			history.setDiffData(rs.getString("diff_data"));
			history.setOpType(OpType.values()[rs.getInt("op_type")]);
			history.setTitle(rs.getString("title"));
			history.setUser(user);
			history.setData(rs.getString("data"));
			history.setOpTime(new Date(rs.getTimestamp("op_time").getTime()));
			return history;
		}
	}
	
	private static class CardRowMapper implements RowMapper<Card> {
		static CardRowMapper INSTANCE = new CardRowMapper();
		@Override
		public Card mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			Card card = new Card();
			card.setId(rs.getLong("id"));
			card.setTitle(rs.getString("title"));
			card.setDetail(rs.getString("detail"));
			card.setSequence(rs.getLong("sequence"));
			card.setCreatedTime(rs.getTimestamp("created_time"));
			card.setLastModifiedTime(rs.getTimestamp("last_modified_time"));
			
			Space space = new Space();
			space.setId(rs.getLong("space_id"));
			card.setSpace(space);
			
			CardType cardType = new CardType();
			cardType.setId(rs.getLong("card_type"));
			card.setType(cardType);
			
			Card parent = new Card();
			parent.setId(rs.getLong("super_id"));
			card.setParent(parent);
			
			User creator = new User();
			creator.setId(rs.getLong("created_user"));
			card.setCreatedUser(creator);
			
			User modifier = new User();
			modifier.setId(rs.getLong("last_modified_user"));
			card.setLastModifiedUser(modifier);
			
			// 暂不处理children
			card.setChildren(new HashSet<Card>());
			//card.setChildren(children)
			// 暂不处理attachment
			card.setAttachments(new ArrayList<Attachment>(0));
			
			
			return card;
		}
	}
	
	private static class CardPropertyValueRowMapper implements RowMapper<CardPropertyValue<?>> {
		static CardPropertyValueRowMapper INSTANCE = new CardPropertyValueRowMapper();
		@Override
		public CardPropertyValue<?> mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			CardPropertyValue<?> cardPropertyValue = null;
			String type = rs.getString("type");
			if (type.equals("text")){
				cardPropertyValue = new TextPropertyValue();
			}
			else if (type.equals("date")){
				cardPropertyValue = new DatePropertyValue();
			}
			else if (type.equals("number")) {
				cardPropertyValue = new NumberPropertyValue();
			}
			else if (type.equals("list")) {
				cardPropertyValue = new ListPropertyValue();
			}
			cardPropertyValue.setId(rs.getLong("id"));
			return cardPropertyValue;
		}
		
	}
}
