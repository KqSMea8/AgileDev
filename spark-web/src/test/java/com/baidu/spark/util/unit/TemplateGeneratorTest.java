package com.baidu.spark.util.unit;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.baidu.spark.util.TemplateGenerator;

/**
 * 模板生成器测试用例.
 * 
 * @author GuoLin
 *
 */
public class TemplateGeneratorTest {

	private TemplateGenerator generator;
	
	@Before
	public void before() {
		generator = new TemplateGenerator();
		generator.setBasePath("/email");
	}
	
	@Test
	@Ignore
	public void smoke() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("hostUrl", "am");
		String result = generator.generate(model, "notification.ftl");
		System.out.println(result);
	}
	
}
