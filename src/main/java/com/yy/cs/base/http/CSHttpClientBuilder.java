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
public class CSHttpClientBuilder {

    private CSHttpClientFactory factory;

    private CSHttpClientBuilder() {
        this.factory = new CSHttpClientFactory();
    }

    public static CSHttpClientBuilder newBuilder() {
        return new CSHttpClientBuilder();
    }

    /**
     * 连接池的长链接最大存活时间
     *
     * @return
     *         int 单位：毫秒
     */
    public int getConnectionTimeToLive() {
        return factory.getConnectionTimeToLive();
    }

    /**
     * 连接池的长链接最大存活时间
     *
     * @param connectionTimeToLive 单位：毫秒 , -1表示不限制
     */
    public CSHttpClientBuilder setConnectionTimeToLive(int connectionTimeToLive) {
        factory.setConnectionTimeToLive(connectionTimeToLive);
        return this;
    }

    /**
     * 获取最大CSHttpClient数量
     *
     * @return
     *         int 最大CSHttpClient数
     */
    public int getMaxTotal() {
        return factory.getMaxTotal();
    }

    /**
     * 设置最大可生产的SHttpClient数量
     *
     * @param maxTotal
     */
    public CSHttpClientBuilder setMaxTotal(int maxTotal) {
        factory.setMaxTotal(maxTotal);
        return this;
    }

    /**
     * 返回连接超时时间
     *
     * @return connectionTimeout time
     */
    public int getConnectionTimeout() {
        return factory.getConnectionTimeout();
    }

    /**
     * 设置连接建立超时时间
     *
     * @param connectionTimeout 连接超时时间
     */
    public CSHttpClientBuilder setConnectionTimeout(int connectionTimeout) {
        factory.setConnectionTimeout(connectionTimeout);
        return this;
    }

    /**
     * 获取Socket连接超时时间
     *
     * @return
     *         连接超时时间
     */
    public int getSocketTimeOut() {
        return factory.getSocketTimeOut();
    }

    /**
     * 设置请求超时时间，单位是ms
     *
     * @param socketTimeOut 请求超时时间
     */
    public CSHttpClientBuilder setSocketTimeOut(int socketTimeOut) {
        factory.setSocketTimeOut(socketTimeOut);
        return this;
    }

    public int getDefaultMaxPerRoute() {
        return factory.getDefaultMaxPerRoute();
    }

    public CSHttpClientBuilder setDefaultMaxPerRoute(int defaultMaxPerRoute) {
        factory.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return this;
    }

    public int getConnectionRequestTimeout() {
        return factory.getConnectionRequestTimeout();
    }

    /**
     * 从池中获取连接的超时时间
     *
     * @param connectionRequestTimeout 请求超时时间
     */
    public CSHttpClientBuilder setConnectionRequestTimeout(int connectionRequestTimeout) {
        factory.setConnectionRequestTimeout(connectionRequestTimeout);
        return this;
    }

    /**
     *
     * @return
     */
    public LogLevel getLogLevel() {
        return factory.getLogLevel();
    }

    /**
     * 请求失败时打印日志的错误级别，默认ERROR
     *
     * @param LogLevel 错误级别
     */
    public CSHttpClientBuilder setLogLevel(LogLevel logLevel) {
        factory.setLogLevel(logLevel);
        return this;
    }

    public boolean isPostRedirect() {
        return factory.isPostRedirect();
    }

    /**
     * 是否根据302重定向，默认false
     *
     * @param postRedirect 是否重定向
     */
    public CSHttpClientBuilder setPostRedirect(boolean postRedirect) {
        factory.setPostRedirect(postRedirect);
        return this;
    }

    public CSHttpClient build() {
        return new CSHttpClient(factory, factory.isPostRedirect());
    }

}
