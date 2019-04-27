package com.baidu.spark.util.unit;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.baidu.spark.util.MailSender;

/**
 * 邮件发送器测试用例.
 * 
 * @author GuoLin
 *
 */
public class MailSenderTest {

	private MailSender sender;
	
	@Before
	public void before() {
		sender = new MailSender();
		sender.setDebug(true);
		sender.setDebugReceiver("guolin@baidu.com");
		sender.setMaxReceiversOnce(45);
		sender.setSystemSender("spark@baidu.com");
		
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost("mail2-in.baidu.com");
		Properties props = new Properties();
		props.put("mail.smtp.auth", false);
		javaMailSender.setJavaMailProperties(props);
		
		sender.setMailSender(javaMailSender);
	}
	
	@Test
	public void smoke() {
		sender.send("Test subject", "Test content", "guolin@baidu.com", "chenhui@baidu.com");
	}
	
}
