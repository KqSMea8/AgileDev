package com.baidu.spark.util.integrate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.spark.util.MailSender;

/**
 * 邮件发送集成测试用例.
 * 
 * @author GuoLin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext-test.xml"})
public class MailSenderTest {

	@Autowired
	private MailSender sender;

	@Test
	public void smoke() {
		sender.send("Test subject", "Test content", "guolin@baidu.com");
	}
}
