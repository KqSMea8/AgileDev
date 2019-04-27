package com.baidu.spark.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baidu.spark.dao.ConfigurationDao;
import com.baidu.spark.model.Configuration;
import com.baidu.spark.service.ConfigurationService;

/**
 * 附件服务实现类
 * 
 * @author shixiaolei
 * 
 */
@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	private ConfigurationDao dao;

	@Override
	public String get(String key) {
		Configuration con = dao.findUniqueByProperty("key", key);
		if (con != null) {
			return con.getValue();
		} else {
			return null;
		}
	}

	@Override
	public void set(String key, String value) {
		Configuration conf = dao.findUniqueByProperty("key", key);
		if (conf != null) {
			conf.setValue(value);
			dao.update(conf);
		} else {
			conf = new Configuration(key, value);
			dao.save(conf);
		}
	}

	@Override
	public List<String> getKeys() {
		List<String> keys = new LinkedList<String>();
		List<Configuration> confs = dao.findAll();
		for (Configuration conf : confs) {
			keys.add(conf.getKey());
		}
		return keys;
	}

	@Override
	public Map<String, String> getConfigurations() {
		Map<String, String> map = new HashMap<String, String>();
		List<Configuration> confs = dao.findAll();
		for (Configuration conf : confs) {
			map.put(conf.getKey(), conf.getValue());
		}
		return map;
	}

	@Autowired
	public void setDao(ConfigurationDao dao) {
		this.dao = dao;
	}
}
