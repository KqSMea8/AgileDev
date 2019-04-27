package com.baidu.spark.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.baidu.spark.dao.AttachmentDao;
import com.baidu.spark.dao.CardDao;
import com.baidu.spark.dao.CardHistoryDao;
import com.baidu.spark.dao.CardPropertyDao;
import com.baidu.spark.dao.CardTypeDao;
import com.baidu.spark.dao.ProjectDao;
import com.baidu.spark.dao.SpaceDao;
import com.baidu.spark.dao.UserDao;
import com.baidu.spark.exception.SparkRuntimeException;
import com.baidu.spark.model.Attachment;
import com.baidu.spark.model.OpType;
import com.baidu.spark.model.User;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.history.CardHistory;
import com.baidu.spark.model.card.history.CardHistoryDiffBean;
import com.baidu.spark.model.card.history.CardHistorySingleDiff;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.model.card.property.CardPropertyValue;
import com.baidu.spark.model.card.property.TextProperty;
import com.baidu.spark.service.CardHistoryService;
import com.baidu.spark.service.impl.helper.CardHistoryDiffHelper;
import com.baidu.spark.util.ListUtils;
import com.baidu.spark.util.json.CollectionAppender;
import com.baidu.spark.util.json.JsonAppender;
import com.baidu.spark.util.json.JsonReader;
import com.baidu.spark.util.json.JsonUtils;
import com.baidu.spark.util.mapper.IncludePathCallback;
import com.baidu.spark.util.mapper.SparkMapper;
import com.baidu.spark.util.mapper.SparkMapperSingletonWrapper;

/**
 * service to record cards' change log
 * @author Adun
 */
@Service
public class CardHistoryServiceImpl implements CardHistoryService {

	
	private CardHistoryDao historyDao;
	private SpaceDao spaceDao;
	private CardDao cardDao;
	private CardTypeDao cardTypeDao;
	private CardPropertyDao cardPropertyDao;
	private UserDao userDao;
	private ProjectDao projectDao;
	private AttachmentDao attachmentDao;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public CardHistory saveHistory(Card card, OpType opType, User user) throws UnsupportedOperationException, IllegalStateException{
		Assert.notNull(card);
		Assert.notNull(opType);
		Assert.notNull(user);
		Assert.notNull(user.getId());
		//保存前的卡片
		Card oldCard = null;
		if (null != card.getId()){
			oldCard = cardDao.get(card.getId());
		}
		//check if optype is correct. with some existing history, the card's optype must not be "new", otherwise, it mustn't be "edit"
		if ((null == oldCard || ListUtils.isEmpty(oldCard.getHistoryList())) != (opType.equals(OpType.Add_Card))){
			throw new IllegalStateException("opType is not matched to the card's history state");
		}
		
		if (null == card.getId()){
			throw new UnsupportedOperationException("card should be persistant when saving its history");
		}

		CardHistory history = new CardHistory();
		String diffJsonString = getDiffJsonString(card);
		//return if there's no diff info
		if (null == diffJsonString){
			return null;
		}
		history.setDiffData(diffJsonString);
		history.setCard(card);
		history.setDetail(card.getDetail());
		history.setTitle(card.getTitle());
		history.setOpTime(new Date());
		history.setOpType(opType);
		history.setUser(user);
		history.setData(serializeHistory(card));
		historyDao.save(history);
		card.addHistory(history);
		return history;
	}
	
	@Override
	public CardHistory getHistory(Long historyId){
		Assert.notNull(historyId);
		return historyDao.get(historyId);
	}
	
	@Override
	public Card deserializeHistory(String historyJsonString){
		Assert.notNull(historyJsonString);
		//return JsonUtils.getObjectByJsonString(historyJsonString, Card.class);
		Card newCard = new Card();
		JsonReader reader;
		
		//try to get serialized history info into a card in bean form
		try{
			reader = new JsonReader(historyJsonString);
		}catch(IOException e){
			logger.error("deserialize history error: jsonString: {}, errorInfo: {}", historyJsonString, e.getMessage());
			e.printStackTrace();
			return newCard;
		}
		
		newCard.setId(reader.readLong("id"));
		Long spaceId = reader.readLong("space");
		newCard.setSpace(null==spaceId?null:spaceDao.get(spaceId));
		Long parentId = reader.readLong("parent");
		newCard.setParent(null==parentId?null:cardDao.get(parentId));
		Long typeId = reader.readLong("type");
		newCard.setType(null==typeId?null:cardTypeDao.get(typeId));
		newCard.setTitle(reader.readString("title"));
		newCard.setDetail(reader.readString("detail"));
		Long createUserId = reader.readLong("createdUser");
		newCard.setCreatedUser(null==createUserId?null:userDao.get(createUserId));
		Long projectId = reader.readLong("project");
		newCard.setProject(null==projectId?null:projectDao.get(projectId));
		newCard.setCreatedTime(reader.readDate("createdTime"));
		newCard.setSequence(reader.readLong("sequence"));
		
		for (JsonReader subReader:reader.getSubReaderList("properties")){
			try{
				Long propertyId = subReader.readLong("id");
				CardProperty property = cardPropertyDao.get(propertyId);
				if (null == property){
					String propertyName = subReader.readString("name");
					property = new TextProperty();
					property.setId(propertyId);
					property.setName(propertyName);
				}
				CardPropertyValue<?> propertyValue = property.generateValue(subReader.readObject("value"));
				newCard.addPropertyValue(propertyValue);
			}catch(Exception e){
				logger.error("deserialize Attachment error: jsonString: {}, errorInfo: {}", historyJsonString, e.getMessage());
				e.printStackTrace();
			}
		}
		for (JsonReader subReader:reader.getSubReaderList("attachments")){
			try{
				SparkMapper mapper = SparkMapperSingletonWrapper.getInstance();
				Attachment attachmentDB = attachmentDao.get(subReader.readLong("id"));
				Attachment attachment = mapper.clone(attachmentDB, new IncludePathCallback("id","name","originalName"
						,"uploadTime"));
				attachment.setCard(attachmentDB.getCard());
				attachment.setOldAttachment(attachmentDB.getOldAttachment());
				attachment.setStatus(Attachment.NORMAL_STATUS);
				attachment.setUploadUser(attachmentDB.getUploadUser());
				newCard.addAttachment(attachment);
			}catch(RuntimeException e){
				logger.error("deserialize propertyValue error: jsonString: {}, errorInfo: {}", historyJsonString, e.getMessage());
				e.printStackTrace();
			}
		}
		return newCard;
	}
	
	/**
	 * serialize a card into serialized string
	 * @param card
	 * @return
	 */
	private String serializeHistory(Card card){
		Assert.notNull(card);
		
		JsonAppender appender = new JsonAppender();
		appender.append("id", card.getId())
			.append("space", null==card.getSpace()?null:card.getSpace().getId())
			.append("parent", null==card.getParent()?null:card.getParent().getId())
			.append("type", null==card.getType()?null:card.getType().getId())
			.append("title", card.getTitle())
			.append("detail", card.getDetail())
			.append("createdUser", null==card.getCreatedUser()?null:card.getCreatedUser().getId())
			.append("createdTime", card.getCreatedTime())
			.append("sequence", card.getSequence())
			.appendList("properties", new CollectionAppender<CardPropertyValue<?>>(card.getPropertyValues()){
				@Override
				public String[] getNames() {
					return new String[]{"id", "name", "value"};
				}
				@Override
				protected Object[] getValues(CardPropertyValue<?> obj) {
					//淇濆瓨propertyId, porpertyName, 鍜宲ropertyValue鐨剉alue鍊�
					return new Object[]{obj.getCardProperty().getId(), obj.getCardProperty().getName(), obj.getValue()};
				}
			})
			.appendList("attachments", new CollectionAppender<Attachment>(card.getValidAttachments()){
				@Override
				public String[] getNames() {
					return new String[]{"id", "name", "oldAttachmentId", "uploadUser"};
				}
				@Override
				protected Object[] getValues(Attachment obj) {
					//淇濆瓨propertyId, porpertyName, 鍜宲ropertyValue鐨剉alue鍊�
					return new Object[]{obj.getId(), obj.getName(), 
							obj.getOldAttachment() == null ? null : obj.getOldAttachment().getId(),
							obj.getUploadUser() == null ? null : obj.getUploadUser().getId()};
				}
			})
			.append("project", null==card.getProject()?null:card.getProject().getId());	//TODO 对icafe项目的处理.重构icafe项目的处理之后,需要对此部分进行考虑
		return appender.getJsonString();
	}

	/**
	 * diff a card with it's last history. if no existigng history, the return will be "isNew : true", or diff info list in json form if the card has any history info already
	 * @param Card
	 * @return diffbean的序列化json字符串.如果没有任何diff信息,则返回null!!!
	 */
	private String getDiffJsonString(Card card) {
		Assert.notNull(card);
		
		//create a new empty bean
		CardHistoryDiffBean diffBean = new CardHistoryDiffBean();
		
		//card
		Card oldCard = null;
		if (null != card.getId()){
			oldCard = cardDao.get(card.getId());
		}
		
		//get diff info
		if (null == oldCard || ListUtils.isEmpty(oldCard.getHistoryList())){
			diffBean.setIsNew(true);
		}else{
			//get diff fields
			Card history;
			try{
				history = deserializeHistory(oldCard.getHistoryList().get(0).getData());
				if (null == history){
					throw new SparkRuntimeException("deserializd history is null");
				}
				//diff each field
				List<CardHistorySingleDiff> diffList = CardHistoryDiffHelper.diffCard(card, history);
				diffBean.setDiffList(diffList);
			}catch(Exception e){
				logger.error("deserialize history error, card: {}, errorInfo: {} ", card, e.getMessage());
				e.printStackTrace();
			}
		}
		if (diffBean.getIsNew() == false && (diffBean.getDiffList() == null || diffBean.getDiffList().size() == 0)){
			return null;
		}else{
			return JsonUtils.getJsonString(diffBean);
		}
	}
	
	@Autowired
	public void setHistoryDao(CardHistoryDao historyDao) {
		this.historyDao = historyDao;
	}

	@Autowired
	public void setCardDao(CardDao cardDao) {
		this.cardDao = cardDao;
	}

	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Autowired
	public void setSpaceDao(SpaceDao spaceDao) {
		this.spaceDao = spaceDao;
	}

	@Autowired
	public void setCardTypeDao(CardTypeDao cardTypeDao) {
		this.cardTypeDao = cardTypeDao;
	}

	@Autowired
	public void setCardPropertyDao(CardPropertyDao cardPropertyDao) {
		this.cardPropertyDao = cardPropertyDao;
	}

	@Autowired
	public void setProjectDao(ProjectDao projectDao) {
		this.projectDao = projectDao;
	}

	@Autowired
	public void setAttachmentDao(AttachmentDao attachmentDao) {
		this.attachmentDao = attachmentDao;
	}

}
