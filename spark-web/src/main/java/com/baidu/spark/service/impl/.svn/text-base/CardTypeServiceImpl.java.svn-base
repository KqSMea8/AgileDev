package com.baidu.spark.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.service.CardTypeBasicService;
import com.baidu.spark.service.CardTypeService;

/**
 * 卡片类型和属性服务类
 * @author chenhui
 *
 */
@Service
public class CardTypeServiceImpl implements CardTypeService {
	
	private CardTypeBasicService cardTypeService;
	
	@Override
	public CardType getCardType(Long cardTypeId){
		return cardTypeService.getCardType(cardTypeId);
	}
	
	@Override
	public CardType getCardType(Long spaceId,Long cardTypeLocalId){
		return cardTypeService.getCardType(spaceId, cardTypeLocalId);
	}
	
	@Override
	public List<CardType> getAllCardTypes(Space space) {
		return cardTypeService.getAllCardTypes(space);
	}
	
	@Override
	public boolean checkConflicts(CardType cardType){
		return cardTypeService.checkConflicts(cardType);
	}

	@Override
	public List<CardType> getValidCardTypesAsParent(CardType cardType) {
		return cardTypeService.getValidCardTypesAsParent(cardType);
	}
	
	@Override
	public void saveCardType(CardType cardType) {
		cardTypeService.saveCardType(cardType);
	}
	
	@Override
	public List<String> saveNewListOptionKey(Long spaceId,List<String> keys){
		return cardTypeService.saveNewListOptionKey(spaceId, keys);
	}
	
	@Override
	public void updateCardType(CardType cardType){
		cardTypeService.updateCardType(cardType);
	}
	
	
	@Override
	public void deleteCardType(CardType cardType){
		cardTypeService.deleteCardType(cardType);
	}
	
	@Override
	public void saveCardProperty(CardProperty cardProperty,Collection<CardType> originCardTypes){
		cardTypeService.saveCardProperty(cardProperty,originCardTypes);
	}
	
	@Override
	public CardProperty getCardProperty(Long id) {
		return cardTypeService.getCardProperty(id);
	}

	@Override
	public void deleteCardProperty(CardProperty cardProperty) {
		cardTypeService.deleteCardProperty(cardProperty);
	}
	
	@Override
	public CardProperty getCardProperty(Long spaceId,Long cardPropertyLocalId){
		return cardTypeService.getCardProperty(spaceId, cardPropertyLocalId);
	}

	@Autowired
	public void setCardTypeService(CardTypeBasicService cardTypeService) {
		this.cardTypeService = cardTypeService;
	}
	

}
