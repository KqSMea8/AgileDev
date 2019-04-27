package com.baidu.spark.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.model.card.property.CardPropertyValue;
import com.baidu.spark.service.CardEmportService;
import com.baidu.spark.util.DateUtils;
import com.baidu.spark.util.MessageHolder;
import com.baidu.spark.util.poi.ExcelData;
import com.baidu.spark.util.poi.ExcelWriter;

/**
 * Excel的Export/Import的service类
 * @author 蹲蹲
 */
@Service
public class CardEmportServiceImpl implements CardEmportService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void generateExcelFile(Space space, List<Card> cardList, String columns, HttpServletRequest request, HttpServletResponse response){
		
		Assert.notNull( space );
		if ( null == columns ){
			columns = "";
		}
		if ( null == cardList ){
			cardList = new ArrayList<Card>();
		}
		
		ExcelData ed = generateExcelData(space, cardList, columns);
		try{
			ExcelWriter ew = new ExcelWriter();
			ew.createSheet("sheet1", ed);
			ew.outPutToResponse(space.getPrefixCode(), request, response);
		}catch( IOException e ){
			logger.error(e.getMessage());
		}
	}
	
	private ExcelData generateExcelData(Space space, List<Card> cardList, String columns){
		Assert.notNull( space );
		Assert.notNull( cardList );
		Assert.notNull( columns );
		
		columns = columns.replaceAll("\\[|\\]|\\+", ",");
		List<String> columnList = com.baidu.spark.util.StringUtils.split(columns);
		if (null == columnList){
			columnList = Collections.emptyList();
		}
		
		ExcelData ed = new ExcelData();
		
		ed.addCellData(MessageHolder.get("card.sequence"), ExcelData.COLOR_YELLOW );
		ed.addCellData(MessageHolder.get("card.title"), ExcelData.COLOR_YELLOW );
		for (String column : columnList){
			ed.addCellData( getColumnTitle( space, column ), ExcelData.COLOR_YELLOW );
		}
		ed.confirmRow();
		
		for (Card card : cardList){
			ed.addCellData(card.getCardCode());
			ed.addCellData(card.getTitle());
			for (String column : columnList){
				ed.addCellData( getColumnData( card, column ) );
			}
			ed.confirmRow();
		}
		return ed;
	}
	
	private String getColumnData(Card card, String column){
		if ( CREATOR.equals(column) ){
			return null == card.getCreatedUser() ? "" : card.getCreatedUser().getName();
		}else if ( CREATE_TIME.equals(column) ){
			return null == card.getCreatedTime() ? "" : DateUtils.DATE_TIME_SHORT_PATTERN_FORMAT.format( card.getCreatedTime() );
		}else if ( MODIFIER.equals(column) ){
			return null == card.getLastModifiedUser() ? "" : card.getLastModifiedUser().getName();
		}else if ( MODIFY_TIME.equals(column) ){
			return null == card.getLastModifiedTime()? "" : DateUtils.DATE_TIME_SHORT_PATTERN_FORMAT.format( card.getLastModifiedTime() );
		}else if ( CARD_TYPE.equals(column) ){
			return null == card.getType()? "" : card.getType().getName();
		}else if ( PARENT.equals(column) ){
			return null == card.getParent() ? "" : card.getParent().getTitle();
		}else if ( PROJECT.equals(column) ){
			return null == card.getProject() ? "" : card.getProject().getName();
		}else{
			for ( CardPropertyValue<?> value : card.getPropertyValues()){
				try{
					if ( value.getCardProperty().getLocalId().toString().equals(column) ){
						return value.getDisplayValue();
					}
				}catch( Exception e ){
					continue;
				}
			}
		}
		return "";
	}
	
	private String getColumnTitle(Space space, String column){
		if ( CREATOR.equals(column) ){
			return MessageHolder.get("card.createdUser");
		}else if ( CREATE_TIME.equals(column) ){
			return MessageHolder.get("card.createdTime");
		}else if ( MODIFIER.equals(column) ){
			return MessageHolder.get("card.lastModifiedUser");
		}else if ( MODIFY_TIME.equals(column) ){
			return MessageHolder.get("card.lastModifiedTime");
		}else if ( CARD_TYPE.equals(column) ){
			return MessageHolder.get("card.cardType");
		}else if ( PARENT.equals(column) ){
			return MessageHolder.get("card.parent");
		}else if ( PROJECT.equals(column) ){
			return MessageHolder.get("card.project");
		}else {		
			for ( CardProperty property : space.getCardProperties()){
				if (  property.getLocalId().toString().equals(column) ){
					return property.getName();
				}
			}
		}
		return "";
	}
}
