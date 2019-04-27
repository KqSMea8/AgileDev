package com.baidu.spark.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import com.baidu.spark.annotations.UIC;
import com.baidu.spark.model.User;
import com.baidu.spark.service.ConfigurationService;
import com.baidu.spark.service.NotificationService;
import com.baidu.spark.service.UserService;
import com.baidu.spark.service.UserSynchronizeService;
import com.baidu.spark.util.BeanMapperSingletonWrapper;

/**
 * 用户从UIC中同步服务接口.
 * 
 * @author shixiaolei
 * 
 */
@Service
public class UserSynchronizeServiceImpl implements UserSynchronizeService {

	private static final long FULL_SYNC_FROM_VERSION = -1L;
	private static final long FULL_SYNC__TO_VERSION = 1000000L;
	private static final String ADD_USER = "A";
	private static final String DELETE_USER = "D";
	private static final String UPDATE_USER = "U";
	private static final String USER_SERVICE_URL = "/versions/{from}-{to}/users";
	private static final String LATEST_VERSION_URL = "versions/latest";

	private Logger logger = LoggerFactory.getLogger(getClass());;
	private UserService userService;
	private ConfigurationService configurationService;
	private NotificationService notificationService;
	private RestTemplate template;



	@Override
	public int syncToLatest() {
		int count = sync(getFromVersion(), getToVersion());
		configurationService.set("uicVersion", getUicCurrentVersion());
		return count;
	}
	
	private Long getFromVersion(){
		String localVersion = configurationService.get("uicVersion");
		if (StringUtils.isNotBlank(localVersion)) {
			return Long.parseLong(localVersion);
		} else {
			return FULL_SYNC_FROM_VERSION;
		}
	}
	
	private Long getToVersion(){
		return FULL_SYNC__TO_VERSION;
	}
	
	/**
	 * 同步UIC数据,将本地用户从fromVersion同步到toVersion,并返回本次同步的数据条数.
	 * 
	 * @param fromVersion
	 *            起始UIC版本
	 * @param toVersion
	 *            中止UIC版本
	 * 
	 * @return 本次同步的数据条数
	 */
	@SuppressWarnings("unchecked")
	private int sync(Long fromVersion, Long toVersion) {
		List<HashMap<?, ?>> users = getDiffUsers(fromVersion, toVersion);
		List<String> failedInfos = new ArrayList<String>();
		long add = 0, delete = 0, update = 0, failed = 0;
		for (HashMap<?, ?> map : users) {
			try {
				String status = (String) map.get("status");
				User user = generateUser(map);
				if (ADD_USER.equals(status) || UPDATE_USER.equals(status)) {
					if (isUserExistsInDB(user)) {
						updateUserFromUic(user);
						update++;
					} else {
						saveUserFromUic(user);
						add++;
					}
				} else if (DELETE_USER.equals(status)) {
					deleteUserFromUic(user);
					delete++;
				} else {
					failedInfos.add(map.toString());
					failed++;
				}
			} catch (Throwable e) {
				failedInfos.add(map.toString());
				failed++;
			}
		}
		logger
				.info(
						"uic synchronied finished, get {} diff messages: {} added, {} deleted, {} updated, {} failed",
						new Object[] { users.size(), add, delete, update, failed });
		handleSynchronizeErrors(failedInfos);
		return users.size();
	}
	
	/**
	 * 得到从fromVersion到toVersion之间更改了的UIC用户数据传输对象. 若希
	 * 望获取全量差异版本，可以直接传入fromVersion=-1,toVersion=1000000.
	 * 
	 * @param fromVersion
	 *            起始UIC版本
	 * @param toVersion
	 *            中止UIC版本
	 * @return 更改了的UIC用户数据传输对象
	 */
	@SuppressWarnings("unchecked")
	private List<HashMap<?, ?>> getDiffUsers(Long fromVersion, Long toVersion) {
		Map<?, ?> ret = template.getForObject(USER_SERVICE_URL, HashMap.class,
				fromVersion, toVersion);
		return (List<HashMap<?, ?>>) ret.get("userVersionDTO");
	}

	private void handleSynchronizeErrors(List<String> failedInfos) {
		if (failedInfos.size() > 0) {
			notificationService.sendUserSynchronizeErrors(failedInfos);
			logger.warn("the failed records are these:");
			for (String info : failedInfos) {
				logger.warn(info);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private User generateUser(Map map) {
		User user = new User();
		Object username = map.get("username");
		if (username == null) {
			throw new IllegalStateException("username is null");
		}
		Long uicId = null;
		try {
			uicId = Long.parseLong(String.valueOf(map.get("id")));
		} catch (Exception e) {
			throw new IllegalStateException("uic id is null");
		}
		user.setUsername(String.valueOf(username));
		user.setUicId(uicId);
		user.setName(map.get("name") == null ? null : map.get("name")
				.toString());
		user.setEmail(map.get("email") == null ? null : map.get("email")
				.toString());
		return user;
	}

	/**
	 * 根据UIC同步来的游离态User用户，删除Spark系统中同UicId的用户信息.
	 * <p>
	 * 策略是：如果在spark系统中存在<strong>同uic id</strong>的<strong>未离职</strong>用户,则将该用户置为离职状态;
	 * 否则，如果不存在，则加入一条离职人员信息.
	 * </p>
	 * 
	 * @param user
	 *            从UIC同步来的游离态User用户.
	 */
	private void deleteUserFromUic(User user) {
		Assert.notNull(user);
		Assert.notNull(user.getUsername());
		Assert.notNull(user.getUicId());
		Assert.isNull(user.getId());
		User dbUser = getDBUserByUicId(user);
		if (dbUser != null) {
			dbUser.setLocked(true);
			userService.updateUser(dbUser);
		} else {
			user.setLocked(true);
			userService.saveUser(user);
		}
	}

	/**
	 * 根据UIC同步来的游离态User用户，存储到Spark系统中.
	 * <p>
	 * 策略是：如果在spark系统中不存在<strong>同uic id</strong>的<strong>未离职</strong>用户,则将该用户加入;
	 * 否则，如果已存在，会抛出一个IllgleStateException.
	 * </p>
	 * 
	 * @param user
	 *            从UIC同步来的游离 态User用户.
	 */
	private void saveUserFromUic(User user) {
		Assert.notNull(user);
		Assert.notNull(user.getUsername());
		Assert.notNull(user.getUicId());
		Assert.isNull(user.getId());
		User dbUser = getDBUserByUicId(user);
		if (dbUser == null) {
			user.setLocked(false);
			userService.saveUser(user);
		} else {
			throw new IllegalStateException(
					"add user, but it is already exists");
		}
	}

	/**
	 * 根据UIC同步来的游离态User用户，修改Spark系统中同UicId的用户信息. *
	 * <p>
	 * 策略是：如果在spark系统中存在<strong>同uic id</strong>的<strong>未离职</strong>用户,则将该用户加入;
	 * 否则，如果不存在，会抛出一个IllgleStateException.
	 * </p>
	 * 
	 * @param user
	 *            从UIC同步来的游离态User用户.
	 */
	private void updateUserFromUic(User user) {
		Assert.notNull(user);
		Assert.notNull(user.getUsername());
		Assert.notNull(user.getUicId());
		Assert.isNull(user.getId());
		User dbUser = getDBUserByUicId(user);
		if (dbUser != null) {
			Mapper mapper = BeanMapperSingletonWrapper.getInstance();
			mapper.map(user, dbUser, "user-updateUser");
			user.setLocked(false);
			userService.updateUser(dbUser);
		} else {
			throw new IllegalStateException("update user, but it is not exists");
		}
	}

	private String getUicCurrentVersion() {
		return String.valueOf(template.getForObject(LATEST_VERSION_URL,
				Object.class));
	}

	private boolean isUserExistsInDB(User user) {
		return userService.checkUicIdConflicts(user);
	}

	private User getDBUserByUicId(User user) {
		Long uicId = user.getUicId();
		return userService.getUserByUicId(uicId);
	}

	@Autowired
	@UIC
	public void setTemplate(RestTemplate template) {
		this.template = template;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Autowired
	public void setConfigurationService(
			ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
