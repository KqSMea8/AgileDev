package com.baidu.spark.index.engine.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.baidu.spark.dao.Pagination;
import com.baidu.spark.exception.IndexException;
import com.baidu.spark.index.converter.CardIndexConverter;
import com.baidu.spark.index.converter.IndexConverter;
import com.baidu.spark.index.engine.CardIndexEngine;
import com.baidu.spark.model.QueryConditionVO;
import com.baidu.spark.model.QueryVO;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.service.CardBasicService;
import com.baidu.spark.util.ListUtils;

/**
 * 卡片索引服务
 * 
 * @author zhangjing_pe
 * 
 */
@Service
public class CardIndexEngineImpl extends BaseIndexSupport<Card> implements
		CardIndexEngine {
	
	
	private CardBasicService cardService;

	@Autowired
	@Override
	public void setConverter(
			@Qualifier("cardIndexConverter") IndexConverter<Card> converter) {
		super.setConverter(converter);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Card> queryByCardQueryVO(final QueryVO queryVo,
			Pagination<Card> cardPage) throws IndexException {
		Assert.notNull(queryVo);
		List<Card> ret = null;
		Query query = getConverter().getQueryFromVo(queryVo);
		Sort sort = null;
		List<SortField> sortList = getConverter().getSortFromVo(queryVo);
		if (!ListUtils.isEmpty(sortList)) {
			sort = new Sort();
			sort.setSort(sortList.toArray(new SortField[] {}));
		}
		ret = findIndexByQuery(null, sort, query, cardPage);
		return ret;
	}

	@Override
	public List<Card> hierarchyQuery(final QueryVO queryVo, Long parentId)
			throws IndexException {
		//查询思路，根据查询条件筛选卡片。然后根据卡片的上级信息（ancestor：“-1-2-3-4-5-”）,获取出指定上级对应的卡片id，及卡片id对应的下级个数
		//如，查询parentId为3,若有一条查询结果为“-1-2-3-4-5-”，则4是一个实际的查询结果，而5则需要记录下来来计算4的子卡片个数
		//lucene索引中的ancestor中并不包括卡片自己的id。为了处理方便，会把卡片自身id拼在path串中
		Assert.notNull(queryVo);
		Query query = getConverter().getQueryFromVo(queryVo);
		List<Document> documentList = findDocumentByQuery(null, null, query);
		//处理查询结果，分析所属的下级卡片
		Set<Long> children = new HashSet<Long>();
		//记录实际查询结果的下级卡片个数
		Map<Long,Set<Long>> resultsChildren = new HashMap<Long,Set<Long>>();
		for(Document document:documentList){
			Long cardId = Long.parseLong(document.getFieldable("id").stringValue());
			
			String path = document.getFieldable(CardIndexConverter.ANCESTOR).stringValue();
			
			//将卡片自己的id附在后面，拼装完整的节点路径轨迹，便于下面统一处理
			addToResult(parentId, children, resultsChildren, new StringBuilder(path).append(cardId).toString());
		}
		if(children.size() == 0){
			return new ArrayList<Card>();
		}
		List<Card> cardList = cardService.getCardsByIdList(children);
		for(Card card:cardList){
			Set<Long> resultChildren = resultsChildren.get(card.getId());
			if(resultChildren == null || resultChildren.isEmpty()){
				card.setChildrenSize(0);
			}else{
				card.setChildrenSize(resultChildren.size());
			}
		}
		return cardList;
	}

	/**计算查询结果，根据当前卡片上级信息计算实际查询结果及查询结果的子卡片个数
	 * @param parentId 查询条件的上级id
	 * @param children 实际查询条件的返回结果
	 * @param resultsChildren 实际查询条件的返回结果中，子卡片的信息
	 * @param cardPath 上级+自己id的序列的字符串
	 *
	 */
	private void addToResult(Long parentId, Set<Long> children,
			Map<Long, Set<Long>> resultsChildren, String cardPath) {
		StringTokenizer tokenizer = new StringTokenizer(cardPath, CardIndexConverter.ANCESTOR_SEPERATOR);
		if (parentId != null) {// parentId不为空，则在继承关系串中寻找parentId；否则，跳过第一步，第一个元素就是实际查询结果
			String parentStr = parentId.toString();
			while (tokenizer.hasMoreTokens()) {
				String current = tokenizer.nextToken();
				if (parentStr.equals(current)) {
					break;
				}
			}
		}
		//取上级串中的下一个id，即为查询结果
		if(tokenizer.hasMoreTokens()){
			Long result = Long.parseLong(tokenizer.nextToken());
			children.add(result);
			
			Set<Long> resultChildren = resultsChildren.get(result);
			//初始化子卡片个数信息的集合对象
			if(resultChildren == null){
				resultChildren = new HashSet<Long>();
				resultsChildren.put(result, resultChildren);
			}
			//如果还有子卡片，将子卡片信息添加到集合中
			if(tokenizer.hasMoreTokens()){
				resultChildren.add(Long.parseLong(tokenizer.nextToken()));
			}
		}
	}

	@Autowired
	public void setCardService(CardBasicService cardService) {
		this.cardService = cardService;
	}
	
	

}
