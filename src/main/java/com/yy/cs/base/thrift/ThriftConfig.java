package com.yy.cs.base.thrift;

import com.duowan.pooling.PoolConfig;

 


public class ThriftConfig extends PoolConfig{
    
	private String host;
    
    private int port;
    
    private int timeout = 5000;
    
    private int weight = 0;
    

	public ThriftConfig(){
    }
    
    public ThriftConfig(String host, int port) {
		this.host = host;
		this.port = port;
    }

    public ThriftConfig(String host, int port, int timeout) {
		super();
		this.host = host;
		this.port = port;
		this.timeout = timeout;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
