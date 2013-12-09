package com.yy.cs.base.thrift.protocol;

import org.apache.commons.pool.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.pooling.IObjectPoolManager;
import com.yy.cs.base.thrift.proxy.Invocation;
import com.yy.cs.base.thrift.transport.Client;

 

public class Invoker  {
	

	private static Logger log = LoggerFactory.getLogger(ClientInvoker.class);
	
	private final IObjectPoolManager<? extends ObjectPool, Client> objPoolMgr;
	
	public Invoker(IObjectPoolManager<? extends ObjectPool, Client> objPoolMgr){
		this.objPoolMgr = objPoolMgr;
	}
 

	public Object invoke(final Invocation invocation) throws Exception {
		Client client = null; 
		try{
			client = objPoolMgr.borrowObject();
	    	return invocation.getMethod().invoke(client.thriftClinet(), invocation.getParameters());
    	} catch (Exception e) {
    		if(client != null){
    			objPoolMgr.invalidateObject(client);
			}
			log.error(e.getMessage(),e);
			throw e;
		}  finally{
			if(client != null){
				objPoolMgr.returnObject(client);
			}
    	}
	}
	 
}
