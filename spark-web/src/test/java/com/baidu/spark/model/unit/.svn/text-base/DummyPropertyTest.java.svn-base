package com.baidu.spark.model.unit;

import static com.baidu.spark.TestUtils.assertReflectionEquals;
import static com.baidu.spark.TestUtils.clearTable;
import static com.baidu.spark.TestUtils.closeSessionInTest;
import static com.baidu.spark.TestUtils.initDatabase;
import static com.baidu.spark.TestUtils.openSessionInTest;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.model.card.property.DateProperty;
import com.baidu.spark.model.card.property.DummyProperty;
import com.baidu.spark.model.card.property.NumberProperty;
import com.baidu.spark.model.card.property.TextProperty;

/**
 * com.baidu.spark.model.card.property.DummyProperty类的测试用例
 * 
 * @author shixiaolei
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml","/applicationContext-security-test.xml"  })
public class DummyPropertyTest {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unused")
	private SimpleJdbcTemplate jdbcTemplate;


	@Before
	public void before() throws Exception {
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		clearTable(dataSource, "card_property_value");
		clearTable(dataSource, "card_property");
		clearTable(dataSource, "card_type");
		clearTable(dataSource, "card");
		clearTable(dataSource, "users");
		clearTable(dataSource, "space_sequence");
		clearTable(dataSource, "spaces");
		clearTable(dataSource, "card_history");
		initDatabase(dataSource,
				"com/baidu/spark/service/integrate/CardTypeServiceTest.xml");
		openSessionInTest(sessionFactory);

	}

	@After
	public void after() throws Exception {
		closeSessionInTest(sessionFactory);
	}

	// //////以下测试实际类型是TextProperty的情况//////
	@Test
	public void generateActualProperty_TextProperty_正常() {

		DummyProperty dp = new DummyProperty();
		dp.setId(20L);
		dp.setHidden(false);
		dp.setType("text");
		dp.setInfo("这是一个TextProperty");
		dp.setName("MyTextProperty");
		dp.setSort(0);
		dp.setSpace(new Space());

		TextProperty tp = new TextProperty();
		tp.setId(20L);
		tp.setHidden(false);
		tp.setInfo("这是一个TextProperty");
		tp.setName("MyTextProperty");
		tp.setSort(0);
		tp.setSpace(new Space());

		CardProperty cp = dp.generateActualProperty();
		Assert.assertEquals(cp.getClass(), TextProperty.class);
		assertReflectionEquals(cp, tp);
	}

	@Test
	public void generateActualProperty_TextProperty_没有为Id赋值() {
		DummyProperty dp = new DummyProperty();
		dp.setHidden(false);
		dp.setType("text");
		dp.setInfo("这是一个TextProperty");
		dp.setName("MyTextProperty");
		dp.setSort(0);
		dp.setSpace(new Space());

		TextProperty tp = new TextProperty();
		tp.setId(20L);
		tp.setHidden(false);
		tp.setInfo("这是一个TextProperty");
		tp.setName("MyTextProperty");
		tp.setSort(0);
		tp.setSpace(new Space());

		CardProperty cp = dp.generateActualProperty();
		Assert.assertEquals(cp.getClass(), TextProperty.class);
		Assert.assertNull(dp.getId());
		Assert.assertEquals(dp.getName(), tp.getName());
	}

	// //////以下测试实际类型是NumberProperty的情况//////
	@Test
	public void generateActualProperty_NumberProperty_正常() {

		DummyProperty dp = new DummyProperty();
		dp.setId(20L);
		dp.setHidden(false);
		dp.setType("number");
		dp.setInfo("这是一个NumberProperty");
		dp.setName("MyNumberProperty");
		dp.setSort(0);
		dp.setSpace(new Space());

		NumberProperty tp = new NumberProperty();
		tp.setId(20L);
		tp.setHidden(false);
		tp.setInfo("这是一个NumberProperty");
		tp.setName("MyNumberProperty");
		tp.setSort(0);
		tp.setSpace(new Space());

		CardProperty cp = dp.generateActualProperty();
		Assert.assertEquals(cp.getClass(), NumberProperty.class);
		assertReflectionEquals(cp, tp);
	}

	@Test
	public void generateActualProperty_NumberProperty_没有为Id赋值() {
		DummyProperty dp = new DummyProperty();
		dp.setHidden(false);
		dp.setType("number");
		dp.setInfo("这是一个NumberProperty");
		dp.setName("MyNumberProperty");
		dp.setSort(0);
		dp.setSpace(new Space());

		NumberProperty tp = new NumberProperty();
		tp.setId(20L);
		tp.setHidden(false);
		tp.setInfo("这是一个NumberProperty");
		tp.setName("MyNumberProperty");
		tp.setSort(0);
		tp.setSpace(new Space());

		CardProperty cp = dp.generateActualProperty();
		Assert.assertEquals(cp.getClass(), NumberProperty.class);
		Assert.assertNull(dp.getId());
		Assert.assertEquals(dp.getName(), tp.getName());
	}

	// //////以下测试实际类型是DateProperty的情况//////
	@Test
	public void generateActualProperty_DateProperty_正常() {

		DummyProperty dp = new DummyProperty();
		dp.setId(20L);
		dp.setHidden(false);
		dp.setType("date");
		dp.setInfo("这是一个DateProperty");
		dp.setName("MyDateProperty");
		dp.setSort(0);
		dp.setSpace(new Space());

		DateProperty tp = new DateProperty();
		tp.setId(20L);
		tp.setHidden(false);
		tp.setInfo("这是一个DateProperty");
		tp.setName("MyDateProperty");
		tp.setSort(0);
		tp.setSpace(new Space());

		CardProperty cp = dp.generateActualProperty();
		Assert.assertEquals(cp.getClass(), DateProperty.class);
		assertReflectionEquals(cp, tp);
	}

	@Test
	public void generateActualProperty_DateProperty_没有为Id赋值() {
		DummyProperty dp = new DummyProperty();
		dp.setHidden(false);
		dp.setType("date");
		dp.setInfo("这是一个DateProperty");
		dp.setName("MyDateProperty");
		dp.setSort(0);
		dp.setSpace(new Space());

		DateProperty tp = new DateProperty();
		tp.setId(20L);
		tp.setHidden(false);
		tp.setInfo("这是一个DateProperty");
		tp.setName("MyDateProperty");
		tp.setSort(0);
		tp.setSpace(new Space());

		CardProperty cp = dp.generateActualProperty();
		Assert.assertEquals(cp.getClass(), DateProperty.class);
		Assert.assertNull(dp.getId());
		Assert.assertEquals(dp.getName(), tp.getName());
	}

	@Test
	public void generateActualProperty_为非ListProperty赋ListProperty专有的属性值() {
		DummyProperty dp = new DummyProperty();
		dp.setHidden(false);
		dp.setType("text");
		dp.setInfo("这是一个TextProperty");
		dp.setName("MyTextProperty");
		dp.setSort(0);
		dp.setSpace(new Space());

		TextProperty tp = new TextProperty();
		tp.setId(20L);
		tp.setHidden(false);
		tp.setInfo("这是一个TextProperty");
		tp.setName("MyTextProperty");
		tp.setSort(0);

		CardProperty cp = dp.generateActualProperty();
		Assert.assertEquals(cp.getClass(), TextProperty.class);
		Assert.assertNull(dp.getId());
		Assert.assertEquals(dp.getName(), tp.getName());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void generateActualProperty_赋一个没有定义的Property类型() {
		DummyProperty dp = new DummyProperty();
		dp.setId(20L);
		dp.setType("undefined");
		dp.setName("测试DUMMY_PROPERTY字符串属性");
		dp.setSort(0);
		Assert.assertNull(dp.generateActualProperty().getType());
	}

	
}
