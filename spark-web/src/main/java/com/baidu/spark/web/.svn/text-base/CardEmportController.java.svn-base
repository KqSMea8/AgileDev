package com.baidu.spark.web;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.baidu.spark.dao.Pagination;
import com.baidu.spark.exception.IndexException;
import com.baidu.spark.exception.ResponseStatusException;
import com.baidu.spark.exception.UnhandledViewException;
import com.baidu.spark.model.QueryVO;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.model.card.property.CardProperty;
import com.baidu.spark.service.CardEmportService;
import com.baidu.spark.service.CardService;
import com.baidu.spark.service.SpaceService;

/**
 * 卡片导入/导出控制器
 * @author 阿蹲
 */
@Controller
@RequestMapping("spaces/{prefixCode}/emport")
public class CardEmportController {

	private static final String PARAM_SEPERATOR = "+";
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private CardService cardService;

	private SpaceService spaceService;
	
	private CardEmportService emportService;
	
	//查询所有的数据属性列
	private static final String COLUMN_TYPE_ALL = "all";
	//
	private static final String COLUMN_TYPE_PICKED = "picked";
	private static final String COLUMN_TYPE_DEFAULT = "default";
	private static final String DATA_TYPE_ALL = "all";
	private static final String DATA_TYPE_QUERIED = "queried";
	/**
	 * 根据URL的查询条件,生成excel文件并输出
	 * 
	 * @param prefixCode
	 *            空间标识
	 * @param q
	 *            查询条件参数.如q=[name1][operation][value1]+[name2][operation][value2]
	 *            多个查询条件用+号分隔
	 * @param s
	 *            排序条件参数.如s=[name1][desc]+[name2][asc] 多个排序条件用+号分隔
	 * 
	 * @return 卡片分页对象
	 */
	@RequestMapping("/exportToExcel")
	@ResponseBody
	public void exportCardListToExcel(
			@PathVariable String prefixCode,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "size", required = false) Integer size,
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "s", required = false) String sort,
			@RequestParam(value = "c", required = false) String columns,
			@RequestParam(value = "columnType", required = true) String columnType,
			@RequestParam(value = "dataType", required = true) String dataType,
			HttpServletRequest request, HttpServletResponse response) {
		
		//TODO : fuck me plz.... 找晶晶要个不带page和size能够一页查全量的方法.
		page = 1;
		size = 999999;
		
		//数据准备
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		if (space == null) {
			throw new UnhandledViewException("space.validate.notFound");
		}
		
		//如果查询所有卡片,则q字段为空
		if (DATA_TYPE_ALL.equals(dataType)){
			query = "";
		}

		//如果查询所有属性,则columns为全部,如果查询默认属性,则column为公用字段
		if (COLUMN_TYPE_ALL.equals(columnType) || COLUMN_TYPE_DEFAULT.equals(columnType)){
			List<String> columnList = new ArrayList<String>();
			columnList.add(CardEmportService.CREATOR);
			columnList.add(CardEmportService.CREATE_TIME);
			columnList.add(CardEmportService.MODIFIER);
			columnList.add(CardEmportService.MODIFY_TIME);
			columnList.add(CardEmportService.CARD_TYPE);
			columnList.add(CardEmportService.PARENT);
			columnList.add(CardEmportService.PROJECT);
			if (COLUMN_TYPE_ALL.equals(columnType)){
				for (CardProperty property : space.getCardProperties()){
					columnList.add(property.getLocalId().toString());
				}
			}
			columns = com.baidu.spark.util.StringUtils.combineToString(columnList.toArray());
		}
		
		//根据条件进行查询
		QueryVO vo = new QueryVO(StringUtils.split(query, PARAM_SEPERATOR), StringUtils.split(sort, PARAM_SEPERATOR));
		Pagination<Card> pagination = new Pagination<Card>(size, page);
		try{
			cardService.queryByCardQueryVO(vo, space.getId(), pagination);
		}catch(IndexException e){
			logger.error("card query error",e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		//根据查询条件,和需要显示的列,生成并导出Excel
		emportService.generateExcelFile(space, pagination.getResults(), columns, request, response);
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
	public void setEmportService(CardEmportService emportService) {
		this.emportService = emportService;
	}
}
