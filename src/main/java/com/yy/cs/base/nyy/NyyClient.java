package com.yy.cs.base.nyy;

import com.yy.cs.base.http.CSHttpClient;

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
	


}
