package com.yy.cs.base.nyy.config;


/**
 *
 */
public class NyyConfig extends AbstractConfig{
	
	public NyyConfig(String address, String appId, String key) {
		super();
		this.address = address;
		this.appId = appId;
		this.key = key;
	}
	
	public NyyConfig(String address) {
		super();
		this.address = address;
	}
	
	

}
