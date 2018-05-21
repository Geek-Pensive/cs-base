package com.yy.cs.base.http;

import org.apache.http.StatusLine;

import java.net.URI;

/**
 * Created by zhupeiquan@yy.com on 2018/5/19.
 */
public class HttpRequestStatistics {

    private URI uri;
    private StatusLine statusLine;
    /** 接口执行所需要的时间，不一定存在 */
    private long spendTime = -1;
    /** 执行请求时抛出的异常 */
    private Exception exception;

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public long getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(long spendTime) {
        this.spendTime = spendTime;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
