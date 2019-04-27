package com.baidu.spark.service.integrate;

import static com.baidu.spark.TestUtils.closeSessionInTest;
import static com.baidu.spark.TestUtils.initDatabase;
import static com.baidu.spark.TestUtils.openSessionInTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.model.card.property.DateProperty;
import com.baidu.spark.model.card.property.ListProperty;
import com.baidu.spark.model.card.property.NumberProperty;
import com.baidu.spark.model.card.property.TextProperty;
import com.baidu.spark.service.CardTypeService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.service.UserService;

/**
 * 卡片类型服务类接口测试
 * 
 * @author chenhui
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml","/applicationContext-security-test.xml" })
public class CardTypeServiceTest {

	@Autowired
	private CardTypeService cardTypeService;

	@Autowired
	private SpaceService spaceService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private DataSource dataSource;

	private static SimpleJdbcTemplate jdbcTemplate;

	@Before
	public void before() throws Exception {
		SparkTestUtils.clearAllTable(dataSource);
		initDatabase(dataSource,
				"com/baidu/spark/service/integrate/CardTypeServiceTest.xml");
		openSessionInTest(sessionFactory);
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		SparkTestUtils.setCurrentUserAdmin(userService);
	}

	@After
	public void after() throws Exception {
		closeSessionInTest(sessionFactory);
	}

	@Test
	public void getCardType_smoke(){
		CardType cardType = cardTypeService.getCardType(1L);
		CardType expected = jdbcTemplate.queryForObject(
				"select * from card_type where id =?", new CardTypeRowMapper(),
				1L);
		Assert.assertNotNull(cardType);
		assertEquals(cardType.getName(), expected.getName());
		assertEquals(cardType.getSpace().getId(), expected.getSpace().getId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void getCardType_输入id为空(){
		cardTypeService.getCardType(null);
	}
	
	@Test
	public void getCardType_输入id为不存在的值(){
		CardType cardType = cardTypeService.getCardType(-1L);
		assertNull(cardType);
	}
	
	@Test
	public void getAllCardTypes_smoke() {
		Space space = spaceService.getSpace(1L);
		List<CardType> cardTypes = cardTypeService.getAllCardTypes(space);
		assertNotNull(cardTypes);
		List<CardType> expected = jdbcTemplate.query(
				"select * from card_type where space_id = ?",
				CardTypeRowMapper.INSTANCE, 1L);
		assertNotNull(expected);
		assertEquals(cardTypes.size(), expected.size());
		TestUtils.assertReflectionArrayEquals(cardTypes.toArray(), expected
				.toArray());
	}

	@Test
	public void getAllCardTypes_不存在cardType的space(){
		Space space = new Space();
		space.setName("test-space");
		space.setPrefixCode("test");
		spaceService.saveSpace(space);
		List<CardType> cardTypes = cardTypeService.getAllCardTypes(space);
		assertNotNull(cardTypes);
		assertTrue(cardTypes.size() == 0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getAllCardTypes_使用临时态的space进行查询(){
		Space space = new Space();
		space.setName("test-space");
		space.setPrefixCode("test");
		cardTypeService.getAllCardTypes(space);
	}
	
	/**
	 * XXX FIXME 这个接口允许使用临时态的空间进行查询，
	 * 只要空间时只读的而且只需要构造查询条件
	 */
	@Ignore
	@Test(expected = IllegalArgumentException.class)
	public void getAllCardTypes_使用临时态且手工设置id的space进行查询(){
		Space space = new Space();
		space.setName("test-space");
		space.setPrefixCode("test");
		space.setId(1L);
		cardTypeService.getAllCardTypes(space);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getAllCardTypes_使用null进行查询(){
		cardTypeService.getAllCardTypes(null);
	}
	
		
	@Test
	public void deleteSpace_smoke(){
		Space space = spaceService.getSpace(1L);
		spaceService.deteleSpace(space);
	}

	@Test
	public void checkConflicts_冒烟() {
		CardType cardType = cardTypeService.getCardType(1L);
		boolean actual = cardTypeService.checkConflicts(cardType);
		Assert.assertFalse(actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkConflicts_cardType为空() {
		cardTypeService.checkConflicts(null);
	}
	
	@Test
	public void checkConflicts_已经存入数据库的cardType() {
		CardType cardType = cardTypeService.getCardType(1L);
		boolean ret = cardTypeService.checkConflicts(cardType);
		assertFalse(ret);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkConflicts_名称为空字符串的cardType() {
		Space space = spaceService.getSpace(1L);
		CardType parent = cardTypeService.getCardType(1L);
		CardType cardType = new CardType(space);
		cardType.setName("");
		cardType.setRecursive(true);
		cardType.setParent(parent);
		cardTypeService.checkConflicts(cardType);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkConflicts_名称为null的cardType() {
		Space space = spaceService.getSpace(1L);
		CardType parent = cardTypeService.getCardType(1L);
		CardType cardType = new CardType(space);
		cardType.setName(null);
		cardType.setRecursive(true);
		cardType.setParent(parent);
		cardTypeService.checkConflicts(cardType);
	}
	
	@Test
	public void checkConflicts_不在同一个空间且名称相同的cardType() {
		Space space = spaceService.getSpace(2L);
		CardType exist = cardTypeService.getCardType(4L);
		CardType cardType = new CardType(space);
		cardType.setName(exist.getName());
		boolean actual = cardTypeService.checkConflicts(cardType);
		assertFalse(actual);
	}
	
	@Test
	public void checkConflicts_不在同一个空间且名称相同的两个已保存cardType() {
		//ID为1的与之相同
		CardType cardType = cardTypeService.getCardType(5L);
		boolean actual = cardTypeService.checkConflicts(cardType);
		assertFalse(actual);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkConflicts_不属于任何一个空间() {
		CardType parent = cardTypeService.getCardType(1L);
		CardType cardType = new CardType(null);
		cardType.setName("test_card_type");
		cardType.setRecursive(true);
		cardType.setParent(parent);
		
		boolean actual = cardTypeService.checkConflicts(cardType);
		assertFalse(actual);
	}
	
	@Test
	public void checkConflicts_存在重复名称且待校验的为Transient() {
		CardType exist = cardTypeService.getCardType(1L);
		CardType cardType = new CardType(exist.getSpace());
		cardType.setName(exist.getName());
		boolean actual = cardTypeService.checkConflicts(cardType);
		Assert.assertTrue(actual);
	}

	@Test
	public void checkConflicts_duplicateOfNameWithOtherSpace() {
		CardType exist1 = cardTypeService.getCardType(1L);
		CardType exist2 = cardTypeService.getCardType(5L);
		CardType cardType = new CardType(exist1.getSpace());
		cardType.setName(exist2.getName());
		boolean actual = cardTypeService.checkConflicts(cardType);
		Assert.assertTrue(actual);
	}

	@Test
	public void getValidCardTypesAsParent_noParent() {
		CardType cardType = cardTypeService.getCardType(1L);
		List<CardType> cardTypes = cardTypeService
				.getValidCardTypesAsParent(cardType);
		assertEquals(cardTypes, Collections.emptyList());
	}

	@Test
	public void getValidCardTypesAsParent_noChild() {
		CardType cardType = cardTypeService.getCardType(3L);
		List<CardType> cardTypes = cardTypeService
				.getValidCardTypesAsParent(cardType);
		assertNotNull(cardTypes);
		assertEquals(3, cardTypes.size());
		// TODO　use jdbc fetch the result
	}

	@Test
	public void getValidCardTypesAsParent_cardTyps不在一颗树上(){
		addAnotherCardTypeTree();
		Map<Long, Integer> map = new HashMap<Long, Integer>();
		map.put(2L, 6);
		map.put(5L, 2);
		map.put(4L, 7);
		for (Long key:map.keySet()){
			CardType cardType = cardTypeService.getCardType(key);
			List<CardType> cardTypes = cardTypeService.getValidCardTypesAsParent(cardType);
			assertNotNull(cardTypes);
			assertEquals(map.get(key).intValue(), cardTypes.size());
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getValidCardTypesAsParent_cardTyp未保存(){
		CardType cardType = new CardType();
		cardTypeService.getValidCardTypesAsParent(cardType);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getValidCardTypesAsParent_cardTyp未保存但已设置基本属性(){
		CardType cardType = new CardType();
		cardType.setSpace(spaceService.getSpace(1L));
		cardType.setName("notSaved_cardType");
		 cardTypeService.getValidCardTypesAsParent(cardType);
	}
	
	/**
	 * XXX 如果是个数据库不存在的ID实体，那么getValidCardTypesAsParent返回所列空间下所有的卡片类型
	 */
	@Test
	@Ignore
	public void getValidCardTypesAsParent_cardType未保存但已设置基本属性和ID(){
		CardType cardType = new CardType();
		cardType.setSpace(spaceService.getSpace(1L));
		cardType.setName("notSaved_cardType");
		cardType.setId(-1L);
		List<CardType> list = cardTypeService.getValidCardTypesAsParent(cardType);
		assertEquals(list.size(), 0);
	}
	
	@Test
	public void getValidCardTypesAsParent_cardType的recursive字段对此函数的返回应该没有影响(){
		CardType cardType = new CardType();
		cardType.setSpace(spaceService.getSpace(1L));
		cardType.setName("saved_cardType");
		cardTypeService.saveCardType(cardType);
		List<CardType> cardTypes;
		
		//not set
		cardTypes = cardTypeService.getValidCardTypesAsParent(cardType);
		assertNotNull(cardTypes);
		assertEquals(cardTypes.size(), 4);
		
		//true
		cardType.setRecursive(true);
		cardTypeService.updateCardType(cardType);
		
		cardTypes = cardTypeService.getValidCardTypesAsParent(cardType);
		assertNotNull(cardTypes);
		assertEquals(cardTypes.size(), 4);
		
		//false
		cardType.setRecursive(false);
		cardTypeService.updateCardType(cardType);
		
		cardTypes = cardTypeService.getValidCardTypesAsParent(cardType);
		assertNotNull(cardTypes);
		assertEquals(cardTypes.size(), 4);
		
	}
	
	@Test
	public void getValidCardTypesAsParent_hasParentAndChild() {
		CardType cardType = cardTypeService.getCardType(2L);
		List<CardType> cardTypes = cardTypeService
				.getValidCardTypesAsParent(cardType);
		assertNotNull(cardTypes);
		assertEquals(2, cardTypes.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveCardType_对已经save的cardType再次save(){
		Space space = spaceService.getSpace(1L);
		CardType parent = cardTypeService.getCardType(1L);
		CardType cardType = new CardType(space);
		cardType.setName("test_card_type");
		cardType.setRecursive(true);
		cardType.setParent(parent);
		cardTypeService.saveCardType(cardType);
		cardTypeService.saveCardType(cardType);
	}
	
	@Test
	public void saveCardType_没有父级(){
		Space space = spaceService.getSpace(1L);
		CardType cardType = new CardType(space);
		cardType.setName("test_card_type");
		cardType.setRecursive(true);
		cardType.setParent(null);
		cardTypeService.saveCardType(cardType);
		
		compareCardTypeWithDB(cardType);
		cardType  = cardTypeService.getCardType(cardType.getId());
		assertEquals(cardType.getLocalId().longValue(),1L);
		int next = jdbcTemplate.queryForInt(
				"select next_card_type_local_id from space_sequence where id = ?", space.getId());
		assertEquals(next,2);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveCardType_已经有ID且ID已存在(){
		Space space = spaceService.getSpace(1L);
		CardType parent = cardTypeService.getCardType(1L);
		CardType cardType = new CardType(space);
		cardType.setId(1L);
		cardType.setName("test_card_type");
		cardType.setRecursive(true);
		cardType.setParent(parent);
		cardTypeService.saveCardType(cardType);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveCardType_已经有ID且ID不存在(){
		Space space = spaceService.getSpace(1L);
		CardType parent = cardTypeService.getCardType(1L);
		CardType cardType = new CardType(space);
		cardType.setId(-100L);
		cardType.setName("test_card_type");
		cardType.setRecursive(true);
		cardType.setParent(parent);
		cardTypeService.saveCardType(cardType);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveCardType_name为空字符串(){
		Space space = spaceService.getSpace(1L);
		CardType parent = cardTypeService.getCardType(1L);
		CardType cardType = new CardType(space);
		cardType.setName("");
		cardType.setRecursive(true);
		cardType.setParent(parent);
		cardTypeService.saveCardType(cardType);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveCardType_name为null(){
		Space space = spaceService.getSpace(1L);
		CardType parent = cardTypeService.getCardType(1L);
		CardType cardType = new CardType(space);
		cardType.setName(null);
		cardType.setRecursive(true);
		cardType.setParent(parent);
		cardTypeService.saveCardType(cardType);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveCardType_space为null() {
		Space space = spaceService.getSpace(1L);
		CardType parent = cardTypeService.getCardType(1L);
		CardType cardType = new CardType(space);
		cardType.setSpace(null);
		cardType.setName("test_card_type");
		cardType.setRecursive(true);
		cardType.setParent(parent);
		cardTypeService.saveCardType(cardType);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveCardType_没有recursive() {
		Space space = spaceService.getSpace(1L);
		CardType parent = cardTypeService.getCardType(1L);
		CardType cardType = new CardType(space);
		cardType.setName("test_card_type");
		cardType.setRecursive(null);
		cardType.setParent(parent);
		cardTypeService.saveCardType(cardType);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveCardType_父级不属于同一个space() {
		Space space = spaceService.getSpace(2L);
		CardType parent = cardTypeService.getCardType(1L);
		CardType cardType = new CardType(space);
		cardType.setName("test_card_type");
		cardType.setRecursive(null);
		cardType.setParent(parent);
		cardTypeService.saveCardType(cardType);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveCardType_重名() {
		Space space = spaceService.getSpace(1L);
		CardType parent = cardTypeService.getCardType(1L);
		{
			CardType cardType = new CardType(space);
			cardType.setName("test_card_type");
			cardType.setRecursive(null);
			cardType.setParent(parent);
			cardTypeService.saveCardType(cardType);
		}
		{
			CardType cardType = new CardType(space);
			cardType.setName("test_card_type");
			cardType.setRecursive(null);
			cardType.setParent(parent);
			cardTypeService.saveCardType(cardType);
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveCardType_父级是自己() {
		Space space = spaceService.getSpace(1L);
		CardType cardType = new CardType(space);
		cardType.setName("test_card_type");
		cardType.setRecursive(true);
		cardType.setParent(cardType);
		cardTypeService.saveCardType(cardType);
	}
	
	@Test
	public void saveCardType_冒烟() {
		Space space = spaceService.getSpace(1L);
		CardType parent = cardTypeService.getCardType(1L);
		CardType cardType = new CardType(space);
		cardType.setName("test_card_type");
		cardType.setRecursive(true);
		cardType.setParent(parent);
		cardTypeService.saveCardType(cardType);
		
		compareCardTypeWithDB(cardType);
		cardType  = cardTypeService.getCardType(cardType.getId());
		assertEquals(cardType.getLocalId().longValue(),1L);
		int next = jdbcTemplate.queryForInt(
				"select next_card_type_local_id from space_sequence where id = ?", space.getId());
		assertEquals(next,2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveCardType_nullCardType() {
		cardTypeService.saveCardType(null);
	}
	
	@Test
	public void updateCardType_冒烟() {
		CardType parent = cardTypeService.getCardType(4L);
		CardType cardType = cardTypeService.getCardType(3L);
		cardType.setName("test_card_type");
		cardType.setRecursive(!cardType.getRecursive());
		cardType.setParent(parent);
		cardTypeService.updateCardType(cardType);
		
		compareCardTypeWithDB(cardType);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void updateCardType_改为null() {
		cardTypeService.updateCardType(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void updateCardType_update一个Transient的对象() {
		Space space = spaceService.getSpace(1L);
		CardType parent = cardTypeService.getCardType(1L);
		CardType cardType = new CardType(space);
		cardType.setName("test_card_type");
		cardType.setRecursive(!cardType.getRecursive());
		cardType.setParent(parent);
		cardTypeService.updateCardType(cardType);
		
		compareCardTypeWithDB(cardType);
	}
	
	@Test(expected = IllegalArgumentException.class)
	@Ignore
	public void updateCardType_update一个对象的space为另一个空间() {
		Space space = spaceService.getSpace(2L);
		CardType cardType = cardTypeService.getCardType(1L);
		cardType.setSpace(space);
		cardTypeService.updateCardType(cardType);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void updateCardType_update一个对象的父级为另一个空间的cardType() {
		CardType parent = cardTypeService.getCardType(5L);
		CardType cardType = cardTypeService.getCardType(1L);
		cardType.setParent(parent);
		cardTypeService.updateCardType(cardType);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void updateCardType_将recursive设为null() {
		CardType cardType = cardTypeService.getCardType(1L);
		cardType.setRecursive(null);
		cardTypeService.updateCardType(cardType);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void updateCardType_将name设为null() {
		CardType cardType = cardTypeService.getCardType(1L);
		cardType.setName(null);
		cardTypeService.updateCardType(cardType);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void updateCardType_将name设为空字符串() {
		CardType cardType = cardTypeService.getCardType(1L);
		cardType.setName("");
		cardTypeService.updateCardType(cardType);
	}
	
	/**
	 * XXX 已经在updateCardType上增加注释说明此事
	 */
	@Ignore
	@Test(expected = IllegalArgumentException.class)
	public void updateCardType_将name设为已有的字符串() {
		CardType cardType = cardTypeService.getCardType(1L);
		cardType.setName("Sprint");
		cardTypeService.updateCardType(cardType);
	}
	
	@Test
	public void updateCardType_将name设为正常的字符串() {
		CardType cardType = cardTypeService.getCardType(1L);
		cardType.setName("nothing");
		cardTypeService.updateCardType(cardType);
	}
	
	@Test
	public void deleteCardType_smoke() {
		long idToDelete = 2L;
		List<CardType> childTypes = jdbcTemplate.query(
				"select * from card_type where parent_card_type_id = ?",
				new CardTypeRowMapper(), idToDelete);
		assertNotNull(childTypes);

		CardType cardType = cardTypeService.getCardType(idToDelete);
		cardTypeService.deleteCardType(cardType);
		// 卡片类型的上级为上级的上级
		for (CardType type : childTypes) {
			type = jdbcTemplate.queryForObject(
					"select * from card_type where id = ?",
					new CardTypeRowMapper(), type.getId());
			assertEquals(Long.valueOf(1), type.getParent().getId());
		}
		// 删除所有该类型的卡片
		int countOfCards = jdbcTemplate
				.queryForInt("select count(*) from card where card_type = 2");
		assertEquals(0, countOfCards);
		// 删除该卡片类型
		List<CardType> expected = jdbcTemplate.query(
				"select * from card_type where id = ?",
				new CardTypeRowMapper(), idToDelete);
		Assert.assertTrue(expected.isEmpty());
	}

	@Test
	public void saveCardProperty_DateProperty_WithoutSort() {
		Space space = spaceService.getSpace(1L);
		// 插入前
		List<CardProperty> before = jdbcTemplate
				.query("select * from card_property " +
						" where space_id = ? and name = ? and hidden = ? and type = ?",
						CardTypeRowMapper.PROPERTY_MAPPER, space.getId(), "测试属性1", 0, "date");
		int max_before = jdbcTemplate.queryForInt(
				"select max(sort) from card_property where space_id = ? ", space.getId());
		// 插入一条日期类型，不设定顺序
		CardProperty cp = new DateProperty();
		cp.setSpace(space);
		cp.setHidden(false);
		cp.setName("测试属性1");
		cardTypeService.saveCardProperty(cp,null);
		// 插入后
		List<CardProperty> after = jdbcTemplate
				.query("select * from card_property " +
						" where space_id = ? and name = ? and hidden = ?  and type = ?",
						CardTypeRowMapper.PROPERTY_MAPPER, space.getId(), "测试属性1", 0, "date");
		int max_after = jdbcTemplate.queryForInt(
				"select max(sort) from card_property where space_id = ? ", space.getId());
		Assert.assertEquals(1, (after.size() - before.size()));
		Assert.assertEquals(1, (max_after - max_before));
	}

	@Test
	public void saveCardProperty_DateProperty_WithSort() {
		Space space = spaceService.getSpace(1L);
		// 插入前
		List<CardProperty> before = jdbcTemplate
				.query("select * from card_property " +
						" where space_id = ? and name = ? and hidden = ? and sort = ? and type = ?",
						CardTypeRowMapper.PROPERTY_MAPPER, space.getId(), "测试属性1", 0, 0, "date");
		// 插入一条日期类型，设定顺序
		CardProperty cp = new DateProperty();
		cp.setSpace(space);
		cp.setHidden(false);
		cp.setName("测试属性1");
		cp.setSort(0);
		cardTypeService.saveCardProperty(cp,null);
		// 插入后
		List<CardProperty> after = jdbcTemplate
				.query("select * from card_property " +
						" where space_id = ? and name = ? and hidden = ? and sort = ? and type = ?",
						CardTypeRowMapper.PROPERTY_MAPPER, space.getId(), "测试属性1", 0, 0, "date");
		Assert.assertEquals(1, (after.size() - before.size()));
	}

	@Test
	public void saveCardProperty_ListProperty_smoke() {
		Space space = spaceService.getSpace(1L);
		List<CardProperty> before = jdbcTemplate
				.query("select * from card_property " +
						" where space_id = ? and name = ? and hidden = ?   and type = ?",
						CardTypeRowMapper.PROPERTY_MAPPER, space.getId(), "测试列表属性1", 0,
						"list");
		CardProperty cp = new ListProperty();
		cp.setSpace(space);
		cp.setHidden(false);
		cp.setName("测试列表属性1");
		cp.setInfo("{\"1302536f-60d7-415d-9e84-1ac102c57e44\":\"高\",\"2cd434d9-0e9a-431f-a492-1a7fb0ea229b\":\"中\",\"97ddb81c-3851-437c-8949-aab203f9f0e5\":\"低\"}");
		cardTypeService.saveCardProperty(cp,null);
		// 插入后
		List<CardProperty> after = jdbcTemplate
				.query("select * from card_property " +
						" where space_id = ? and name = ? and hidden = ? and type = ?",
						CardTypeRowMapper.PROPERTY_MAPPER, space.getId(), "测试列表属性1", 0, "list");
		Assert.assertEquals(1, (after.size() - before.size()));
	}
	
	@Test
	public void saveCardProperty_LocalIdSmoke() {
		Space space = spaceService.getSpace(1L);
		CardType ct = cardTypeService.getCardType(2L);
		CardProperty cp = new DateProperty();
		cp.setSpace(space);
		cp.setHidden(false);
		cp.setName("测试属性1");
		cp.setSort(0);
		cardTypeService.saveCardProperty(cp,null);
		cp = cardTypeService.getCardProperty(cp.getId());
		assertEquals(cp.getLocalId().longValue(), 1L);
		ct = cardTypeService.getCardType(2L);
		cp = new DateProperty();
		cp.setSpace(space);
		cp.setHidden(false);
		cp.setName("测试属性1");
		cp.setSort(0);
		cardTypeService.saveCardProperty(cp,null);
		assertEquals(cp.getLocalId().longValue(), 2L);
		
		int next = jdbcTemplate.queryForInt(
				"select next_card_property_local_id from space_sequence where id = ?", ct.getSpace().getId());
		assertEquals(3,next);
	}

	@Test
	public void getCardPropertyById_smoke() {
		CardProperty cp = cardTypeService.getCardProperty(1L);
		CardProperty expected = jdbcTemplate.queryForObject(
				"select * from card_property where id = ?",
				CardTypeRowMapper.PROPERTY_MAPPER, 1L);
		Assert.assertNotNull(cp);
		Assert.assertEquals(cp.getId(), expected.getId());
	}
	
	@Test
	public void getCardPropertyBySpaceIdAndLocalId_smoke(){
		CardProperty cp = cardTypeService.getCardProperty(1L,1L);
		Assert.assertEquals(cp.getId(), new Long(1));
	}

	@Test
	public void deleteCardProperty_smoke() {
		CardProperty cp = cardTypeService.getCardProperty(1L);
		Assert.assertNotNull(cp);
		cardTypeService.deleteCardProperty(cp);
		CardProperty cp_after = cardTypeService.getCardProperty(1L);
		Assert.assertNull(cp_after);
	}
	
	@Test
	public void saveNewListOptionKey_smoke(){
		List<String> srcString = new ArrayList<String>();
		srcString.add("");
		srcString.add("");
		srcString.add("ttt");
		srcString.add("mmm");
		srcString.add("");
		
		List<String> targetString = new ArrayList<String>();
		targetString.add("1");
		targetString.add("2");
		targetString.add("ttt");
		targetString.add("mmm");
		targetString.add("3");
		
		srcString = cardTypeService.saveNewListOptionKey(1L, srcString);
		
		TestUtils.assertReflectionArrayEquals(srcString.toArray(), targetString.toArray());
		int next = jdbcTemplate.queryForInt(
				"select next_list_value_local_id from space_sequence where id = ?", 1L);
		assertEquals(next,4);
		
	}

	private static class CardTypeRowMapper implements RowMapper<CardType> {
		private static final CardTypeRowMapper INSTANCE = new CardTypeRowMapper();
		private static final RowMapper<CardProperty> PROPERTY_MAPPER = new RowMapper<CardProperty>() {
			@Override
			public CardProperty mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				String type = rs.getString("type");
				CardProperty p = null;
				if (type != null) {
					if (ListProperty.TYPE.equals(type)) {
						p = new ListProperty();
					} else if (NumberProperty.TYPE.equals(type)) {
						p = new NumberProperty();
					} else if (TextProperty.TYPE.equals(type)) {
						p = new TextProperty();
					} else if (DateProperty.TYPE.equals(type)) {
						p = new DateProperty();
					}
					p.setId(rs.getLong("id"));
				}
				return p;
			}
		};

		@Override
		public CardType mapRow(ResultSet rs, int rowNum) throws SQLException {
			CardType cardtype = new CardType();
			cardtype.setId(rs.getLong("id"));
			cardtype.setName(rs.getString("name"));
			cardtype.setRecursive(rs.getBoolean("recursive"));
			Space space = new Space();
			space.setId(rs.getLong("space_id"));
			if (rs.getObject("parent_card_type_id") != null) {
				CardType parent = new CardType();
				parent.setId(rs.getLong("parent_card_type_id"));
				cardtype.setParent(parent);
			}
			cardtype.setSpace(space);
			List<CardProperty> cps = jdbcTemplate.query(
					"select * from card_property left join card_type_property on card_type_property.card_property_id = card_property.id where card_type_property.card_type_id = ?",
					PROPERTY_MAPPER, cardtype.getId());
			cardtype.setCardProperties(new HashSet<CardProperty>(cps));
			List<CardType> children = jdbcTemplate.query(
					"select * from card_type where parent_card_type_id = ?",
					new RowMapper<CardType>() {
						@Override
						public CardType mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							CardType child = new CardType();
							child.setId(rs.getLong("id"));
							return child;
						}

					}, cardtype.getId());
			cardtype.setChildren(new HashSet<CardType>(children));
			return cardtype;
		}
	}

	//除了dbunit之外,新增4,5,6,7四个cardType,且以4为根
	private void addAnotherCardTypeTree(){
		Space space = spaceService.getSpace(1L);
		
		CardType card4 = new CardType();
		card4.setSpace(space);
		card4.setName("card4");
		cardTypeService.saveCardType(card4);
		
		CardType card5 = new CardType();
		card5.setSpace(space);
		card5.setName("card5");
		card5.setParent(card4);
		cardTypeService.saveCardType(card5);
		
		CardType card6 = new CardType();
		card6.setSpace(space);
		card6.setName("card6");
		card6.setParent(card5);
		cardTypeService.saveCardType(card6);
		
		CardType card7 = new CardType();
		card7.setSpace(space);
		card7.setName("card7");
		card7.setParent(card6);
		cardTypeService.saveCardType(card7);
	}
	
	//根据cardType对保存到数据库中的数据进行检查,校验与cardType是否一致.
	private void compareCardTypeWithDB(CardType cardType){
		CardType expected = jdbcTemplate.queryForObject(
				"select * from card_type where id = ?",
				new CardTypeRowMapper(), cardType.getId());
		assertNotNull(expected);
		assertEquals(expected.getName(), cardType.getName());
		assertEquals(expected.getRecursive(), cardType.getRecursive());
		if (expected.getSpace() != null || cardType.getSpace() != null){
			assertEquals(expected.getSpace().getId(), cardType.getSpace().getId());
		}
		if (expected.getParent() != null || cardType.getParent() != null){
			assertEquals(expected.getParent().getId(), cardType.getParent().getId());
		}
	}
}
