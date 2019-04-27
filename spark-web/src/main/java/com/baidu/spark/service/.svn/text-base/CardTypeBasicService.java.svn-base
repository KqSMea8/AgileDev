package com.baidu.spark.service;

import java.util.Collection;
import java.util.List;

import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.property.CardProperty;

/**
 * 卡片类型和卡片属性服务接口
 * @author chenhui
 *
 */
public interface CardTypeBasicService {
	
	/**
	 * 获取空间的所有卡片类型
	 * @param space 所属空间
	 * @return 卡片类型列表
	 */
	public List<CardType> getAllCardTypes(Space space);
	
	/**
	 * 获取指定id的卡片类型
	 * @param id 卡片类型id
	 * @return 卡片类型
	 */
	public CardType getCardType(Long id);
	
	/**
	 * 根据spaceId和cardTypeLocalId获取cardType
	 * @param spaceId
	 * @param cardTypeLocalId
	 * @return
	 */
	public CardType getCardType(Long spaceId,Long cardTypeLocalId);
	
	
	/**
	 * 获取有效的可设置为上级卡片类型的列表
	 * @param cardTypeId
	 * @return
	 */
	public List<CardType> getValidCardTypesAsParent(CardType cardType);
	
	/**
	 * 判断是否已经存在其他同名卡片类型
	 * 用于卡片类型的校验
	 * @param cardType
	 * @return 是否有其他存在
	 */
	public boolean checkConflicts(CardType cardType);
	
	/**
	 * 保存卡片类型
	 * 若需要冲突校验,需要先调用checkConflicts
	 * @param cardType
	 * @see checkConflicts
	 */
	public void saveCardType(CardType cardType);
	
	/**
	 * 删除卡片类型
	 * @param cardType
	 */
	public void deleteCardType(CardType cardType);
	
	/**
	 * 更新卡片类型
	 * <p>
	 * 若需要冲突校验,需要先调用checkConflicts
	 * 如果卡片类型的recursive发生变化，将会处理相关级联操作
	 * </p>
	 * @param cardType 待更新的卡片类型
	 * @see checkConflicts
	 */
	public void updateCardType(CardType cardType);
	
	/**
	 * 保存卡片属性.
	 * <p>
	 * 如果卡片属性上的排序号(seq)为空，则会自动生成一个.
	 * 在并发情况下此值可能不够准确，但不影响实际使用
	 * </p>
	 * @param cardProperty 待保存的卡片属性
	 * @param originCardTypes 原始的映射的卡片类型。用于比较来更新卡片索引
	 */
	public void saveCardProperty(CardProperty cardProperty,Collection<CardType> originCardTypes);
	
	/**
	 * 将list类型的未保存的option的key进行生成
	 * @param spaceId 所属空间
	 * @param keys option的key列表，从controller中传入
	 * @return key option的key列表，将新添加的key也进行了生成
	 */
	public List<String> saveNewListOptionKey(Long spaceId,List<String> keys);
	
	/**
	 * 获取指定的卡片属性.
	 * @param id 卡片属性ID
	 * @return 卡片属性
	 */
	public CardProperty getCardProperty(Long id);
	
	/**
	 * 获取根据空间id和cardProperty的localId获取卡片属性
	 * @param spaceId 空间id
	 * @param cardPropertyLocalId
	 * @return
	 */
	public CardProperty getCardProperty(Long spaceId,Long cardPropertyLocalId);
	
	/**
	 * 根据空间获取所有的卡片属性定义
	 * @param spaceId 空间id
	 * @param cardPropertyLocalId
	 * @return
	 */
	public List<CardProperty> getAllCardProperties(Long spaceId);
	
	/**
	 * 删除指定的卡片属性.
	 * @param cardProperty 卡片属性对象，其ID值必须不为空
	 */
	public void deleteCardProperty(CardProperty cardProperty);
	
}
