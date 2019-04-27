package com.baidu.spark.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.Card;

/**
 * Excel的Export/Import的service接口
 * @author 蹲子
 */
public interface CardEmportService {

	/**
	 * 创建人字段
	 */
	public static final String CREATOR = "createdUser";
	/**
	 * 创建时间字段
	 */
	public static final String CREATE_TIME = "createdTime";
	/**
	 * 最后修改时间字段
	 */
	public static final String MODIFIER = "lastModifiedUser";
	/**
	 * 最后修改人字段
	 */
	public static final String MODIFY_TIME = "lastModifiedTime";
	/**
	 * 卡片类型字段
	 */
	public static final String CARD_TYPE = "cardType";
	/**
	 * 父卡片字段
	 */
	public static final String PARENT = "parent";
	/**
	 * 所属项目字段
	 */
	public static final String PROJECT = "project";
	
	/**
	 * 根据查询的结果,将卡片列表,将结果导出到Excel中,并输出到response
	 * @param space
	 * 		卡片所属的空间
	 * @param cardList
	 * 		查询结果的卡片列表
	 * @param columns
	 * 		需要展现的列的列表
	 * @param request
	 * 		HttpServletRequest
	 * @param response
	 * 		HttpServletResponse
	 */
	public void generateExcelFile(Space space, List<Card> cardList, String columns, HttpServletRequest request, HttpServletResponse response);
	
}
