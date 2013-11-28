package com.yy.cs.base.thrift;


public class ThriftClientFactory<T> {

	private ThriftConfig thriftConfig;

	public ThriftConfig getThriftConfig() {
		return thriftConfig;
	}
	
	public void setThriftConfig(ThriftConfig thriftConfig) {
		this.thriftConfig = thriftConfig;
	}
	
	
	private T t;
	
	
	public T  getClient(Class<T> clazz){
		
		return null;
	}

}
