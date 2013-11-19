package com.yy.cs.base.nyy.remoting;

import com.yy.cs.base.nyy.common.ConfigUtil;
import com.yy.cs.base.nyy.remoting.http.HttpInvoker;
import com.yy.cs.base.nyy.remoting.thrift.ThriftInvoker;


public class ProtocolFactory {
	
	
	private ProtocolFactory(){
		
	}
	public static Invoker getInvoker(ConfigUtil config){
		
		ProtocolType type = ProtocolType.getType(config.getProtocol());
		if(type == null){
			throw new IllegalStateException("Could not find protocol: " + config.getProtocol());
		}
		switch (type) {
        case HTTP:
        	return new HttpInvoker(config);
        case THRIFT:
        	return new ThriftInvoker(config);
        default:
        	throw new IllegalStateException("Could not find protocol: " + config.getProtocol());
		}
	}
	
	
	public enum ProtocolType{
		HTTP("http"),
		THRIFT("thrift");
		private String name = "";
		
		private ProtocolType(String name){
			this.name = name;
		}
		
		public static ProtocolType getType(String name){
			for (ProtocolType type : ProtocolType.values()) {
				if (type.getName().equalsIgnoreCase(name)) {
					return type;
				}
			}
			return null;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	} 
}
