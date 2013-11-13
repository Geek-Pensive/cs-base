package com.yy.cs.base.nyy.proxy;

import com.yy.cs.base.nyy.proxy.jdk.JDKProxy;

	
public class ProxyFactory {

	private ProxyFactory(){
	}
	public static Proxy  getProxy(){
		return new JDKProxy();
	}


}
