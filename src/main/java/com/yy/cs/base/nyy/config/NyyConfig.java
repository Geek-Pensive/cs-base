package com.yy.cs.base.nyy.config;

import java.util.concurrent.locks.ReentrantLock;

import com.yy.cs.base.nyy.NyyClient;
import com.yy.cs.base.nyy.proxy.ProxyFactory;
import com.yy.cs.base.nyy.remoting.Invoker;
import com.yy.cs.base.nyy.remoting.RemotingFactory;
import com.yy.cs.base.nyy.remoting.RemotingFactory.RemotingFactoryType;

/**
 *
 */
public class NyyConfig extends AbstractConfig{
	
	private ReentrantLock lock = new ReentrantLock();

	private NyyClient nyyClient; 
	
	//public NyyConfig(Map<String, String> nyyHand) {
	//	super();
	//	this.nyyHand.putAll(nyyHand);
	//}
    
//	public NyyConfig(String appId, String key) {
//		super();
//		this.appId = appId;
//		this.key = key;
//	}
	
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
	
	public NyyClient getNyyClient(){
		if(nyyClient == null){
			try{
				lock.lock();
				if(nyyClient == null){
					Invoker invoker = RemotingFactory.getInvoker(RemotingFactoryType.HTTP,address);
					nyyClient = ProxyFactory.getProxy().getProxy(invoker, new Class[]{NyyClient.class},this);
				}
			}finally{
				lock.unlock();	
			}
		}
		return nyyClient;
	}

//	public NyyConfig() {
//		super();
//	}

}
