package com.baidu.spark.web.ajax;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baidu.spark.model.Discussion;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.service.CardService;
import com.baidu.spark.service.DiscussionService;
import com.baidu.spark.service.NotificationService;
import com.baidu.spark.util.WebUtils;

/**
 * 卡片AJAX前端控制器.
 * 
 * @author shixiaolei
 * 
 */
@Controller
@RequestMapping("ajax/spaces/{prefixCode}/cards/{cardId}/discussions")
public class DiscussionAjaxController {

	/** 讨论服务接口 */
	private DiscussionService service;
	/** 卡片服务接口 */
	private CardService cardService;
	/** 邮件服务接口 */
	private NotificationService notificationService;
	
	@RequestMapping
	@ResponseBody
	public List<Discussion> getAllDiscussionsInCard(
			@PathVariable("cardId") Long cardId) {
		Card card = cardService.getCard(cardId);
		WebUtils.notFoundIfNull(card);
		return service.getAllDiscussionsInCard(card);
	}

	@RequestMapping("/{id}")
	@ResponseBody
	public Discussion getById(@PathVariable("id") Long id,
			@PathVariable("cardId") Long cardId) {
		Card card = cardService.getCard(cardId);
		WebUtils.notFoundIfNull(card);
		return service.getById(id, card);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Discussion replyCard(@PathVariable("cardId") Long cardId,
			@RequestParam String msg) {
		Card card = cardService.getCard(cardId);
		WebUtils.notFoundIfNull(card);
		return service.replyCard(msg,  card);
	}

	@RequestMapping(value = "/{id}/replies", method = RequestMethod.POST)
	@ResponseBody
	public Discussion replyDiscussion(@PathVariable("id") Long id,
			@PathVariable("cardId") Long cardId, @RequestParam String msg) {
		Card card = cardService.getCard(cardId);
		WebUtils.notFoundIfNull(card);
		return service.replyDiscussion(msg, id, card);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	public Discussion modifyDiscussion(@PathVariable("id") Long id,
			@PathVariable("cardId") Long cardId, @RequestParam String msg) {
		Card card = cardService.getCard(cardId);
		WebUtils.notFoundIfNull(card);
		return service.updateDiscussion(msg, id, card);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public Discussion deleteDiscussion(@PathVariable("id") Long id,
			@PathVariable("cardId") Long cardId) {
		Card card = cardService.getCard(cardId);
		WebUtils.notFoundIfNull(card);
		return service.deleteDiscussion(id, card);
	}
	
	@RequestMapping(value = "/{id}/notifications", method = RequestMethod.POST)
	@ResponseBody
	public void notifyDiscussion(@PathVariable("prefixCode") String prefixCode, 
			@PathVariable("cardId") Long cardId, @PathVariable("id") Long id, 
			@RequestParam("customMessage") String notifyMessage, @RequestParam("emails[]") String[] emails) {
		
		Card card = cardService.getCard(cardId);
		WebUtils.notFoundIfNull(card);
		Discussion discussion = service.getById(id, card);
		WebUtils.notFoundIfNull(discussion);
		
		// 如果用户没有传入任何内容待发送对象，则直接跳过
		if (emails == null || emails.length == 0) {
			return;
		}
		
		// 发送邮件
		notificationService.send(discussion, notifyMessage, card, emails);
	}

	@Autowired
	public void setService(DiscussionService service) {
		this.service = service;
	}

	@Autowired
	public void setCardService(CardService cardService) {
		this.cardService = cardService;
	}

	@Autowired
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

}
