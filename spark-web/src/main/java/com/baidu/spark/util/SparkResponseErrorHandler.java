package com.baidu.spark.util;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;

import com.baidu.spark.exception.ResponseStatusException;

/**
 * REST Template错误处理器.
 * 
 * @author GuoLin
 * @author shixiaolei
 * 
 */
public class SparkResponseErrorHandler extends DefaultResponseErrorHandler {

	public void handleError(ClientHttpResponse response) throws IOException { 
		HttpStatus statusCode = response.getStatusCode();
		switch (statusCode.series()) {
			case CLIENT_ERROR:
				throw new ResponseStatusException(statusCode);
			case SERVER_ERROR:
				throw new ResponseStatusException(statusCode);
			default:
				throw new RestClientException("Unknown status code [" + statusCode
						+ "]");
		}
	}

}
