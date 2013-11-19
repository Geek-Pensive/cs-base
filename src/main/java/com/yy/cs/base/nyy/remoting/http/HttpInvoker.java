package com.yy.cs.base.nyy.remoting.http;

import java.io.IOException;

//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpException;
//import org.apache.commons.httpclient.methods.PostMethod;

import com.yy.cs.base.nyy.common.ConfigUtil;
import com.yy.cs.base.nyy.common.Constants;
import com.yy.cs.base.nyy.exception.NyyException;
import com.yy.cs.base.nyy.proxy.Invocation;
import com.yy.cs.base.nyy.remoting.Invoker;
import com.yy.cs.base.nyy.remoting.RemotingResult;
import com.yy.cs.base.nyy.remoting.Result;

/**
 * 一个地址一个invoker
 */

public class HttpInvoker implements Invoker{
	
	
	private final ConfigUtil config;
	
//	private final HttpClient httpClient;
//	
//	
	public HttpInvoker(ConfigUtil config){
//		httpClient = HttpClientFactory.getHttpClient(config);
		this.config = config;
	}
//	
	public Result invoke(Invocation invocation) throws NyyException {
//		PostMethod post = new PostMethod(this.config.toString());
//		if(invocation.getAppId() != null){
//			post.addParameter(Constants.APPID,invocation.getAppId());
//		}
//		post.addParameter(Constants.SIGN,invocation.getSign());
//		post.addParameter(Constants.DATA,invocation.getData());
//		String responseBody = "";
//	    try {
//			httpClient.executeMethod(post);
//			responseBody = post.getResponseBodyAsString();
//		} catch (HttpException e) {
//			throw new NyyException(e);
//		} catch (IOException e) {
//			throw new NyyException(e);
//		}
//		return new RemotingResult(responseBody);
		return null;
	}

	public ConfigUtil getConfigUtil() {
		return config;
	}

}
