package com.yy.cs.base.http;



/**
 * 
 * http连接的配置对象
 * 
 * @param maxTotal  池中的最大连接数
 * @param defaultMaxPerRoute HttpClient中每个远程host最大连接数,一个host可能有多个连接
 * @param connectionTimeout 建立http连接的超时时间
 * @param socketTimeOut socket读取的超时时间（0为无限）
 * @param connectionRequestTimeout 从连接池获取连接的超时时间
 *
 */
public class HttpClientConfig {

	private int maxTotal = 30;
    private int defaultMaxPerRoute = 4;
    private int connectionTimeout = 5000;
    private int socketTimeOut = 5000;
    private int connectionRequestTimeout = 2000;
    
    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * 连接建立超时时间
     * @param connectionTimeout
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSocketTimeOut() {
        return socketTimeOut;
    }

    /**
     * 设置请求超时时间，单位是ms
     * @param socketTimeOut
     */
    public void setSocketTimeOut(int socketTimeOut) {
        this.socketTimeOut = socketTimeOut;
    }

    public int getDefaultMaxPerRoute() {
		return defaultMaxPerRoute;
	}

	public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
		this.defaultMaxPerRoute = defaultMaxPerRoute;
	}

	public int getConnectionRequestTimeout() {
		return connectionRequestTimeout;
	}
	/**
	 * 从池中获取连接的超时时间
	 * @param connectionRequestTimeout
	 */
	public void setConnectionRequestTimeout(int connectionRequestTimeout) {
		this.connectionRequestTimeout = connectionRequestTimeout;
	}

    
}