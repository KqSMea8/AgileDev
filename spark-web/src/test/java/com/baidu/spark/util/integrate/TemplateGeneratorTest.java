package com.baidu.spark.util.integrate;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.spark.util.TemplateGenerator;

/**
 * 模板生成器测试用例.
 * 
 * @author GuoLin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext-test.xml"})
public class TemplateGeneratorTest {

	private TemplateGenerator generator;
	
	@Before
	public void before() {
		generator = new TemplateGenerator();
		generator.setBasePath("/com/baidu/spark/util/unit");
	}
	
	@Test
	public void smoke() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("hostUrl", "am");
		String result = generator.generate(model, "TemplateGeneratorTest.ftl");
		System.out.println(result);
	}
	
}
