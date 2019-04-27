package com.baidu.spark.util;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 基于URL的RestTemplate.
 * 扩展于原始的Spring RestTemplate，但增加了默认URL支持
 * 即允许在实例化RestTemplate时注入默认的URL前缀，减少了在应用中调用时写入不必要的前缀.
 * 
 * @author GuoLin
 *
 */
public class UrlBasedRestTemplate extends RestTemplate {
	
	/** URL前缀 */
	private String urlPrefix;
	
	/**
	 * 构造器.
	 * @param urlPrefix URL前缀
	 * @param requestFactory 请求工厂，用于传递给父类构造器
	 */
	public UrlBasedRestTemplate(String urlPrefix, ClientHttpRequestFactory requestFactory) {
		super(requestFactory);
		this.urlPrefix = urlPrefix;
	}

	@Override
	public <T> T execute(String url,
			HttpMethod method,
			RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor,
			Object... urlVariables) throws RestClientException {
		
		// 拼接url
		if (!"/".equals(url.substring(0, 1))) {
			url = "/" + url;
		}
		String realUrl = urlPrefix + url;
		
		return super.execute(realUrl, method, requestCallback, responseExtractor, urlVariables);
	}

	/**
	 * 设定URL前缀.
	 * 用于全局的URL前缀，使得客户端传入URL时可以不必要每次都输入主机名、端口等.
	 * @param urlPrefix URL前缀
	 */
	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}

}
