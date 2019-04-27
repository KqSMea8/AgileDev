package com.baidu.spark.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

import com.baidu.spark.dao.Pagination;
import com.baidu.spark.exception.IndexException;
import com.baidu.spark.model.Project;
import com.baidu.spark.model.QueryVO;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.User;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.model.card.property.CardPropertyValue;
import com.baidu.spark.service.CardBasicService;
import com.baidu.spark.service.CardService;

/**
 * @author zhangjing_pe
 * 
 */
@Service
public class CardServiceImpl implements CardService {

	private CardBasicService cardService = null;

	@Override
	public Pagination<Card> getCardsBySpaceId(Long spaceId,
			Pagination<Card> page) {
		return cardService.getCardsBySpaceId(spaceId, page);
	}

	@Override
	public Card getCardBySpaceAndSeq(Space space, Long sequence) {
		return cardService.getCardBySpaceAndSeq(space, sequence);
	}

	@Override
	public List<Card> getRecentUpdateCards(User user, int count) {
		return cardService.getRecentUpdateCards(user, count);
	}

	@Override
	public void deleteCard(Card card) {
		cardService.deleteCard(card);
	}

	@Override
	public void saveCard(Card card) {
		cardService.saveCard(card);
	}
	@Override
	public Integer initCardTypeCardIndex(CardType cardType,boolean batch,boolean clear) {
		return cardService.initCardTypeCardIndex(cardType,batch,clear);
	}

	@Override
	public Integer initSpaceCardIndex(Long spaceId,boolean batch,boolean clear) {
		return cardService.initSpaceCardIndex(spaceId,batch,clear);
	}

	@Override
	public Integer initAllCardIndex(boolean batch,boolean clear) {
		return cardService.initAllCardIndex(batch,clear);
	}

	@Override
	public void updateCard(Card card) {
		cardService.updateCard(card);
	}
	


	@Override
	public Card getCard(Long id) {
		return cardService.getCard(id);
	}

	@Override
	public List<Card> getAllCardsByType(CardType cardType) {
		return cardService.getAllCardsByType(cardType);
	}
	@Override
	public Pagination<Card> getAllCardsByType(CardType cardType,Pagination<Card> pagination){
		return cardService.getAllCardsByType(cardType,pagination);
	}
	
	@Override
	public List<Card> getCardsByIdList(List<Long> ids) {
		return cardService.getCardsByIdList(ids);
	}

	@Override
	public Long getCountOfCardsByType(Long cardTypeId) {
		return cardService.getCountOfCardsByType(cardTypeId);
	}

	@Override
	public Long getCountOfCardsByParentType(Long parentTypeId) {
		return cardService.getCountOfCardsByParentType(parentTypeId);
	}

	@Override
	public Long getCountOfCardByParentType(Long parentTypeId, Long currentTypeId) {
		return cardService.getCountOfCardByParentType(parentTypeId, currentTypeId);
	}

	@Override
	public List<Card> getAllCardsByParent(Card parent) {
		return cardService.getAllCardsByParent(parent);
	}

	@Override
	public Pagination<Card> getAllCardsByParent(Card parent,
			Pagination<Card> pagination) {
		return cardService.getAllCardsByParent(parent, pagination);
	}

	@Override
	public List<Card> queryByCardQueryVO(final QueryVO queryVo, Long spaceId,
			Pagination<Card> cardPage) throws IndexException{
		return cardService.queryByCardQueryVO(queryVo,spaceId,cardPage);
	}

	@Override
	public List<Card> queryByCardQueryVO(final QueryVO queryVo,
			Pagination<Card> cardPage) throws IndexException{
		return cardService.queryByCardQueryVO(queryVo, cardPage);
	}
	
	@Override
	public List<Card> getCardInHierarchy(QueryVO queryVo,Long spaceId, Long parentId)
		throws IndexException{
		return cardService.getCardInHierarchy(queryVo,spaceId, parentId);
	}

	@Override
	public List<Card> getCardsUnderBaseCardValidForCardParent(Card currentCard,
			Card baseCard) {
		return cardService.getCardsUnderBaseCardValidForCardParent(currentCard, baseCard);
	}

	@Override
	public List<Card> getCardsUnderBaseCardValidForParentByCardType(
			CardType currentCardType, Card baseCard) {
		return cardService.getCardsUnderBaseCardValidForParentByCardType(currentCardType, baseCard);
	}

	@Override
	public List<Card> getRootCardsValidForCardParent(Card currentCard) {
		return cardService.getRootCardsValidForCardParent(currentCard);
	}

	@Override
	public List<Card> getRootCardsValidForParentByCardType(
			CardType currentCardType) {
		return cardService.getRootCardsValidForParentByCardType(currentCardType);
	}

	@Override
	public boolean isAssignableAsParent(Card cardToBeChanged, Card cardAsParent) {
		return cardService.isAssignableAsParent(cardToBeChanged, cardAsParent);
	}

	@Override
	public List<Card> getAllRootCards(Space space) {
		return cardService.getAllRootCards(space);
	}

	@Override
	public Pagination<Card> getAllRootCards(Space space,
			Pagination<Card> pagination) {
		return cardService.getAllRootCards(space, pagination);
	}
	
	@Override
	public List<Card> getCardsInProject(Project project) {
		return cardService.getCardsInProject(project);
	}
	
	@Override
	public List<Long> getAncestorCardIds(Card card) {
		return cardService.getAncestorCardIds(card);
	}
	
	@Override
	public List<CardProperty> getCommonProperties(Collection<Card> cards) {
		return cardService.getCommonProperties(cards);
	}

	@Override
	public Collection<CardPropertyValue<?>> getCardPropertyValueFromCard(
			Card card) {
		return cardService.getCardPropertyValueFromCard(card);
	}
	
	@Override
	public List<CardPropertyValue<?>> generateCardPropertyValues(
			Map<String, ?> parameterMap, Collection<CardProperty> properties,
			List<ObjectError> validationErrors) {
		return cardService.generateCardPropertyValues(parameterMap, properties, validationErrors);
	}

	@Override
	public void batchUpdateCards(Space space, Collection<Card> cards, List<CardPropertyValue<?>> newCpvList) {
		cardService.batchUpdateCards(space, cards, newCpvList);
	}
	
	@Override
	public void updateCardProperties(Space space,Card card,List<CardPropertyValue<?>> newCpvList){
		cardService.updateCardProperties(space, card, newCpvList);
	}

	@Override
	public void cascadeDelete(Card card) {
		cardService.cascadeDelete(card);
	}
	
	@Autowired
	public void setCardService(CardBasicService cardService) {
		this.cardService = cardService;
	}

}
