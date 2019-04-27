package com.baidu.spark.service.integrate;

import static com.baidu.spark.TestUtils.clearTable;
import static com.baidu.spark.TestUtils.initDatabase;

import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.spark.SparkTestUtils;
import com.baidu.spark.model.Project;
import com.baidu.spark.model.Space;
import com.baidu.spark.service.ProjectService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml","/applicationContext-security-test.xml" })
public class ProjectServiceTest {
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private SpaceService spaceService;
	
	@Autowired
	private UserService userService;
	
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
		clearTable(dataSource, "icafe_project");
		jdbcTemplate.update("SET REFERENTIAL_INTEGRITY TRUE");
		initDatabase(dataSource,
				"com/baidu/spark/service/integrate/ProjectServiceTest.xml");
		SparkTestUtils.setCurrentUserAdmin(userService);
	}

	@After
	public void after() throws Exception {
	}
	
	@Test
	public void getAll_smoke(){
		Space space = spaceService.getSpace(1L);
		List<Project> l = projectService.getAll(space);
		Assert.assertNotNull(l);
	}
}
