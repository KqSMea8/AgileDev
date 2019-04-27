package com.baidu.spark.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.baidu.spark.service.UserSynchronizeService;

/**
 * 用户同步JOB.定期触发从UIC到Spark的人员同步.
 * 
 * @author shixiaolei
 */
public class UserSynchronizeJob {

	private static Logger logger = LoggerFactory
			.getLogger(UserSynchronizeJob.class);
	private UserSynchronizeService service;

	public void execute() {
		logger.info("Begin synchronizing users from uic to spark");
		service.syncToLatest();
		logger.info("synchronizing finished！");
	}

	@Autowired
	public void setAs(UserSynchronizeService as) {
		this.service = as;
	}
}
