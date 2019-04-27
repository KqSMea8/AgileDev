package com.baidu.spark.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.baidu.spark.service.CardBasicService;
/**
 * 系统初始化
 * @author zhangjing_pe
 *
 */
public class SparkInitializer {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private CardBasicService cardService = null;
	
	private String initCardIndexStrategy = null;
	/**
	 * 初始化方法
	 */
	public void init() {
		if (initCardIndexStrategy != null && initCardIndexStrategy.equals("clearAndBuildAll")) {
			new Thread(){
				public void run(){
					logger.info("clearAndBuildAll card index...");
					int total = cardService.initAllCardIndex(true,true);
					logger.info("clearAndBuildAll card index finish!total:{}",total);
				}
			}.start();
		}else if (initCardIndexStrategy != null && initCardIndexStrategy.equals("addOrUpdateAll")) {
			new Thread(){
				public void run(){
					logger.info("addOrUpdateAll card index...");
					int total = cardService.initAllCardIndex(true,false);
					logger.info("addOrUpdateAll card index finish!total:{}",total);
				}
			}.start();
		}
	}

	@Autowired
	public void setCardService(CardBasicService cardService) {
		this.cardService = cardService;
	}

	public void setInitCardIndexStrategy(String initCardIndexStrategy) {
		this.initCardIndexStrategy = initCardIndexStrategy;
	}
	
}
