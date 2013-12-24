package com.yy.cs.base.nyy;

import java.io.UnsupportedEncodingException;

import com.yy.cs.base.http.CSHttpClient;
import com.yy.cs.base.http.HttpClientException;

/**
 * 
 * @author haoqing
 *
 */
public class NyyClient {
	
	private static CSHttpClient csHttpClient = new CSHttpClient();

	/**
	 * 业务的id
	 */
	private String appId;

	/**
	 * 与业务约定的key
	 */
	private String key;
	
	public NyyClient(){
	}
	
	public NyyClient(String appId, String key){
		this.key = key;
		this.appId = appId;
	}
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * 
	 * @param uri  要get请求的uri
	 * @param dataJson	nyy协议中的datajson,这里不需要事先encode
	 * @param withNyyKey  是否包含nyy key,是否包含nyy key的区别可见 <a href="http://dev.yypm.com/?post=posts/standard/nyy/nyy.md">http://dev.yypm.com/?post=posts/standard/nyy/nyy.md</a>
	 * @return  http请求后的返回
	 * @throws UnsupportedEncodingException
	 * @throws HttpClientException
	 */
	public String doGet(String uri, String dataJson, boolean withNyyKey) throws UnsupportedEncodingException, HttpClientException{
		String sign = NyySecureHelper.genSha256(key, dataJson);
		String doGetUrl = NyyProtocolHelper.assembleNyyGetUrl(uri, this.appId, sign, dataJson, withNyyKey);
		return csHttpClient.doGet(doGetUrl);
	}
	
	/**
	 * 
	 * @param uri  要post请求的uri
	 * @param dataJson  nyy协议中的 dataJson 
	 * @return    http请求后的返回
	 * @throws HttpClientException
	 */
	public String doPost(String uri, String dataJson) throws HttpClientException{
		String sign = NyySecureHelper.genSha256(key, dataJson);
		String strToPost = NyyProtocolHelper.formatNyyJson(appId, sign, dataJson);
		return csHttpClient.doPost(uri, strToPost);
	}

	/**
	 * 从返回的字符串中获取json格式的data,以pojo的方式返回 
	 * @param respJson  返回的字符串
	 * @param cls   对应pojo类
	 * @param sha256HashSecurityCheck  是否要进行算法为sha256的哈希校验
	 * </br>此方法会抛出runtimeException 
	 * @return
	 */
	public <T> T parseDataFromRespJson(String respJson, Class<T> cls, boolean sha256HashSecurityCheck){
		return NyyProtocolHelper.parseDataFromRespJson(key, respJson, cls, sha256HashSecurityCheck);
	}

}
