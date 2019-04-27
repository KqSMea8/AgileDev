package com.baidu.spark.web.ajax;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.baidu.spark.model.Attachment;
import com.baidu.spark.model.OpType;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.service.AttachmentService;
import com.baidu.spark.service.CardBasicService;
import com.baidu.spark.service.CardHistoryService;
import com.baidu.spark.service.NotificationService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.util.WebUtils;

/**
 * 附件管理前端Ajax控制器
 * 
 * @author shixiaolei
 * 
 */
@Controller
@RequestMapping("ajax/spaces/{prefixCode}/cards/{sequence}/attachments/")
public class AttachmentAjaxController {

	private AttachmentService attachmentService;

	private CardBasicService cardService;

	private SpaceService spaceService;
	
	private NotificationService notificationService;
	/** 卡片历史记录服务 */
	private CardHistoryService historyService;

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public String remove(@PathVariable String prefixCode, @PathVariable Long sequence, @PathVariable Long id) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		WebUtils.notFoundIfNull(space);
		Card card = cardService.getCardBySpaceAndSeq(space, sequence);
		WebUtils.notFoundIfNull(card);
		Attachment attach = attachmentService.getById(id);
		WebUtils.notFoundIfNull(attach);
		
		attachmentService.remove(attach);
		historyService.saveHistory(card, OpType.Delete_Attachment, attach
				.getUploadUser());
		return "success";
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getAll(@PathVariable String prefixCode, @PathVariable Long sequence) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		WebUtils.notFoundIfNull(space);
		Card card = cardService.getCardBySpaceAndSeq(space, sequence);
		WebUtils.notFoundIfNull(card);
		List<Attachment> attachments = attachmentService.getAttachments(card);

		ModelAndView mav = new ModelAndView("ajax/cards/attachment");
		mav.addObject("attachments", attachments);
		return mav;
	}
	
	@RequestMapping(value = "/{id}/notifications", method = RequestMethod.POST)
	@ResponseBody
	public void notifyAttachment(@PathVariable String prefixCode, @PathVariable Long sequence, 
			@PathVariable Long id, @RequestParam("customMessage") String notifyMessage, 
			@RequestParam("emails[]") String[] emails ) {
		
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		WebUtils.notFoundIfNull(space);
		Card card = cardService.getCardBySpaceAndSeq(space, sequence);
		WebUtils.notFoundIfNull(card);
		Attachment attach = attachmentService.getById(id);
		WebUtils.notFoundIfNull(attach);
		
		if (emails != null && emails.length != 0) {
			notificationService.send(attach, notifyMessage, emails);
		}
	}

	@Autowired
	public void setAttachmentService(AttachmentService attachmentService) {
		this.attachmentService = attachmentService;
	}

	@Autowired
	public void setCardService(CardBasicService cardService) {
		this.cardService = cardService;
	}

	@Autowired
	public void setSpaceService(SpaceService spaceService) {
		this.spaceService = spaceService;
	}

	@Autowired
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}
	
	@Autowired
	public void setHistoryService(CardHistoryService historyService) {
		this.historyService = historyService;
	}

}
