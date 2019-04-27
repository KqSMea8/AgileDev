package com.baidu.spark.service.impl.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.util.Assert;

import com.baidu.spark.model.Attachment;
import com.baidu.spark.model.Project;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.CardType;
import com.baidu.spark.model.card.history.CardHistorySingleDiff;
import com.baidu.spark.model.card.property.CardPropertyValue;
import com.baidu.spark.util.StringUtils;

/**
 * 卡片间的diff信息生成的工具类
 * @author Adun
 */
public class CardHistoryDiffHelper {
	
	/**
	 * 将两张卡片进行对比(需要均不为空),将diff的结果封装为一个CardHistorySingleDiff的列表
	 * @param newCard
	 * @param oldCard
	 * @return
	 * @author Adun
	 * 2010-06-12
	 */
	public static List<CardHistorySingleDiff> diffCard(Card newCard, Card oldCard){
		Assert.notNull(newCard);
		Assert.notNull(oldCard);
		
		List<CardHistorySingleDiff> diffList = new ArrayList<CardHistorySingleDiff>();
		diffList.add(diffSpace(newCard, oldCard));
		diffList.add(diffPrent(newCard, oldCard));
		diffList.add(diffType(newCard, oldCard));
		diffList.add(diffTitle(newCard, oldCard));
		diffList.add(diffDetail(newCard, oldCard));
		diffList.add(diffProject(newCard, oldCard));
//		diffList.add(diffCreateUser(newCard, oldCard));
//		diffList.add(diffCreateTime(newCard, oldCard));
		diffList.addAll(diffProperties(newCard, oldCard));
		diffList.addAll(diffAttachment(newCard, oldCard));
		
		//删除其中为null值的diff信息
		for (Iterator<CardHistorySingleDiff> iterator=diffList.iterator();iterator.hasNext();){
			if (null == iterator.next()){
				iterator.remove();
			}
		}
		return diffList;
	}
	
	/**
	 * 对两张卡片的项目进行diff操作,并生成diff信息
	 * @param newCard
	 * @param oldCard
	 * @return
	 */
	private static CardHistorySingleDiff diffProject(Card newCard, Card oldCard) {
		Project newProject = getProject(newCard);
		Project oldProject = getProject(oldCard);
		if (newProject.getId().equals(oldProject.getId())){
			return null;
		}else{
			return CardHistorySingleDiff.createChangeFieldDiff("project", oldProject.getName(), newProject.getName() );
		}
	}

	/**
	 * 对两张卡片的自定义字段进行diff操作,并生成diff信息
	 * @param newCard
	 * @param oldCard
	 * @return
	 */
	private static List<CardHistorySingleDiff> diffProperties(Card newCard, Card oldCard) {
		List<CardHistorySingleDiff> diffList = new ArrayList<CardHistorySingleDiff>();
		
		//获取newCard中propertyValueList的拷贝
		List<CardPropertyValue<?>> newPropertyList = new ArrayList<CardPropertyValue<?>>();
		if (null != newCard.getPropertyValues()){
			for (CardPropertyValue<?> newValue:newCard.getPropertyValues()){
				if (null != newValue){
					newPropertyList.add(newValue);
				}
			}
		}
		
		//获取oldCard中propertyValueList的拷贝
		List<CardPropertyValue<?>> oldPropertyList = new ArrayList<CardPropertyValue<?>>();
		if (null != oldCard.getPropertyValues()){
			for (CardPropertyValue<?> oldValue:oldCard.getPropertyValues()){
				if (null != oldValue){
					oldPropertyList.add(oldValue);
				}
			}
		}
		
		//将两个list进行对比.如果发先两个cardPpropertyId一样,则进行对比.如果值不同则记录diff....然后,不管是否相同,两个list中均删除.
		for (Iterator<CardPropertyValue<?>> newIterator=newPropertyList.iterator();newIterator.hasNext();){
			CardPropertyValue<?> newValue = newIterator.next();
			Long propertyId = newValue.getCardProperty().getId();
			for (Iterator<CardPropertyValue<?>> oldIterator=oldPropertyList.iterator();oldIterator.hasNext();){
				CardPropertyValue<?> oldValue = oldIterator.next();
				if (propertyId.equals(oldValue.getCardProperty().getId())){
					if (! newValue.getValueString().equals(oldValue.getValueString())){
						diffList.add(CardHistorySingleDiff.createChangeFieldDiff(newValue.getCardProperty().getName(), oldValue.getDisplayValue(), newValue.getDisplayValue()));
					}
					newIterator.remove();
					oldIterator.remove();
					break;
				}
			}
		}
		
		//处理新增的
		for (CardPropertyValue<?> newValue : newPropertyList){
			if (StringUtils.notEmpty(newValue.getValueString())){
				diffList.add(CardHistorySingleDiff.createChangeFieldDiff(newValue.getCardProperty().getName(), "", newValue.getDisplayValue()));
			}
		}
		
		//处理删除的
		for (CardPropertyValue<?> oldValue : oldPropertyList){
//			diffList.add(CardHistorySingleDiff.createDelFieldDiff(oldValue.getCardProperty().getName(), oldValue.getDisplayValue()));
		}
		
		//返回结果
		return diffList;
	}


	/**
	 * 根据两个卡片进行diff,获取diff数据,如果没有变化则返回null
	 * @param newCard
	 * @param oldCard
	 * @return
	 */
	private static CardHistorySingleDiff diffDetail(Card newCard, Card oldCard) {
		Assert.notNull(oldCard);
		Assert.notNull(newCard);
		
		if (ObjectUtils.equals(newCard.getDetail(), oldCard.getDetail())){
			return null;
		}else{
			return CardHistorySingleDiff.createChangeFieldDiff("detail", oldCard.getDetail(), newCard.getDetail());
		}
	}

	/**
	 * 根据两个卡片进行diff,获取diff数据,如果没有变化则返回null
	 * @param newCard
	 * @param oldCard
	 * @return
	 */
	private static CardHistorySingleDiff diffTitle(Card newCard, Card oldCard) {
		Assert.notNull(oldCard);
		Assert.notNull(newCard);
		
		if (ObjectUtils.equals(newCard.getTitle(), oldCard.getTitle())){
			return null;
		}else{
			return CardHistorySingleDiff.createChangeFieldDiff("title", oldCard.getTitle(), newCard.getTitle());
		}
	}

	/**
	 * 根据两个卡片进行diff,获取diff数据,如果没有变化则返回null
	 * @param newCard
	 * @param oldCard
	 * @return
	 */
	private static CardHistorySingleDiff diffType(Card newCard, Card oldCard) {
		CardType newType = getCardType(newCard);
		CardType oldType = getCardType(oldCard);
		if (newType.getId().equals(oldType.getId())){
			return null;
		}else{
			return CardHistorySingleDiff.createChangeFieldDiff("type", oldType.getName(), newType.getName());
		}
	}

	/**
	 * 根据两个卡片进行diff,获取diff数据,如果没有变化则返回null
	 * @param newCard
	 * @param oldCard
	 * @return
	 */
	private static CardHistorySingleDiff diffPrent(Card newCard, Card oldCard) {
		Card newParent = getParent(newCard);
		Card oldParent = getParent(oldCard);
		if (newParent.getId().equals(oldParent.getId())){
			return null;
		}else{
			return CardHistorySingleDiff.createChangeFieldDiff("parent", oldParent.getTitle(), newParent.getTitle());
		}
	}

	/**
	 * 根据两个卡片进行diff,获取diff数据,如果没有变化则返回null
	 * @param newCard
	 * @param oldCard
	 * @return
	 */
	private static CardHistorySingleDiff  diffSpace(Card newCard, Card oldCard){
		Space newSpace = getSpace(newCard);
		Space oldSpace = getSpace(oldCard);
		if (newSpace.getId().equals(oldSpace.getId())){
			return null;
		}else{
			return CardHistorySingleDiff.createChangeFieldDiff("space", oldSpace.getName(), newSpace.getName());
		}
	}
	
	/**
	 * 对两张卡片的附件进行diff操作,并生成diff信息
	 * @param newCard
	 * @param oldCard
	 * @return
	 */
	private static List<CardHistorySingleDiff> diffAttachment(Card newCard, Card oldCard) {
		List<CardHistorySingleDiff> diffList = new ArrayList<CardHistorySingleDiff>();
		
		Map<Long, Attachment> newAttach = new HashMap<Long, Attachment>(); 
		Map<Long, Attachment> oldAttach = new HashMap<Long, Attachment>(); 
		// 将新卡片的附件放入Map
		for(Attachment attach : newCard.getValidAttachments()){
			if(attach!=null){
				newAttach.put(attach.getId(), attach);
			}
		}
		// 将旧卡片的附件放入Map
		for(Attachment attach : oldCard.getValidAttachments()){
			if(attach!=null){
				oldAttach.put(attach.getId(), attach);
			}
		}
	
		Map<Long, Attachment> replacedAttachs = new HashMap<Long, Attachment>();
		// 如果一个卡片在新卡片中有，旧卡片中没有，则为新增或重新上传。
		// 其中,如果该卡片的oldAttachment字段为null，表示为新增附件，将其加入“新增DIFF”列表
		// 如果该卡片的oldAttachment不为null，则表示为重新上传，将其加入“修改DIFF”列表，同时将oldAttachment放入“被替换附件的HashSet”，供下一步使用
		for(Map.Entry<Long, Attachment> map : newAttach.entrySet()){
			Long id = map.getKey();
			Attachment attachment = map.getValue();
			if(!oldAttach.containsKey(id)){
				if(attachment.getOldAttachment()==null){
					diffList.add(CardHistorySingleDiff.createAddAttachmentDiff("attachment", attachment.getOriginalName()));
				}else{
					Attachment attReplaced = attachment.getOldAttachment();
					replacedAttachs.put(attReplaced.getId(), attReplaced);
					diffList.add(CardHistorySingleDiff.createChangeAttachmentDiff("attachment", attReplaced.getOriginalName(), attachment.getOriginalName()));
				}
			}
		}
		// 如果一个卡片在旧卡片中有，新卡片中没有，而且它不在““被替换附件的HashSet”，则表示为删除的附件，将其放入“删除DIFF”列表中
		for(Map.Entry<Long, Attachment> map : oldAttach.entrySet()){
			Long id = map.getKey();
			Attachment attachment = map.getValue();
			if(!newAttach.containsKey(id) && !replacedAttachs.containsKey(id)){
				diffList.add(CardHistorySingleDiff.createDelAttachmentDiff("attachment", attachment.getOriginalName()));
			}
		}
		return diffList;
	}
	
	/**
	 * 根据card获取project对象,并保证id和name不为空(分别补0L和空字符串),供比较实用
	 * @param card
	 * @return
	 */
	private static Project getProject(Card card){
		Assert.notNull(card);
		Assert.notNull(card.getSpace());
		
		Project project = card.getProject();
		if (null == project){
			project = new Project();
		}
		if (null == project.getId()){
			project.setId(0L);
		}
		if (null == project.getName()){
			project.setName("");
		}
		return project;
	}
	
	/**
	 * 根据card获取space对象,并保证id和name不为空(分别补0L和空字符串),供比较实用
	 * @param card
	 * @return
	 */
	private static Space getSpace(Card card){
		Assert.notNull(card);
		Assert.notNull(card.getSpace());
		
		Space space = card.getSpace();
		if (null == space.getId()){
			space.setId(0L);
		}
		if (null == space.getName()){
			space.setName("");
		}
		return space;
	}
	
	/**
	 * 根据card获取cardType对象,并保证id和name不为空(分别补0L和空字符串),供比较实用
	 * @param card
	 * @return
	 */
	private static CardType getCardType(Card card){
		Assert.notNull(card);
		Assert.notNull(card.getType());
		
		CardType type = card.getType();
		if (null == type.getId()){
			type.setId(0L);
		}
		if (null == type.getName()){
			type.setName("");
		}
		return type;
	}
	
	/**
	 * 根据card获取parent对象,并保证id和title不为空(分别补0L和空字符串),供比较实用
	 * @param card
	 * @return
	 */
	private static Card getParent(Card card){
		Assert.notNull(card);
		
		Card parent;
		if (null == card.getParent()){
			parent = new Card();
		}else{
			parent = card.getParent();
		}
		if (null == parent.getId()){
			parent.setId(0L);
		}
		if (null == parent.getTitle()){
			parent.setTitle("");
		}
		return parent;
	}
}
