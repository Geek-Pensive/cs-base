package com.yy.cs.base.nyy.proxy;

import java.io.Serializable;


public class RemotingInvocation implements Invocation, Serializable {

    private static final long serialVersionUID = -4355285085441097045L;

    private String appId;
    
    private String sign;
    
    private String data;

    public RemotingInvocation() {
    }

    public RemotingInvocation(String appId,String sign) {
        this(appId, sign, "");
    }
    
    
    public RemotingInvocation(String appId,String sign,String data) {
    	 this.appId = appId;
         this.sign = sign;
         this.data = data;
    }
    
    public RemotingInvocation(String data) {
        this.data = data;
    }
    
	public String getAppId() {
		return this.appId;
	}

	public String getSign() {
		return this.sign;
	}

	public String getData() {
		return this.data;
	}
   


}