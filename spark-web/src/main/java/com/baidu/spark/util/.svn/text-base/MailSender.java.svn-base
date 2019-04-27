package com.baidu.spark.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;

/**
 * 系统邮件发送者，负责向系统管理员账号发送邮件.
 * 
 * @author GuoLin
 */
public class MailSender {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/** 邮件发送对象. */
	private JavaMailSender mailSender;

	/** 系统发件人. */
	private String systemSender;

	/** 调试模式开关. */
	private boolean debug = false;

	/** 调试模式下的收件人. */
	private String debugReceiver;
	
	/** 一次发送最多可以接受的收件人数量，默认为50. */
	private int maxReceiversOnce = 50;

	/** 邮件编码，默认为UTF-8. */
	private String encode = "UTF-8";

	/**
	 * 使用系统发件人发送邮件.
	 * @param subject 邮件标题
	 * @param content 邮件正文
	 * @param receivers 收件人
	 */
	public void send(String subject, String content, String... receivers) {
		send(subject, content, null, receivers);
	}
	
	/**
	 * 发送邮件.
	 * <p>
	 * 如果收件人数量大于一次最大可发送数量，则自动拆分为多组发送.
	 * </p>
	 * @param subject 邮件标题
	 * @param content 邮件正文
	 * @param sender 发件人
	 * @param receivers 收件人
	 */
	public void send(String subject, String content, String sender, String[] receivers) {
		this.send(subject, content, receivers, sender, null, null, null, null);
	}

	/**
	 * 发送邮件.
	 * <p>
	 * 如果收件人数量大于一次最大可发送数量，则自动拆分为多组发送.
	 * </p>
	 * @param subject 邮件标题
	 * @param content 邮件正文
	 * @param receivers 收件人
	 * @param sender 发件人
	 * @param cc 抄送人
	 * @param bcc 暗送人
	 * @param attachmentFilename 附件名称
	 * @param attach 附件文件
	 */
	public void send(String subject, String content, String[] receivers, String sender, 
			String[] cc, String[] bcc, String attachmentFilename, File attach) {

		List<String> group = new ArrayList<String>(maxReceiversOnce);
		for (int i = 0; i < receivers.length; i++) {
			if (i % maxReceiversOnce == 0 && !group.isEmpty()) {
				sendOnce(subject, content, group.toArray(new String[group.size()]), sender, cc, bcc, attachmentFilename, attach);
				group.clear();
			}
			group.add(receivers[i]);
		}
		
		if (!group.isEmpty()) {
			sendOnce(subject, content, group.toArray(new String[group.size()]), sender, cc, bcc, attachmentFilename, attach);
		}
	}
	
	/**
	 * 发送一次邮件.
	 * @param subject 邮件标题
	 * @param content 邮件正文
	 * @param receivers 收件人
	 * @param sender 发件人
	 * @param cc 抄送人
	 * @param bcc 暗送人
	 * @param attachmentFilename 附件名称
	 * @param attach 附件文件
	 */
	private void sendOnce(String subject, String content, String[] receivers, String sender, 
			String[] cc, String[] bcc, String attachmentFilename, File attach) {
		
		Assert.hasText(subject);
		Assert.notNull(content);
		Assert.notEmpty(receivers);
		
		// 检测在调试模式下是否允许发送邮件
		if (debug && StringUtils.isBlank(debugReceiver)) {
			logger.warn("Mail [ SUBJECT: {}, RECEIVER(S): {} ] did not send because [debugReceiver] not set.", subject, Arrays.toString(receivers));
			return;
		}
		
		// 创建MIME消息准备发送邮件
		MimeMessage message = mailSender.createMimeMessage();
		
		try {

			MimeMessageHelper helper = new MimeMessageHelper(message, true, encode);
	
			// 调试模式
			if (debug) {
				content = new StringBuilder(content)
					.append("<br /> Receivers：").append(Arrays.toString(receivers))
					.append("<br /> CC: ").append(cc)
					.append("<br /> BCC: ").append(bcc).toString();
				subject = "Debug mail：" + subject;
				helper.setTo(debugReceiver);
			}
			// 非调试模式
			else {
				if (!ArrayUtils.isEmpty(bcc)) {
					helper.setBcc(bcc);
				}
				if (!ArrayUtils.isEmpty(cc)) {
					helper.setCc(cc);
				}
				helper.setTo(receivers);
			}
			
			helper.setFrom(StringUtils.isBlank(sender) ? systemSender : sender);
			helper.setSubject(subject);
			helper.setText(content, true);
			
			// 设置附件
			if (attachmentFilename != null && attach != null && attach.exists()) {
				try {
					attachmentFilename = MimeUtility.encodeText(attachmentFilename);
				} catch (UnsupportedEncodingException ex) {
					throw new RuntimeException(ex);
				}
				helper.addAttachment(attachmentFilename, attach);
			}
			
		} catch (MessagingException ex) {
			logger.error("Mail send fail.", ex);
			throw new RuntimeException(ex);
		}
		
		logger.info("Sending mail [ SUBJECT: {} ] to [ RECEIVER(S): {} ]...", subject, Arrays.toString(receivers));
		mailSender.send(message);
	}

	@Required
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	@Required
	public void setSystemSender(String systemSender) {
		this.systemSender = systemSender;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setDebugReceiver(String debugReceiver) {
		this.debugReceiver = debugReceiver;
	}

	public void setMaxReceiversOnce(int maxReceiversOnce) {
		this.maxReceiversOnce = maxReceiversOnce;
	}
	
}
