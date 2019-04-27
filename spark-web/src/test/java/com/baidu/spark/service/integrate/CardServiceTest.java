package com.baidu.spark.service.integrate;

import static com.baidu.spark.TestUtils.assertReflectionEquals;
import static com.baidu.spark.TestUtils.clearTable;
import static com.baidu.spark.TestUtils.initDatabase;
import static com.baidu.spark.model.QueryConditionVO.QueryOperationType.BETWEEN;
import static com.baidu.spark.model.QueryConditionVO.QueryOperationType.EQUALS;
import static com.baidu.spark.model.QueryConditionVO.QueryOperationType.LESSTHAN;
import static com.baidu.spark.model.QueryConditionVO.QueryOperationType.LIKE;
import static com.baidu.spark.model.QueryConditionVO.QueryOperationType.MORETHAN;
import static com.baidu.spark.model.QueryConditionVO.QueryOperationType.NOTEQUALS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

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
import com.baidu.spark.TestUtils;
import com.baidu.spark.dao.CardPropertyDao;
import com.baidu.spark.dao.Pagination;
import com.baidu.spark.exception.IndexException;
import com.baidu.spark.index.converter.CardIndexConverter;
import com.baidu.spark.index.engine.CardIndexEngine;
import com.baidu.spark.model.OpType;
import com.baidu.spark.model.QueryConditionVO.QueryOperationType;
import com.baidu.spark.model.QueryVO;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.Space.SpaceType;
import com.baidu.spark.model.User;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.property.CardPropertyValue;
import com.baidu.spark.model.card.property.DatePropertyValue;
import com.baidu.spark.model.card.property.NumberProperty;
import com.baidu.spark.model.card.property.NumberPropertyValue;
import com.baidu.spark.model.card.property.TextProperty;
import com.baidu.spark.model.card.property.TextPropertyValue;
import com.baidu.spark.service.CardHistoryService;
import com.baidu.spark.service.CardService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.service.UserService;
import com.baidu.spark.util.DateUtils;

/**
 * 
 * 卡片服务接口测试
 * 
 * @author chenhui
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml","/applicationContext-security-test.xml" })
public class CardServiceTest {
	
	@Autowired
	private UserService userService;

	@Autowired
	private CardService cardService;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private CardIndexEngine cardIndexService;

	@Autowired
	private CardHistoryService cardHistoryService;

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private SpaceService spaceService;
	
	@Autowired
	private CardPropertyDao cardPropertyDao;

	private SimpleJdbcTemplate jdbcTemplate;

	private final String CARD_SQL_STRING = "select * from card, spaces where  card.space_id = spaces.id ";

	@Before
	public void before() throws Exception {
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		SparkTestUtils.initAclDatabase(dataSource);
		jdbcTemplate.update("SET REFERENTIAL_INTEGRITY FALSE");
		clearTable(dataSource, "card_property_value");
		clearTable(dataSource, "card_property");
		clearTable(dataSource, "card_type");
		clearTable(dataSource, "card");
		clearTable(dataSource, "card_history");
		clearTable(dataSource, "users");
		clearTable(dataSource, "space_sequence");
		clearTable(dataSource, "spaces");
		jdbcTemplate.update("SET REFERENTIAL_INTEGRITY TRUE");
		initDatabase(dataSource,
				"com/baidu/spark/service/integrate/CardServiceTest.xml");
		SparkTestUtils.setCurrentUserAdmin(userService);
		cardIndexService.clearAllIndex();
		TestUtils.openSessionInTest(sessionFactory);
	}

	@After
	public void after() throws Exception {
		TestUtils.closeSessionInTest(sessionFactory);
	}

	/**
	 * @param actural
	 * @param expected
	 */
	private void assertCard(Card actural, Card expected) {
		assertEquals(actural.getTitle(), expected.getTitle());
		assertEquals(actural.getDetail(), expected.getDetail());
		assertEquals(actural.getSequence(), expected.getSequence());
		Assert.assertNotNull(actural.getSpace());
		Assert.assertNotNull(expected.getSpace());
		Assert.assertEquals(actural.getSpace().getId(), expected.getSpace()
				.getId());
	}

	@Test
	public void getCardsBySpaceId_smoke() {
		Pagination<Card> cardPage = cardService.getCardsBySpaceId(1L,
				new Pagination<Card>());
		List<Card> expected = jdbcTemplate.query(CARD_SQL_STRING
				+ " and spaces.id = ? ;", new CardRowMapper(), 1L);

		Assert.assertEquals(expected.size(), cardPage.getTotal());
		Assert.assertEquals(expected.size() / cardPage.getSize() + (expected.size()%cardPage.getSize()==0?0:1), cardPage
				.getTotalPages());
		Assert.assertEquals(cardPage.getResults().size(), expected.size()
				- cardPage.getSize() * (cardPage.getPage() - 1) > 10 ? 10
				: expected.size() - cardPage.getSize()
						* (cardPage.getPage() - 1));
	}

	@Test
	public void getCardBySpaceAndSeq_smoke() {
		Space space = spaceService.getSpaceByPrefixCode("spark");
		Card card = cardService.getCardBySpaceAndSeq(space, 2L);
		List<Card> expected = jdbcTemplate.query(CARD_SQL_STRING
				+ " and spaces.prefix_code = ? and card.sequence = ? ;",
				new CardRowMapper(), "spark", 2L);
		if (card == null) {
			Assert.assertEquals(expected.size(), 0);
			return;
		}
		assertCard(card, expected.get(0));
	}

	@Test
	public void deleteCardById() throws IndexException {
		cardService.initAllCardIndex(true,true);
		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition(CardIndexConverter.ANCESTOR, QueryOperationType.EQUALS, 1);
		int beforeSize = cardService.queryByCardQueryVO(queryVO, 1L, null).size();
		Long cardId = 2L;
		Card card = cardService.getCard(cardId);
		cardService.deleteCard(card);
		card = cardService.getCard(cardId);
		List<Card> expected = jdbcTemplate.query(CARD_SQL_STRING
				+ " and card.id = ?", new CardRowMapper(), cardId);
		assertNull(card);
		List<CardPropertyValue> cardPropertyValueList = jdbcTemplate.query(
				"select * from card_property_value where card_id = ?",
				new CardPropertyValueRowMapper(), cardId);
		assertEquals(0, cardPropertyValueList.size());
		assertEquals(0, expected.size());
		
		Card child1 = jdbcTemplate.queryForObject(CARD_SQL_STRING
				+ " and card.id = ?", new CardRowMapper(), 4L);
		Card child2 = jdbcTemplate.queryForObject(CARD_SQL_STRING
				+ " and card.id = ?", new CardRowMapper(), 5L);
		assertEquals(child1.getParent().getId(),child2.getParent().getId());
		assertTrue(child1.getParent().getId().equals(1L));
		try{
			SparkTestUtils.assertHasObjectIdentity(jdbcTemplate, Card.class, cardId);
		}catch(AssertionError error){
			//pass
		}
		int afterSize = cardService.queryByCardQueryVO(queryVO, 1L, null).size();
		assertEquals(beforeSize,afterSize+1);
	}
	
	@Test
	@Ignore("重构后，暂不可用")
	public void changeParent_ancesterTest()throws IndexException {
		TestUtils.openSessionInTest(sessionFactory);
		cardService.initAllCardIndex(true,true);
		Card card1 = cardService.getCard(2L);
		Card card2 = cardService.getCard(3L);
		QueryVO queryVO1 = new QueryVO();
		queryVO1.addQueryCondition( CardIndexConverter.ANCESTOR, QueryOperationType.EQUALS, 1 );
		QueryVO queryVO2 = new QueryVO();
		queryVO1.addQueryCondition( CardIndexConverter.ANCESTOR, QueryOperationType.EQUALS, 3 );
		int beforeSize1 = cardService.queryByCardQueryVO(queryVO1, 1L, null).size();
		int beforeSize2 = cardService.queryByCardQueryVO(queryVO2, 1L, null).size();
		assertTrue(beforeSize1!=0);
		
		cardHistoryService.saveHistory(cardService.getCard(2L),OpType.Add_Card, TestUtils.getCurrentUser());
//		cardService.changeParent("spark", 2L, 3L);
		int afterSize1 = cardService.queryByCardQueryVO(queryVO1, 1L, null).size();
		int afterSize2 = cardService.queryByCardQueryVO(queryVO2, 1L, null).size();
		assertTrue(beforeSize1+beforeSize2 == afterSize1+afterSize2);
		assertTrue(beforeSize1!= afterSize1);
		
		TestUtils.closeSessionInTest(sessionFactory);
	}

	@Test
	public void saveCard() {
		Card card = cleanSaveCard();
		// validation
		Card sqlCard = jdbcTemplate.queryForObject(CARD_SQL_STRING
				+ " and card.id = ?", new CardRowMapper(), card.getId());

		assertEquals(card.getTitle(), sqlCard.getTitle());
		assertEquals(card.getDetail(), sqlCard.getDetail());
		assertEquals(card.getSequence(), sqlCard.getSequence());
		assertEquals(card.getCreatedTime(), sqlCard.getCreatedTime());
		assertEquals(card.getCreatedUser().getId(), sqlCard.getCreatedUser()
				.getId());
		assertEquals(card.getParent().getId(), sqlCard.getParent().getId());

		List<CardPropertyValue<?>> list = card.getPropertyValues();
		List<CardPropertyValue> cardPropertyValueList = jdbcTemplate.query(
				"select * from card_property_value where card_id = ?",
				new CardPropertyValueRowMapper(), card.getId());

		assertCardPropertyValueList(list, cardPropertyValueList);
		SparkTestUtils.assertHasObjectIdentity(jdbcTemplate, Card.class, card.getId());
	}

	/**
	 * @return
	 */
	private Card cleanSaveCard(String... suffixNames) {
		String suffixName = "";
		if (suffixNames == null) {
			suffixName = "";
		} else {
			for (String suffix : suffixNames) {
				suffixName += suffix;
			}
		}
		if (suffixName == null) {
			suffixName = "";
		}
		Card card = new Card();
		card.setTitle("卡片标题" + suffixName);
		card.setDetail("卡片描述" + suffixName);
		card.setCreatedTime(new Date());
		Space space = new Space();
		space.setId(1L);
		card.setSpace(space);

		User user = new User();
		user.setId(1L);
		card.setCreatedUser(user);

		Card parentCard = new Card();
		parentCard.setId(1L);
		card.setParent(parentCard);

		CardType cardType = new CardType();
		cardType.setId(1L);
		cardType.setLocalId(1L);
		card.setType(cardType);

		List<CardPropertyValue<?>> cpvList = new ArrayList<CardPropertyValue<?>>();
		NumberProperty nn = new NumberProperty();
		nn.setId(1L);
		nn.setLocalId(1L);
		TextProperty tn = new TextProperty();
		tn.setId(2L);
		tn.setLocalId(2L);
		cpvList.add(nn.generateValue("123"));
		cpvList.add(tn.generateValue("321"));
		card.setPropertyValues(cpvList);
		cardService.saveCard(card);
		return card;
	}

	private Card updateCardCustomFieldValue(Card card) {
		for (CardPropertyValue value : card.getPropertyValues()) {
			value.setValue(value.getCardProperty().generateValue(
					value.getValueString() + card.getId()).getValue());
		}
		return card;
	}

	@Test
	public void updateCard() {
		Card card = cardService.getCard(1L);
//		cardHistoryService.saveHistory(card, OpType.Add, card.getCreatedUser());
		card = cardService.getCard(1L);
		User user = new User();
		user.setId(1L);
		card.setLastModifiedUser(user);
		card.setLastModifiedTime(new Date());
		List<CardPropertyValue<?>> cpvList = new ArrayList<CardPropertyValue<?>>();
		NumberProperty nn = new NumberProperty();
		nn.setId(1L);
		TextProperty tn = new TextProperty();
		tn.setId(2L);
		cpvList.add(nn.generateValue("12345667"));
		cpvList.add(tn.generateValue("321数字"));
		card.setPropertyValues(cpvList);
		card.setLastModifiedUser(user);
		cardService.updateCard(card);
		// validation
		Card sqlCard = jdbcTemplate.queryForObject(CARD_SQL_STRING
				+ " and card.id = ?", new CardRowMapper(), card.getId());

		assertEquals(card.getTitle(), sqlCard.getTitle());
		assertEquals(card.getDetail(), sqlCard.getDetail());
		assertEquals(card.getSequence(), sqlCard.getSequence());
		assertEquals(card.getLastModifiedUser().getId(), sqlCard
				.getLastModifiedUser().getId());
		List<CardPropertyValue<?>> list = card.getPropertyValues();
		List<CardPropertyValue> cardPropertyValueList = jdbcTemplate.query(
				"select * from card_property_value where card_id = ?",
				new CardPropertyValueRowMapper(), card.getId());

		assertCardPropertyValueList(list, cardPropertyValueList);
	}

	@Test
	public void getCardById() {
		Card card = cardService.getCard(1L);
		Card sqlCard = jdbcTemplate.queryForObject(CARD_SQL_STRING
				+ " and card.id = ?", new CardRowMapper(), card.getId());
		assertEquals(card.getTitle(), sqlCard.getTitle());
		assertEquals(card.getDetail(), sqlCard.getDetail());
		assertEquals(card.getSequence(), sqlCard.getSequence());
		assertEquals(card.getLastModifiedTime(), sqlCard.getLastModifiedTime());
	}

	@Test
	public void getAllCardsByParent() {
		Card card = new Card();
		card.setId(2L);
		List<Card> cardList = cardService.getAllCardsByParent(card);
		List<Card> expectList = jdbcTemplate.query(CARD_SQL_STRING
				+ " and super_id = ?", new CardRowMapper(), card.getId());
		assertEquals(cardList.size(), expectList.size());
		for (Card card1 : cardList) {
			boolean found = false;
			for (Card card2 : expectList) {
				if (card2.getId().equals(card1.getId())) {
					found = true;
					break;
				}
			}
			assertTrue(found);
		}
	}

	@Test
	public void getAllCardsByParent_bypage() throws IndexException {
		Card card = new Card();
		card.setId(2L);
		Pagination<Card> cardList = cardService.getAllCardsByParent(card,
				new Pagination<Card>());
		List<Card> expectList = jdbcTemplate.query(CARD_SQL_STRING
				+ " and super_id = ?", new CardRowMapper(), card.getId());
		assertEquals(cardList.getResults().size(), expectList.size());
		for (Card card1 : cardList.getResults()) {
			boolean found = false;
			for (Card card2 : expectList) {
				if (card2.getId().equals(card1.getId())) {
					found = true;
					break;
				}
			}
			assertTrue(found);
		}
		assertEquals(cardList.getPage(), 1);
		assertEquals(cardList.getTotalPages(), 1);
	}

	@Test
	public void getCountOfCardsByCardType() {
		assertEquals(new Long(4), cardService.getCountOfCardsByType(2L));
	}

	@Test
	public void getCountOfCardsByParentType() {
		assertEquals(new Long(4), cardService.getCountOfCardsByParentType(2L));
	}

	@Test
	public void getAllRootCards() {
		Space space = spaceService.getSpaceByPrefixCode("spark");
		List<Card> cardList = cardService.getAllRootCards(space);
		List<Card> expectList = jdbcTemplate.query(CARD_SQL_STRING
				+ " and spaces.prefix_code = ? and super_id is null",
				new CardRowMapper(), "spark");
		assertEquals(cardList.size(), expectList.size());
		for (Card card1 : cardList) {
			boolean found = false;
			for (Card card2 : expectList) {
				if (card2.getId().equals(card1.getId())) {
					found = true;
					break;
				}
			}
			assertTrue(found);
		}
	}

	@Test
	public void getAllRootCards_bypage() {
		Space space = spaceService.getSpaceByPrefixCode("spark");
		Pagination<Card> cardList = cardService.getAllRootCards(space,
				new Pagination<Card>());
		List<Card> expectList = jdbcTemplate.query(CARD_SQL_STRING
				+ " and spaces.prefix_code = ?  and super_id is null",
				new CardRowMapper(), "spark");
		assertEquals(cardList.getResults().size(), expectList.size());
		for (Card card1 : cardList.getResults()) {
			boolean found = false;
			for (Card card2 : expectList) {
				if (card2.getId().equals(card1.getId())) {
					found = true;
					break;
				}
			}
			assertTrue(found);
		}
		assertEquals(cardList.getPage(), 1);
		assertEquals(cardList.getTotalPages(), 1);

	}

	/**
	 * 基本lucene测试
	 * 
	 * @throws IndexException
	 */
	@Test
	public void luceneInsertTest() throws IndexException {
		Card card = cleanSaveCard();
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("id", card.getId());
		query.put("title", card.getTitle());
		query.put("detail", card.getDetail());
		query.put(CardIndexConverter.INDEX_CUS_FIELD_PREFIX + "1", 123);
		query.put(CardIndexConverter.INDEX_CUS_FIELD_PREFIX + "2", "321");
		List<Card> cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(1, cardList.size());
	}

	@Test
	public void luceneUpdateTest() throws IndexException {
		Card card = cleanSaveCard();
		// 删除一个，再查询
		Iterator<CardPropertyValue<?>> it = card.getPropertyValues().iterator();
		while (it.hasNext()) {
			CardPropertyValue<?> value = it.next();
			if (value.getCardProperty().getId().equals(2L)) {
				it.remove();
				break;
			}
		}
		User user = new User();
		user.setId(1L);
		card.setLastModifiedUser(user);
		cardService.updateCard(card);
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("id", card.getId());
		query.put("title", card.getTitle());
		query.put("detail", card.getDetail());
		query.put(CardIndexConverter.INDEX_CUS_FIELD_PREFIX + "1", 123);

		List<Card> cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(1, cardList.size());
		query.put(CardIndexConverter.INDEX_CUS_FIELD_PREFIX + "2", "321");
		cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(cardList.size(), 0);
	}

	@Test
	public void luceneDeleteTest() throws IndexException {
		Card card = cleanSaveCard();
		try {
			cardService.deleteCard(card);
		} catch (Exception e) {

		}
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("id", 6L);
		List<Card> cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(cardList.size(), 0);
	}

	@Test
	public void cardquery_null() throws IndexException {
		Card card1 = cleanSaveCard("第一个");
		Card card2 = cleanSaveCard("第二个");
		QueryVO queryVO = new QueryVO();

		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(2, cardList.size());
	}

	@Test
	public void cardquery_StringEquals() throws IndexException {
		Card card1 = cleanSaveCard("第一个");
		Card card2 = cleanSaveCard("第二个");

		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("title",EQUALS, "卡片标题第一个");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());
		assertEquals(card1.getId(), cardList.get(0).getId());

		// 对日期的特殊处理
		queryVO = new QueryVO();
		queryVO.addQueryCondition("createdTime",EQUALS, DateUtils.formatDate(new Date()));
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(2, cardList.size());

		// 对自定义字段的处理
		queryVO = new QueryVO();
		queryVO.addQueryCondition("createdTime",EQUALS, DateUtils.formatDate(new Date()));
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(2, cardList.size());
	}

	@Test
	public void cardquery_StringLike() throws IndexException {
		Card card1 = cleanSaveCard("第一个");
		Card card2 = cleanSaveCard("第二个");

		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("title",LIKE, "一");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());
		assertEquals(card1.getId(), cardList.get(0).getId());

		queryVO = new QueryVO();
		queryVO.addQueryCondition("title",LIKE, "一 第");
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());
		assertEquals(card1.getId(), cardList.get(0).getId());

		queryVO = new QueryVO();
		queryVO.addQueryCondition("title",LIKE, "第");
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(2, cardList.size());

		queryVO = new QueryVO();
		queryVO.addQueryCondition("title",LIKE, "卡片标题第一个");
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());
		assertEquals(card1.getId(), cardList.get(0).getId());
	}

	@Test
	public void cardquery_StringBefore()throws IndexException  {
		Date today = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.DAY_OF_YEAR, -1);
		Date yesterday = c.getTime();
		System.out.println(today.getTime() - yesterday.getTime());
		c.add(Calendar.DAY_OF_YEAR, -1);
		Date thedaybeforeyesterday = c.getTime();

		Card card1 = cleanSaveCard("1");
		Card card2 = cleanSaveCard("2");
		card1.setCreatedTime(yesterday);
		User user = new User();
		user.setId(1L);
		try {
			cardIndexService.updateIndex(card1);
		} catch (IndexException e) {
			e.printStackTrace();
		}

		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("title",LESSTHAN, "卡片标题2");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());
		assertEquals(card1.getId(), cardList.get(0).getId());

		queryVO = new QueryVO();
		queryVO.addQueryCondition("title",LESSTHAN, "卡片标题1");
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(0, cardList.size());
		// 时间的特殊操作
		queryVO = new QueryVO();
		queryVO.addQueryCondition("createdTime",LESSTHAN, DateUtils.formatDate(today));
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());

		queryVO = new QueryVO();
		queryVO.addQueryCondition("createdTime",LESSTHAN, DateUtils.formatDate(today));
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());
		assertEquals(card1.getId(), cardList.get(0).getId());

		queryVO = new QueryVO();
		queryVO.addQueryCondition("createdTime",LESSTHAN, DateUtils.formatDate(thedaybeforeyesterday));
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(0, cardList.size());
	}

	@Test
	public void cardquery_DateBetween()throws IndexException  {
		Date today = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.DAY_OF_YEAR, -1);
		Date yesterday = c.getTime();
		System.out.println(today.getTime() - yesterday.getTime());
		c.add(Calendar.DAY_OF_YEAR, -1);
		Date thedaybeforeyesterday = c.getTime();

		Card card1 = cleanSaveCard("1");
		Card card2 = cleanSaveCard("2");
		card1.setCreatedTime(yesterday);
		User user = new User();
		user.setId(1L);
		try {
			cardIndexService.updateIndex(card1);
		} catch (IndexException e) {
			e.printStackTrace();
		}

		QueryVO queryVO = new QueryVO();
		// 时间的特殊操作
		queryVO = new QueryVO();
		queryVO.addQueryCondition("createdTime",BETWEEN, 
				DateUtils.formatDate(today)+","+DateUtils.formatDate(today));
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());

		queryVO = new QueryVO();
		queryVO.addQueryCondition("createdTime",BETWEEN, 
				DateUtils.formatDate(yesterday)+","+DateUtils.formatDate(today));
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(2, cardList.size());
		
	}

	@Test
	public void cardquery_StringAfter() throws IndexException {
		Date today = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.DAY_OF_YEAR, -1);
		Date yesterday = c.getTime();
		System.out.println(today.getTime() - yesterday.getTime());
		c.add(Calendar.DAY_OF_YEAR, 2);
		Date tomorrow = c.getTime();

		Card card1 = cleanSaveCard("1");
		Card card2 = cleanSaveCard("2");
		card1.setCreatedTime(yesterday);
		User user = new User();
		user.setId(1L);
		try {
			cardIndexService.updateIndex(card1);
		} catch (IndexException e) {
			e.printStackTrace();
		}

		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("title",MORETHAN, "卡片标题1");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());
		assertEquals(card2.getId(), cardList.get(0).getId());

		queryVO = new QueryVO();
		queryVO.addQueryCondition("title",MORETHAN, "卡片标题第二个");
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(0, cardList.size());
		// 时间的特殊操作
		queryVO = new QueryVO();
		queryVO.addQueryCondition("createdTime",MORETHAN, DateUtils.formatDate(yesterday));
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());

		queryVO = new QueryVO();
		queryVO.addQueryCondition("createdTime",MORETHAN, DateUtils.formatDate(yesterday));
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());
		assertEquals(card2.getId(), cardList.get(0).getId());

		queryVO = new QueryVO();
		queryVO.addQueryCondition("createdTime",MORETHAN, DateUtils.formatDate(tomorrow));
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(0, cardList.size());
	}

	@Test
	public void cardquery_NumberEquals() throws IndexException {
		Card card1 = cleanSaveCard("第一个");
		Card card2 = cleanSaveCard("第二个");

		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition(CardIndexConverter.CARD_CREATED_USER_ID,
				EQUALS, "zhangjing");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(2, cardList.size());

		queryVO = new QueryVO();
		queryVO.addQueryCondition("id",EQUALS, card1.getId());
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());
		assertEquals(card1.getId(), cardList.get(0).getId());
	}

	@Test
	public void cardquery_NumberBefore() throws IndexException {
		Card card1 = cleanSaveCard("第一个");
		Card card2 = cleanSaveCard("第二个");

		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("id",LESSTHAN, (card2.getId() + 1));
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(2, cardList.size());

		queryVO = new QueryVO();
		queryVO.addQueryCondition("id",LESSTHAN, card2.getId());
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());
		assertEquals(card1.getId(), cardList.get(0).getId());

		queryVO = new QueryVO();
		queryVO.addQueryCondition("id",LESSTHAN, card1.getId());
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(0, cardList.size());
	}

	@Test
	public void cardquery_NumberAfter() throws IndexException {
		Card card1 = cleanSaveCard("第一个");
		Card card2 = cleanSaveCard("第二个");

		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("id",MORETHAN, card1.getId()-1);
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(2, cardList.size());

		queryVO = new QueryVO();
		queryVO.addQueryCondition("id",MORETHAN, card1.getId());
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());
		assertEquals(card2.getId(), cardList.get(0).getId());

		queryVO = new QueryVO();
		queryVO.addQueryCondition("id",MORETHAN, card2.getId());
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(0, cardList.size());
	}
	
	@Test
	public void cardquery_User_smoke() throws IndexException {
		cardService.initAllCardIndex(true, true);

		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("createdUser",EQUALS, "zhangjing");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(4, cardList.size());
		for(Card card : cardList){
			assertTrue("zhangjing".equals(card.getCreatedUser().getUsername()));
		}
	}
	
	@Test
	public void cardquery_User_两个是条件取并() throws IndexException {
		cardService.initAllCardIndex(true, true);

		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("createdUser",EQUALS, "zhangjing");
		queryVO.addQueryCondition("createdUser",EQUALS, "shixiaolei");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(5, cardList.size());
		for(Card card : cardList){
			assertTrue("zhangjing".equals(card.getCreatedUser().getUsername()) || "shixiaolei".equals(card.getCreatedUser().getUsername()));
		}
	}
	
	//@Test
	//TODO: fix it
	public void cardquery_User_单个User取非() throws IndexException {
		cardService.initAllCardIndex(true, true);
		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("createdUser",EQUALS, "zhangjing");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(6, cardList.size());
		for(Card card : cardList){
			assertTrue(!"zhangjing".equals(card.getCreatedUser().getUsername()));
		}
	}
	
	//@Test
	//TODO: fix it
	public void cardquery_User_两个非条件的取交() throws IndexException {
		cardService.initAllCardIndex(true, true);
		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("createdUser",EQUALS, "zhangjing");
		queryVO.addQueryCondition("createdUser",EQUALS, "shixiaolei");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(5, cardList.size());
		for(Card card : cardList){
			assertTrue(!"zhangjing".equals(card.getCreatedUser().getUsername()) && !"shixiaolei".equals(card.getCreatedUser().getUsername()));
		}
	}
	
	//@Test
	//TODO: fix it
	public void cardquery_User_一个是条件一个非条件取交() throws IndexException {
		cardService.initAllCardIndex(true, true);
		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("createdUser",EQUALS, "zhangjing");
		queryVO.addQueryCondition("createdUser",EQUALS, "shixiaolei");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());
		for(Card card : cardList){
			assertTrue("shixiaolei".equals(card.getCreatedUser().getUsername()));
		}
	}

	//@Test
	//TODO: fix it
	public void cardquery_User_User条件与其它条件的组合() throws IndexException {
		cardService.initAllCardIndex(true, true);
		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("createdUser",EQUALS, "zhangjing");
		queryVO.addQueryCondition("title",LIKE, "1");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());
		for(Card card : cardList){
			assertTrue("zhangjing".equals(card.getCreatedUser().getUsername()));
		}
		
		queryVO = new QueryVO();
		queryVO.addQueryCondition("createdUser",EQUALS, "zhangjing");
		queryVO.addQueryCondition("createdUser",EQUALS, "shixiaolei");
		queryVO.addQueryCondition("title",LIKE, "我是");
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(5, cardList.size());
		for(Card card : cardList){
			assertTrue(!"zhangjing".equals(card.getCreatedUser().getUsername()) && !"shixiaolei".equals(card.getCreatedUser().getUsername()));
		}
		
	}
	
	
	@Test
	public void cardquery_List_smoke() throws IndexException {
		cardService.initAllCardIndex(true, true);

		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("3",EQUALS, "97ddb81c-3851-437c-8949-aab203f9f0e5");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(2, cardList.size());
	}
	
	@Test
	public void cardquery_List_两个是条件取并() throws IndexException {
		cardService.initAllCardIndex(true, true);

		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("3",EQUALS, "97ddb81c-3851-437c-8949-aab203f9f0e5");
		queryVO.addQueryCondition("3",EQUALS, "1302536f-60d7-415d-9e84-1ac102c57e44");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(5, cardList.size());
	}
	
	@Test
	public void cardquery_List_单个User取非() throws IndexException {
		cardService.initAllCardIndex(true, true);
		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("3",NOTEQUALS, "97ddb81c-3851-437c-8949-aab203f9f0e5");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(8, cardList.size());
	}
	
	@Test
	public void cardquery_List_两个非条件的取交() throws IndexException {
		cardService.initAllCardIndex(true, true);
		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("3",NOTEQUALS, "97ddb81c-3851-437c-8949-aab203f9f0e5");
		queryVO.addQueryCondition("3",NOTEQUALS, "1302536f-60d7-415d-9e84-1ac102c57e44");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(5, cardList.size());
	}
	
	@Test
	public void cardquery_List_一个是条件一个非条件取交() throws IndexException {
		cardService.initAllCardIndex(true, true);
		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("3",NOTEQUALS, "97ddb81c-3851-437c-8949-aab203f9f0e5");
		queryVO.addQueryCondition("3",EQUALS, "1302536f-60d7-415d-9e84-1ac102c57e44");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(3, cardList.size());
	}

	@Test
	public void cardquery_List_List条件与其它条件的组合() throws IndexException {
		cardService.initAllCardIndex(true, true);
		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition("createdUser",EQUALS, "zhangjing");
		queryVO.addQueryCondition("3",EQUALS, "97ddb81c-3851-437c-8949-aab203f9f0e5");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());
		
		queryVO = new QueryVO();
		queryVO.addQueryCondition("3",NOTEQUALS, "97ddb81c-3851-437c-8949-aab203f9f0e5");
		queryVO.addQueryCondition("3",NOTEQUALS, "1302536f-60d7-415d-9e84-1ac102c57e44");
		queryVO.addQueryCondition("title",LIKE, "我是");
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(5, cardList.size());
		
		queryVO = new QueryVO();
		queryVO.addQueryCondition("3",NOTEQUALS, "97ddb81c-3851-437c-8949-aab203f9f0e5");
		queryVO.addQueryCondition("3",NOTEQUALS, "1302536f-60d7-415d-9e84-1ac102c57e44");
		queryVO.addQueryCondition("title",LIKE, "我是");
		queryVO.addQueryCondition("createdUser",NOTEQUALS, "zhangjing");
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(5, cardList.size());
		 
		
	} 
	
	@Test
	public void cardquery_StringSort()throws IndexException  {
		Card card1 = cleanSaveCard("12");
		Card card2 = cleanSaveCard("2");

		QueryVO queryVO = new QueryVO();
		queryVO.addQuerySort("title");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(2, cardList.size());
		assertEquals(card1.getId(), cardList.get(0).getId());
		assertEquals(card2.getId(), cardList.get(1).getId());

		queryVO = new QueryVO();
		queryVO.addQuerySort("title",true);
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(2, cardList.size());
		assertEquals(card2.getId(), cardList.get(0).getId());
		assertEquals(card1.getId(), cardList.get(1).getId());
	}

	@Test
	public void cardquery_NumberSort() throws IndexException {
		Card card1 = cleanSaveCard("1");
		Card card2 = cleanSaveCard("2");

		QueryVO queryVO = new QueryVO();
		queryVO.addQuerySort("id");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(2, cardList.size());
		assertEquals(card1.getId(), cardList.get(0).getId());
		assertEquals(card2.getId(), cardList.get(1).getId());

		queryVO = new QueryVO();
		queryVO.addQuerySort("id",true);
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(2, cardList.size());
		assertEquals(card2.getId(), cardList.get(0).getId());
		assertEquals(card1.getId(), cardList.get(1).getId());
	}

	@Test
	public void cardquery_TimeSort() throws IndexException {
		Card card1 = cleanSaveCard("1");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		Card card2 = cleanSaveCard("2");

		QueryVO queryVO = new QueryVO();
		queryVO.addQuerySort("createdTime");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(2, cardList.size());
		assertEquals(card1.getId(), cardList.get(0).getId());
		assertEquals(card2.getId(), cardList.get(1).getId());

		queryVO = new QueryVO();
		queryVO.addQuerySort("createdTime",true);
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(2, cardList.size());
		assertEquals(card2.getId(), cardList.get(0).getId());
		assertEquals(card1.getId(), cardList.get(1).getId());
	}

	@Test
	public void cardquery_CombinationSort() throws IndexException {
		Card card1 = cleanSaveCard("1");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		Card card2 = cleanSaveCard("2");
		Card card3 = cleanSaveCard("2");
		User user = new User();
		user.setId(2L);
		card3.setCreatedUser(user);
		try {
			cardIndexService.updateIndex(card3);
		} catch (IndexException e) {
			e.printStackTrace();
		}

		QueryVO queryVO = new QueryVO();
		queryVO.addQuerySort("title",true);
		queryVO.addQuerySort(CardIndexConverter.CARD_CREATED_USER_ID,true);
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(3, cardList.size());
		assertEquals(card3.getId(), cardList.get(0).getId());
		assertEquals(card2.getId(), cardList.get(1).getId());
		assertEquals(card1.getId(), cardList.get(2).getId());
	}

	@Test
	public void cardquery_QueryAndSortAndPage()throws IndexException  {
		Card card1 = cleanSaveCard("123");
		User user = new User();
		user.setId(2L);
		card1.setCreatedUser(user);
		try {
			cardIndexService.updateIndex(card1);
		} catch (IndexException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		Card card2 = cleanSaveCard("234");
		card2.setLastModifiedUser(user);
		try {
			cardIndexService.updateIndex(card2);
		} catch (IndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Card card3 = cleanSaveCard("234");
		Card card4 = cleanSaveCard("4");

		QueryVO queryVO = new QueryVO();
		queryVO.addQuerySort("title",true);
		queryVO.addQuerySort(CardIndexConverter.CARD_LAST_MODIFIED_USER_ID ,true);
		queryVO.addQueryCondition(CardIndexConverter.CARD_CREATED_USER_ID,EQUALS, "zhangjing");
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(3, cardList.size());
		assertEquals(card4.getId(), cardList.get(0).getId());
		assertEquals(card2.getId(), cardList.get(1).getId());
		assertEquals(card3.getId(), cardList.get(2).getId());
		// 分页测试
		Pagination<Card> page = new Pagination<Card>(1, 1);
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, page);
		assertEquals(1, cardList.size());
		assertEquals(3, page.getTotal());
		assertEquals(3, page.getTotalPages());
		assertEquals(card4.getId(), page.getResults().get(0).getId());
		page = new Pagination<Card>(1, 2);
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, page);
		assertEquals(1, cardList.size());
		assertEquals(3, page.getTotal());
		assertEquals(3, page.getTotalPages());
		assertEquals(card2.getId(), page.getResults().get(0).getId());
		page = new Pagination<Card>(1, 3);
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, page);
		assertEquals(1, cardList.size());
		assertEquals(3, page.getTotal());
		assertEquals(3, page.getTotalPages());
		assertEquals(card3.getId(), page.getResults().get(0).getId());

		page = new Pagination<Card>(2, 1);
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, page);
		assertEquals(2, cardList.size());
		assertEquals(3, page.getTotal());
		assertEquals(2, page.getTotalPages());
		assertEquals(card4.getId(), page.getResults().get(0).getId());
		assertEquals(card2.getId(), page.getResults().get(1).getId());
		page = new Pagination<Card>(2, 2);
		cardList = cardService.queryByCardQueryVO(queryVO, 1L, page);
		assertEquals(1, cardList.size());
		assertEquals(3, page.getTotal());
		assertEquals(2, page.getTotalPages());
		assertEquals(card3.getId(), page.getResults().get(0).getId());
	}

	@Test
	public void cardquery_CustomNumberEquals()throws IndexException  {
		Card card1 = cleanSaveCard("第一个");
		Card card2 = cleanSaveCard("第二个");
		updateCardCustomFieldValue(card1);
		updateCardCustomFieldValue(card2);
		try {
			cardIndexService.updateIndex(card1);
			cardIndexService.updateIndex(card2);
		} catch (IndexException e) {
			e.printStackTrace();
		}

		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition(CardIndexConverter.INDEX_CUS_FIELD_PREFIX+"1",
				EQUALS, "123" + card1.getId());
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());
		assertEquals(card1.getId(), cardList.get(0).getId());
	}

	@Test
	public void cardquery_CustomTextEquals()throws IndexException  {
		Card card1 = cleanSaveCard("第一个");
		Card card2 = cleanSaveCard("第二个");
		updateCardCustomFieldValue(card1);
		updateCardCustomFieldValue(card2);
		try {
			cardIndexService.updateIndex(card1);
			cardIndexService.updateIndex(card2);
		} catch (IndexException e) {
			e.printStackTrace();
		}

		QueryVO queryVO = new QueryVO();
		queryVO.addQueryCondition(CardIndexConverter.INDEX_CUS_FIELD_PREFIX + "2",
				EQUALS,"321" + card1.getId());
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(1, cardList.size());
		assertEquals(card1.getId(), cardList.get(0).getId());
	}

	@Test
	public void cardquery_CustomTextSort() throws IndexException {
		Card card1 = cleanSaveCard("第一个");
		Card card2 = cleanSaveCard("第二个");
		updateCardCustomFieldValue(card1);
		updateCardCustomFieldValue(card2);
		try {
			cardIndexService.updateIndex(card1);
			cardIndexService.updateIndex(card2);
		} catch (IndexException e) {
			e.printStackTrace();
		}

		QueryVO queryVO = new QueryVO();
		queryVO.addQuerySort(CardIndexConverter.INDEX_CUS_FIELD_PREFIX + "2", true);
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(2, cardList.size());
		assertEquals(card2.getId(), cardList.get(0).getId());
		assertEquals(card1.getId(), cardList.get(1).getId());
	}

	@Test
	public void cardquery_CustomNumberSort()throws IndexException  {
		Card card1 = cleanSaveCard("第一个");
		Card card2 = cleanSaveCard("第二个");
		updateCardCustomFieldValue(card1);
		updateCardCustomFieldValue(card2);
		try {
			cardIndexService.updateIndex(card1);
			cardIndexService.updateIndex(card2);
		} catch (IndexException e) {
			e.printStackTrace();
		}

		QueryVO queryVO = new QueryVO();
		queryVO.addQuerySort( CardIndexConverter.INDEX_CUS_FIELD_PREFIX + "1", true);
		List<Card> cardList = cardService.queryByCardQueryVO(queryVO, 1L, null);
		assertEquals(2, cardList.size());
		assertEquals(card2.getId(), cardList.get(0).getId());
		assertEquals(card1.getId(), cardList.get(1).getId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void initSpaceCardIndex_spaceIdIsNull() {
		cardService.initSpaceCardIndex(null,true,true);
	}

	@Test
	public void initSpaceCardIndex_smoke()throws IndexException  {
		QueryVO query = new QueryVO();
		List<Card> cards = cardService.queryByCardQueryVO(query, 1l, null);
		assertEquals(0, cards.size());
		cardService.initSpaceCardIndex(1L,true,true);
		cards = cardService.queryByCardQueryVO(query, 1l, null);
		Pagination<Card> cardsFromDB = cardService.getCardsBySpaceId(1L,
				new Pagination<Card>(10000, 1));
		assertEquals(cards.size(), cardsFromDB.getTotal());
	}

	@Test
	public void initAllCardIndex_smoke() throws IndexException {
		QueryVO query = new QueryVO();
		List<Card> cards = cardService.queryByCardQueryVO(query, null);
		assertEquals(0, cards.size());
		cardService.initAllCardIndex(true,true);
		cards = cardService.queryByCardQueryVO(query, null);
		List<Card> expected = jdbcTemplate.query(CARD_SQL_STRING,
				new CardRowMapper());
		assertEquals(expected.size(), cards.size());
		cardService.initAllCardIndex(true,false);
		cards = cardService.queryByCardQueryVO(query, null);
		expected = jdbcTemplate.query(CARD_SQL_STRING,
				new CardRowMapper());
		assertEquals(expected.size(), cards.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void queryByCardQueryVO_paramWithSpaceId_queryVoIsNull() throws IndexException {
		cardService.queryByCardQueryVO(null, 1l, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void queryByCardQueryVO_paramWithSpaceId_spaceIdIsNull() throws IndexException {
		cardService.queryByCardQueryVO(new QueryVO(), null, null);
	}

	@Test
	public void queryByCardQueryVO_paramWithSpace_smoke() throws IndexException {
		cardService.initSpaceCardIndex(1L,true,true);
		List<Card> cardList = cardService.queryByCardQueryVO(new QueryVO(), 1L,
				null);
		Pagination<Card> expected = cardService.getCardsBySpaceId(1L,
				new Pagination<Card>(100000, 1));
		assertTrue("card query result is null!", cardList.size() > 0);
		assertReflectionEquals(expected.getResults(), cardList);
	}

	@Test(expected = IllegalArgumentException.class)
	public void queryByCardQueryVO_paramWithoutSpaceId_queryVoIsNull()throws IndexException  {
		cardService.queryByCardQueryVO(null, 1l, null);
	}

	@Test
	public void queryByCardQueryVO_paramWithoutSpaceId_smoke() throws IndexException {
		cardService.initAllCardIndex(true,true);
		List<Card> cardList = cardService.queryByCardQueryVO(new QueryVO(),
				null);
		List<Card> expected = jdbcTemplate.query(CARD_SQL_STRING,
				new CardRowMapper());
		assertTrue("card query result is null!", cardList.size() > 0);
		// TODO cardList通过多对一链接，默认使用eager，无法判断出是hibernate代理类，不能只使用id判断
		// assertReflectionEquals(expected,cardList);
		assertEquals(cardList.size(), expected.size());
	}

	// ///以下测试getAllValidRootCardAsParent///////
	// TODO 只测了一种情况
	@Test
	@Ignore("重构后暂不可用")
	public void getAllValidRootCardAsParent_smoke() {
/*		Space space = spaceService.getSpaceByPrefixCode("spark");
		List<Card> p = cardService.getAllValidRootCardAsParent(space, 9L);
		assertNotNull(p);
		assertEquals(p.size(), 4);
*/	}

	@Test
	@Ignore("重构后暂不可用")
	public void getAllValidRootCardAsParent_没有任何符合条件的情况() {
/*		Space space = spaceService.getSpaceByPrefixCode("spark");
		List<Card> p = cardService.getAllValidRootCardAsParent(space, 3L);
		assertNotNull(p);
		assertEquals(p.size(), 0);
*/	}

	@Test 
	@Ignore("重构后暂不可用")
	public void getAllValidRootCardAsParent_找不到对应空间SEQ的情况() {
		Space space = spaceService.getSpaceByPrefixCode("spark");
//		List<Card> p = cardService.getAllValidRootCardAsParent(space, 3L);
//		assertNotNull(p);
//		assertEquals(p.size(), 0);
	}
	
	
	private void assertCardPropertyValueList(List<CardPropertyValue<?>> list1,
			List<CardPropertyValue> list2) {
		assertEquals(list1.size(), list2.size());
		for (CardPropertyValue value1 : list1) {
			boolean found = false;
			for (CardPropertyValue value2 : list2) {
				if (value1.getId().equals(value2.getId())) {
					found = true;
					assertEquals(value1.getValue(), value2.getValue());
				}
			}
			if (!found) {
				assertTrue("not found!", false);
			}
		}
	}

	private void assertCardTypeList(List<CardType> list1, List<CardType> list2) {
		assertEquals(list1.size(), list2.size());
		for (CardType value1 : list1) {
			boolean found = false;
			for (CardType value2 : list2) {
				if (value1.getId().equals(value2.getId())) {
					found = true;
					assertEquals(value1.getName(), value2.getName());
				}
			}
			if (!found) {
				assertTrue("not found!", false);
			}
		}
	}
}

class CardRowMapper implements RowMapper<Card> {

	@Override
	public Card mapRow(ResultSet rs, int rowNum) throws SQLException {
		Card card = new Card();
		card.setId(rs.getLong("id"));
		card.setTitle(rs.getString("title"));
		card.setDetail(rs.getString("detail"));
		card.setSequence(rs.getLong("sequence"));
		card.setCreatedTime(rs.getTimestamp("created_time"));
		card.setLastModifiedTime(rs.getTimestamp("last_modified_time"));

		Long spaceId = rs.getLong("space_id");
		if (spaceId != null && spaceId != 0) {
			Space space = new Space();
			space.setId(rs.getLong("space_id"));
			space.setPrefixCode(rs.getString("prefix_code"));
			space.setName(rs.getString("name"));
			space.setType(SpaceType.values()[rs.getInt("type")]);
			card.setSpace(space);
		}
		long cardTypeId = rs.getLong("card_type");
		if (cardTypeId != 0) {
			CardType cardType = new CardType();
			cardType.setId(cardTypeId);
			card.setType(cardType);
		}
		long parentId = rs.getLong("super_id");
		if (parentId != 0) {
			Card card2 = new Card();
			card2.setId(parentId);
			card.setParent(card2);
		}
		long createdUserId = rs.getLong("created_user");
		if (createdUserId != 0) {
			User user = new User();
			user.setId(createdUserId);
			card.setCreatedUser(user);
		}
		long lastModifiedUser = rs.getLong("last_modified_user");
		if (lastModifiedUser != 0) {
			User user = new User();
			user.setId(lastModifiedUser);
			card.setLastModifiedUser(user);
		}
		return card;
	}
}

class CardPropertyValueRowMapper implements RowMapper<CardPropertyValue> {

	@Override
	public CardPropertyValue mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		CardPropertyValue<?> cardPropertyValue = null;
		String type = rs.getString("type");
		if (type.equals("text")) {
			cardPropertyValue = new TextPropertyValue();
			((TextPropertyValue) cardPropertyValue).setValue(rs
					.getString("strvalue"));
		}
		if (type.equals("date")) {
			cardPropertyValue = new DatePropertyValue();
			((DatePropertyValue) cardPropertyValue).setValue(rs
					.getDate("datevalue"));
		}
		if (type.equals("number")) {
			cardPropertyValue = new NumberPropertyValue();
			((NumberPropertyValue) cardPropertyValue).setValue(rs
					.getInt("intvalue"));
		}
		if (type.equals("list")) {
			cardPropertyValue = new NumberPropertyValue();
			((NumberPropertyValue) cardPropertyValue).setValue(rs
					.getInt("intvalue"));
		}
		cardPropertyValue.setId(rs.getLong("id"));
		return cardPropertyValue;
	}

}