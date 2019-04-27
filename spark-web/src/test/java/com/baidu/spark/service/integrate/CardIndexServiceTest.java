package com.baidu.spark.service.integrate;

import static com.baidu.spark.TestUtils.assertReflectionEquals;
import static com.baidu.spark.TestUtils.clearTable;
import static com.baidu.spark.TestUtils.closeSessionInTest;
import static com.baidu.spark.TestUtils.initDatabase;
import static com.baidu.spark.TestUtils.openSessionInTest;
import static com.baidu.spark.model.QueryConditionVO.QueryOperationType.LIKE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.spark.SparkTestUtils;
import com.baidu.spark.exception.IndexException;
import com.baidu.spark.exception.SparkRuntimeException;
import com.baidu.spark.index.converter.CardIndexConverter;
import com.baidu.spark.index.engine.CardIndexEngine;
import com.baidu.spark.model.QueryConditionVO;
import com.baidu.spark.model.QuerySortVO;
import com.baidu.spark.model.QueryVO;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.property.CardPropertyValue;
import com.baidu.spark.model.card.property.NumberProperty;
import com.baidu.spark.model.card.property.TextProperty;
import com.baidu.spark.service.CardService;
import com.baidu.spark.service.UserService;
import com.baidu.spark.util.LuceneUtils;

/**
 * 索引接口测试
 * 
 * @author shixiaolei
 */
@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml","/applicationContext-security-test.xml"  })
public class CardIndexServiceTest {
	@Autowired
	private CardService cardService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private CardIndexEngine cardIndexService;

	@Autowired
	private CardIndexConverter cardIndexConverter;
	
	@Autowired
	private DataSource dataSource;

	@Autowired
	private SessionFactory sessionFactory;

	private String indexLocation;

	private String cardIndexLocation;

	private SimpleJdbcTemplate jdbcTemplate;

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
		openSessionInTest(sessionFactory);
	}

	@After
	public void after() throws Exception {
		closeSessionInTest(sessionFactory);
	}

	// ////////以下用例用于测试addIndex///////////////
	@Test
	public void addIndex_smoke() throws IndexException {
		Card expected = cardService.getCard(1L);
		cardIndexService.addIndex(expected);

		Map<String, Object> query = getQuery(expected);
		List<Card> cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(1, cardList.size());
		assertReflectionEquals(cardList.get(0), expected);

		query.put(CardIndexConverter.INDEX_CUS_FIELD_PREFIX + "2", "322");
		cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(cardList.size(), 0);
	}

	//@Test
	//TODO: fix it
	public void addIndex_新增一个不存在的记录() throws IndexException {
		Card card = generateCard("abc");
		cardIndexService.addIndex(card);

		Map<String, Object> query = new LinkedHashMap<String, Object>();
		query.put("id", 800);
		List<Card> cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(0, cardList.size());
	}

	@Test
	public void addIndex_多线程() {
		class MyThread extends Thread {
			private int i = 0;

			public void run() {
				while (true) {
					String suffixName = "just_a_test";
					if (suffixName == null) {
						suffixName = "";
					}
					Card card = new Card();
					card.setTitle("卡片标题" + suffixName);
					card.setDetail("卡片描述" + suffixName);
					card.setCreatedTime(new Date());
					card.setLastModifiedTime(new Date());
					Space space = new Space();
					space.setId(1L);
					card.setSpace(space);

					User user = new User();
					user.setId(1L);
					card.setCreatedUser(user);
					card.setLastModifiedUser(user);

					Card parentCard = new Card();
					parentCard.setId(1L);
					card.setParent(parentCard);

					CardType cardType = new CardType();
					cardType.setId(1L);
					card.setType(cardType);

					List<CardPropertyValue<?>> cpvList = new ArrayList<CardPropertyValue<?>>();
					NumberProperty nn = new NumberProperty();
					nn.setId(1L);
					TextProperty tn = new TextProperty();
					tn.setId(2L);
					cpvList.add(nn.generateValue("123"));
					cpvList.add(tn.generateValue("321"));
					card.setPropertyValues(cpvList);
					// cardService.saveCard(card);
					try {
						cardIndexService.addIndex(card);
					} catch (Throwable e) {
						e.printStackTrace();
					}
					i++;
					if (i == 20) {
						break;
					}
				}
			}
		}
		Thread t1 = new MyThread();
		Thread t2 = new MyThread();

		t1.start();
		t2.start();
		try {
			t1.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	// ////////以下用例用于测试deleteIndex///////////////
	@Test
	public void deleteIndex_多条记录删一条_即最常见情况() throws IndexException {
		// 插入两条
		Card card1 = cardService.getCard(1L);
		Card card2 = cardService.getCard(2L);
		assertNotNull(card1);
		assertNotNull(card2);
		cardIndexService.addIndex(card1);
		cardIndexService.addIndex(card2);

		// 按一条的查询条件来查
		Map<String, Object> query = getQuery(card1);
		List<Card> cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(1, cardList.size());
		assertReflectionEquals(cardList.get(0), card1);

		cardIndexService.deleteIndex(card1);
		query = getQuery(card1);
		cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(0, cardList.size());

		query = getQuery(card2);
		cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(1, cardList.size());
	}

	@Test
	public void deleteIndex_存在一条删一次() throws IndexException {
		Card card = cardService.getCard(1L);
		assertNotNull(card);
		cardIndexService.addIndex(card);

		Map<String, Object> query = getQuery(card);
		List<Card> cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(1, cardList.size());
		cardIndexService.deleteIndex(card);
		cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(cardList.size(), 0);
	}

	@Test
	public void deleteIndex_存在两条除ID都相同记录删一次() throws IndexException {
		// 插入两条
		Card card1 = cardService.getCard(1L);
		Card card2 = cardService.getCard(1L);
		assertNotNull(card1);
		assertNotNull(card2);
		cardIndexService.addIndex(card1);
		cardIndexService.addIndex(card2);
		// 按一条的查询条件来查
		Map<String, Object> query = getQuery(card1);
		List<Card> cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(2, cardList.size());
		// 按通用查询条件来查
		cardIndexService.deleteIndex(card1);
		query = getQuery(card1);
		cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(0, cardList.size());

		query = getQuery(card2);
		cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(0, cardList.size());
	}

	@Test
	public void deleteIndex_存在一条但删了两次() throws IndexException {
		Card card = cardService.getCard(1L);
		assertNotNull(card);
		cardIndexService.addIndex(card);
		Map<String, Object> query = getQuery(card);
		List<Card> cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(1, cardList.size());
		cardIndexService.deleteIndex(card);
		cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(cardList.size(), 0);
		cardIndexService.deleteIndex(card);
		cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(cardList.size(), 0);
	}

	@Test
	public void deleteIndexByField_按已有字段删除已有记录() throws IndexException {
		Card card = cardService.getCard(1L);
		assertNotNull(card);
		cardIndexService.addIndex(card);
		Map<String, Object> query = getQuery(card);
		List<Card> cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(1, cardList.size());
		cardIndexService.deleteIndexByField("id", card.getId());
		cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(cardList.size(), 0);
	}

	@Test
	public void deleteIndexByField_按已有字段删除没有的记录() throws IndexException {
		Card card = cardService.getCard(1L);
		assertNotNull(card);
		cardIndexService.addIndex(card);
		Map<String, Object> query = getQuery(card);
		List<Card> cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(1, cardList.size());
		cardIndexService.deleteIndexByField(
				CardIndexConverter.INDEX_CUS_FIELD_PREFIX + "1", 111);
		cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(cardList.size(), 1);
	}

	@Test
	public void deleteIndexByField_按没有字段删除() throws IndexException {
		Card card = cardService.getCard(1L);
		assertNotNull(card);
		cardIndexService.addIndex(card);
		Map<String, Object> query = getQuery(card);
		List<Card> cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(1, cardList.size());
		cardIndexService.deleteIndexByField("iid", card.getId());
		cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(cardList.size(), 1);
	}

	// ////////以下用例用于测试updateIndex///////////////
	@Test
	// TODO
	public void updateIndex_smoke() throws IndexException {
		Card card = cardService.getCard(1L);
		assertNotNull(card);
		cardIndexService.addIndex(card);
		Map<String, Object> query = getQuery(card);
		List<Card> cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(1, cardList.size());

		card.setDetail("New Detail");
		User user = new User();
		user.setId(1L);
		card.setLastModifiedUser(user);

		cardService.updateCard(card);
		cardIndexService.updateIndex(card);

		cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(cardList.size(), 0);
	}

	// ////////以下用例用于测试clearAllIndex///////////////
	@Test
	public void clearAllIndex_smoke() throws IndexException {
		Card card = cardService.getCard(1L);
		assertNotNull(card);
		cardIndexService.addIndex(card);

		Map<String, Object> query = getQuery(card);
		List<Card> cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(1, cardList.size());
		cardIndexService.clearAllIndex();
		cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(cardList.size(), 0);
	}

	@Test
	public void clearAllIndex_索引库为空时的clearAll() throws IndexException {
		Map<String, Object> query = new HashMap<String, Object>();
		List<Card> cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(0, cardList.size());
		cardIndexService.clearAllIndex();
		cardList = cardIndexService.findIndexByFieldValues(query);
		assertEquals(cardList.size(), 0);
	}

	// ////////以下用例用于测试CardIndexConverter.getIndexableDocument///////////////
	@Test
	public void getIndexableDocument_smoke() throws IndexException, ParseException {
		Card card = cardService.getCard(1L);
		assertNotNull(card);
		cardIndexService.addIndex(card);
		Document doc = cardIndexConverter.getIndexableDocument(card);
		checkDocument(card, doc);
	}

	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void getIndexableDocument_将一个不存在的Card放入document() throws ParseException {
		Card card = new Card();
		Document doc = cardIndexConverter.getIndexableDocument(card);
		checkDocument(card, doc);
	}

	// ////////以下用例用于测试CardIndexConverter.getQueryFromVo///////////////
	@Test
	public void getQueryFromVo_smoke() throws IndexException, IOException {
		Card card1 = cardService.getCard(1L);
		Card card2 = cardService.getCard(2L);
		assertNotNull(card1);
		assertNotNull(card2);
		cardIndexService.addIndex(card1);
		cardIndexService.addIndex(card2);

		List<QueryConditionVO> queryVO = new LinkedList<QueryConditionVO>();
		queryVO
				.add(new QueryConditionVO("[title]["
						+ LIKE.getOperationTypeString() + "]["
						+ card1.getTitle() + "]"));
		queryVO.add(new QueryConditionVO("[detail]["
				+ LIKE.getOperationTypeString() + "][" + card1.getDetail()
				+ "]"));

		Query query = cardIndexConverter.getQueryFromVo(new QueryVO(queryVO,null));

		TopDocs hits = getHits(query);
		assertEquals(hits.totalHits, 1);
	}

	@Test
	public void getQueryFromVo_查询条件为空() throws IndexException, IOException {
		Card card1 = cardService.getCard(1L);
		Card card2 = cardService.getCard(2L);
		assertNotNull(card1);
		assertNotNull(card2);
		cardIndexService.addIndex(card1);
		cardIndexService.addIndex(card2);

		List<QueryConditionVO> queryVO = new LinkedList<QueryConditionVO>();
		Query query = cardIndexConverter.getQueryFromVo(new QueryVO(queryVO,null));

		TopDocs hits = getHits(query);
		assertEquals(hits.totalHits, 2);
	}

	@Test
	public void getQueryFromVo_查询条件为NULL() throws IndexException, IOException {
		Card card1 = cardService.getCard(1L);
		Card card2 = cardService.getCard(2L);
		assertNotNull(card1);
		assertNotNull(card2);
		cardIndexService.addIndex(card1);
		cardIndexService.addIndex(card2);

		Query query = cardIndexConverter.getQueryFromVo(null);

		TopDocs hits = getHits(query);
		assertEquals(hits.totalHits, 2);
	}

	@Test(expected = SparkRuntimeException.class)
	public void getQueryFromVo_QueryConditionVO非法格式() throws IndexException,
			IOException {
		Card card1 = cardService.getCard(1L);
		Card card2 = cardService.getCard(2L);
		assertNotNull(card1);
		assertNotNull(card2);
		cardIndexService.addIndex(card1);
		cardIndexService.addIndex(card2);

		List<QueryConditionVO> queryVO = new LinkedList<QueryConditionVO>();
		queryVO.add(new QueryConditionVO("[title]"));

		Query query = cardIndexConverter.getQueryFromVo(new QueryVO(queryVO,null));
	}

	@Test(expected = SparkRuntimeException.class)
	public void getQueryFromVo_QueryConditionVO中操作符非法() throws IndexException,
			IOException {
		Card card1 = cardService.getCard(1L);
		Card card2 = cardService.getCard(2L);
		assertNotNull(card1);
		assertNotNull(card2);
		cardIndexService.addIndex(card1);
		cardIndexService.addIndex(card2);

		List<QueryConditionVO> queryVO = new LinkedList<QueryConditionVO>();
		queryVO.add(new QueryConditionVO("[title][not_an_operator]["
				+ card1.getTitle() + "]"));

		Query query = cardIndexConverter.getQueryFromVo(new QueryVO(queryVO,null));
	}

	// ////////以下用例用于测试CardIndexConverter.getSortFromVo///////////////
	@Test
	public void getQuerySortVO_smoke() throws IndexException {
		Card card = cardService.getCard(1L);
		assertNotNull(card);
		cardIndexService.addIndex(card);

		List<QuerySortVO> queryVO = new LinkedList<QuerySortVO>();
		QuerySortVO qs1 = new QuerySortVO("[title][desc]");
		QuerySortVO qs2 = new QuerySortVO("[id]");
		queryVO.add(qs1);
		queryVO.add(qs2);

		List<SortField> ss = cardIndexConverter.getSortFromVo(new QueryVO(null,queryVO));
		assertEquals(ss.size(), 2);

		assertTrue(("title".equals(ss.get(0).getField())
				&& ss.get(0).getReverse() == true
				&& "id".equals(ss.get(1).getField()) && ss.get(1).getReverse() == false)
				|| ("title".equals(ss.get(1).getField())
						&& ss.get(1).getReverse() == true
						&& "id".equals(ss.get(0).getField()) && ss.get(0)
						.getReverse() == false));
	}

	@Test(expected = SparkRuntimeException.class)
	public void getQuerySortVO_参数非法_多了一个方括号() throws IndexException {
		Card card = cardService.getCard(1L);
		assertNotNull(card);
		cardIndexService.addIndex(card);

		List<QuerySortVO> queryVO = new LinkedList<QuerySortVO>();
		QuerySortVO qs1 = new QuerySortVO("[title][desc][one_more_param]");
		queryVO.add(qs1);

		cardIndexConverter.getSortFromVo(new QueryVO(null,queryVO));
	}

	@Test
	public void getQuerySortVO_QuerySortVO为空() throws IndexException {
		Card card = cardService.getCard(1L);
		assertNotNull(card);
		cardIndexService.addIndex(card);

		List<QuerySortVO> queryVO = new LinkedList<QuerySortVO>();
		cardIndexConverter.getSortFromVo(new QueryVO(null,queryVO));
	}

	@Test
	public void getQuerySortVO_QuerySortVOList为null() throws IndexException {
		Card card = cardService.getCard(1L);
		assertNotNull(card);
		cardIndexService.addIndex(card);
		cardIndexConverter.getSortFromVo(null);
	}

	// 以下用例用于测试QueryConditionVO.QueryConditionVO
	@Test
	public void QueryConditionVO_smoke() throws IndexException {
		Card card = cardService.getCard(1L);
		assertNotNull(card);
		cardIndexService.addIndex(card);

		QueryConditionVO vo = new QueryConditionVO("[title]["
				+ LIKE.getOperationTypeString() + "][" + card.getTitle() + "]");
		assertNotNull(vo);
		assertEquals(vo.getFieldName(), "title");
		assertEquals(vo.getOperationType().name().toString(), "LIKE");
		assertEquals(vo.getValue(), card.getTitle());
	}

	@Test(expected = SparkRuntimeException.class)
	public void QueryConditionVO_格式非法_少方括号() throws IndexException {
		Card card = cardService.getCard(1L);
		assertNotNull(card);
		cardIndexService.addIndex(card);
		QueryConditionVO vo = new QueryConditionVO("[title]");
	}

	@Test(expected = SparkRuntimeException.class)
	public void QueryConditionVO_格式非法_多方括号() throws IndexException {
		Card card = cardService.getCard(1L);
		assertNotNull(card);
		cardIndexService.addIndex(card);
		QueryConditionVO vo = new QueryConditionVO("[title]["
				+ LIKE.getOperationTypeString() + "][" + card.getTitle()
				+ "][多了一个]");
	}

	@Test(expected = SparkRuntimeException.class)
	public void QueryConditionVO_非法操作符() throws IndexException {
		Card card = cardService.getCard(1L);
		assertNotNull(card);
		cardIndexService.addIndex(card);
		QueryConditionVO vo = new QueryConditionVO(
				"[title][Undefined_Operator][aaa]");
	}

	// 以下用例用于测试QuerySortVO
	@Test
	public void QuerySortVO_参数中第二个中括号参数为小写desc() throws IndexException {
		QuerySortVO vo = new QuerySortVO("[title][desc]");
		assertNotNull(vo);
		assertEquals(vo.getFieldName(), "title");
		assertEquals(vo.isDesc(), true);
	}

	@Test
	public void QuerySortVO_参数中第二个中括号参数为大写DESC() throws IndexException {
		QuerySortVO vo = new QuerySortVO("[title][DESC]");
		assertNotNull(vo);
		assertEquals(vo.getFieldName(), "title");
		assertEquals(vo.isDesc(), true);
	}

	@Test
	public void QuerySortVO_参数中第二个中括号参数大小写结合() throws IndexException {
		QuerySortVO vo = new QuerySortVO("[title][DeSc]");
		assertNotNull(vo);
		assertEquals(vo.getFieldName(), "title");
		assertEquals(vo.isDesc(), true);
	}

	@Test
	public void QuerySortVO_只有一个中括号() throws IndexException {
		QuerySortVO vo = new QuerySortVO("[detail]");
		assertNotNull(vo);
		assertEquals(vo.getFieldName(), "detail");
		assertEquals(vo.isDesc(), false);
	}

	@Test
	public void QuerySortVO_参数中第二个中括号参数为未知() throws IndexException {
		QuerySortVO vo = new QuerySortVO("[title][undefied_sort]");
		assertNotNull(vo);
		assertEquals(vo.getFieldName(), "title");
		assertEquals(vo.isDesc(), false);
	}

	@Test(expected = SparkRuntimeException.class)
	public void QuerySortVO_参数中中括号个数多于两个() throws IndexException {
		QuerySortVO vo = new QuerySortVO("[title][desc][another]");
	}

	@Test(expected = SparkRuntimeException.class)
	public void QuerySortVO_参数中中括号个数为0个() throws IndexException {
		QuerySortVO vo = new QuerySortVO("aaa");
	}

	// ////////以下用户测试CardIndexConverter.getOriginObj
	@Test
	public void getOriginObj_Document对应一个数据库中存在的记录() throws ParseException {
		Card cardExpected = cardService.getCard(1L);
		// 因为此前已经对CardIndexConverter.getIndexableDocument进行测试，所以此处直接信任此函数，
		// 而不是自己再写一个generateDocument函数
		Document doc = cardIndexConverter.getIndexableDocument(cardExpected);
		Card cardFromDoc = cardIndexConverter.getOriginObj(doc);
		assertNotNull(cardFromDoc);
		assertReflectionEquals(cardExpected, cardFromDoc);
	}

	@Test
	public void getOriginObj_Document对应一个数据库中不存在的记录() throws ParseException {
		Document doc = new Document();
		doc.add(new Field("id", "200", Field.Store.NO, Field.Index.ANALYZED));
		Card cardFromDoc = cardIndexConverter.getOriginObj(doc);
		assertNull(cardFromDoc);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getOriginObj_Document为null() throws ParseException {
		Card cardFromDoc = cardIndexConverter.getOriginObj((Document)null);
	}

	/**
	 * @param card
	 * @param doc
	 * @throws ParseException 
	 */
	private void checkDocument(Card card, Document doc) throws ParseException {
		assertTrue((card.getId() == null && StringUtils.isEmpty(doc.get("id")))
				|| card.getId().toString().equals(doc.get("id")));
		assertTrue((card.getSequence() == null && StringUtils.isEmpty(doc
				.get("sequence")))
				|| card.getSequence().toString().equals(doc.get("sequence")));
		assertTrue((card.getTitle() == null && StringUtils.isEmpty(doc
				.get("title")))
				|| card.getTitle().equals(doc.get("title")));
		assertTrue((card.getDetail() == null && StringUtils.isEmpty(doc
				.get("detail")))
				|| card.getDetail().equals(doc.get("detail")));

		assertTrue((card.getCreatedTime() == null && DateTools.stringToDate(doc
				.get("createdTime")).equals(LuceneUtils.NOT_SET_DATE))
				|| DateTools.dateToString(card.getCreatedTime(),
						DateTools.Resolution.SECOND).equals(
						doc.get("createdTime")));
		assertTrue((card.getLastModifiedTime() == null && DateTools.stringToDate(doc
				.get("lastModifiedTime")).equals(LuceneUtils.NOT_SET_DATE))
				|| DateTools.dateToString(card.getLastModifiedTime(),
						DateTools.Resolution.SECOND).equals(
						doc.get("lastModifiedTime")));
		assertTrue((card.getType() == null && StringUtils.isEmpty(doc
				.get(CardIndexConverter.CARD_TYPE_ID)))
				|| (card.getType().getLocalId() == null && StringUtils.isEmpty(doc
						.get(CardIndexConverter.CARD_TYPE_ID)))
				|| card.getType().getLocalId().toString().equals(
						doc.get(CardIndexConverter.CARD_TYPE_ID)));
		assertTrue((card.getCreatedUser() == null && StringUtils.isEmpty(doc
				.get(CardIndexConverter.CARD_CREATED_USER_ID)))
				|| (card.getCreatedUser().getId() == null && StringUtils
						.isEmpty(doc
								.get(CardIndexConverter.CARD_CREATED_USER_ID)))
				|| card.getCreatedUser().getId().toString().equals(
						doc.get(CardIndexConverter.CARD_CREATED_USER_ID)));
		assertTrue((card.getLastModifiedUser() == null && doc.get(CardIndexConverter.CARD_LAST_MODIFIED_USER_ID).equals(LuceneUtils.NOT_SET_NUMBER.toString()))
				|| (card.getLastModifiedUser().getId() == null && StringUtils
						.isEmpty(doc
								.get(CardIndexConverter.CARD_LAST_MODIFIED_USER_ID)))
				|| card.getLastModifiedUser().getId().toString().equals(
						doc.get(CardIndexConverter.CARD_LAST_MODIFIED_USER_ID)));
		assertTrue((card.getParent() == null && doc
				.get(CardIndexConverter.PARENT_CARD_ID).equals(LuceneUtils.NOT_SET_NUMBER.toString()))
				|| (card.getParent().getId() == null && doc
						.get(CardIndexConverter.PARENT_CARD_ID).equals(LuceneUtils.NOT_SET_NUMBER.toString()))
				|| card.getParent().getId().toString().equals(
						doc.get(CardIndexConverter.PARENT_CARD_ID)));
		assertTrue((card.getParent() == null && doc
				.get(CardIndexConverter.PARENT_CARD_SEQUENCE).equals(LuceneUtils.NOT_SET_NUMBER.toString()))
				|| (card.getParent().getSequence() == null && doc
								.get(CardIndexConverter.PARENT_CARD_SEQUENCE).equals(LuceneUtils.NOT_SET_NUMBER.toString()))
				|| card.getParent().getSequence().toString().equals(
						doc.get(CardIndexConverter.PARENT_CARD_SEQUENCE)));
		assertTrue((card.getSpace() == null && doc
				.get(CardIndexConverter.CARD_SPACE_ID).equals(LuceneUtils.NOT_SET_NUMBER.toString()))
				|| (card.getSpace().getId() == null && doc
						.get(CardIndexConverter.CARD_SPACE_ID).equals(LuceneUtils.NOT_SET_NUMBER.toString()))
				|| card.getSpace().getId().toString().equals(
						doc.get(CardIndexConverter.CARD_SPACE_ID)));
	}

	private Card generateCard(String suffixName) {
		Card card = new Card();
		card.setTitle("卡片标题" + suffixName);
		card.setDetail("卡片描述" + suffixName);
		card.setCreatedTime(new Date());
		card.setLastModifiedTime(new Date());

		Space space = new Space();
		space.setId(1L);
		card.setSpace(space);

		User user = new User();
		user.setId(1L);
		card.setCreatedUser(user);
		card.setLastModifiedUser(user);

		Card parentCard = new Card();
		parentCard.setId(1L);
		card.setParent(parentCard);

		CardType cardType = new CardType();
		cardType.setId(1L);
		card.setType(cardType);

		List<CardPropertyValue<?>> cpvList = new ArrayList<CardPropertyValue<?>>();
		NumberProperty nn = new NumberProperty();
		nn.setId(1L);
		TextProperty tn = new TextProperty();
		tn.setId(2L);
		cpvList.add(nn.generateValue("123"));
		cpvList.add(tn.generateValue("321"));
		card.setPropertyValues(cpvList);
		return card;
	}

	/**
	 * 按一个card的条件来查
	 * 
	 * @param card
	 * @return
	 */
	private Map<String, Object> getQuery(Card card) {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("id", card.getId());
		query.put("title", card.getTitle());
		query.put("detail", card.getDetail());
		return query;
	}

	/**
	 * 查询的通用条件
	 * 
	 * @return
	 */
	private List<QueryConditionVO> getCommonQueryVO() {
		List<QueryConditionVO> queryVO = new LinkedList<QueryConditionVO>();
		queryVO.add(new QueryConditionVO("[title]["
				+ LIKE.getOperationTypeString() + "][卡片标题]"));
		queryVO.add(new QueryConditionVO("[detail]["
				+ LIKE.getOperationTypeString() + "][卡片描述]"));
		return queryVO;
	}

	/**
	 * 根据Query进行查询
	 * 
	 * @param query
	 * @return
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	private TopDocs getHits(Query query) throws IOException,
			CorruptIndexException {
		Directory directory = new SimpleFSDirectory(new File(
				getCardIndexLocation()));
		IndexSearcher searcher = new IndexSearcher(directory);
		TopDocs hits = searcher.search(query, 100);
		return hits;
	}

	@Value("#{properties['lucene.indexDirPath']}")
	public void setIndexLocation(String indexLocation) {
		this.indexLocation = indexLocation;
	}

	private String getCardIndexLocation() {
		return indexLocation + "/Card";
	}

}
