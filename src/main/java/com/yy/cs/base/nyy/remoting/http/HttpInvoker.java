package com.yy.cs.base.nyy.remoting.http;

import java.util.HashMap;
import java.util.Map;

import com.yy.cs.base.http.HttpClientException;
import com.yy.cs.base.http.HttpClientManagerUtil;
import com.yy.cs.base.nyy.common.ConfigUtil;
import com.yy.cs.base.nyy.common.Constants;
import com.yy.cs.base.nyy.exception.NyyException;
import com.yy.cs.base.nyy.proxy.Invocation;
import com.yy.cs.base.nyy.remoting.Invoker;
import com.yy.cs.base.nyy.remoting.RemotingResult;
import com.yy.cs.base.nyy.remoting.Result;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpException;
//import org.apache.commons.httpclient.methods.PostMethod;

/**
 * 一个地址一个invoker
 */

public class HttpInvoker implements Invoker{
	
	
	private final ConfigUtil config;
	
	private final HttpClientManagerUtil  httpClient;
//	
//	
	public HttpInvoker(ConfigUtil config){
		httpClient = HttpClientFactory.getHttpClient(config);
		this.config = config;
	}
//	
	public Result invoke(Invocation invocation) throws NyyException {
		Map<String,String> m = new HashMap<String,String>();
		if(invocation.getAppId() != null){
			m.put(Constants.APPID, invocation.getAppId());
		}
		m.put(Constants.SIGN,invocation.getSign());
		m.put(Constants.DATA,invocation.getData());
		String responseBody = "";
	    try {
	    	responseBody = httpClient.doPost(config.toString(),m);
		}  catch (HttpClientException e) {
			throw new NyyException(e);
		}
		return new RemotingResult(responseBody);
	}

	public ConfigUtil getConfigUtil() {
		return config;
	}

}
