package com.yy.cs.base.nyy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractConfig {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractConfig.class);

    protected String address;//地址
    
    protected String timeout;//超时时间
    
    protected String appId;//nyy固定参数appid，可以不配置

    protected String key;//nyy固定参数key，可以不配置
    
    public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
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
    
}