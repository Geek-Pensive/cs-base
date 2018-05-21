package com.yy.cs.base.http;

/**
 * Created by zhupeiquan@yy.com on 2018/5/19.
 */
public interface HttpRequestStatisticsInterceptor {

    void handle(HttpRequestStatistics httpRequestStatistics);
}
