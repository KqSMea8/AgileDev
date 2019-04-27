package com.baidu.spark.web.ajax;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.baidu.spark.dao.Pagination;
import com.baidu.spark.exception.IndexException;
import com.baidu.spark.exception.ResponseStatusException;
import com.baidu.spark.index.converter.CardIndexConverter;
import com.baidu.spark.model.QueryVO;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.model.card.property.CardPropertyValue;
import com.baidu.spark.model.card.property.ListProperty;
import com.baidu.spark.service.CardService;
import com.baidu.spark.service.CardTypeService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.util.MessageHolder;
import com.baidu.spark.util.WebUtils;
import com.baidu.spark.util.mapper.IncludePathCallback;
import com.baidu.spark.util.mapper.SparkMapper;
import com.baidu.spark.util.mapper.SparkMapperSingletonWrapper;

/**
 * 卡片AJAX前端控制器.
 * 
 * @author GuoLin
 * 
 */
@Controller
@RequestMapping("ajax/spaces/{prefixCode}/cards")
public class CardAjaxController {

	private static final String PARAM_SEPERATOR = "+";
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String[] CARD_INCLUDING_PATH = new String[] {
			"id", "title", "detail", "sequence", 
			"type.id", "type.localId", "type.name","type.recursive", "type.color", 
			"space.id", "space.prefixCode","space.name", "space.type", 
			"lastModifiedTime", "lastModifiedUser.id", "lastModifiedUser.username", "lastModifiedUser.name", 
			"createdTime","createdUser.id","createdUser.username", "createdUser.name", 
			"parent.id", "parent.createTime", "parent.title",
			"parent.detail",
			"parent.sequence",
			"parent.type.id",
			"parent.type.name",
			"parent.type.recursive",
			"childrenSize",
			"propertyValues.id", "propertyValues.value",
			"propertyValues.cardProperty.id",
			"propertyValues.cardProperty.localId",
			"propertyValues.cardProperty.name",
			"propertyValues.cardProperty.hidden",
			"propertyValues.cardProperty.info",
			"project.id","project.icafeProjectId","project.name",
			"attachments.id", "attachments.status"};

	private CardService cardService;
	private SpaceService spaceService;
	private CardTypeService cardTypeService;

	private SparkMapper mapper = SparkMapperSingletonWrapper.getInstance();

	/**
	 * 根据父卡片的编号获取子卡片列表.
	 * 
	 * @param prefixCode
	 *            空间标识
	 * @param sequence
	 *            编号
	 * @return 卡片列表
	 */
	@RequestMapping("/{cardId}")
	@ResponseBody
	public Card getCardById(@PathVariable Long cardId) {
		Card card = cardService.getCard(cardId);
		return mapper.clone(card, new IncludePathCallback(CARD_INCLUDING_PATH));
	}

	/**
	 * 根据父卡片的编号获取子卡片列表.
	 * 
	 * @param prefixCode
	 *            空间标识
	 * @param sequence
	 *            编号
	 * @return 卡片列表
	 */
	@RequestMapping("/{sequence}/children")
	@ResponseBody
	public List<Card> getAllCardsByParent(@PathVariable String prefixCode,
			@PathVariable Long sequence,@RequestParam(value = "q", required = false) String query) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		QueryVO vo = new QueryVO(StringUtils.split(query, PARAM_SEPERATOR),
				null);
		Card parent = getCard(prefixCode, sequence);
		if (parent == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		List<Card> cards;
		try {
			cards = cardService.getCardInHierarchy(vo, space.getId(), parent.getId());
			return mapper
			.clone(cards, new IncludePathCallback(CARD_INCLUDING_PATH));
		} catch (IndexException e) {
			logger.error("card query error",e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	/**
	 * 获取指定空间上的所有根卡片.
	 * 
	 * @param prefixCode
	 *            空间标识
	 * @return 卡片列表
	 */
	@RequestMapping("/root")
	@ResponseBody
	public List<Card> getAllRootCards(@PathVariable String prefixCode,
			@RequestParam(value = "q", required = false) String query) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		QueryVO vo = new QueryVO(StringUtils.split(query, PARAM_SEPERATOR),
				null);
		try{
			List<Card> cards = cardService.getCardInHierarchy(vo, space.getId(), null);
			return mapper
				.clone(cards, new IncludePathCallback(CARD_INCLUDING_PATH));
		}catch(IndexException e){
			logger.error("card query error",e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 根据URL的查询条件获取卡片列表数据
	 * 
	 * @param prefixCode
	 *            空间标识
	 * @param page
	 *            当前页数
	 * @param size
	 *            每页最大条目
	 * @param q
	 *            查询条件参数.如q=[name1][operation][value1]+[name2][operation][value2]
	 *            多个查询条件用+号分隔
	 * @param s
	 *            排序条件参数.如s=[name1][desc]+[name2][asc] 多个排序条件用+号分隔
	 * 
	 * @return 卡片分页对象
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Pagination<Card> getCardsByPagination(
			@PathVariable String prefixCode,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "size", required = false) Integer size,
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "s", required = false) String sort,
			HttpServletRequest request) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		Pagination<Card> pagination = new Pagination<Card>(size, page);
		QueryVO vo = new QueryVO(StringUtils.split(query, PARAM_SEPERATOR),
				StringUtils.split(sort, PARAM_SEPERATOR));
		try{
			cardService.queryByCardQueryVO(vo, space.getId(), pagination);
		}catch(IndexException e){
			logger.error("card query error",e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		pagination.setResults(mapper.clone(pagination.getResults(),
				new IncludePathCallback(CARD_INCLUDING_PATH)));
		return pagination;
	}

	/**
	 * 根据父卡片的编号获取子卡片列表.
	 * 
	 * @param prefixCode
	 *            空间标识
	 * @param sequence
	 *            编号
	 * @return 卡片列表
	 */
	@RequestMapping("/{sequence}/valid_parents")
	@ResponseBody
	public List<Card> getAllValidParents(@PathVariable String prefixCode,
			@PathVariable Long sequence) {
		Card parent = getCard(prefixCode, sequence);
		if (parent == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		List<Card> cards = cardService.getAllCardsByParent(parent);
		return mapper
				.clone(cards, new IncludePathCallback(CARD_INCLUDING_PATH));
	}
	
	/**
	 * 在currentSequence的子卡片中，按是否能够成为指定当前sequence卡片上级区分卡片列表.
	 * 
	 * 区分方式是：如果能，则Json对象中validForParent属性为true，否则为false
	 * 
	 * @param prefixCode
	 *            空间前缀
	 * @param currentSequence
	 *            作为"筛选条件"的卡片ID,即：想成为子节点的卡片ID
	 * @param parentSequence
	 *            筛选的父节点，只返回该节点的（一级）子卡片。
	 * @return 可以作为上级的卡片列表JSON对象
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/under/{parent_sequence}/valid_parents_for_card/{current_sequence}")
	public ModelAndView getAllValidParentCardsByCard(
			@PathVariable String prefixCode,
			@PathVariable Long current_sequence,
			@PathVariable Long parent_sequence) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		Card currentCard = cardService.getCardBySpaceAndSeq(space,
				current_sequence);
		Card baseCard = cardService
				.getCardBySpaceAndSeq(space, parent_sequence);
		List<Card> validCards = cardService
				.getCardsUnderBaseCardValidForCardParent(currentCard, baseCard);
		List<Card> allCard = cardService.getAllCardsByParent(baseCard);
		List<Card> invalidCards = (List<Card>) CollectionUtils.subtract(
				allCard, validCards);

		ModelAndView mav = new ModelAndView();
		mav.addObject("validCards", validCards);
		mav.addObject("invalidCards", invalidCards);
		mav.addObject("space", space);
		mav.setViewName("ajax/cards/cards_for_parent_select");

		return mav;
	}

	/**
	 * 在根节点中，按是否能够成为指定当前sequence卡片的上级区分卡片列表.
	 * 
	 * 区分方式是：如果能，则Json对象中validForParent属性为true，否则为false
	 * 
	 * @param prefixCode
	 *            空间前缀
	 * @param currentSequence
	 *            作为"筛选条件"的卡片ID,即：想成为子节点的卡片ID
	 * @param parentSequence
	 *            筛选的父节点，从根节点找。
	 * @return 可以作为上级的卡片列表JSON对象
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/under_root/valid_parents_for_card/{current_sequence}")
	public ModelAndView getAllRootValidParentCardsByCard(
			@PathVariable String prefixCode, @PathVariable Long current_sequence) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		Card currentCard = cardService.getCardBySpaceAndSeq(space,
				current_sequence);

		List<Card> validCards = cardService
				.getRootCardsValidForCardParent(currentCard);

		List<Card> allCard = cardService.getAllRootCards(space);
		List<Card> invalidCards = (List<Card>) CollectionUtils.subtract(
				allCard, validCards);

		ModelAndView mav = new ModelAndView();
		mav.addObject("validCards", validCards);
		mav.addObject("invalidCards", invalidCards);
		mav.addObject("space", space);
		mav.setViewName("ajax/cards/cards_for_parent_select");

		return mav;
	}

	/**
	 * 在currentSequence的子卡片中，按是否能够成为指定当前sequence卡片的上级区分卡片列表.
	 * 
	 * 区分方式是：如果能，则Json对象中validForParent属性为true，否则为false
	 * 
	 * @param prefixCode
	 *            空间前缀
	 * @param currentSequence
	 *            作为"筛选条件"的卡片ID,即：想成为子节点的卡片ID
	 * @param parentSequence
	 *            筛选的父节点，只返回该节点的（一级）子卡片。如果parentSequence等于-1，则表示从根节点找。
	 * @return 可以作为上级的卡片列表JSON对象
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/under/{parentCardSequence}/valid_parents_for_type/{currentCardTypeId}")
	public ModelAndView getAllValidParentCardsByCardType(
			@PathVariable String prefixCode,
			@PathVariable Long currentCardTypeId,
			@PathVariable Long parentCardSequence) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		CardType cardType = cardTypeService.getCardType(currentCardTypeId);

		Card parentCard = cardService.getCardBySpaceAndSeq(space,
				parentCardSequence);
		List<Card> validCards = cardService
				.getCardsUnderBaseCardValidForParentByCardType(cardType,
						parentCard);

		List<Card> allCard = cardService.getAllCardsByParent(parentCard);
		List<Card> invalidCards = (List<Card>) CollectionUtils.subtract(
				allCard, validCards);

		ModelAndView mav = new ModelAndView();
		mav.addObject("validCards", validCards);
		mav.addObject("invalidCards", invalidCards);
		mav.addObject("space", space);
		mav.setViewName("ajax/cards/cards_for_parent_select");

		return mav;
	}

	/**
	 * 在根节点中按是否能够成为成为指定当前sequence卡片的上级上级区分卡片列表.
	 * 
	 * 区分方式是：如果能，则Json对象中validForParent属性为true，否则为false
	 * 
	 * 
	 * @param prefixCode
	 *            空间前缀
	 * @param currentSequence
	 *            作为"筛选条件"的卡片ID,即：想成为子节点的卡片ID
	 * @param parentSequence
	 *            筛选的父节点，只返回该节点的（一级）子卡片。如果parentSequence等于-1，则表示从根节点找。
	 * @return 可以作为上级的卡片列表JSON对象
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/under_root/valid_parents_for_type/{currentCardTypeId}")
	public ModelAndView getAllValidRootParentCardsByCardType(
			@PathVariable String prefixCode,
			@PathVariable Long currentCardTypeId) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		CardType cardType = cardTypeService.getCardType(currentCardTypeId);

		List<Card> validCards = cardService
				.getRootCardsValidForParentByCardType(cardType);
		List<Card> allCard = cardService.getAllRootCards(space);
		List<Card> invalidCards = (List<Card>) CollectionUtils.subtract(
				allCard, validCards);

		ModelAndView mav = new ModelAndView();
		mav.addObject("validCards", validCards);
		mav.addObject("invalidCards", invalidCards);
		mav.addObject("space", space);
		mav.setViewName("ajax/cards/cards_for_parent_select");

		return mav;
	}

	@RequestMapping("/{id}/ancestorIds")
	@ResponseBody
	public List<Long> getAncestorCardIds(@PathVariable String prefixCode,@PathVariable Long id){
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		Card card = cardService.getCard(id);
		return cardService.getAncestorCardIds(card);
	}
	
	/**
	 * 根据URL的查询条件获取卡片列表数据
	 * 
	 * @param prefixCode
	 *            空间标识
	 * @param page
	 *            当前页数
	 * @param size
	 *            每页最大条目
	 * @param q
	 *            查询条件参数.如q=[name1][operation][value1]+[name2][operation][value2]
	 *            多个查询条件用+号分隔
	 * @param s
	 *            排序条件参数.如s=[name1][desc]+[name2][asc] 多个排序条件用+号分隔
	 * 
	 * @return 卡片分页对象
	 */
	@RequestMapping("/wall")
	@ResponseBody
	public List<Card> getCardsWall(
			@PathVariable String prefixCode,
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "s", required = false) String sort,
			HttpServletRequest request) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		
		QueryVO vo = new QueryVO(StringUtils.split(query, PARAM_SEPERATOR),
				StringUtils.split(sort, PARAM_SEPERATOR));
		List<Card> cardList = null;
		vo.addQuerySort(CardIndexConverter.CARD_LAST_MODIFIED_TIME, true);
		try{
			cardList = cardService.queryByCardQueryVO(vo, space.getId(), null);
		}catch(IndexException e){
			logger.error("card query error",e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return mapper
		.clone(cardList, new IncludePathCallback(CARD_INCLUDING_PATH));
	}
	
	@RequestMapping(value = "/batchUpdate", method = RequestMethod.POST)
	@ResponseBody
	public void batchUpdate(@PathVariable String prefixCode, 
			@RequestParam String ids, @RequestParam Map<String, String> data ) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		WebUtils.notFoundIfNull(space);
		
		List<Card> cards = cardService.getCardsByIdList(
				com.baidu.spark.util.StringUtils.splitAndParseLong(ids, ","));
		List<CardProperty> properties = cardService.getCommonProperties(cards);
		// 处理cpvList
		List<ObjectError> errors = new ArrayList<ObjectError>();
		@SuppressWarnings("unchecked")
		List<CardPropertyValue<?>> newCpvList = cardService.generateCardPropertyValues(data, properties, errors);
		
		if (!errors.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for(ObjectError error : errors){
				String message = MessageHolder.get(error.getCode(), error.getArguments());
				sb.append(message).append(";");
			}
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "card.batchUpdate.validation", sb.toString());
		}
		cardService.batchUpdateCards(space, cards, newCpvList);
	}
	
	@RequestMapping(value = "/batchDelete", method = RequestMethod.POST)
	@ResponseBody
	public void batchDelete(@PathVariable String prefixCode, @RequestParam Boolean cascade,
			@RequestParam String ids) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		WebUtils.notFoundIfNull(space);
		
		List<Card> cards = cardService.getCardsByIdList(
				com.baidu.spark.util.StringUtils.splitAndParseLong(ids, ","));
		for(Card card : cards){
			if(cascade){
				cardService.cascadeDelete(card);
			} else {
				cardService.deleteCard(card);
			}
		} 
	}
	@RequestMapping(value = "/{sequence}/changeProperty", method = RequestMethod.POST)
	@ResponseBody
	public void changeProperty(@PathVariable String prefixCode,
			@PathVariable Long sequence, @RequestParam Long propertyId,
			@RequestParam String propertyValue) {
		
		//验证卡片是否存在
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null ) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		Card card = cardService.getCardBySpaceAndSeq(space, sequence);
		if ( card == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		//验证卡片是否可编辑这个属性
		CardType cardType = card.getType();
		CardProperty property = cardTypeService.getCardProperty(space.getId(), propertyId);
		if(property == null || !cardType.containsProperty(property)){
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,"card.changeProperty.noThisProperty");
		}
		//验证卡片的属性值是否合法
		if( property instanceof ListProperty){
			List<String> listKey = ((ListProperty)property).getListKey();
			
			if(propertyValue.equals("0")){
				updateCardProperty(space,card, property, "");
				return;
			}
			
			if(CollectionUtils.isNotEmpty(listKey)){
				boolean found = false;
				for(String key:listKey){
					if(key.equals(propertyValue)){
						found = true;
						break;
					}
				}
				if(found){
					updateCardProperty(space,card, property, propertyValue);
					return;
				}
			}
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,"card.changeProperty.noThisPropertyValue");
		}else{
			//当前要求所有可拖拽的都是list类型的卡片
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,"card.changeProperty.thisPropertyCanNotBeChanged");
		}
	}
	
	private void updateCardProperty(Space space,Card card,CardProperty property,String value){
		CardPropertyValue<?> cpv = property.generateValue(value);
		List<CardPropertyValue<?>> cpvList = new ArrayList<CardPropertyValue<?>>();
		cpvList.add(cpv);
		cardService.updateCardProperties(space, card, cpvList);
	}
	
	/**
	 * @param prefixCode
	 * @param sequence
	 * @return
	 */
	private Card getCard(String prefixCode, Long sequence) {
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		WebUtils.notFoundIfNull(space);
		return cardService.getCardBySpaceAndSeq(space, sequence);
	}
	

	@Autowired
	public void setCardService(CardService cardService) {
		this.cardService = cardService;
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
