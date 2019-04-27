package com.baidu.spark.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;


/**
 * RestTemplate工厂.
 * <p>
 * 用于构造Spring 3.0 RestTemplate，使之支持认证、基础URL等功能.
 * </p>
 * 
 * @author GuoLin,shixiaoleiw
 *
 */
public class RestTemplateFactory implements FactoryBean<RestTemplate> {
	
	/** 主机名 */
	private String hostname;
	
	/** 端口 */
	private int port;
	
	/** 用户名，如果不需要认证则不需要填入 */
	private String username;
	
	/** 密码，如果不需要认证则不需要填入 */
	private String password;
	
	/** 上下文路径 */
	private String contextPath;

	public RestTemplate getObject() throws Exception {
		// 认证配置
		CommonsClientHttpRequestFactory httpRequestFactory = new CommonsClientHttpRequestFactory();
		HttpClient httpClient = httpRequestFactory.getHttpClient();
		httpClient.getHostConfiguration().getParams().setParameter("Content-Type", "application/x-www-form-urlencoded");
		// 是否需要Basic认证
		if (username != null && password != null) {
			httpClient.getState().setCredentials(new AuthScope(hostname, port, AuthScope.ANY_REALM), 
					new UsernamePasswordCredentials(username, password));
		}
		String urlPrefix = buildUrlPrefix(hostname, port, contextPath);
		RestTemplate restTemplate = new UrlBasedRestTemplate(urlPrefix, httpRequestFactory);
		
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>(1);
		converters.add(new MappingJacksonHttpMessageConverter());
		restTemplate.setMessageConverters(converters);
		//restTemplate.setMessageConverters(new HttpMessageConverter[] { new MappingJacksonHttpMessageConverter<HashMap<String, List<Map<String, String>>>>() });
		restTemplate.setErrorHandler(new SparkResponseErrorHandler());
		return restTemplate;
	}
	
	/**
	 * 拼装URL前缀.
	 * @param hostname 主机名
	 * @param port 端口
	 * @param contextPath 上下文路径
	 * @return 拼装完成的URL前缀字符串
	 */
	private String buildUrlPrefix(String hostname, int port, String contextPath) {
		String protocol = port == 443 ? "https://" : "http://";
		StringBuilder urlPrefix = new StringBuilder(protocol).append(hostname);
		if (port != 80) {
			urlPrefix.append(":").append(port);
		}
		
		if (StringUtils.hasText(contextPath)) {
			if (!"/".equals(contextPath.substring(0, 1))) {
				urlPrefix.append("/");
			}
			urlPrefix.append(contextPath);
			if ("/".equals(urlPrefix.substring(urlPrefix.length() - 1))) {
				urlPrefix.substring(0, urlPrefix.length() - 1);
			}
		}
		return urlPrefix.toString();

	}

	public Class<? extends RestTemplate> getObjectType() {
		return RestTemplate.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	
}
