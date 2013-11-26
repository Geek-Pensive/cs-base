package com.yy.cs.base.nyy.remoting.http;

//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
//import org.apache.commons.httpclient.params.HttpClientParams;

import com.yy.cs.base.http.CSHttpClient;
import com.yy.cs.base.http.CSHttpClientFactory;
import com.yy.cs.base.nyy.common.ConfigUtil;

/**
 * 创建http连接对象管理池
 */
public class HttpClientFactory {
	
	private final  CSHttpClient  httpClient; 
	
	private static HttpClientFactory httpClientFactory;
	private HttpClientFactory(ConfigUtil params){
		CSHttpClientFactory factory = new CSHttpClientFactory();
		httpClient = new CSHttpClient(factory);
	}
	
	public static CSHttpClient getHttpClient(ConfigUtil params){
		if(httpClientFactory == null){
			synchronized (HttpClientFactory.class) {
				if(httpClientFactory == null){
					httpClientFactory = new HttpClientFactory(params);
				}
			}
		}
		return httpClientFactory.httpClient;
	}
}
