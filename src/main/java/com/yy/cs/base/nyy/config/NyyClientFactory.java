package com.yy.cs.base.nyy.config;

import com.yy.cs.base.nyy.NyyClient;
import com.yy.cs.base.nyy.common.ConfigUtil;
import com.yy.cs.base.nyy.common.Constants;
import com.yy.cs.base.nyy.proxy.ProxyFactory;
import com.yy.cs.base.nyy.remoting.Invoker;
import com.yy.cs.base.nyy.remoting.ProtocolFactory;

public class NyyClientFactory {
	
	private NyyClient nyyClient;
	
	public synchronized NyyClient getNyyClient(AbstractConfig config){
		if(nyyClient == null){
			Invoker invoker = ProtocolFactory.getInvoker(this.parseConfig(config));
			nyyClient = ProxyFactory.getProxy().getProxy(invoker, new Class[]{NyyClient.class});
		}
		return nyyClient;
	}
	
	private ConfigUtil parseConfig (AbstractConfig c){
		ConfigUtil config = ConfigUtil.valueOf(c.getAddress());
		config.addParameter(Constants.APPID,c.getAppId() );
		config.addParameter(Constants.KEY, c.getKey());
		return config; 
	}
//	private NyyClientFactory(){}
	
	
}
