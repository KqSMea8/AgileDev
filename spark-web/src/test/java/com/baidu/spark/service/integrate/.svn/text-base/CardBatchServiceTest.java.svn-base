package com.baidu.spark.service.integrate;

import static com.baidu.spark.TestUtils.clearTable;
import static com.baidu.spark.TestUtils.initDatabase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.spark.SparkTestUtils;
import com.baidu.spark.TestUtils;
import com.baidu.spark.dao.CardPropertyDao;
import com.baidu.spark.dao.Pagination;
import com.baidu.spark.index.engine.CardIndexEngine;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.model.card.property.CardPropertyValue;
import com.baidu.spark.model.card.property.ListProperty;
import com.baidu.spark.model.card.property.ListPropertyValue;
import com.baidu.spark.model.card.property.NumberProperty;
import com.baidu.spark.model.card.property.NumberPropertyValue;
import com.baidu.spark.service.CardService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.service.UserService;

/**
 * 
 * 卡片服务接口测试
 * 
 * @author shixiaolei
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml","/applicationContext-security-test.xml" })
public class CardBatchServiceTest {
	
	@Autowired
	private UserService userService;

	@Autowired
	private CardService cardService;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private CardIndexEngine cardIndexService;

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private SpaceService spaceService;
	
	@Autowired
	private CardPropertyDao cardPropertyDao;

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
		initDatabase(dataSource, "com/baidu/spark/service/integrate/CardBatchServiceTest.xml");
		SparkTestUtils.setCurrentUserAdmin(userService);
		cardIndexService.clearAllIndex();
		TestUtils.openSessionInTest(sessionFactory);
	}

	@After
	public void after() throws Exception {
		TestUtils.closeSessionInTest(sessionFactory);
	}

	 
	@Test 
	public void getCommonProperties_smoke() {
		List<Card> cards =  cardService.getCardsBySpaceId(1L, new Pagination<Card>()).getResults();
		List<CardProperty> properties = cardService.getCommonProperties(cards);
		assertNotNull(properties);
		assertEquals(1, properties.size());
		CardProperty property = properties.get(0);
		assertEquals(Long.valueOf(1L), property.getId());
	}
	
	@Test 
	public void getCommonProperties_传入空列表得到空属性集() {
		List<Card> cards =  new ArrayList<Card>(3);
		cards.add(cardService.getCard(1L));
		cards.add(cardService.getCard(2L));
		cards.add(cardService.getCard(4L));
		List<CardProperty> properties = cardService.getCommonProperties(cards);
		assertEquals(2, properties.size());
	}
	
	@Test
	public void batchUpdateCards_smoke(){
		Space space = spaceService.getSpace(1L);
		List<Card> cards =  cardService.getCardsBySpaceId(1L, new Pagination<Card>()).getResults();
		List<CardPropertyValue<?>> pvs = new ArrayList<CardPropertyValue<?>>(1);
		
		NumberProperty property = (NumberProperty)cardPropertyDao.get(1L);
		NumberPropertyValue pv = new NumberPropertyValue();
		pv.setCardProperty(property);
		pv.setValue(123);
		pvs.add(pv);

		cardService.batchUpdateCards(space, cards, pvs);
		
		cards =  cardService.getCardsBySpaceId(1L, new Pagination<Card>()).getResults();
		
		for(Card card : cards){
			boolean flag = false;
			List<CardPropertyValue<?>> dbPvs = card.getPropertyValues();
			for(CardPropertyValue<?> dbPv : dbPvs){
				if(dbPv.getCardProperty().getId().equals(1L)){
					if(! (dbPv instanceof NumberPropertyValue)) fail(); 
					NumberPropertyValue lpv = (NumberPropertyValue)dbPv ;
					Integer value = lpv.getValue();
					if( value == 123 ){
						flag = true;
						break;
					}
				}
			}
			if(!flag){
				fail();
			}
		}
	}
	
	@Test
	public void batchUpdateCards_列表属性(){
		Space space = spaceService.getSpace(1L);
		List<Card> cards =  new ArrayList<Card>(3); 
		cards.add(cardService.getCard(1L));
		cards.add(cardService.getCard(2L));
		cards.add(cardService.getCard(4L));
		List<CardPropertyValue<?>> pvs = new ArrayList<CardPropertyValue<?>>(1);
		
		ListProperty property = (ListProperty)cardPropertyDao.get(3L);
		ListPropertyValue pv = new ListPropertyValue();
		pv.setCardProperty(property);
		pv.setValue("1302536f-60d7-415d-9e84-1ac102c57e44");
		pvs.add(pv);

		cardService.batchUpdateCards(space, cards, pvs);
		
		cards =  new ArrayList<Card>(3); 
		cards.add(cardService.getCard(1L));
		cards.add(cardService.getCard(2L));
		cards.add(cardService.getCard(4L));
		
		for(Card card : cards){
			boolean flag = false;
			List<CardPropertyValue<?>> dbPvs = card.getPropertyValues();
			for(CardPropertyValue<?> dbPv : dbPvs){
				if(dbPv.getCardProperty().getId().equals(3L)){
					if(! (dbPv instanceof ListPropertyValue)) fail(); 
					ListPropertyValue lpv = (ListPropertyValue)dbPv ;
					String value = lpv.getValue();
					if("1302536f-60d7-415d-9e84-1ac102c57e44".equals(value) ){
						flag = true;
						break;
					}
				}
			}
			if(!flag){
				fail();
			}
		}
	}
	
	@Test
	public void cascadeDelete_smoke(){
		Card card = cardService.getCard(2L);
		cardService.cascadeDelete(card);
		List<Card> cards =  cardService.getCardsBySpaceId(1L, new Pagination<Card>()).getResults();
		assertEquals(3, cards.size());
	}
	
	@Test
	public void cascadeDelete_删除一个父同时删除一个子_不会重复删而导致错误(){
		Card card1 = cardService.getCard(2L);
		Card card2 = cardService.getCard(7L);
		cardService.cascadeDelete(card1);
		cardService.cascadeDelete(card2);
		List<Card> cards =  cardService.getCardsBySpaceId(1L, new Pagination<Card>()).getResults();
		assertEquals(3, cards.size());
	}
	
	@Test
	public void getDescents_顶级卡片(){
		Card card1 = cardService.getCard(1L);
		Card card2 = cardService.getCard(2L);
		Card card3 = cardService.getCard(9L);
		Set<Card> d1 = card1.getDescendants();
		assertEquals(7, d1.size());
		assertTrue(d1.contains(card2));
		assertTrue(d1.contains(card3));
	}

	@Test
	public void getDescents_只有子没有孙(){
		Card card1 = cardService.getCard(8L);
		Card card2 = cardService.getCard(9L);
		Set<Card> d1 = card1.getDescendants();
		assertEquals(1, d1.size());
		assertTrue(d1.contains(card2));
	}
	
	@Test
	public void getDescents_叶子节点(){
		Card card1 = cardService.getCard(5L);
		Set<Card> d1 = card1.getDescendants();
		assertNotNull(d1);
		assertEquals(0, d1.size());
	}
 
} 
 