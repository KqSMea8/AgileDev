package com.baidu.spark.util;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.SystemPropertyUtils;

import com.baidu.spark.model.Group;

/**
 * 系统常量配置
 * 
 * @author zhangjing_pe
 * 
 */
public class SparkConfig {
	/** 系统默认编码 */

	private static Properties props = null;

	private static Set<String> defaultAdminAccountSet = null;
	/**
	 * 解析property的placeholder工具
	 */
	private static PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper(SystemPropertyUtils.PLACEHOLDER_PREFIX, SystemPropertyUtils.PLACEHOLDER_SUFFIX, SystemPropertyUtils.VALUE_SEPARATOR, false);

	/**
	 * 获取配置中的编码字符集
	 * 
	 * @return 配置中的编码字符集
	 */
	public static String getCharacterEncoding() {
		return getSparkConfig("system.encoding");
	}
	/**
	 * 是否debug模式
	 * @return
	 */
	public static Boolean isDebugMode() {
		return Boolean.valueOf(getSparkConfig("system.mode.debug"));
	}
	/**
	 * 根据配置的key
	 * @param configKey
	 * @return
	 */
	public static String getSparkConfig(String configKey) {
		if (props == null) {
			return null;
		}
		return getProperty(configKey);
	}
	/**
	 * 获取系统默认帐号的配置
	 * @return
	 */
	public static String getDefaultAdminAccountConf() {
		return props == null ? null : getProperty("system.defaultAdminAccount");
	}
	/**
	 * 用户是否是系统默认管理员
	 * @param username 用户名
	 * @return 判断结果。若系统时默认管理员，则返回为true。否则，返回false
	 */
	public static boolean isDefaultAdminAccount(String username) {
		if (username == null || username.length() == 0) {
			return false;
		}
		if (defaultAdminAccountSet == null) {
			defaultAdminAccountSet = new HashSet<String>();
			String accounts = getDefaultAdminAccountConf();
			if (accounts != null && accounts.length() > 0) {
				String[] accs = StringUtils.split(accounts, ",");
				for (String str : accs) {
					defaultAdminAccountSet.add(str.trim());
				}
			}
		}
		return defaultAdminAccountSet.contains(username);
	}
	/**
	 * 获取包含所有人的用户组
	 * @return
	 */
	public static Group getEveryOneGroup(){
		Group group = new Group();
		group.setId(NumberUtils.toLong(getProperty("system.group.everyone.id")));
		group.setName(getProperty("system.group.everyone.namekey"));
		return group;
	}
	
	/**
	 * 获取配置文件中主机URL前缀.
	 * @return 主机URL前缀，最后不包含斜杠(/)
	 */
	public static String getServerUrlPrefix() {
		String urlPrefix = getSparkConfig("host.server.url.prefix");
		if ("/".equals(urlPrefix.substring(urlPrefix.length() - 1))) {
			return urlPrefix.substring(0, urlPrefix.length() - 1);
		}
		else {
			return urlPrefix;
		}
	}

	public void setProps(Properties props) {
		SparkConfig.props = props;
	}
	
	private static String getProperty(String key){
		return helper.replacePlaceholders(props.getProperty(key), props);
	}

}
