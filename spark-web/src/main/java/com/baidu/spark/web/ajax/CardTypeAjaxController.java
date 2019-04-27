package com.baidu.spark.web.ajax;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.service.CardTypeService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.util.BeanMapperSingletonWrapper;

/**
 * 卡片类型相关AJAX前端控制器.
 * 
 * @author GuoLin
 *
 */
@Controller
@RequestMapping("ajax/spaces/{prefixCode}/cardtypes")
public class CardTypeAjaxController {
	
	private SpaceService spaceService;
	
	private CardTypeService cardTypeService;

	@RequestMapping(value = "{cardTypeId}/info", method = RequestMethod.POST)
	@ResponseBody
	public CardType getCardType(@PathVariable("cardTypeId") Long cardTypeId) {
		CardType cardType = cardTypeService.getCardType(cardTypeId);
		CardType handledCardType = new CardType();
		BeanMapperSingletonWrapper.getInstance().map(cardType, handledCardType, BeanMapperSingletonWrapper.GET_AVAILABLE_FIELD_4_SEARCH_MAP_ID);
		return handledCardType;
	}
	
	/**
	 * 编辑卡片类型提交
	 * @param cardType
	 * @return
	 */
	@RequestMapping(value="/{cardTypeId}/color/{color}", method=RequestMethod.PUT)
	@ResponseBody
	public String editColor(@PathVariable String prefixCode, @PathVariable Long cardTypeId, @PathVariable String color ){
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		CardType dbCardType = cardTypeService.getCardType(space.getId(), cardTypeId);
		dbCardType.setColor(color);
		cardTypeService.updateCardType(dbCardType);
		return "success" ;
	}
	
	@Autowired
	public void setSpaceService(SpaceService spaceService) {
		this.spaceService = spaceService;
	}

	@Autowired
	public void setCardTypeService(CardTypeService cardTypeService) {
		this.cardTypeService = cardTypeService;
	}
	
}
