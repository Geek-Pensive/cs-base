package com.yy.cs.base.nyy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractConfig {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractConfig.class);

    protected String address;
	
	//组成nyyHander的appid
    protected String appId;

	//组成nyyHander的key
    protected String key;

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
    
    
    //应用对key的组合，用户nyy解析端的校验 	<appId,key>
  	//private ConcurrentMap<String,String> nyyHand = new ConcurrentHashMap<String, String>(); 
    
	//public Map<String, String> getNyyHand() {
	//	return Collections.unmodifiableMap(nyyHand);
	//}
	//
	//public void setNyyHand(Map<String, String> nyyHand) {
	//	this.nyyHand.putAll(nyyHand);
	//}
	
	//public String getAppId() {
	//	return appId;
	//}
	//
	//public void setAppId(String appId) {
	//	this.appId = appId;
	//}
	//
	//public String getKey() {
	//	return key;
	//}
	//
	//public void setKey(String key) {
	//	this.key = key;
	//}
}