package com.yy.cs.base.thrift.protocol;

import org.apache.commons.pool.ObjectPool;

import com.duowan.pooling.IObjectPoolManager;
import com.yy.cs.base.thrift.transport.Client;


public class InvokerFacoty {
	
	
	private InvokerFacoty(){
	}
	
	
	public static Invoker getInvoker(IObjectPoolManager<? extends ObjectPool, Client> objPoolMgr){
		return new Invoker(objPoolMgr);
	}
	
	
	 
}
