package com.baidu.spark.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

/**
 * 卡片服务接口.
 * 
 * @author 张晶
 * 
 */
public interface CardBasicService{

	/**
	 * 获取某空间下的所有卡片
	 * 
	 * @param spaceId
	 * @param page
	 * @return
	 */
	public Pagination<Card> getCardsBySpaceId(Long spaceId,
			Pagination<Card> page);
	
	/**
	 * 根据卡片id列表获得卡片对象列表
	 * @param ids
	 * @return
	 */
	public List<Card> getCardsByIdList(Collection<Long> ids) ;

	/**
	 * 根据空间和顺序码获取卡片
	 * 
	 * @param prefixCode
	 *            卡片所属空间的简称
	 * @param sequence
	 *            卡片在空间内的唯一顺序码
	 * @return
	 */
	public Card getCardBySpaceAndSeq(Space space, Long sequence);

	/**
	 * 根据id获取卡片
	 * 
	 * @param id
	 */
	public Card getCard(Long id);

	/**
	 * 删除卡片
	 * 将下级卡片的父设置为当前卡片的上级卡片。
	 * 更新索引中的卡片路径字段
	 * @param id
	 * @return
	 */
	public void deleteCard(Card card);

	/**
	 * 保存卡片信息 卡片中必须拥有space、createdUser，用于创建sequence和createdUser
	 * 
	 * @param card
	 */
	public void saveCard(Card card);

	/**
	 * 更新卡片信息 在实现中，将会更新卡片的title、描述、修改人等字段，并对卡片原有的自定义属性进行对比，对自定义属性值进行增删改
	 * 要求card中应传入lastModifiedUser,用于创建历史
	 * 
	 * @param card
	 *            view层的卡片基本信息
	 * 
	 */
	public void updateCard(Card card);

	/**
	 * 根据父卡片获取子卡片列表.
	 * 
	 * @param parent
	 *            父卡片，若为空则取根卡片列表
	 * @return 子卡片列表
	 */
	public List<Card> getAllCardsByParent(Card parent);

	/**
	 * 根据父卡片获取子卡片列表.
	 * 
	 * @param parent
	 *            父卡片
	 * @param pagination
	 *            分页对象
	 * @return 分页后的子卡片列表
	 */
	public Pagination<Card> getAllCardsByParent(Card parent,
			Pagination<Card> pagination);

	/**
	 * 根据queryString查询卡片列表.将spaceId拼装为查询条件，并增加默认排序（sequenceNum）
	 * 
	 * @param queryVo
	 * @param spaceId
	 * @param cardPage
	 * @return
	 */
	public List<Card> queryByCardQueryVO(final QueryVO queryVo, Long spaceId,
			Pagination<Card> cardPage) throws IndexException;

	/**
	 * 根据queryString查询卡片列表
	 * 
	 * @param queryVo
	 * @param cardPage
	 * @return
	 */
	public List<Card> queryByCardQueryVO(final QueryVO queryVo,
			Pagination<Card> cardPage) throws IndexException;
	/**
	 * 进行hierarchy视图的查询
	 * @param queryVo 查询vo
	 * @param spaceId 空间id
	 * @param parentId 上级id
	 * @return
	 * @throws IndexException
	 */
	public List<Card> getCardInHierarchy(QueryVO queryVo,Long spaceId, Long parentId)
			throws IndexException;

	/**
	 * 获取指定空间的根卡片列表.
	 * 
	 * @param space
	 *            空间对象
	 * @return 根卡片列表
	 * @see #getAllRootCards(String, Pagination)
	 */
	public List<Card> getAllRootCards(Space space);

	/**
	 * 获取指定空间的根卡片列表.
	 * 
	 * @param space
	 *            空间对象
	 * @param pagination
	 *            分页对象
	 * @return 分页后的根卡片列表
	 * @see #getAllRootCards(String)
	 */
	public Pagination<Card> getAllRootCards(Space space,
			Pagination<Card> pagination);

	/**
	 * 获取空间内指定类型的卡片列表
	 * 
	 * @param cardType
	 *            指定的卡片类型
	 * @return 该类型的所有卡片
	 */
	public List<Card> getAllCardsByType(CardType cardType);
	/**
	 * 获取空间内指定类型的卡片列表
	 * @param cardType 指定的卡片类型
	 * @param pagination 分页对象
	 * @return
	 */
	public Pagination<Card> getAllCardsByType(CardType cardType,Pagination<Card> pagination); 

	/**
	 * 获取空间内为指定类型的卡片的数量
	 * 
	 * @param cardType
	 *            卡片类型
	 * @return 该类型的卡片数量
	 */
	public Long getCountOfCardsByType(Long cardTypeId);

	/**
	 * 获取空间内指定上级类型的某种类型的卡片数量
	 * 
	 * @param parentTypeId
	 *            指定的上级卡片类型
	 * @param currentTypeId
	 *            指定的当前卡片类型
	 * @return 符合条件的卡片数量
	 */
	public Long getCountOfCardByParentType(Long parentTypeId, Long currentTypeId);

	/**
	 * 获取空间内指定上级类型卡片数量
	 * 
	 * @param parentTypeId
	 *            指定的上级卡片类型
	 * @return 符合条件的卡片数量
	 */
	public Long getCountOfCardsByParentType(Long parentTypeId);

	/**
	 * 初始化指定卡片类型的卡片索引 
	 * @param cardType 卡片类型对象
	 * @param batch 是否批量构建。这个方法会在一个批量操作数量执行中，始终占用lucene锁
	 * @param clear
	 * @return
	 */
	public Integer initCardTypeCardIndex(CardType cardType,boolean batch,boolean clear) ;
	/**
	 * 初始化指定空间的卡片索引 TODO 在外部增加全局变量，避免同时对同一个空间执行创建索引的操作 TODO
	 * 在外部增加全局变量，记录索引创建的进度情况。前台通过线程id实时刷新索引进度
	 * 
	 * @param spaceId
	 * @param batch 是否批量构建。这个方法会在一个批量操作数量执行中，始终占用lucene锁
	 * @param clear 是否清除原有卡片数据
	 * @return 卡片总数
	 */
	public Integer initSpaceCardIndex(Long spaceId,boolean batch,boolean clear);

	/**
	 * 初始化所有空间的卡片索引 TODO 在外部增加全局变量，避免同时对同一个空间执行创建索引的操作 TODO
	 * 在外部增加全局变量，记录索引创建的进度情况。前台通过线程id实时刷新索引进度
	 * 
	 * @param batch 是否批量构建。这个方法会在一个批量操作数量执行中，始终占用lucene锁
	 * @param clear 是否清除原有卡片数据
	 * @return 卡片总数
	 */
	public Integer initAllCardIndex(boolean batch,boolean clear);

	/**
	 * 获取用户最后操作的N条操作
	 * 
	 * @param user
	 *            当前用户
	 * @param count
	 *            指定的条目数量
	 * @return 卡片列表
	 */
	public List<Card> getRecentUpdateCards(User user, int count);
	

	/**
	 * 返回能够成为currentCard卡片的上级的根卡片列表；如果不存在满足条件的卡片，返回空List.
	 * <p>
	 * 判断规则为：
	 * </p>
	 * 1.所有<strong>类型为currentCard的卡片类型祖先类型</strong>的根节点均可作为上级
	 * 2.如果currentCard的卡片类型可以自循环，那么与currentCard<strong>同类型</strong>的根节点卡片（即：没有父级的节点），均可作上级
	 * 3.其它情况均不能作为上级 
	 * 
	 * @param currentCard
	 *            要选上级的卡片
	 * @return 能成为currentCard卡片的上级的根卡片
	 */
	public List<Card> getRootCardsValidForCardParent(Card currentCard);
	
	
	/**
	 * 返回某指定节点的卡片baseCard下，能够成为currentCard卡片的上级的卡片列表；如果不存在满足条件的卡片，返回空List.
	 * <p>
	 * 输入条件要求为：
	 * </p>
	 * 要求baseCard与currentCard必须在同一空间内，且都已持久化。否则抛出IlleglArgumentException。
	 * <p>
	 * 判断规则为：
	 * </p>
	 * 1.所有baseCard下，<strong>类型为currentCard的卡片类型祖先</strong>的节点均可作为上级
	 * 2.如果currentCard的卡片类型可以自循环，
	 * 那么对于baseCard下与currentCard同类型、而且<strong>不是currentCard的子卡片</strong>的卡片，也可作上级
	 * 3.其它情况均不能作为上级 
	 * 
	 * @param currentCard
	 *            要选上级的卡片
	 * @param baseCard
	 *            在baseCard的子卡片中选（如果为null，则从根节点中选）
	 * @return 能成为currentCard卡片的上级的卡片
	 */
	public List<Card> getCardsUnderBaseCardValidForCardParent(Card currentCard,
			Card baseCard);
	
	/**
	 * 返回某指定节点的卡片baseCard下，能够成为currentCardType卡片类型的卡片的上级的卡片列表.
	 * <p>
	 * 条件为：
	 * </p>
	 * 1. baseCard可为null，此时表示在此空间的顶级节点（即：没有父级的节点）中，找可以作为currentCardType卡片类型的卡片的上级的卡片列表 2.
	 * 如果baseCard不为null
	 * ，则要求与currentCardType必须在同一空间内，否则抛出IlleglArgumentException。
	 * <p>
	 * 规则为：
	 * </p>
	 * 1.所有类型为currentCardType祖先的节点均可作为上级
	 * 2.如果currentCardType可以自循环，那么与currentCardType同类型的卡片（即：没有父级的节点），均可作上级
	 * 3.其它情况均不能作为上级 如果不存在满足条件的卡片，返回空List
	 * 
	 * @param currentCard
	 *            要选上级的卡片
	 * @param baseCard
	 *            在baseCard的子卡片中选（如果为null，则从根节点中选）
	 * @return 能成为currentCard卡片的上级的卡片
	 */
	public List<Card> getRootCardsValidForParentByCardType(
			CardType currentCardType);
	
	
	/**
	 * 返回某指定节点的卡片baseCard下，能够成为currentCardType卡片类型的卡片的上级的卡片列表.
	 * <p>
	 * 条件为：
	 * </p>
	 * 1. baseCard可为null，此时表示在此空间的顶级节点（即：没有父级的节点）中，找可以作为currentCardType卡片类型的卡片的上级的卡片列表 2.
	 * 如果baseCard不为null
	 * ，则要求与currentCardType必须在同一空间内，否则抛出IlleglArgumentException。
	 * <p>
	 * 规则为：
	 * </p>
	 * 1.所有类型为currentCardType祖先的节点均可作为上级
	 * 2.如果currentCardType可以自循环，那么与currentCardType同类型的卡片（即：没有父级的节点），均可作上级
	 * 3.其它情况均不能作为上级 如果不存在满足条件的卡片，返回空List
	 * 
	 * @param currentCard
	 *            要选上级的卡片
	 * @param baseCard
	 *            在baseCard的子卡片中选（如果为null，则从根节点中选）
	 * @return 能成为currentCard卡片的上级的卡片
	 */
	public List<Card> getCardsUnderBaseCardValidForParentByCardType(
			CardType currentCardType, Card baseCard);

	/**
	 * 判断card_as_parent能否作为card_to_be_changed的父节点.
	 * <p>
	 * 规则是：
	 * </p>
	 * 自己不能作为自己的上级卡片;<br/> 如果card_as_parent与card_to_be_changed属于不同空间，则不允许；
	 * （同一空间时）如果card_as_parent的类型是card_to_be_changed类型的父类型或祖先类型，则一定可以；
	 * 如果card_as_parent与card_to_be_changed类型相同
	 * ，而且这种类型可以自循环，而且card_as_parent不是card_to_be_changed的子卡片，则可以； 否则不可以。
	 * 
	 * @param card_to_be_changed
	 *            要选上级的卡片
	 * @param card_as_parent
	 *            要作为上级的卡片
	 * @return
	 */
	public boolean isAssignableAsParent(Card card_to_be_changed,
			Card card_as_parent);
	
	
	/**
	 * 返回该卡片的祖先路径上卡片的ID.
	 * 其顺序是从该卡片的父节点到卡片根。
	 * @param card 当前卡片
	 * @return 卡片的祖先路径上卡片的ID
	 */
	public List<Long> getAncestorCardIds(Card card);
	
	/**
	 * 返回与某项目关联的全部卡片.
	 * 
	 * @param project
	 *            所在项目
	 * @return
	 */
	public List<Card> getCardsInProject(Project project);
	
	
	/**
	 * 根据card获取无值的cpv list.
	 * @param cardType
	 * @return
	 */
	public Collection<CardPropertyValue<?>> getCardPropertyValueFromCard(Card card);


	/**
	 * 得到一组卡片的共有属性.
	 * @param cards 卡片 
	 * @return 卡片的共有属性
	 */
	public List<CardProperty> getCommonProperties(Collection<Card> cards);
	
	/**
	 * 从一组键/值Map中，得到properties对应的卡片属性值列表，并完成校验，将校验失败结果放在validationErrors里.
	 * @param parameterMap 键/值Map
	 * @param properties  卡片属性
	 * @param validationErrors 校验失败结果
	 * @return
	 */
	public List<CardPropertyValue<?>> generateCardPropertyValues(
			Map<String, ?> parameterMap, Collection<CardProperty> properties, List<ObjectError> validationErrors);
	
	/**
	 * 批量修改卡片.
	 * <ol>
	 * <li>如果卡片已有对应的CardPropertyValue，则将其值修改为新值</li>
	 * <li>如果卡片没有对应的CardPropertyValue，则加入一个CardPropertyValue</li>
	 * <li>如果一个CardPropertyValue是DummyPropertyValue，则对应这个属性不进行修改保持原状</li>
	 * </ol>
	 * <strong>在使用上，要求这些CardPropertyValue对应的CardProperty， 与每一个card的CardType有关联。但是在代码中因为效率因素未加此校验，
	 * 请在使用中注意此点，否则会抛异常</strong>
	 * @param cards 卡片
	 * @param newCpvList 卡片中要修改成的属性值列表.
	 */
	public void batchUpdateCards(Space space,  Collection<Card> cards, List<CardPropertyValue<?>> newCpvList) ;
	
	/**
	 * 修改卡片属性值
	 * <ol>
	 * <li>如果卡片已有对应的CardPropertyValue，则将其值修改为新值</li>
	 * <li>如果卡片没有对应的CardPropertyValue，则加入一个CardPropertyValue</li>
	 * <li>如果一个CardPropertyValue是DummyPropertyValue，则对应这个属性不进行修改保持原状</li>
	 * </ol>
	 * <strong>在使用上，要求这些CardPropertyValue对应的CardProperty， 与每一个card的CardType有关联。但是在代码中因为效率因素未加此校验，
	 * 请在使用中注意此点，否则会抛异常</strong>
	 * @param space
	 * @param card
	 * @param newCpvList
	 */
	public void updateCardProperties(Space space,Card card,List<CardPropertyValue<?>> newCpvList);
	
	/**
	 * 级联删除卡片.
	 * @param card 卡片
	 */
	public void cascadeDelete(Card card);
}
