package com.baidu.spark.service.integrate;

import static com.baidu.spark.TestUtils.assertReflectionArrayEquals;
import static com.baidu.spark.TestUtils.assertReflectionEquals;
import static com.baidu.spark.TestUtils.clearTable;
import static com.baidu.spark.TestUtils.closeSessionInTest;
import static com.baidu.spark.TestUtils.initDatabase;
import static com.baidu.spark.TestUtils.openSessionInTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.spark.SparkTestUtils;
import com.baidu.spark.dao.Pagination;
import com.baidu.spark.dao.SpaceSequenceDao;
import com.baidu.spark.model.Project;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.SpaceView;
import com.baidu.spark.model.Space.SpaceType;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.model.space.Spacegroup;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.service.UserService;

/**
 * 空间服务接口测试.
 * 
 * @author GuoLin
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml" ,"/applicationContext-security-test.xml"})
public class SpaceServiceTest {

	@Autowired
	private SpaceService spaceService;

	@Autowired
	private SpaceSequenceDao spaceSequenceDao;
	
	@Autowired
	private UserService userService;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private DataSource dataSource;

	private static SimpleJdbcTemplate jdbcTemplate;
	
	@Before
	public void before() throws Exception {
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		jdbcTemplate.update("SET REFERENTIAL_INTEGRITY FALSE");
		clearTable(dataSource, "users");
		clearTable(dataSource, "card");
		clearTable(dataSource, "card_property_value");
		clearTable(dataSource, "card_property");
		clearTable(dataSource, "card_type");
		clearTable(dataSource,"group_space");
		clearTable(dataSource,"group_user");
		clearTable(dataSource,"groups");
		clearTable(dataSource,"spaces");
		jdbcTemplate.update("SET REFERENTIAL_INTEGRITY TRUE");
		initDatabase(dataSource, "com/baidu/spark/service/integrate/SpaceServiceTest.xml");
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		SparkTestUtils.setCurrentUserAdmin(userService);
		SparkTestUtils.initAclDatabase(dataSource);
		openSessionInTest(sessionFactory);
	}

	@After
	public void after() throws Exception {
		closeSessionInTest(sessionFactory);
	}

	@Test
	public void getAllSpaces_pagination_smoke() {
		Pagination<Space> pagination = spaceService.getAllSpaces(new Pagination<Space>(1));
		assertNotNull(pagination);
		assertEquals(3, pagination.getTotal());
		assertNotNull(pagination.getResults());
		assertEquals(3, pagination.getResults().size());

		List<Space> spaces = jdbcTemplate.query("select * from spaces", SpaceRowMapper.INSTANCE);
		assertReflectionArrayEquals(spaces.toArray(), pagination.getResults().toArray());
	}

	@Test
	public void getAllSpaces_smoke() {
		List<Space> list = spaceService.getAllSpaces();
		assertNotNull(list);
		assertEquals(3, list.size());

		List<Space> spaces = jdbcTemplate.query("select * from spaces", SpaceRowMapper.INSTANCE);
		assertReflectionArrayEquals(spaces.toArray(), list.toArray());
	}

	@Test
	public void getSpace_smoke() {
		Space actual = spaceService.getSpace(2L);
		Space expected = jdbcTemplate.queryForObject("select * from spaces where id = ?",
				SpaceRowMapper.INSTANCE, 2L);
		assertReflectionEquals(expected, actual);
	}

	@Test
	public void getSpace_notExists() {
		Space actual = spaceService.getSpace(2020l);
		assertNull(actual);
	}

	@Test
	public void getSpaceByPrefixCode_smoke() {
		Space expected = jdbcTemplate.queryForObject("select * from spaces where prefix_code = ?",
				SpaceRowMapper.INSTANCE, "spark1");
		Space actual = spaceService.getSpaceByPrefixCode("spark1");
		assertReflectionEquals(expected, actual);
	}

	@Test
	public void getSpaceByPrefixCode_notExists() {
		Space actual = spaceService.getSpaceByPrefixCode("xxxAAA");
		assertNull(actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getSpaceByPrefixCode_null() {
		spaceService.getSpaceByPrefixCode(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getSpaceByPrefixCode_empty() {
		spaceService.getSpaceByPrefixCode("");
	}

	@Test
	public void saveSpace_smoke() {
		Space actual1 = new Space();
		actual1.setName("name1");
		actual1.setPrefixCode("pc1");
		actual1.setType(SpaceType.NORMAL);
		actual1.setCardProperties(new HashSet<CardProperty>());
		actual1.setProjects((new HashSet<Project>()));
		actual1.setViews(new ArrayList<SpaceView>(0));
		actual1.setCardTypes(new HashSet<CardType>());
		spaceService.saveSpace(actual1);
		Space expected1 = jdbcTemplate.queryForObject("select * from spaces where prefix_code = ?",
				SpaceRowMapper.INSTANCE, "pc1");
		assertReflectionEquals(expected1, actual1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveSpace_null() {
		spaceService.saveSpace(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveSpace_nullName() {
		Space actual = new Space();
		actual.setName(null);
		actual.setPrefixCode("pc1");
		actual.setType(SpaceType.NORMAL);
		spaceService.saveSpace(actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveSpace_emptyName() {
		Space actual = new Space();
		actual.setName("");
		actual.setPrefixCode("pc1");
		actual.setType(SpaceType.NORMAL);
		spaceService.saveSpace(actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveSpace_nullPrefixCode() {
		Space actual = new Space();
		actual.setName("name1");
		actual.setPrefixCode(null);
		actual.setType(SpaceType.NORMAL);
		spaceService.saveSpace(actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveSpace_emptyPrefixCode() {
		Space actual = new Space();
		actual.setName("name1");
		actual.setPrefixCode("");
		actual.setType(SpaceType.NORMAL);
		spaceService.saveSpace(actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveSpace_typeNull() {
		Space actual = new Space();
		actual.setName("name1");
		actual.setPrefixCode("pc1");
		actual.setType(null);
		spaceService.saveSpace(actual);
	}

	@Test
	public void getNextSpaceSeq_smoke() {
		Long beforeDbData = spaceSequenceDao.get(1L).getNextCardSeqNum();
		Long nextSeq = spaceService.getNextSpaceSeq(1L);
		spaceSequenceDao.clear();
		Long dbData = spaceSequenceDao.get(1L).getNextCardSeqNum();
		Long expected = jdbcTemplate.queryForLong(
				"select next_card_seq_num from space_sequence where id = ?", 1L);

		assertEquals(beforeDbData, nextSeq);
		assertEquals(new Long(nextSeq + 1), expected);
		assertEquals(dbData, expected);
		assertNotNull(nextSeq);
	}
	
	@Test
	public void deleteSpace_smoke(){
		Space space = spaceService.getSpace(1L);
		spaceService.deteleSpace(space);
	}
	
	//deleting a null space should raise an IllegalArgumentException
	@Test(expected = IllegalArgumentException.class)
	public void deleteSpace_nullSpace(){
		spaceService.deteleSpace(null);
	}

	private static class SpaceRowMapper implements RowMapper<Space> {
		private static final SpaceRowMapper INSTANCE = new SpaceRowMapper();
		private static final RowMapper<CardType> CARDTYPE_MAPPER = new RowMapper<CardType>() {
			@Override
			public CardType mapRow(ResultSet rs, int rowNum) throws SQLException {
				CardType cardType = new CardType();
				cardType.setId(rs.getLong("id"));
				return cardType;
			}
		};

		@Override
		public Space mapRow(ResultSet rs, int rowNum) throws SQLException {
			Space space = new Space();
			space.setId(rs.getLong("id"));
			space.setName(rs.getString("name"));
			space.setPrefixCode(rs.getString("prefix_code"));
			space.setType(SpaceType.values()[rs.getInt("type")]);
			space.setIsPublic(rs.getString("is_public")==null?false:rs.getBoolean("is_public"));
			List<CardType> cardTypes = jdbcTemplate.query("select id from card_type where space_id = ?", CARDTYPE_MAPPER, space.getId());
			space.setCardTypes(new HashSet<CardType>(cardTypes));
			space.setCardProperties(new HashSet<CardProperty>());
			space.setProjects((new HashSet<Project>()));
			space.setViews(new ArrayList<SpaceView>(0));
			space.setSpaceGroups(new HashSet<Spacegroup>());
			return space;
		}
	}
	
}
