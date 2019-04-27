package com.baidu.spark.model.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.spark.exception.SparkRuntimeException;
import com.baidu.spark.model.QueryConditionVO;
import com.baidu.spark.model.QuerySortVO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext-test.xml"})
public class QueryVOTest {

	@Test
	public void testAnalyseQueryConditionVO(){
		String queryString="[title][equals][222]";
		QueryConditionVO conditionVo = new QueryConditionVO(queryString);
		assertEquals("title", conditionVo.getFieldName());
		assertEquals(QueryConditionVO.QueryOperationType.EQUALS, conditionVo.getOperationType());
		assertEquals("222", conditionVo.getValue());
	}
	
	@Test(expected=SparkRuntimeException.class)
	public void testAnalyseQueryConditionVO_fragmentNumMore(){
		String queryString="[title][equals][222][222]";
		QueryConditionVO conditionVo = new QueryConditionVO(queryString);
	}
	@Test(expected=SparkRuntimeException.class)
	public void testAnalyseQueryConditionVO_fragmentNumLess(){
		String queryString="[title][equals]";
		QueryConditionVO conditionVo = new QueryConditionVO(queryString);
	}
	@Test(expected=SparkRuntimeException.class)
	public void testAnalyseQueryConditionVO_fragmentOperationError(){
		String queryString="[title][equal][222]";
		QueryConditionVO conditionVo = new QueryConditionVO(queryString);
	}
	@Test
	public void testAnalyseSortConditionVO(){
		String queryString="[title][DESC]";
		QuerySortVO sortVo = new QuerySortVO(queryString);
		assertEquals("title", sortVo.getFieldName());
		assertTrue(sortVo.isDesc());
		queryString="[title][desc]";
		sortVo = new QuerySortVO(queryString);
		assertEquals("title", sortVo.getFieldName());
		assertTrue(sortVo.isDesc());
		
		queryString="[title]";
		sortVo = new QuerySortVO(queryString);
		assertEquals("title", sortVo.getFieldName());
		assertTrue(!sortVo.isDesc());
		
		queryString="[title][faint]";
		sortVo = new QuerySortVO(queryString);
		assertEquals("title", sortVo.getFieldName());
		assertTrue(!sortVo.isDesc());
	}
	
	@Test(expected=SparkRuntimeException.class)
	public void testAnalyseQuerySortVO_fragmentNumMore(){
		String queryString="[title][DESC][1123]";
		QuerySortVO sortVo = new QuerySortVO(queryString);
	}
	
}
