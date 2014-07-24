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
public class CSHttpClientFactory {


	private int maxTotal = 30;
    private int defaultMaxPerRoute = 4;
    private int connectionTimeout = 5000;
    private int socketTimeOut = 5000;
    private int connectionRequestTimeout = 5000;
    
    /**
     * 获取最大CSHttpClient数量
     * @return
     * 		int 最大CSHttpClient数
     */
    public int getMaxTotal() {
        return maxTotal;
    }
    /**
     * 设置最大可生产的SHttpClient数量
     * @param maxTotal
     */
    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    
   /**
    * 返回连接超时时间
    * @return   connectionTimeout time
    */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * 设置连接建立超时时间
     * @param connectionTimeout  连接超时时间
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    /**
     * 获取Socket连接超时时间
     * @return
     * 		连接超时时间
     */	
    public int getSocketTimeOut() {
        return socketTimeOut;
    }

    /**
     * 设置请求超时时间，单位是ms
     * @param socketTimeOut 请求超时时间
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
	 * @param connectionRequestTimeout  请求超时时间
	 */
	public void setConnectionRequestTimeout(int connectionRequestTimeout) {
		this.connectionRequestTimeout = connectionRequestTimeout;
	}
     
}
