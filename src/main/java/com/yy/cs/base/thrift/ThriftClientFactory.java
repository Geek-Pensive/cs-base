package com.yy.cs.base.thrift;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.pooling.InitalizableObjectPool;
import com.duowan.pooling.impl.InspectableBalanceController;
import com.duowan.pooling.impl.WeightedObjectPoolManager;
import com.yy.cs.base.thrift.protocol.Invoker;
import com.yy.cs.base.thrift.protocol.InvokerFacoty;
import com.yy.cs.base.thrift.proxy.JDKProxy;
import com.yy.cs.base.thrift.transport.Client;
import com.yy.cs.base.thrift.transport.ClientFactory;


public class ThriftClientFactory<T> {
	
	private static Logger log = LoggerFactory.getLogger(ThriftClientFactory.class);
	
	private String protocol;
	
	private String transport;
	
	private JDKProxy proxy;
	
	private Class<?> interfaceClass;
	
	private String interfaceName;
	
	private WeightedObjectPoolManager<InitalizableObjectPool, Client> mgr;
	
	private T t;
	
	private List<ThriftConfig> thriftConfig;
	
	public synchronized T getClient() {
		if (t == null) {
			init();
			Invoker invoker = InvokerFacoty.getInvoker(mgr);
			t = proxy.getProxy(invoker, interfaceClass);
		}
		return t;
	}
	
	private void init(){
		checkConfig();
		checkInterfaces();
		proxy = new JDKProxy();	
		InspectableBalanceController<InitalizableObjectPool> ctrl = new InspectableBalanceController<InitalizableObjectPool>(1000);
		mgr = new WeightedObjectPoolManager<InitalizableObjectPool, Client>(ctrl);
		int poolNum = 0;
		for(ThriftConfig tc : thriftConfig){
			InitalizableObjectPool pool = new InitalizableObjectPool(new ClientFactory(tc,interfaceClass),tc);
			mgr.addPool( tc.getHost() + tc.getPort() + "-" + ++poolNum, pool, tc.getWeight());
		}
	}
	
	private void checkConfig(){
		if(thriftConfig == null || thriftConfig.size() == 0){
			log.error("thriftConfig == null ");
			throw new IllegalStateException("thriftConfig == null ");
		}
	}
	
	private void checkInterfaces(){
		if(getInterface() == null){
			log.error("interfaces == null");
			throw new IllegalStateException("interfaces == null");
		}
	}
	
	/**
	 * 关闭所有管理的池
	 */
	public void destroy(){
		if (mgr != null){
			mgr.close();
		}
		t = null;
		mgr = null;
    }
	
	
	public String getProtocol() {
		return protocol;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}
	
	
	public void setInterface(String interfaceName) {
        this.interfaceName = interfaceName;
	}

	public void setInterface(Class<?> interfaceClass) {
		if (interfaceClass != null && !interfaceClass.isInterface()) {
			throw new IllegalStateException("The interface class "
					+ interfaceClass + " is not a interface!");
		}
		this.interfaceClass = interfaceClass;
		this.interfaceName = interfaceClass == null ? null : interfaceClass
				.getName();
	}
	
	
	public Class<?> getInterface() {
		if (interfaceClass != null) {
	        return interfaceClass;
	    }
	    try {
	        if (interfaceName != null && interfaceName.length() > 0) {
	            this.interfaceClass = Class.forName(interfaceName);
	        }
	    } catch (ClassNotFoundException t) {
	        throw new IllegalStateException(t.getMessage(), t);
	    }
	    return interfaceClass;
	}
	
	
	public String getInterfaceName() {
		return interfaceName;
	}
	
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	
	
	public List<ThriftConfig> getThriftConfig() {
		if(thriftConfig == null){
			thriftConfig = new ArrayList<ThriftConfig>();
		}
		return thriftConfig;
	}
	
	public void setThriftConfig(List<ThriftConfig> thriftConfig) {
		this.thriftConfig = thriftConfig;
	}
}
