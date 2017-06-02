package com.yy.cs.base.http;

import com.yy.cs.base.status.LogLevel;

/**
 *
 * http连接的配置对象
 *
 * @param maxTotal 池中的最大连接数
 * @param defaultMaxPerRoute HttpClient中每个远程host最大连接数,一个host可能有多个连接
 * @param connectionTimeout 建立http连接的超时时间
 * @param socketTimeOut socket读取的超时时间（0为无限）
 * @param connectionRequestTimeout 从连接池获取连接的超时时间
 * @param connectionTimeToLive 连接池的长链接最大存活时间
 *
 */
public class CSHttpClientFactory {

    private int maxTotal = 30;
    private int defaultMaxPerRoute = 4;
    private int connectionTimeout = 5000;
    private int socketTimeOut = 5000;
    private int connectionRequestTimeout = 5000;
    private int connectionTimeToLive = -1;
    private LogLevel logLevel = LogLevel.ERROR;

    /**
     * 连接池的长链接最大存活时间
     *
     * @return
     *         int 单位：毫秒
     */
    public int getConnectionTimeToLive() {
        return connectionTimeToLive;
    }

    /**
     * 连接池的长链接最大存活时间
     *
     * @param connectionTimeToLive 单位：毫秒 , -1表示不限制
     */
    public void setConnectionTimeToLive(int connectionTimeToLive) {
        this.connectionTimeToLive = connectionTimeToLive;
    }

    /**
     * 获取最大CSHttpClient数量
     *
     * @return
     *         int 最大CSHttpClient数
     */
    public int getMaxTotal() {
        return maxTotal;
    }

    /**
     * 设置最大可生产的SHttpClient数量
     *
     * @param maxTotal
     */
    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    /**
     * 返回连接超时时间
     *
     * @return connectionTimeout time
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * 设置连接建立超时时间
     *
     * @param connectionTimeout 连接超时时间
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * 获取Socket连接超时时间
     *
     * @return
     *         连接超时时间
     */
    public int getSocketTimeOut() {
        return socketTimeOut;
    }

    /**
     * 设置请求超时时间，单位是ms
     *
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
     *
     * @param connectionRequestTimeout 请求超时时间
     */
    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    /**
     *
     * @return
     */
    public LogLevel getLogLevel() {
        return logLevel;
    }

    /**
     * 请求失败时打印日志的错误级别，默认ERROR
     *
     * @param LogLevel 错误级别
     */
    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

}
