package com.yy.cs.base.nyy;

/**
 * 针对nyy协议的安全异常
 * @author haoqing
 *
 */
public class CsNyySecurityException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CsNyySecurityException(String msg, Throwable cause){
		super(msg,cause);
	}

	public CsNyySecurityException(String msg){
		super(msg);
	}
	
	public CsNyySecurityException(Throwable cause){
		super(cause);
	}

}
