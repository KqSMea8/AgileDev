package com.baidu.spark.web;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.baidu.spark.exception.UnhandledViewException;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.model.card.property.DummyProperty;
import com.baidu.spark.model.card.property.ListProperty;
import com.baidu.spark.model.card.property.TextProperty;
import com.baidu.spark.service.CardTypeService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.util.BeanMapperSingletonWrapper;

/**
 * 卡片类型的自定义字段前端控制器.
 * 
 * @author GuoLin
 * 
 */
@Controller
@RequestMapping("/spaces/{prefixCode}/cardproperties")
public class CardPropertyController {
	
	private SpaceService spaceService;
	
	private CardTypeService cardTypeService;
	
	@ModelAttribute("space")
	public Space prepareSpaceModel(@PathVariable String prefixCode){
		return getSpaceAndValidate(prefixCode);
	}
	
	@RequestMapping("/list")
	public String list(@PathVariable String prefixCode, ModelMap modelMap) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		modelMap.addAttribute("space", space);
		modelMap.addAttribute("cardProperties", space.getCardProperties());
		return "/cardproperties/list";
	}

	@RequestMapping(value="/new", method=RequestMethod.GET)
	public String blank(@PathVariable String prefixCode, ModelMap modelMap) {
//		CardType cardType = getCardTypeAndValidate(prefixCode, cardTypeId);
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		
		// 默认选中TextProperty
		CardProperty cardProperty = new TextProperty();
		cardProperty.setSpace(space);
		
		modelMap.addAttribute("space", space);
		modelMap.addAttribute("cardProperty", cardProperty);
		return "/cardproperties/new";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String create(@PathVariable String prefixCode, @Valid @ModelAttribute("cardProperty") DummyProperty cardProperty,BindingResult result,
			 @RequestParam(required=false)Long[] typeIds,ModelMap modelMap) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (result.hasErrors()) {
			modelMap.addAttribute("space", space);
			modelMap.addAttribute("typeIds", typeIds);
			modelMap.addAttribute("cardProperty", cardProperty);
			return "/cardproperties/new";
		}
		cardProperty.setSpace(space);
		
		if (null != typeIds){
			for (Long typeId:typeIds){
				CardType type = cardTypeService.getCardType(typeId);
				if (null != type){
					cardProperty.addCardType(type);
				}
			}
		}
		
		cardTypeService.saveCardProperty(cardProperty.generateActualProperty(),null);
		return "redirect:cardproperties/list";
	}
	
	@RequestMapping(value="/{cardPropertyId}/edit", method=RequestMethod.GET)
	public String edit(@PathVariable String prefixCode, @PathVariable Long cardPropertyId, ModelMap modelMap) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		CardProperty cardProperty = getCardPropertyAndValidate(cardPropertyId, space);
		modelMap.addAttribute("space", space);
		modelMap.addAttribute("cardProperty", cardProperty);
		return "cardproperties/edit";
	}
	
	@RequestMapping(value="/{cardPropertyId}", method=RequestMethod.PUT)
	public String update(@PathVariable String prefixCode, @PathVariable Long cardPropertyId, 
			@Valid @ModelAttribute("cardProperty") DummyProperty dummyProperty, BindingResult result,
			 @RequestParam(required=false)Long[] typeIds,ModelMap modelMap) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		CardProperty cardProperty = getCardPropertyAndValidate(cardPropertyId, space);
		CardProperty targetCardProperty = null;
		dummyProperty.setId(cardPropertyId);
		targetCardProperty = dummyProperty.generateActualProperty();
				
		if (result.hasErrors()) {
			modelMap.addAttribute("typeIds", typeIds);
			modelMap.addAttribute("space", space);
			return "/cardproperties/edit";
		}
		
		Collection<CardType> originCardTypes =cardProperty.getCardTypes();
		
		Mapper mapper = BeanMapperSingletonWrapper.getInstance();
		mapper.map(targetCardProperty, cardProperty,"cardProperty-controllerMapping");
		
		Set<CardType> types = new HashSet<CardType>();
		if (null != typeIds){
			for (Long typeId:typeIds){
				CardType type = cardTypeService.getCardType(typeId);
				if (null != type){
					types.add(type);
				}
			}
		}
		cardProperty.setCardTypes(types);
		cardTypeService.saveCardProperty(cardProperty,originCardTypes);
		return "redirect:list";
	}
	
	@RequestMapping(value="/{cardPropertyId}/listoption", method=RequestMethod.GET)
	public String editListValue(@PathVariable String prefixCode, @PathVariable Long cardPropertyId, ModelMap modelMap) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		CardProperty cardProperty = getCardPropertyAndValidate(cardPropertyId, space);
		if(!(cardProperty instanceof ListProperty)){
			throw new UnhandledViewException("cardproperty.listproperty.mismatch");
		}
		modelMap.addAttribute("space", space);
		modelMap.addAttribute("cardProperty", cardProperty);
		modelMap.addAttribute("listKey",(((ListProperty)cardProperty).getListKey()));
		modelMap.addAttribute("listValue",(((ListProperty)cardProperty).getListValue()));
		return "cardproperties/editlistoption";
	}
	
	@RequestMapping(value="/{cardPropertyId}/listoption", method=RequestMethod.PUT)
	public String editListValueSubmit(@PathVariable String prefixCode, @PathVariable Long cardPropertyId, 
			 @RequestParam(required=false) List<String> listKey,@RequestParam(required=false) List<String> listValue,
			 @ModelAttribute("cardProperty") DummyProperty dummyProperty,BindingResult result,ModelMap modelMap
			 ) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		CardProperty cardProperty = getCardPropertyAndValidate(cardPropertyId, space);
		dummyProperty.setName(cardProperty.getName());
		if(!(cardProperty instanceof ListProperty)){
			throw new UnhandledViewException("cardproperty.listproperty.mismatch");
		}else{
			if (listValue != null && listValue.size() > 0) {
				Set<String> valueSet = new HashSet<String>();
				for (String value : listValue) {
					if (!StringUtils.isNotBlank(value)) {
						result.addError(new ObjectError("cardPropertyInfo",new String[]{"required.cardproperty.list.notnullvalue"},null,null));
					}
					if (StringUtils.length(value)>30) {
						result.addError(new ObjectError("cardPropertyInfo",new String[]{"required.cardproperty.list.nametoolong"},new String[]{value},null));
					}
					if(valueSet.contains(value.trim())){
						result.addError(new ObjectError("cardPropertyInfo",new String[]{"invalid.cardproperty.list.duplicatevalue"},new String[]{value},null));
					}
					valueSet.add(value);
				}
			}
		}
		if (result.hasErrors()) {
			modelMap.addAttribute("space", space);
			modelMap.addAttribute("listKey",listKey);
			modelMap.addAttribute("listValue",listValue);
			return "/cardproperties/editlistoption";
		}
		//对list类型进行info字段的设置
		List<String> keys = cardTypeService.saveNewListOptionKey(space.getId(), listKey);
		((ListProperty)cardProperty).setInfoFromKeyAndValue(keys, listValue);
		cardTypeService.saveCardProperty(cardProperty,cardProperty.getCardTypes());
		return "redirect:/spaces/"+prefixCode+"/cardproperties/list";
	}
	
	@RequestMapping(value="/{cardPropertyId}/delete", method=RequestMethod.GET)
	public String deleteConfirm(@PathVariable String prefixCode,
			@PathVariable Long cardPropertyId, ModelMap modelMap) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		CardProperty cardProperty = getCardPropertyAndValidate(cardPropertyId, space);
		modelMap.addAttribute("sapce", space);
		modelMap.addAttribute("cardProperty", cardProperty);
		return "cardproperties/delete";
	}
	
	@RequestMapping(value="/{cardPropertyId}", method=RequestMethod.DELETE)
	public String delete(@PathVariable String prefixCode, 
			@PathVariable Long cardPropertyId) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		CardProperty cardProperty = getCardPropertyAndValidate(cardPropertyId, space);
		cardTypeService.deleteCardProperty(cardProperty);
		return "redirect:list";
	}
	
	private Space getSpaceAndValidate(String prefixCode) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new UnhandledViewException("space.validate.notFound", prefixCode);
		}
		return space;
	}
	
	private CardProperty getCardPropertyAndValidate(Long cardPropertyId, Space space) {
		CardProperty cardProperty = cardTypeService.getCardProperty(cardPropertyId);
		if (cardProperty == null) {
			throw new UnhandledViewException("cardproperty.validate.notFound", cardPropertyId);
		}
		if (cardProperty.getSpace() != space) {
			throw new UnhandledViewException("cardproperty.validate.mismatch", cardProperty.getName(), space.getName());
		}
		return cardProperty;
	}
	
	@Autowired
	public void setSpaceService(SpaceService spaceService) {
		this.spaceService = spaceService;
	}
	
	@Autowired
	public void setCardTypeService(CardTypeService cardTypeService){
		this.cardTypeService = cardTypeService;
	}
}
