package com.baidu.spark.web.ajax;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baidu.spark.exception.ResponseStatusException;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.service.CardTypeService;
import com.baidu.spark.service.SpaceService;

/**
 * 卡片类型相关AJAX前端控制器.
 * 
 * @author GuoLin
 *
 */
@Controller
@RequestMapping("ajax/spaces/{prefixCode}/cardproperties")
public class CardPropertyAjaxController {
	
	private SpaceService spaceService;
	
	private CardTypeService cardTypeService;

	/**
	 * 对卡片属性进行重新排序.
	 * @param prefixCode 空间别名
	 * @param cardTypeId 卡片类型ID
	 * @param sortedIds 按顺序存放的卡片属性ID列表
	 */
	@RequestMapping(value = "/resort", method = RequestMethod.POST)
	@ResponseBody
	public void resortProperties(@PathVariable("prefixCode") String prefixCode, 
			@RequestBody Long[] sortedIds) {
		
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		// 排序
		Set<CardProperty> cardProperties = space.getCardProperties();
		Map<Long, CardProperty> idMap = new HashMap<Long, CardProperty>();
		for (CardProperty cardProperty : cardProperties) {
			idMap.put(cardProperty.getId(), cardProperty);
		}
		for (int i = 0; i < sortedIds.length; i++) {
			idMap.get(sortedIds[i]).setSort(i);
			cardTypeService.saveCardProperty(idMap.get(sortedIds[i]),idMap.get(sortedIds[i]).getCardTypes());
		}
	}
	
	@RequestMapping(value = "{cardPropertyId}/show", method = RequestMethod.PUT)
	@ResponseBody
	public void showProperty(@PathVariable("prefixCode") String prefixCode,
			@PathVariable("cardPropertyId") Long cardPropertyId) {
		
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		
		CardProperty cardProperty = cardTypeService.getCardProperty(cardPropertyId);
		if (cardProperty == null || cardProperty.getSpace()!=space) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		
		cardProperty.setHidden(false);
		cardTypeService.saveCardProperty(cardProperty,cardProperty.getCardTypes());
	}

	@RequestMapping(value = "{cardPropertyId}/hide", method = RequestMethod.PUT)
	@ResponseBody
	public void hideProperty(@PathVariable("prefixCode") String prefixCode, 
			@PathVariable("cardPropertyId") Long cardPropertyId) {
		
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		
		CardProperty cardProperty = cardTypeService.getCardProperty(cardPropertyId);
		if (cardProperty == null || cardProperty.getSpace()!=space) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		
		
		cardProperty.setHidden(true);
		cardTypeService.saveCardProperty(cardProperty,cardProperty.getCardTypes());
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
