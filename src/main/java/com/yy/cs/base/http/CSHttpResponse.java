package com.yy.cs.base.http;

import org.apache.http.StatusLine;

import java.util.Map;

/**
 * Created by zhupeiquan@yy.com on 2019/4/11.
 */
public class CSHttpResponse {

    private byte[] bytes;
    private Map<String,String> headers;
    private StatusLine statusLine;

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }
}
