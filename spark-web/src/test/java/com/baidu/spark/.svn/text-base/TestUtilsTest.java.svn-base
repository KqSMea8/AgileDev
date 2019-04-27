package com.baidu.spark;

import static com.baidu.spark.TestUtils.assertReflectionArrayEquals;
import static com.baidu.spark.TestUtils.assertReflectionEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * 测试工具类测试用例.
 * 
 * 
 * @author GuoLin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext-test.xml"})
public class TestUtilsTest {
	
	private Mock child1;
	
	private Mock child2;
	
	private Mock parent1;
	
	private Mock parent2;
	
	@Autowired
	private DataSource dataSource;

	private SimpleJdbcTemplate jdbcTemplate;
	
	@Before
	public void before() {
		Date date1 = new Date();
		Date date2 = new Date();
		
		parent1 = new Mock();
		parent1.setPPrimativeInt(10);
		parent1.setPInteger(11);
		parent1.setPPrimativeLong(100l);
		parent1.setPLong(1000l);
		parent1.setPString("Testing");
		parent1.setPPrimativeShort(Short.parseShort("1"));
		parent1.setPShort(Short.valueOf("2"));
		parent1.setPPrimativeBoolean(true);
		parent1.setPBoolean(false);
		parent1.setPDate(date1);
		parent1.setType(MockType.A);
		
		child1 = new Mock();
		child1.setPPrimativeInt(10);
		child1.setPInteger(11);
		child1.setPPrimativeLong(100l);
		child1.setPLong(1000l);
		child1.setPString("Testing");
		child1.setPPrimativeShort(Short.parseShort("1"));
		child1.setPShort(Short.valueOf("2"));
		child1.setPPrimativeBoolean(true);
		child1.setPBoolean(false);
		child1.setPDate(date2);
		child1.setType(MockType.B);
		child1.setParent(parent1);
		
		parent2 = new Mock();
		parent2.setPPrimativeInt(10);
		parent2.setPInteger(11);
		parent2.setPPrimativeLong(100l);
		parent2.setPLong(1000l);
		parent2.setPString("Testing");
		parent2.setPPrimativeShort(Short.parseShort("1"));
		parent2.setPShort(Short.valueOf("2"));
		parent2.setPPrimativeBoolean(true);
		parent2.setPBoolean(false);
		parent2.setPDate(date1);
		parent2.setType(MockType.A);
		
		child2 = new Mock();
		child2.setPPrimativeInt(10);
		child2.setPInteger(11);
		child2.setPPrimativeLong(100l);
		child2.setPLong(1000l);
		child2.setPString("Testing");
		child2.setPPrimativeShort(Short.parseShort("1"));
		child2.setPShort(Short.valueOf("2"));
		child2.setPPrimativeBoolean(true);
		child2.setPBoolean(false);
		child2.setPDate(date2);
		child2.setType(MockType.B);
		child2.setParent(parent2);
		
		List<Mock> children = new ArrayList<Mock>();
		children.add(child1);
		parent1.setChildren(children);
		
		children = new ArrayList<Mock>();
		children.add(child2);
		parent2.setChildren(children);
		
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		try{
			jdbcTemplate.update("DROP TABLE test_user");
		}catch(Exception e){
			
		}
		jdbcTemplate.update("CREATE TABLE test_user ( user_id int NOT NULL IDENTITY, group_id int, first_name LONGVARCHAR, last_name LONGVARCHAR)");
		
	}
	
	@Test
	public void equalsByAllProperties_smokeEquals() {
		assertReflectionEquals(child1, child2);
	}
	
	/*************************自身属性不同**************************************/
	
	@Test
	public void equalsByAllProperties_smokeChildIntegerNotEquals() {
		child1.setPInteger(133);
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	@Test
	public void equalsByAllProperties_smokeChildBooleanNotEquals() {
		child1.setPBoolean(!child1.getPBoolean());
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void equalsByAllProperties_smokeChildPShortNotEquals() {
		child1.setPShort(new Integer(child1.getPShort()+1).shortValue());
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	@Test
	public void equalsByAllProperties_smokeChildLongNotEquals() {
		child1.setPLong(child1.getPLong()+1);
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void equalsByAllProperties_smokeChildPrimativeBooleanNotEquals() {
		child1.setPPrimativeBoolean(!child1.isPPrimativeBoolean());
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void equalsByAllProperties_smokeChildPrimativeIntNotEquals() {
		child1.setPPrimativeInt(child1.getPPrimativeInt()+1);
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	@Test
	public void equalsByAllProperties_smokeChildPPrimativeLongNotEquals() {
		child1.setPPrimativeLong(child1.getPPrimativeLong()+1);
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void equalsByAllProperties_smokeChildPPrimativeShortNotEquals() {
		child1.setPPrimativeShort(new Integer(child1.getPPrimativeShort()+1).shortValue());
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void equalsByAllProperties_smokeChildPStringNotEquals() {
		child1.setPString("good");
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void assertReflectionEquals_smokeWithChildDateNotEqual(){
		child1.setPDate(new Date(10000l));
		try {
			assertReflectionEquals(parent1, parent2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	/***************************关联对象属性值不同********************************/
	@Test
	public void equalsByAllProperties_smokeParentIntegerNotEquals() {
		parent1.setPInteger(133);
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	@Test
	public void equalsByAllProperties_smokeParentBooleanNotEquals() {
		parent1.setPBoolean(!parent1.getPBoolean());
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void equalsByAllProperties_smokeParentPShortNotEquals() {
		parent1.setPShort(new Integer(parent1.getPShort()+1).shortValue());
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	@Test
	public void equalsByAllProperties_smokeParentLongNotEquals() {
		parent1.setPLong(parent1.getPLong()+1);
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void equalsByAllProperties_smokeParentPrimativeBooleanNotEquals() {
		parent1.setPPrimativeBoolean(!parent1.isPPrimativeBoolean());
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void equalsByAllProperties_smokeParentPrimativeIntNotEquals() {
		parent1.setPPrimativeInt(parent1.getPPrimativeInt()+1);
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	@Test
	public void equalsByAllProperties_smokeParentPPrimativeLongNotEquals() {
		parent1.setPPrimativeLong(parent1.getPPrimativeLong()+1);
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void equalsByAllProperties_smokeParentPPrimativeShortNotEquals() {
		parent1.setPPrimativeShort(new Integer(parent1.getPPrimativeShort()+1).shortValue());
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void equalsByAllProperties_smokeParentPStringNotEquals() {
		parent1.setPString("good");
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void assertReflectionEquals_smokeWithParentDateNotEqual(){
		parent1.setPDate(new Date(10000l));
		try {
			assertReflectionEquals(child1, child2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	/*********************************集合元素值不同*******************************/
	@Test
	public void equalsByAllProperties_smokeChildrenIntegerNotEquals() {
		child1.setPInteger(133);
		try {
			assertReflectionEquals(parent1, parent2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	@Test
	public void equalsByAllProperties_smokeChildrenBooleanNotEquals() {
		child1.setPBoolean(!child1.getPBoolean());
		try {
			assertReflectionEquals(parent1, parent2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void equalsByAllProperties_smokeChildrenPShortNotEquals() {
		child1.setPShort(new Integer(child1.getPShort()+1).shortValue());
		try {
			assertReflectionEquals(parent1, parent2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	@Test
	public void equalsByAllProperties_smokeChildrenLongNotEquals() {
		child1.setPLong(child1.getPLong()+1);
		try {
			assertReflectionEquals(parent1, parent2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void equalsByAllProperties_smokeChildrenPrimativeBooleanNotEquals() {
		child1.setPPrimativeBoolean(!child1.isPPrimativeBoolean());
		try {
			assertReflectionEquals(parent1, parent2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void equalsByAllProperties_smokeChildrenPrimativeIntNotEquals() {
		child1.setPPrimativeInt(child1.getPPrimativeInt()+1);
		try {
			assertReflectionEquals(parent1, parent2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	@Test
	public void equalsByAllProperties_smokeChildrenPPrimativeLongNotEquals() {
		child1.setPPrimativeLong(child1.getPPrimativeLong()+1);
		try {
			assertReflectionEquals(parent1, parent2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void equalsByAllProperties_smokeChildrenPPrimativeShortNotEquals() {
		child1.setPPrimativeShort(new Integer(child1.getPPrimativeShort()+1).shortValue());
		try {
			assertReflectionEquals(parent1, parent2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void equalsByAllProperties_smokeChildrenPStringNotEquals() {
		child1.setPString("good");
		try {
			assertReflectionEquals(parent1, parent2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void assertReflectionEquals_smokeWithChildrenDateNotEqual(){
		child1.setPDate(new Date(10000l));
		try {
			assertReflectionEquals(parent1, parent2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	
	/************************************************************************************/
	/**************************************arrayToString testcase************************/
	/************************************************************************************/
	/**
	 * 空参数
	 */
	@Test
	public void arrayToString_nullParam(){
		String ret = null;
		try{
			ret = TestUtils.arrayToString(null);
		}catch(Exception e){
			throw new AssertionError("arrayToString with null param throws exception");
		}
		assertEquals("null", ret);
	}
	/**
	 * 对象数组
	 */
	@Test
	public void arrayToString_ObjectArray(){
		String ret = null;
		String[] param = {"123","321","111","222"};
		try{
			ret = TestUtils.arrayToString(param);
		}catch(Exception e){
			throw new AssertionError("arrayToString with ObjectArray throws exception");
		}
		assertEquals("[123, 321, 111, 222]",ret);
	}
	/**
	 * 对象list
	 */
	@Test
	public void arrayToString_ObjectList(){
		String ret = null;
		List<String> param = new ArrayList<String>();
		param.add("123");
		param.add("321");
		param.add("111");
		param.add("222");
		try{
			ret = TestUtils.arrayToString(param);
		}catch(Exception e){
			throw new AssertionError("arrayToString with ObjectArray throws exception");
		}
		assertEquals("[123, 321, 111, 222]",ret);
	}
	/**
	 * 对象set
	 */
	@Test
	public void arrayToString_ObjectSet(){
		String ret = null;
		Set<String> param = new LinkedHashSet<String>();
		param.add("123");
		param.add("321");
		param.add("111");
		param.add("222");
		try{
			ret = TestUtils.arrayToString(param);
		}catch(Exception e){
			throw new AssertionError("arrayToString with ObjectArray throws exception");
		}
		assertEquals(param.toString(),ret);
	}
	/**
	 * 对象map
	 */
	@Test
	public void arrayToString_ObjectMap(){
		String ret = null;
		Map<String,String> param = new HashMap<String,String>();
		param.put("123","123");
		param.put("321","321");
		param.put("111","111");
		param.put("222","222");
		try{
			ret = TestUtils.arrayToString(param);
		}catch(Exception e){
			throw new AssertionError("arrayToString with ObjectArray throws exception");
		}
		assertEquals(param.toString(),ret);
	}
	/**************************************************************************************************/
	/**************************************assertReflectionArrayEquals testcase************************/
	/**************************************************************************************************/
	@Test
	public void assertReflectionArrayEquals_leftParamIsNull(){
		try {
			assertReflectionArrayEquals(null,new Integer[0]);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
		
	}
	
	@Test
	public void assertReflectionArrayEquals_rightParamIsNull(){
		try {
			assertReflectionArrayEquals(new Integer[0],null);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	@Test
	public void assertReflectionArrayEquals_twoParamAreNull(){
		try {
			assertReflectionArrayEquals(null,null);
		} catch (AssertionError err) {
			fail();
		}
	}
	
	@Test
	public void assertReflectionArrayEquals_leftParamIsNotArray(){
		boolean fail = false;
		try {
			assertReflectionArrayEquals(new HashSet(),new Integer[0]);
			fail = true;
		} catch (AssertionError err) {
			// this one pass
		}
		if(fail){
			fail();
		}
		try {
			assertReflectionArrayEquals(new HashMap(),new Integer[0]);
			fail = true;
		} catch (AssertionError err) {
			// this one pass
		}
		if(fail){
			fail();
		}
		try {
			assertReflectionArrayEquals(new Mock(),new Integer[0]);
			fail = true;
		} catch (AssertionError err) {
			// this one pass
		}
		if(fail){
			fail();
		}
		try {
			assertReflectionArrayEquals(new Integer(1),new Integer[0]);
			fail = true;
		} catch (AssertionError err) {
			// this one pass
		}
		if(fail){
			fail();
		}
		try {
			assertReflectionArrayEquals(new Long(1),new Integer[0]);
			fail = true;
		} catch (AssertionError err) {
			// this one pass
		}
		if(fail){
			fail();
		}
	}
	
	@Test
	public void assertReflectionArrayEquals_rightParamIsNotArray(){
		boolean fail = false;
		try {
			assertReflectionArrayEquals(new Integer[0],new HashSet());
			fail = true;
		} catch (AssertionError err) {
			// this one pass
		}
		if(fail){
			fail();
		}
		try {
			assertReflectionArrayEquals(new Integer[0],new HashMap());
			fail = true;
		} catch (AssertionError err) {
			// this one pass
		}
		if(fail){
			fail();
		}
		try {
			assertReflectionArrayEquals(new Integer[0],new Mock());
			fail = true;
		} catch (AssertionError err) {
			// this one pass
		}
		if(fail){
			fail();
		}
		try {
			assertReflectionArrayEquals(new Integer[0],new Integer(1));
			fail = true;
		} catch (AssertionError err) {
			// this one pass
		}
		if(fail){
			fail();
		}
		try {
			assertReflectionArrayEquals(new Integer[0],new Long(1));
			fail = true;
		} catch (AssertionError err) {
			// this one pass
		}
		if(fail){
			fail();
		}
	}
	
	@Test
	public void assertReflectionArrayEquals_twoParamAreNotArray(){
		boolean fail = false;
		try {
			assertReflectionArrayEquals(new HashMap(),new HashSet());
			fail = true;
		} catch (AssertionError err) {
			// this one pass
		}
		if(fail){
			fail();
		}
		try {
			assertReflectionArrayEquals(new HashSet(),new HashMap());
			fail = true;
		} catch (AssertionError err) {
			// this one pass
		}
		if(fail){
			fail();
		}
		try {
			assertReflectionArrayEquals(new Mock(),new Mock());
			fail = true;
		} catch (AssertionError err) {
			// this one pass
		}
		if(fail){
			fail();
		}
		try {
			assertReflectionArrayEquals(new Integer(1),new Integer(1));
			fail = true;
		} catch (AssertionError err) {
			// this one pass
		}
		if(fail){
			fail();
		}
		try {
			assertReflectionArrayEquals(new Integer(1),new Long(1));
			fail = true;
		} catch (AssertionError err) {
			// this one pass
		}
		if(fail){
			fail();
		}
		try {
			assertReflectionArrayEquals(new Long(1),new Long(1));
			fail = true;
		} catch (AssertionError err) {
			// this one pass
		}
		if(fail){
			fail();
		}
		try {
			assertReflectionArrayEquals(new Long(1),new Integer(1));
			fail = true;
		} catch (AssertionError err) {
			// this one pass
		}
		if(fail){
			fail();
		}
	}
	
	@Test
	public void assertReflectionArrayEquals_twoParamArray(){
		Integer[] ints1 = {1};
		Integer[] ints2 = {1};
		assertReflectionArrayEquals(ints1,ints2);
	}
	//针对数组元素的类型是否相同进行判断
	/**
	 * 左右两个array拥有相同的数组类型
	 */
	@Test
	public void assertReflectionArrayEquals_assertArrayOfSingleElementType(){
		Integer[] ints1 = {1,2,3,4,5};
		Integer[] ints2 = {1,2,3,4,5};
		assertReflectionArrayEquals(ints1,ints2);
	}
	
	/**
	 * array类型，左侧有的元素拥有不同类型
	 */
	@Test
	public void assertReflectionArrayEquals_assertLeftParamArrayOfDifferentElementType(){
		Number[] ints1 = {1,2,new Long(3),4,5};
		Number[] ints2 = {1,2,3,4,5};
		try {
			assertReflectionArrayEquals(ints1,ints2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	
	/**
	 * array类型，右侧有的元素拥有不同类型
	 */
	@Test
	public void assertReflectionArrayEquals_assertRightParamArrayOfDifferentElementType(){
		Number[] ints1 = {1,2,3,4,5};
		Number[] ints2 = {1,2,new Long(3),4,5};
		try {
			assertReflectionArrayEquals(ints1,ints2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	/**
	 * array左右两侧顺序不相同时的验证
	 */
	@Test
	public void assertReflectionArrayEquals_assertArrayDifferentSequence(){
		Number[] ints1 = {1,2,3,4,5};
		Number[] ints2 = {5,4,3,2,1};
		assertReflectionArrayEquals(ints1,ints2);
	}
	
	/**
	 * 左侧array中有重复的元素，右侧没有，两边array长度不等
	 */
	@Test
	public void assertReflectionArrayEquals_assertArrayLeftParamDuplicate(){
		Number[] ints1 = {1,2,3,3,4,5};
		Number[] ints2 = {1,2,3,4,5};
		try {
			assertReflectionArrayEquals(ints1,ints2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	/**
	 * 右侧array中有重复的元素，左侧没有，两边array长度不等
	 */
	@Test
	public void assertReflectionArrayEquals_assertArrayRightParamDuplicate(){
		Number[] ints1 = {1,2,3,4,5};
		Number[] ints2 = {1,2,3,3,4,5};
		try {
			assertReflectionArrayEquals(ints1,ints2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	/**
	 * 左侧array中有重复的元素，右侧也有，重复的元素相同，两边array长度不等
	 */
	@Test
	public void assertReflectionArrayEquals_两个数组都有重复元素且重复元素相同且长度不等(){
		Number[] ints1 = {1,2,3,3,3,4,5};
		Number[] ints2 = {1,2,3,3,4,5};
		try {
			assertReflectionArrayEquals(ints1,ints2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	/**
	 * 两个array中都有重复的元素，重复的元素相同，两边array长度相等
	 */
	@Test
	public void assertReflectionArrayEquals_两个数组都有重复元素且重复元素相同且长度相等(){
		Number[] ints1 = {1,2,2,3,4,5};
		Number[] ints2 = {1,2,2,3,4,5};
		assertReflectionArrayEquals(ints1,ints2);
	}
	/**
	 * 两个array中都有重复的元素，重复的元素不同，两边array长度不等
	 */
	@Test
	public void assertReflectionArrayEquals_两个数组都有重复元素且重复元素不同且长度不等(){
		Number[] ints1 = {1,2,2,3,3,4,5};
		Number[] ints2 = {1,2,2,3,4,5};
		try {
			assertReflectionArrayEquals(ints1,ints2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}
	/**
	 * 右侧array中有重复的元素，右侧也有，重复的元素不同，两边array长度相等
	 */
	@Test
	public void assertReflectionArrayEquals_两个数组都有重复元素且重复元素不同且长度相等(){
		Number[] ints1 = {1,2,3,3,4,5};
		Number[] ints2 = {1,2,2,3,4,5};
		try {
			assertReflectionArrayEquals(ints1,ints2);
		} catch (AssertionError err) {
			// pass
			return;
		}
		fail();
	}

	
	
	/**
	 * 元素的类型：map,list,collection,简单类型，bean，hibernate对象。hibernate关联对象的处理？
	 */
	
	/**
	 * 简单数据类型，Boolean.class, Byte.class, Character.class, Double.class, 
			Float.class, Integer.class, Long.class, Short.class,
			// objects
			String.class, BigDecimal.class, BigInteger.class, Class.class, 
			File.class, Date.class, java.sql.Date.class, java.sql.Time.class, 
			java.sql.Timestamp.class, URL.class,
			// enumeration
			Enum.class
	 */
	@Test
	public void assertReflectionArrayEquals_简单类型元素判断(){
		assertReflectionArrayEquals_test(Boolean.TRUE,Boolean.TRUE,Boolean.FALSE,Boolean.TRUE);
		assertReflectionArrayEquals_test(Byte.parseByte("33"),Byte.parseByte("21"),Byte.parseByte("21"),Byte.parseByte("55"));
		assertReflectionArrayEquals_test(Character.valueOf('a'),Character.valueOf('c'),Character.valueOf('e'),Character.valueOf('g'),Character.valueOf('i'),Character.valueOf('g'));
		assertReflectionArrayEquals_test(Double.valueOf(2.2),Double.valueOf(1.5),Double.valueOf(3.3),Double.valueOf(2.2));
		assertReflectionArrayEquals_test(Float.valueOf("2.2"),Float.valueOf("3.3"),Float.valueOf("2.2"),Float.valueOf("1.5"));
		assertReflectionArrayEquals_test(1,3,2,4,5,7,7,9);
		assertReflectionArrayEquals_test(1L,3L,2L,4L,5L,7L,7L,9L);
		assertReflectionArrayEquals_test(Short.parseShort("1"),Short.parseShort("3"),Short.parseShort("2"),Short.parseShort("4"),Short.parseShort("5"),Short.parseShort("7"),Short.parseShort("7"),Short.parseShort("9"));
		assertReflectionArrayEquals_test("123","234","567","123");
		assertReflectionArrayEquals_test(new BigDecimal(1.1),new BigDecimal(2.1),new BigDecimal(3.1),new BigDecimal(4.1),new BigDecimal(5.1));
		assertReflectionArrayEquals_test(new BigInteger("1"),new BigInteger("2"),new BigInteger("3"),new BigInteger("4"),new BigInteger("5"));
		assertReflectionArrayEquals_test(Integer.class,Long.class,Integer.class,TestUtils.class);
		assertReflectionArrayEquals_test(new File("/"),new File("/a/b"),new File("/"));
		assertReflectionArrayEquals_test(java.sql.Date.valueOf("2010-06-30"),java.sql.Date.valueOf("2010-06-30"),java.sql.Date.valueOf("2010-06-29"));
		assertReflectionArrayEquals_test(java.sql.Time.valueOf("21:00:00"),java.sql.Time.valueOf("21:00:11"),java.sql.Time.valueOf("21:00:00"));
		assertReflectionArrayEquals_test(java.sql.Timestamp.valueOf("2010-06-03 11:00:00"),java.sql.Timestamp.valueOf("2010-06-03 11:00:11"),java.sql.Timestamp.valueOf("2010-06-03 11:00:00"));
		try {
			assertReflectionArrayEquals_test(new URL("http://www.baidu.com"),new URL("http://www.google.com"),new URL("http://www.baidu.com"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		assertReflectionArrayEquals_test(MockType.A,MockType.B,MockType.A);
	}
	/**
	 * 验证 assertReflectionArrayEquals合法性。将参数进行Array和List的组合
	 */
	private void assertReflectionArrayEquals_test(Object... objectArray){
		if(objectArray == null||objectArray.length == 0){
			return ;
		}
		List list1 = new ArrayList();
		List list2 = new ArrayList();
		for(Object obj:objectArray){
			list1.add(obj);
			list2.add(0, obj);
		}
		
		Object[] oo1 = list1.toArray();
		Object[] oo2 = list2.toArray();
		assertReflectionArrayEquals(oo1,oo2);
	}
	
	@Test
	public void assertReflectionArrayEquals_Bean(){
		assertReflectionArrayEquals_test(child1,child1,child2,child2);
	}
	@Test
	public void assertReflectionArrayEquals_rightArrayHasDifferentTypeElement(){
		assertReflectionArrayEquals(null,null);
	}
	
	
	@Test
	public void reflectionEquals_arraysEmpty(){
		List list1 = new ArrayList();
		List list2 = new ArrayList();
		assertReflectionEquals(list1,list2);
	}
	
	@Test
	public void reflectionEquals_intElement(){
		List list1 = new ArrayList();
		list1.add(1);
		List list2 = new ArrayList();
		list2.add(1);
		assertReflectionEquals(list1,list2);
	}

	@Test
	public void reflectionEquals_ObjectElement(){
		List list1 = new ArrayList();
		list1.add(child1);
		List list2 = new ArrayList();
		list2.add(child1);
		assertReflectionEquals(list1,list2);
	}
	
	//TODO hibernateproxy或hibernatecollection类型的验证
	//TODO isHibernateProxy
	@Test
	@Ignore
	public void isHibernateProxy(){
		
	}
	
	//TODO dbunit部分的测试，暂时只完成的基本的smoke功能
	@Test
	public void clearTable_smoke() throws Exception{
		insertIntoDb();
		List<MockUser> list = jdbcTemplate.query("select * from test_user", new MockUserRowMapping(), new Object[0]);
		assertTrue("clearTable init data failed!",list.size()==1);
		TestUtils.clearTable(dataSource, "test_user");
		list = jdbcTemplate.query("select * from test_user", new MockUserRowMapping(), new Object[0]);
		assertTrue("clearTable failed!",list.size()==0);
		
	}
	@Test
	public void initDatabase_smoke() throws Exception
	{
		insertIntoDb();
		List<MockUser> list = jdbcTemplate.query("select * from test_user", new MockUserRowMapping(), new Object[0]);
		assertTrue("initDatabase_smoke init data failed!",list.size()==1);
		TestUtils.initDatabase(dataSource, "com/baidu/spark/TestUtilsTest.xml");
		list = jdbcTemplate.query("select * from test_user", new MockUserRowMapping(), new Object[0]);
		assertTrue("initDatabase_smoke failed!",list.size()==1);
		MockUser dbUser = list.get(0);
		assertEquals(1, dbUser.getUserId());
		assertEquals(2, dbUser.getGroupId());
		assertEquals("zhang", dbUser.getFirstName());
		assertEquals("jing", dbUser.getLastName());
	}
	
	@Test
	public void getObjectByPropertyFromArray_smoke(){
		MockUser user1  = new MockUser(1,1,"a","a");
		MockUser user2  = new MockUser(2,2,"b","a");
		MockUser user3  = new MockUser(3,3,"c","a");
		MockUser user4  = new MockUser(4,4,"a","a");
		MockUser user5  = new MockUser(5,5,"b","a");
		MockUser user6  = new MockUser(6,6,"c","a");
		List<MockUser> list = new ArrayList<MockUser>();
		list.add(user1);list.add(user2);list.add(user3);list.add(user4);list.add(user5);list.add(user6);
		assertTrue(TestUtils.getObjectByPropertyFromArray(list.toArray(), "userId", 1)==user1);
		assertTrue(TestUtils.getObjectByPropertyFromArray(list.toArray(), "firstName", "b")==user2);
		assertTrue(TestUtils.getObjectByPropertyFromArray(list.toArray(), "lastName", "a")==user1);
	}
	
	@Test
	public void getObjectByPropertyFromArray_paramArrayIsNull(){
		try {
			TestUtils.getObjectByPropertyFromArray(null, "123", 123);
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getObjectByPropertyFromArray_paramPropertyNameIsNull(){
		MockUser user1  = new MockUser(1,1,"a","a");
		List<MockUser> list = new ArrayList<MockUser>();
		list.add(user1);
		try {
			TestUtils.getObjectByPropertyFromArray(list.toArray(), null, 123);
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getObjectByPropertyFromArray_paramValueAndPropertyValueAreNull(){
		MockUser user1  = new MockUser(1,1,"a",null);
		List<MockUser> list = new ArrayList<MockUser>();
		list.add(user1);
		TestUtils.getObjectByPropertyFromArray(list.toArray(), "lastName", null);
	}
	
	@Test
	public void getObjectByPropertyFromArray_paramValueIsNull(){
		MockUser user1  = new MockUser(1,1,"a","b");
		List<MockUser> list = new ArrayList<MockUser>();
		list.add(user1);
		Object o = TestUtils.getObjectByPropertyFromArray(list.toArray(), "lastName", null);
		assertNull(o);
	}
	
	@Test
	public void getObjectByPropertyFromArray_propertyValueIsNull(){
		MockUser user1  = new MockUser(1,1,"a",null);
		List<MockUser> list = new ArrayList<MockUser>();
		list.add(user1);
		Object o = TestUtils.getObjectByPropertyFromArray(list.toArray(), "lastName", "abc");
		assertNull(o);
	}
	
	@Test
	public void getObjectByPropertyFromArray_propertyNameNotExists(){
		MockUser user1  = new MockUser(1,1,"a",null);
		List<MockUser> list = new ArrayList<MockUser>();
		list.add(user1);
		try {
			TestUtils.getObjectByPropertyFromArray(list.toArray(), "lastName123", "123");
			fail();
		} catch (RuntimeException e) {
			assertEquals(e.getCause().getClass(), NoSuchMethodException.class);
		}
	}
	
	@Test
	public void getObjectByPropertyFromArray_propertyNameAndValueTypeNotMatch(){
		MockUser user1  = new MockUser(1,1,"a",null);
		List<MockUser> list = new ArrayList<MockUser>();
		list.add(user1);
		Object o = TestUtils.getObjectByPropertyFromArray(list.toArray(), "lastName", 123);
		assertNull(o);
	}
	
	@Test
	@Ignore("逻辑有待讨论")
	public void isPropertyValuesInArray_smoke(){
		MockUser user1  = new MockUser(1,1,"a","a");
		MockUser user2  = new MockUser(2,2,"b","a");
		MockUser user3  = new MockUser(3,3,"c","a");
		MockUser user4  = new MockUser(4,4,"a","a");
		MockUser user5  = new MockUser(5,5,"b","a");
		MockUser user6  = new MockUser(6,6,"c","a");
		List<MockUser> list = new ArrayList<MockUser>();
		list.add(user1);list.add(user2);list.add(user3);list.add(user4);list.add(user5);list.add(user6);
		assertTrue(TestUtils.isPropertyValuesInArray(list.toArray(), "userId", new Integer[]{1,2,3,4,5,6}));
		// TODO 逻辑有待讨论
		assertTrue(TestUtils.isPropertyValuesInArray(list.toArray(), "firstName", new String[]{"a","b","c"}));
		assertTrue(TestUtils.isPropertyValuesInArray(list.toArray(), "lastName", new String[]{"a"}));
		assertTrue(TestUtils.isPropertyValuesInArray(list.toArray(), "userId", new Integer[]{1,2,3,4,5,6,7}));
		assertTrue(TestUtils.isPropertyValuesInArray(list.toArray(), "firstName", new String[]{"a","b","c","d"}));
		assertTrue(TestUtils.isPropertyValuesInArray(list.toArray(), "lastName", new String[]{"a","b"}));
	}
	
	
	
	@Test
	public void isPropertyValuesInArray_paramArrayIsNull(){
		try {
			TestUtils.isPropertyValuesInArray(null, "123", 123);
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void isPropertyValuesInArray_paramPropertyNameIsNull(){
		MockUser user1  = new MockUser(1,1,"a","a");
		List<MockUser> list = new ArrayList<MockUser>();
		list.add(user1);
		try {
			TestUtils.isPropertyValuesInArray(list.toArray(), null, 123);
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void isPropertyValuesInArray_paramValueAndPropertyValueAreNull(){
		MockUser user1  = new MockUser(1,1,"a",null);
		List<MockUser> list = new ArrayList<MockUser>();
		list.add(user1);
		TestUtils.isPropertyValuesInArray(list.toArray(), "lastName", null);
	}
	
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void isPropertyValuesInArray_paramValueIsNull(){
		MockUser user1  = new MockUser(1,1,"a","b");
		List<MockUser> list = new ArrayList<MockUser>();
		list.add(user1);
		TestUtils.isPropertyValuesInArray(list.toArray(), "lastName", null);
	}
	
	@Test
	public void isPropertyValuesInArray_propertyValueIsNull(){
		MockUser user1  = new MockUser(1,1,"a",null);
		List<MockUser> list = new ArrayList<MockUser>();
		list.add(user1);
		assertTrue(!TestUtils.isPropertyValuesInArray(list.toArray(), "lastName", "abc"));
	}
	
	@Test
	public void isPropertyValuesInArray_propertyNameNotExists(){
		MockUser user1  = new MockUser(1,1,"a",null);
		List<MockUser> list = new ArrayList<MockUser>();
		list.add(user1);
		try {
			TestUtils.isPropertyValuesInArray(list.toArray(), "lastName123", "123");
			fail();
		} catch (RuntimeException e) {
			assertEquals(e.getCause().getClass(), NoSuchMethodException.class);
		}
	}
	
	@Test
	public void isPropertyValuesInArray_propertyNameAndValueTypeNotMatch(){
		MockUser user1  = new MockUser(1,1,"a",null);
		List<MockUser> list = new ArrayList<MockUser>();
		list.add(user1);
		assertTrue(!TestUtils.isPropertyValuesInArray(list.toArray(), "lastName", 123));
	}
	@Test
	public void newCollection_smoke(){
		Collection<MockUser> list = TestUtils.newCollection(ArrayList.class, new MockUser());
		assertEquals(1,list.size());
	}
	
	private void insertIntoDb(){
		jdbcTemplate.update("insert into test_user values (1,1,'zhang','jing')");
	}
	
	public static class Mock {
		private int pPrimativeInt;
		private Integer pInteger;
		private String pString;
		private long pPrimativeLong;
		private Long pLong;
		private short pPrimativeShort;
		private Short pShort;
		private boolean pPrimativeBoolean;
		private Boolean pBoolean;
		private Date pDate;
		private MockType type;
		private Mock parent;
		private List<Mock> children;
		public int getPPrimativeInt() {
			return pPrimativeInt;
		}
		public void setPPrimativeInt(int primativeInt) {
			pPrimativeInt = primativeInt;
		}
		public Integer getPInteger() {
			return pInteger;
		}
		public void setPInteger(Integer integer) {
			pInteger = integer;
		}
		public String getPString() {
			return pString;
		}
		public void setPString(String string) {
			pString = string;
		}
		public long getPPrimativeLong() {
			return pPrimativeLong;
		}
		public void setPPrimativeLong(long primativeLong) {
			pPrimativeLong = primativeLong;
		}
		public Long getPLong() {
			return pLong;
		}
		public void setPLong(Long long1) {
			pLong = long1;
		}
		public short getPPrimativeShort() {
			return pPrimativeShort;
		}
		public void setPPrimativeShort(short primativeShort) {
			pPrimativeShort = primativeShort;
		}
		public Short getPShort() {
			return pShort;
		}
		public void setPShort(Short short1) {
			pShort = short1;
		}
		public boolean isPPrimativeBoolean() {
			return pPrimativeBoolean;
		}
		public void setPPrimativeBoolean(boolean primativeBoolean) {
			pPrimativeBoolean = primativeBoolean;
		}
		public Boolean getPBoolean() {
			return pBoolean;
		}
		public void setPBoolean(Boolean boolean1) {
			pBoolean = boolean1;
		}
		public Date getPDate() {
			return pDate;
		}
		public void setPDate(Date date) {
			pDate = date;
		}
		public MockType getType() {
			return type;
		}
		public void setType(MockType type) {
			this.type = type;
		}
		public Mock getParent() {
			return parent;
		}
		public void setParent(Mock parent) {
			this.parent = parent;
		}
		
		public void setChildren(List<Mock> children){
			this.children = children;
		}
		public List<Mock> getChildren(){
			return children;
		}
		
	}
	
	private static enum MockType {
		A, B
	}
	
}

class MockUserRowMapping implements RowMapper<MockUser>{
	@Override
	public MockUser mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		MockUser user = new MockUser();
		user.setUserId(rs.getInt("user_id"));
		user.setGroupId(rs.getInt("group_id"));
		user.setFirstName(rs.getString("first_name"));
		user.setLastName(rs.getString("last_name"));
		return user;
	}
}
