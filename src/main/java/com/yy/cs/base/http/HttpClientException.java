package com.yy.cs.base.http;

/**
 * HttpClient异常
 * @author duowan-leedk
 *
 */
public class HttpClientException extends Exception {

    private static final long serialVersionUID = -9095702616213992961L;
    /**
     * 无参构造函数
     */
    public HttpClientException() {
        super();
    }
    /**
     * 
     * @param message  
     * 		异常信息
     */
    public HttpClientException(String message) {
        super(message);
    }
    /**
     * @param cause 
     * 		异常原因
     */
    public HttpClientException(Throwable cause) {
        super(cause);
    }
   /**
    * 
    * @param  message
    * 		异常信息
    * @param  cause
    * 		异常原因
    */
    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
