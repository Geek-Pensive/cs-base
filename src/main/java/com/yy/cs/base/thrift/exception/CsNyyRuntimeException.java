package com.yy.cs.base.thrift.exception;

/**
 * 针对nyy协议的runtime异常
 * @author haoqing
 *
 */
public class CsNyyRuntimeException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CsNyyRuntimeException(String msg, Throwable cause){
		super(msg,cause);
	}

	public CsNyyRuntimeException(String msg){
		super(msg);
	}
	
	public CsNyyRuntimeException(Throwable cause){
		super(cause);
	}

}
