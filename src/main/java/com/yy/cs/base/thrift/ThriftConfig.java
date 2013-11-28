package com.yy.cs.base.thrift;
 


public class ThriftConfig {
    
	private String host;
    
    private int port;
    
    private int timeout = 5000;
    
    private int poolSize = 5;
    
    private int weight = 5;
    

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

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
