package com.baidu.spark.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.baidu.spark.exception.AttachmentException;
import com.baidu.spark.exception.UnhandledViewException;
import com.baidu.spark.model.Attachment;
import com.baidu.spark.model.OpType;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.history.CardHistory;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.model.card.property.CardPropertyValue;
import com.baidu.spark.service.AttachmentService;
import com.baidu.spark.service.CardHistoryService;
import com.baidu.spark.service.CardService;
import com.baidu.spark.service.CardTypeService;
import com.baidu.spark.service.NotificationService;
import com.baidu.spark.service.ProjectService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.util.MessageHolder;
import com.baidu.spark.util.SpringSecurityUtils;
import com.baidu.spark.util.WebUtils;

/**
 * 卡片前端控制器.
 * 
 * @author zhangjing_pe
 * 
 */
@Controller
@RequestMapping("spaces/{prefixCode}/cards")
public class CardController {

	private CardService cardService;

	private SpaceService spaceService;

	private CardTypeService cardTypeService;
	
	private CardHistoryService cardHistoryService;
	
	private ProjectService projectService;
	
	private NotificationService notificationService;
	
	private AttachmentService attachmentService;

	private static final Logger logger = LoggerFactory.getLogger(CardController.class);

	/**
	 * 增加对卡片基本属性的验证规则 TODO 未来增加对自定义字段的验证，可使用自定义validator的方式
	 * 
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setRequiredFields(new String[] { "title", "type.id",
						"space.id" });
	}

	/**
	 * 查看卡片信息
	 * 
	 * @param prefixCode
	 * @param sequence
	 * @return
	 */
	@RequestMapping("/{sequence}")
	public ModelAndView show(@PathVariable String prefixCode,
			@PathVariable Long sequence) {
		Card card = getCard(prefixCode, sequence);
		if (card == null) {
			throw new UnhandledViewException("space.validate.notFound", prefixCode);
		}
		if (card.getType() == null) {
			throw new UnhandledViewException("required.card.type.id");
		}
		ModelAndView mav = new ModelAndView();
		mav.addObject("cardPropertyValueList",
				cardService.getCardPropertyValueFromCard(card));
		mav.addObject("card", card);
		mav.setViewName("/cards/show");
		return mav;
	}
	
	/**
	 * 查看卡片历史信息
	 * 
	 * @param prefixCode
	 * @param sequence
	 * @return
	 */
	@RequestMapping("/{sequence}/history/{historyId}")
	public ModelAndView showHistory(@PathVariable String prefixCode, @PathVariable Long sequence, @PathVariable Long historyId) {
		Card card = getCard(prefixCode, sequence);
		if (card == null) {
			throw new UnhandledViewException("space.validate.notFound", prefixCode);
		}
		if (card.getType() == null) {
			throw new UnhandledViewException("required.card.type.id");
		}
		
		Card historyCard = null;
		CardHistory currentHistory = null;
		if (null != card.getHistoryList()){
			for (CardHistory history : card.getHistoryList()){
				if (history.getId().equals(historyId)){
					currentHistory = history;
					historyCard = cardHistoryService.deserializeHistory(history.getData());
					break;
				}
			}
		}
		if (null == historyCard){
			throw new UnhandledViewException("history.validate.notFound", card.getId(), historyId);
		}
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("cardPropertyValueList", cardService.getCardPropertyValueFromCard(card));
		mav.addObject("history", currentHistory);
		mav.addObject("historyCard", historyCard);
		mav.addObject("currentCard", card);
		mav.setViewName("/cards/showHistory");
		return mav;
	}

	/**
	 * 新建卡片跳转
	 * 
	 * @param card
	 * @param request
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView newCard(@PathVariable String prefixCode,
			Long cardTypeId, Long parentId) {
		CardType cardType = cardTypeService.getCardType(cardTypeId);
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new UnhandledViewException("global.exception.noSuchSpace");
		}
		if (cardType == null) {
			throw new UnhandledViewException("global.exception.noSuchCardType");
		}
		if (cardType.getSpace() == null
				|| !cardType.getSpace().getId().equals(space.getId())) {
			throw new UnhandledViewException(
					"global.exception.cardTypeNotInThisSpace");
		}
		ModelAndView mav = new ModelAndView();
		mav.addObject("cardPropertyValueList",
				getCardPropertyValueFromCardType(cardType));

		Card parent = new Card();
		if (parentId != null) {
			parent = cardService.getCard(parentId);
		}

		Card card = new Card();
		card.setType(cardType);
		card.setSpace(space);
		card.setParent(parent);
		mav.addObject("card", card);
		mav.setViewName("/cards/cardform");
		return mav;
	}

	/**
	 * 新建卡片提交
	 * 
	 * @param prefixCode
	 * @param card
	 * @param bindingResult
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String newCardSubmit(@PathVariable String prefixCode, 
			@RequestParam("notifyEmails") String notifyEmails, @RequestParam("notifyMessage") String notifyMessage, 
			@RequestParam("attachmentIds") String attachmentIds, @RequestParam Long addNext, 
			@ModelAttribute("card") Card card, 
			BindingResult bindingResult,HttpServletRequest request, ModelMap map) {
		
		// 校验space是否存在
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new UnhandledViewException("global.exception.noSuchSpace");
		}
		if (!space.getId().equals(card.getSpace().getId())) {
			throw new UnhandledViewException("global.exception.illegalArgumentExceptionName");
		}
		card.setSpace(space);
		
		CardType cardType = cardTypeService.getCardType(card.getType().getId());
		card.setType(cardType);
		// 处理cpvList
		Set<CardProperty> properties = cardType.getCardProperties();
		List<ObjectError> errors = new ArrayList<ObjectError>();
		@SuppressWarnings("unchecked")
		List<CardPropertyValue<?>> cpvList = cardService.generateCardPropertyValues(
				request.getParameterMap(), properties, errors);
		for(ObjectError error : errors){
			bindingResult.addError(error);
		}
		User user = SpringSecurityUtils.getCurrentUser();
		if (user == null) {
			bindingResult.addError(new ObjectError("loginuser",
					new String[] { "required.loginuser" }, null, null));
		}
		if (bindingResult.hasErrors()) {
			Card parent = null;
			if (card.getParent() != null && card.getParent().getId()!=null&&card.getParent().getId()>0) {
				parent = cardService.getCard(card.getParent().getId());
			}
			card.setParent(parent);
			map.put("cardPropertyValueList", cpvList);
			return "/cards/cardform";
		}
		// 验证通过，保存卡片信息
		card.setCreatedTime(new Date());
		card.setCreatedUser(user);
		// 由页面的checkbox来控制是否提交parent，避免parent为空对象
		if (card.getParent() != null && card.getParent().getId() == null ||card.getParent().getId() == -1) {
			card.setParent(null);
		}
		// 处理iCafe项目
		if (card.getProject() == null || card.getProject().getId() == null) {
			card.setProject(null);
		}else{
			card.setProject(projectService.get(card.getProject().getId()));
		}
		
		card.setPropertyValues(cpvList);
		cardService.saveCard(card);
		
		// 处理附件
		List<Attachment> attachments = new ArrayList<Attachment>();
		for(String sid : attachmentIds.split(",")){
			try{
				if(StringUtils.hasText(sid)){
					Attachment attach = attachmentService.getById(Long.parseLong(sid.trim()));
					attachmentService.saveToCard(attach, card);
					card.addAttachment(attach);
					attachments.add(attach);
				}
			}catch(AttachmentException e){
				logger.warn("fail to save attachment to card", e);
			}
		}
		cardService.updateCard(card);
		
		// 保存历史记录
		cardHistoryService.saveHistory(card, OpType.Add_Card, user);

		
		// 发送通知
		if (StringUtils.hasText(notifyEmails)) {
			String[] receivers = notifyEmails.split(";");
			CardHistory latestHistory = card.getHistoryList().get(0);
			notificationService.send(latestHistory, attachments, notifyMessage, receivers);
		}
		
		//info信息
		WebUtils.setInfoMessage(request, MessageHolder.get("info.success.savecard",new StringBuilder(prefixCode).append("-").append(card.getSequence()).toString()));
		if(addNext!=null&&addNext>0){
			return "redirect:cards/new?cardTypeId="+card.getType().getId()+((card.getParent()!=null&&card.getParent().getId()!=null&&card.getParent().getId()>0)?"&parentId="+card.getParent().getId():"");
		}
		return "redirect:cards/" + card.getSequence();

	}

	/**
	 * 编辑卡片
	 * 
	 * @param card
	 * @param request
	 */
	@RequestMapping(value = "/{sequence}/edit")
	public ModelAndView editCard(@PathVariable String prefixCode,
			@PathVariable Long sequence) {
		Card card = getCard(prefixCode, sequence);
		if (card == null) {
			throw new UnhandledViewException("global.exception.noSuchCard");
		}
		if (card.getType() == null) {
			throw new UnhandledViewException("required.card.type.id");
		}

		ModelAndView mav = new ModelAndView();
		mav.addObject("cardPropertyValueList",
				cardService.getCardPropertyValueFromCard(card));
		mav.addObject("card", card);
		if (card.getParent() != null) {
			mav.addObject("parent.id", card.getParent().getId());
		}
		mav.setViewName("/cards/cardform");
		return mav;
	}

	/**
	 * 更新卡片
	 * 
	 * @param card
	 * @param request
	 */
	@RequestMapping(value = { "/{sequence}" }, method = RequestMethod.PUT)
	public String editCardSubmit(@PathVariable String prefixCode,
			@RequestParam("notifyEmails") String notifyEmails, @RequestParam("notifyMessage") String notifyMessage,
			@RequestParam("attachmentIds") String attachmentIds, @ModelAttribute("card") Card card, 
			BindingResult bindingResult, HttpServletRequest request, ModelMap map) {
		// 校验space是否存在
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new UnhandledViewException("global.exception.noSuchSpace");
		}
		if (!space.getId().equals(card.getSpace().getId())) {
			throw new UnhandledViewException("global.exception.illegalArgumentExceptionName");
		}
		card.setSpace(space);
		
		CardType cardType = cardTypeService.getCardType(card.getType().getId());
		card.setType(cardType);
		if (card.getParent() == null || card.getParent().getId() == null
				|| card.getParent().getId() == -1) {
			card.setParent(null);
		} else {
			Card parent = cardService.getCard(card.getParent().getId());
			if (cardService.isAssignableAsParent(card, parent)) {
				card.setParent(parent);
			}
		}
		// 处理cpvList
		Set<CardProperty> properties = cardType.getCardProperties();
		
		List<ObjectError> errors = new ArrayList<ObjectError>();
		@SuppressWarnings("unchecked")
		List<CardPropertyValue<?>> cpvList = cardService.generateCardPropertyValues(
				request.getParameterMap(), properties, errors);
		for(ObjectError error : errors){
			bindingResult.addError(error);
		}
		
		User user = SpringSecurityUtils.getCurrentUser();
		if (user == null) {
			bindingResult.addError(new ObjectError("loginuser",
					new String[] { "required.loginuser" }, null, null));
		}
		if (bindingResult.hasErrors()) {
			map.putAll(bindingResult.getModel());
			map.put("cardPropertyValueList", cpvList);
			return "/cards/cardform";
		}
		
		// 处理iCafe项目
		if (card.getProject() == null || card.getProject().getId() == null) {
			card.setProject(null);
		}else{
			card.setProject(projectService.get(card.getProject().getId()));
		}
		
		// 验证通过，保存卡片信息
		card.setLastModifiedTime(new Date());
		card.setLastModifiedUser(user);
		card.setPropertyValues(cpvList);
		
		// 处理附件
		List<Attachment> attachments = new ArrayList<Attachment>();
		for(String sid : attachmentIds.split(",")){
			try{
				if(StringUtils.hasText(sid)){
					Attachment attach = attachmentService.getById(Long.parseLong(sid.trim()));
					attachmentService.saveToCard(attach, card);
					card.addAttachment(attach);
					attachments.add(attach);
				}
			}catch(AttachmentException e){
				logger.warn("fail to save attachment to card", e);
			}
		}
		
		cardService.updateCard(card);
		// 保存历史记录
		cardHistoryService.saveHistory(card, OpType.Edit_Card, user);

		// 发送通知
		if (StringUtils.hasText(notifyEmails)) {
			String[] receivers = notifyEmails.split(";");
			CardHistory latestHistory;
			if (card.getHistoryList() == null) {
				latestHistory = new CardHistory();
				latestHistory.setCard(card);
				latestHistory.setDiffData("{}");
			} 
			else {
				latestHistory = card.getHistoryList().get(0);
			}
			notificationService.send(latestHistory, attachments, notifyMessage, receivers );
		}
		
		return "redirect:" + card.getSequence();

	}

	/**
	 * 删除卡片
	 * 
	 * @param prefixCode
	 * @param sequence
	 * @return
	 */
	@RequestMapping(value = "/{sequence}", method = RequestMethod.DELETE)
	public String delete(@PathVariable String prefixCode, @PathVariable Long sequence, @RequestParam Boolean cascade) {
		Card card = getCard(prefixCode, sequence);
		WebUtils.unhandledViewIfNull(card, "global.exception.noSuchCard");
		if(cascade){
			cardService.cascadeDelete(card);
		}else {
			cardService.deleteCard(card);
		}
		return "redirect:list";
	}

	/**
	 * @param prefixCode
	 * @param sequence
	 * @return
	 */
	private Card getCard(String prefixCode, Long sequence) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new UnhandledViewException("global.exception.noSuchSpace");
		}
		Card card = cardService.getCardBySpaceAndSeq(space, sequence);
		return card;
	}

	/**
	 * 根据cardtype中的cp列表，获取无值的cpv list
	 * 
	 * @param cardType
	 * @return
	 */
	private Collection<CardPropertyValue<?>> getCardPropertyValueFromCardType(CardType cardType) {
		if (cardType != null && cardType.getCardProperties() != null) {
			List<CardPropertyValue<?>> list = new ArrayList<CardPropertyValue<?>>();
			for (CardProperty cp : cardType.getCardProperties()) {
				list.add(cp.generateValue(null));
			}
			return list;
		}
		return null;
	}

	/**
	 * 查看卡片的Roadmap
	 * @param prefixCode
	 * @return
	 */
	@RequestMapping("/roadmap")
	public ModelAndView roadmap(@PathVariable String prefixCode){
		ModelAndView mav = new ModelAndView();
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		mav.addObject("space", space);
		mav.setViewName("/spaces/projects");
		return mav;
	}
	
	@RequestMapping( value="/view", method = RequestMethod.GET)
	public String view(@PathVariable String prefixCode , HttpServletRequest request){
		String lastVisitedView = "/cards/list";
		Cookie[] cookies = request.getCookies();
		for( Cookie cookie : cookies){
			if( cookie.getName()!= null && cookie.getName().equals("last-visited-view")){
				try {
					lastVisitedView = "/cards" + URLDecoder.decode(cookie.getValue(),"UTF8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "redirect:/spaces/" + prefixCode + lastVisitedView;
	}
	
	@RequestMapping("/batchUpdate")
	public String showBatchUpdate(@PathVariable String prefixCode, @RequestParam String ids, ModelMap map) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new UnhandledViewException("space.validate.notFound", prefixCode);
		}
		List<Card> cards = cardService.getCardsByIdList(
				com.baidu.spark.util.StringUtils.splitAndParseLong(ids, ","));
		List<CardProperty> properties = cardService.getCommonProperties(cards);
		List<CardPropertyValue<?>> list = new ArrayList<CardPropertyValue<?>>();
		for(CardProperty property : properties){
			list.add(property.generateValue(null));
		}
		map.addAttribute("cardPropertyValueList", list);
		map.addAttribute("cards", cards);
		map.addAttribute("space", space);
		map.addAttribute("ids", ids);
		return "cards/batchUpdate";
	}

	
	@RequestMapping("/list")
	public String list() {
		return "cards/list";
	}
	
	@RequestMapping("/hierarchy")
	public String hierarchy() {
		return "/cards/hierarchy";
	}
	
	@RequestMapping("/wall")
	public String wall(){
		return "/cards/wall";
	}

	@ModelAttribute("space")
	public Space prepareSpaceModel(@PathVariable String prefixCode) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new UnhandledViewException("space.validate.notFound",
					prefixCode);
		}
		return space;
	}

	@Autowired
	public void setCardService(CardService cardService) {
		this.cardService = cardService;
	}

	@Autowired
	public void setCardTypeService(CardTypeService cardTypeService) {
		this.cardTypeService = cardTypeService;
	}

	@Autowired
	public void setSpaceService(SpaceService spaceService) {
		this.spaceService = spaceService;
	}

	@Autowired
	public void setCardHistoryService(CardHistoryService cardHistoryService) {
		this.cardHistoryService = cardHistoryService;
	}

	@Autowired
	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}

	@Autowired
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Autowired
	public void setAttachmentService(AttachmentService attachmentService) {
		this.attachmentService = attachmentService;
	}

}
