package com.yy.cs.base.nyy.remoting;

import com.yy.cs.base.nyy.remoting.http.HttpInvoker;


public class RemotingFactory {
	
	
	private RemotingFactory(){
		
	}
	public static Invoker getInvoker(RemotingFactoryType type,String address){
		switch (type) {
        case HTTP:
        	return new HttpInvoker(address);
        default:
        	return new HttpInvoker(address);
		}
	}
	
	
	public enum RemotingFactoryType{
		HTTP("http");
		
		private String name = "";
		
		private RemotingFactoryType(String name){
			this.name = name;
		}
		
		public RemotingFactoryType getType(String name){
			for (RemotingFactoryType type : RemotingFactoryType.values()) {
				if (type.getName().equals(name)) {
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
