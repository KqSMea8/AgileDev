package com.baidu.spark.web;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.baidu.spark.exception.UnhandledViewException;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.service.CardService;
import com.baidu.spark.service.CardTypeService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.util.MessageHolder;

/**
 * 卡片类型前端控制器.
 * @author Adun
 * @author chenhui
 */
@Controller
@RequestMapping("/spaces/{prefixCode}/cardtypes")
public class CardTypeController {
	
	private SpaceService spaceService;
	private CardTypeService cardTypeService;
	private CardService cardService;
	
	
	/**
	 * 空间卡片类型列表
	 * @param prefixCode 空间标识
	 * @param response   页面请求
	 * @param modelMap   页面Model
	 * @return
	 */
	@RequestMapping("/list")
	public String list(@PathVariable String prefixCode, ModelMap modelMap) {
		Space space = getSpaceAndValidate(prefixCode);
		modelMap.addAttribute(space);
		modelMap.addAttribute("cardTypeList", cardTypeService.getAllCardTypes(space));
		return "/cardtypes/list";
	}

	/**
	 * 新建卡片类型
	 * @param prefixCode 空间标识
	 * @param response   页面请求
	 * @param modelMap   页面Model
	 * @return
	 */
	@RequestMapping(value="/new")
	public String create(@PathVariable String prefixCode, ModelMap modelMap){
		Space space = getSpaceAndValidate(prefixCode);
		CardType newType = new CardType(space);
		modelMap.addAttribute("space",space);
		modelMap.addAttribute("cardType", newType);
		modelMap.addAttribute("cardTypeList", cardTypeService.getAllCardTypes(space));
		return "/cardtypes/new";
	}
	
	
	/**
	 * 新建卡片类型提交
	 * @param cardType
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public String createSubmit(@Valid CardType cardType, BindingResult result,@RequestParam(required=false) Long[] propertyIds, ModelMap modelMap,@PathVariable String prefixCode){
		Space space = getSpaceAndValidate(prefixCode);
		cardType.setSpace(space);
		//validate if has existed the same name already 
		if (!StringUtils.isEmpty(cardType.getName()) && cardTypeService.checkConflicts(cardType)){
			result.rejectValue("name", "cardtype.validate.duplicateOfName");
		}
		if ( result.hasErrors() ){
			modelMap.addAttribute("space", space );
			modelMap.addAttribute("propertyIds", propertyIds);
			modelMap.addAttribute("cardTypeList", cardTypeService.getAllCardTypes(cardType.getSpace()));
			return "/cardtypes/new";
		}
		//handle the scenario when form submits null parent.id to 
		//avoid hibernate try to save a transient object
		if( cardType.getParent() != null && cardType.getParent().getId() == null ){
			cardType.setParent(null);
		}
		if (null != propertyIds){
			for (Long propertyId:propertyIds){
				CardProperty property = cardTypeService.getCardProperty(propertyId);
				if (null != property){
					cardType.addCardProperty(property);
				}
			}
		}
		cardTypeService.saveCardType(cardType);
		return "redirect:/spaces/" + prefixCode +"/cardtypes/list";
	}
	
	
	/**
	 * 编辑卡片类型
	 * @param id 编辑CardTypeId
	 * @param response 页面请求
	 * @param modelMap 页面Model
	 * @return
	 */
	@RequestMapping(value="/{cardTypeId}/edit", method = RequestMethod.GET)
	public String edit(@PathVariable String prefixCode, @PathVariable Long cardTypeId,ModelMap modelMap){
		CardType cardType = getCardTypeAndValidate(prefixCode, cardTypeId);

		modelMap.addAttribute("cardType", cardType);
		modelMap.addAttribute("cardTypeList", cardTypeService.getValidCardTypesAsParent(cardType));
		return "/cardtypes/edit";
	}
	
	/**
	 * 编辑卡片类型提交
	 * @param cardType
	 * @return
	 */
	@RequestMapping(value="/{cardTypeId}", method=RequestMethod.PUT)
	public String editSubmit(@PathVariable String prefixCode, @PathVariable Long cardTypeId, 
			@Valid CardType cardType,BindingResult result, @RequestParam(required=false) Long[] propertyIds, ModelMap modelMap){
		CardType dbCardType = getCardTypeAndValidate(prefixCode, cardTypeId);
		cardType.setSpace(dbCardType.getSpace());
		//validate if has existed the same name already 
		if (!StringUtils.isEmpty(cardType.getName()) && cardTypeService.checkConflicts(cardType)){
			result.rejectValue("name", "cardtype.validate.duplicateOfName");
		}
		if ( result.hasErrors() ){
			modelMap.addAttribute("propertyIds", propertyIds);
			modelMap.addAttribute("cardTypeList", cardTypeService.getAllCardTypes(cardType.getSpace()));
			return "/cardtypes/edit";
		}
		if( cardType.getParent() != null && cardType.getParent().getId() == null ){
			cardType.setParent(null);
		}
		
		if (null != propertyIds){
			for (Long propertyId:propertyIds){
				CardProperty property = cardTypeService.getCardProperty(propertyId);
				if (null != property){
					cardType.addCardProperty(property);
				}
			}
		}
		cardTypeService.updateCardType(cardType);
		return "redirect:/spaces/" + prefixCode + "/cardtypes/list" ;
	}
	
	/**
	 * 删除卡片类型确认
	 * @param id 删除确认的卡片类型id
	 * @param modelMap 页面Model
	 */
	@RequestMapping(value="{cardTypeId}/delete",method = RequestMethod.GET )
	public String confirmDelete(@PathVariable String prefixCode,
			@PathVariable Long cardTypeId, ModelMap modelMap){
		CardType cardType = getCardTypeAndValidate(prefixCode, cardTypeId);
		List<String> messages = new ArrayList<String>();
		messages.add(MessageHolder.get("cardtype.deleteconfirm.cards", cardService.getCountOfCardsByType(cardTypeId)));
		messages.add(MessageHolder.get("cardtype.deleteconfirm.childtypes", cardType.getAllChildrenTypes().size()));
		messages.add(MessageHolder.get("cardtype.deleteconfirm.childcards", cardService.getCountOfCardsByParentType(cardTypeId)));
		modelMap.addAttribute("cardType", cardType);
		modelMap.addAttribute("messages", messages);
		return "cardtypes/delete";
	}
	
	/**
	 * 删除卡片类型提交
	 * @param id
	 * @return
	 */
	@RequestMapping(value="{cardTypeId}", method=RequestMethod.DELETE)
	public String delete(@PathVariable String prefixCode,@PathVariable Long cardTypeId){
		CardType cardType = getCardTypeAndValidate(prefixCode, cardTypeId);
		if( cardType == null ){
			throw new UnhandledViewException("global.exception.noSuchCardType");
		}
		cardTypeService.deleteCardType(cardType);
		return "redirect:/spaces/" + prefixCode + "/cardtypes/list" ;
	}
	
	private Space getSpaceAndValidate(String prefixCode) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new UnhandledViewException("space.validate.notFound", prefixCode);
		}
		return space;
	}
	
	private CardType getCardTypeAndValidate(String prefixCode, Long cardTypeId) {
		Space space= getSpaceAndValidate(prefixCode);
		CardType cardType = cardTypeService.getCardType(cardTypeId);
		if (cardType == null) {
			throw new UnhandledViewException("cardtype.validate.notFound", cardTypeId);
		}
		if (cardType.getSpace() != space) {
			throw new UnhandledViewException("cardtype.validate.mismatch", cardType, space);
		}
		return cardType;
	}

	@ModelAttribute("space")
	public Space prepareSpaceModel(@PathVariable String prefixCode){
		return spaceService.getSpaceByPrefixCode(prefixCode);
	}
	
	@Autowired
	public void setSpaceService(SpaceService spaceService) {
		this.spaceService = spaceService;
	}
	
	@Autowired
	public void setCardTypeService(CardTypeService cardTypeService){
		this.cardTypeService = cardTypeService;
	}
	
	@Autowired
	public void setCardService(CardService cardService){
		this.cardService = cardService;
	}
}
