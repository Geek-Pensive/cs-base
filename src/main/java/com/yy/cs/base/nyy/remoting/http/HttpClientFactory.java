package com.yy.cs.base.nyy.remoting.http;

//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
//import org.apache.commons.httpclient.params.HttpClientParams;

import com.yy.cs.base.nyy.common.ConfigUtil;

/**
 * 创建http连接对象管理池
 */
public class HttpClientFactory {
	
//	private final  MultiThreadedHttpConnectionManager httpClientManager; 
//	
//	private final  HttpClient  httpClient;
//	private static HttpClientFactory httpClientFactory;
//	private HttpClientFactory(ConfigUtil params){
//	    httpClientManager = new MultiThreadedHttpConnectionManager();
////	    httpClientManager.getParams().setMaxTotalConnections(100);
////	    httpClientManager.getParams().setDefaultMaxConnectionsPerHost(50);
//	    httpClientManager.getParams().setConnectionTimeout(5000);
//	    httpClientManager.getParams().setSoTimeout(5000);
//	    httpClient = new HttpClient(httpClientManager);
//	    HttpClientParams httpClientParams = httpClient.getParams();
//        httpClientParams.setConnectionManagerTimeout(3000);
//	}
//	
//	public static HttpClient getHttpClient(ConfigUtil params){
//		if(httpClientFactory == null){
//			synchronized (HttpClientFactory.class) {
//				if(httpClientFactory == null){
//					httpClientFactory = new HttpClientFactory(params);
//				}
//			}
//		}
//		return httpClientFactory.httpClient;
//	}
}
