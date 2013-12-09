package com.yy.cs.base.thrift.exception;

/**
 * 
 * @author haoqing
 *
 */
public class CsThriftRuntimeException extends RuntimeException{
	
	private static final long serialVersionUID = 3100326186979666730L;

	public CsThriftRuntimeException() {
        super();
    }

    public CsThriftRuntimeException(String message) {
        super(message);
    }
    
    public CsThriftRuntimeException(Throwable cause) {
        super(cause);
    }

    public CsThriftRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
