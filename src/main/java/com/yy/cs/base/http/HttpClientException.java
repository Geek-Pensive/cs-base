package com.yy.cs.base.http;

public class HttpClientException extends Exception {

    private static final long serialVersionUID = -9095702616213992961L;

    public HttpClientException() {
        super();
    }

    public HttpClientException(String message) {
        super(message);
    }
    
    public HttpClientException(Throwable cause) {
        super(cause);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
