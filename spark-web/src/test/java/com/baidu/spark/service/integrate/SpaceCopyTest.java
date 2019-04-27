package com.baidu.spark.service.integrate;

import static com.baidu.spark.SparkTestUtils.initAclDatabase;
import static com.baidu.spark.TestUtils.clearTable;
import static com.baidu.spark.TestUtils.initDatabase;
import static com.baidu.spark.TestUtils.openSessionInTest;

import java.util.List;

import javax.sql.DataSource;

import junit.framework.Assert;

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
import com.baidu.spark.model.Group;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.SpaceSequence;
import com.baidu.spark.model.SpaceView;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.service.CardTypeBasicService;
import com.baidu.spark.service.SpaceCopyService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.service.UserService;
import com.baidu.spark.service.impl.helper.copyspace.metadata.Metadata;
import com.baidu.spark.service.impl.helper.copyspace.metadata.impl.CardPropertyMetadata;
import com.baidu.spark.service.impl.helper.copyspace.metadata.impl.CardTypeMetadata;
import com.baidu.spark.service.impl.helper.copyspace.metadata.impl.GroupMetadata;
import com.baidu.spark.service.impl.helper.copyspace.metadata.impl.SpaceSequenceMetadata;
import com.baidu.spark.service.impl.helper.copyspace.metadata.impl.SpaceViewMetadata;
import com.baidu.spark.service.impl.helper.copyspace.transformer.impl.CardPropertyTransformer;
import com.baidu.spark.service.impl.helper.copyspace.transformer.impl.CardTypeTransformer;
import com.baidu.spark.service.impl.helper.copyspace.transformer.impl.GroupTransformer;
import com.baidu.spark.service.impl.helper.copyspace.transformer.impl.SpaceSequenceTransformer;
import com.baidu.spark.service.impl.helper.copyspace.transformer.impl.SpaceViewTransformer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext-test.xml","/applicationContext-security-test.xml"})
public class SpaceCopyTest {
	
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private SpaceService spaceService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private GroupTransformer groupTransformer;
	
	@Autowired
	private CardTypeTransformer cardTypeTransformer;
	
	@Autowired
	private CardTypeBasicService cardTypeService;
	
	@Autowired
	private CardPropertyTransformer cardPropertyTransformer;
	
	@Autowired
	private SpaceSequenceTransformer spaceSequenceTransformer;
	
	@Autowired
	private SpaceViewTransformer spaceViewTransformer;
	
	@Autowired
	private SpaceCopyService spaceCopyService;
	
	@Autowired
	private SessionFactory sessionFactory;

	private static SimpleJdbcTemplate jdbcTemplate;

	
	@Before
	public void before() throws Exception {
		clearTable(dataSource, "card");
		clearTable(dataSource, "card_history");
		clearTable(dataSource, "groups");
		clearTable(dataSource, "card_type");
		clearTable(dataSource, "card_property");
		clearTable(dataSource, "spaces");
		clearTable(dataSource, "group_user");
		clearTable(dataSource, "group_space");
		initAclDatabase(dataSource);
		initDatabase(dataSource,
				"com/baidu/spark/service/integrate/SpaceCopierTest.xml");
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		SparkTestUtils.setCurrentUserAdmin(userService);
		openSessionInTest(sessionFactory);
		
	}
	@After
	public void after(){
		TestUtils.closeSessionInTest(sessionFactory);
	}
	
	@Test
	public void testSpaceSequenceTransformerGetJson_smoke(){
		Space space = spaceService.getSpace(1L);
		SpaceSequenceMetadata metadata = spaceSequenceTransformer.exportMetadata(space);
		Assert.assertEquals("{\"nextCardSeqNum\":1,\"nextCardTypeLocalId\":3,\"nextCardPropertyLocalId\":1,\"nextListValueLocalId\":4}", metadata.getResultData());
		SpaceSequence sequence = spaceSequenceTransformer.getMetadata(metadata.getResultData()).getPojos().get(0);
		Assert.assertEquals(sequence.getNextCardPropertyLocalId(), new Long(1));
		Assert.assertEquals(sequence.getNextCardSeqNum(), new Long(1));
		Assert.assertEquals(sequence.getNextCardTypeLocalId(), new Long(3));
		Assert.assertEquals(sequence.getNextListValueLocalId(), new Long(4));
//		spaceSequenceTransformer.importMetadata(space, metadata.getResultData());

	}
	
	@Test
	public void testCardTypeTransformerGetJson_smoke(){
		Space space = spaceService.getSpace(1L);
		CardTypeMetadata metadata = cardTypeTransformer.exportMetadata(space);
		System.out.println(metadata.getResultData());
		List<CardType> retList = cardTypeTransformer.getMetadata(metadata.getResultData()).getPojos();
		System.out.println(retList.size());
	}
	
	@Test
	public void testSpaceViewTransformerGetJson_smoke(){
		Space space = spaceService.getSpace(1L);
		SpaceViewMetadata metadata = spaceViewTransformer.exportMetadata(space);
		System.out.println(metadata.getResultData());
		List<SpaceView> retList = spaceViewTransformer.getMetadata(metadata.getResultData()).getPojos();
		System.out.println(retList.size());
	}
	
	@Test
	public void testCardPropertyTransformerGetJson_smoke(){
		Space space = spaceService.getSpace(1L);
		CardPropertyMetadata metadata = cardPropertyTransformer.exportMetadata(space);
		System.out.println(metadata.getResultData());
		List<CardProperty> retList = cardPropertyTransformer.getMetadata(metadata.getResultData()).getPojos();
		System.out.println(retList.size());
	}
	@Test
	public void testGroupTransformerGetJson_smoke(){
		Space space = spaceService.getSpace(1L);
		GroupMetadata metadata = groupTransformer.exportMetadata(space);
		System.out.println(metadata.getResultData());
		List<Group> retList = groupTransformer.getMetadata(metadata.getResultData()).getGroups();
		System.out.println(retList.size());
	}
	
	@Test
	public void testSpaceCopy(){
		Space targetSpace = new Space();
		targetSpace.setName("123");
		targetSpace.setPrefixCode("123");
		spaceService.saveSpace(targetSpace);
		
		Space space = spaceService.getSpace(1L);
		List<Metadata> metadataList = spaceCopyService.getSpaceMetadata(space);
		spaceCopyService.importMetadata(targetSpace, metadataList);
		
	}
	

}
