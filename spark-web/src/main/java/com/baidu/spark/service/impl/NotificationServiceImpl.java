package com.baidu.spark.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.baidu.spark.exception.SparkRuntimeException;
import com.baidu.spark.model.Attachment;
import com.baidu.spark.model.Discussion;
import com.baidu.spark.model.User;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.history.CardHistory;
import com.baidu.spark.model.card.history.CardHistoryDiffBean;
import com.baidu.spark.service.CardBasicService;
import com.baidu.spark.service.NotificationService;
import com.baidu.spark.util.MailSender;
import com.baidu.spark.util.MessageHolder;
import com.baidu.spark.util.SpringSecurityUtils;
import com.baidu.spark.util.TemplateGenerator;

/**
 * 通知服务接口实现类.
 * 
 * @author GuoLin
 *
 */
@Service
public class NotificationServiceImpl implements NotificationService {
	
	private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
	
	/** 卡片变更通知邮件模板文件. */
	private static final String TEMPLATE_CARD = "notification-card.ftl";

	/** 讨论通知邮件模板文件. */
	private static final String TEMPLATE_DISCUSSION = "notification-discussion.ftl";
	
	/** 附件通知邮件模板文件. */
	private static final String TEMPLATE_ATTACHMENT = "notification-attachment.ftl";
	
	/** 人员同步错误信息通知邮件模板文件. */
	private static final String TEMPLATE_USER_SYNCHRONIZE = "notification-user-synchronization.ftl";
	
	/** 模板生成器. */
	private TemplateGenerator generator;
	
	/** 邮件发送器. */
	private MailSender mailSender;
	
	/** 卡片服务. */
	private CardBasicService cardService;
	
	/** 系统管理员收件人. */
	private String systemAdminEmail;
	
	/** 系统名称. */
	private String systemName;
	
	/** 系统URL. */
	private String systemURL;

	@Override
	public void send(CardHistory history, List<Attachment> attachments , String notifyMessage, String... receivers) {
		Assert.notNull(history);
		Assert.notNull(history.getCard());
		Assert.notEmpty(receivers);
		
		// 获取当前用户
		User user = SpringSecurityUtils.getCurrentUser();
		if (user == null) {
			throw new SparkRuntimeException("global.exception.unauthenticate");
		}

		// 获取卡片
		Card card = history.getCard();
		
		// 注入
		Map<String, Object> model = new HashMap<String, Object>();
		CardHistoryDiffBean diff = history.getDiffBean();
		model.put("diff", diff);
		model.put("user", user);
		model.put("card", card);
		if(card.getParent()!= null){
			Long id = card.getParent().getId();
			Card parent = cardService.getCard(id);
			model.put("parentSequence", parent.getSequence());
			model.put("parentName", parent.getTitle());
		}
		model.put("cardPropertyValueList",
				cardService.getCardPropertyValueFromCard(card));
		model.put( "notifyMessage", notifyMessage );
		model.put( "attachments", attachments );
		
		// 生成邮件内容
		String content = generator.generate(model, TEMPLATE_CARD);
		String subject = MessageHolder.get("notification.mail.template.update-card.subject", user.getName(), card.getTitle());
		if( diff.getIsNew()){
			subject = MessageHolder.get("notification.mail.template.add-card.subject", user.getName(), card.getTitle());
		}
		
		// 发送邮件
		try {
			mailSender.send(subject, content, user.getEmail(), receivers);
		} catch (MailSendException ex) {
			logger.error("", ex);
		}
	}

	@Override
	public void send(Discussion discussion, String notifyMessage, Card card, String... receivers) {
		Assert.notNull(discussion);
		Assert.notNull(card);
		Assert.notEmpty(receivers);
		
		// 获取当前用户
		User user = SpringSecurityUtils.getCurrentUser();
		if (user == null) {
			throw new SparkRuntimeException("global.exception.unauthenticate");
		}

		// 注入
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("discussion", discussion);
		model.put("user", user);
		model.put("card", card);
		model.put( "notifyMessage", notifyMessage );
		
		// 生成邮件内容
		String content = generator.generate(model, TEMPLATE_DISCUSSION);
		String subject = MessageHolder.get("notification.mail.template.discussion.subject", user.getName(), card.getTitle());
		
		// 发送邮件
		try {
			mailSender.send(subject, content, user.getEmail(), receivers);
		} catch (MailSendException ex) {
			logger.error("", ex);
		}
	}
	
	@Override
	public void send(Attachment attachment, String notifyMessage, String... receivers) {
		Assert.notNull(attachment);
		Assert.notNull(attachment.getOriginalName());
		Assert.notEmpty(receivers);
		
		// 获取当前用户
		User user = SpringSecurityUtils.getCurrentUser();
		if (user == null) {
			throw new SparkRuntimeException("global.exception.unauthenticate");
		}

		// 获取卡片
		Card card = attachment.getCard();
		if (card == null) {
			throw new SparkRuntimeException("global.exception.noCardOnAttachment");
		}
		
		// 注入
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("attachment", attachment);
		model.put("user", user);
		model.put("card", card);
		if(attachment.getOldAttachment()!=null){
			model.put("isReplace", true);
			model.put("oldAttachment", attachment.getOldAttachment());
		}else{
			model.put("isReplace", false);
		}
		model.put("notifyMessage", notifyMessage);
		
		// 生成邮件内容
		String content = generator.generate(model, TEMPLATE_ATTACHMENT);
		String subject = MessageHolder.get("notification.mail.template.attachment.subject", user.getName(), card.getTitle());
		
		// 发送邮件
		try {
			mailSender.send(subject, content, user.getEmail(), receivers);
		} catch (MailSendException ex) {
			logger.error("", ex);
		}
		
	}

	@Override
	public void sendUserSynchronizeErrors(List<String> infos,
			String... receivers) {
		if(infos == null || infos.size()==0){
			return;
		}
		if(receivers == null || receivers.length ==0){
			receivers = new String[]{systemAdminEmail};
		}
		// 注入
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("infos", infos);
		model.put("systemName", systemName);
		model.put("systemURL", systemURL);
		
		// 生成邮件内容
		String content = generator.generate(model, TEMPLATE_USER_SYNCHRONIZE);
		String subject = MessageHolder.get("notification.mail.template.user-synchronize.subject");
		
		// 发送邮件
		try {
			mailSender.send(subject, content, receivers);
		} catch (MailSendException ex) {
			logger.error("", ex);
		}
	}
	
	@Autowired
	public void setGenerator(TemplateGenerator generator) {
		this.generator = generator;
	}

	@Autowired
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Autowired
	public void setCardService(CardBasicService cardService) {
		this.cardService = cardService;
	}

	@Value("#{properties['mail.system.sender']}")
	public void setSystemEmail(String systemEmail) {
		this.systemAdminEmail = systemEmail;
	}

	@Value("#{properties['system.name']}")
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	@Value("#{properties['host.server.url.prefix']}")
	public void setSystemURL(String systemURL) {
		this.systemURL = systemURL;
	}
	
}
