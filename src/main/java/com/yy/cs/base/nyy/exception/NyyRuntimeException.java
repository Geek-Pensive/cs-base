package com.yy.cs.base.nyy.exception;

/**
 * 
 * @author haoqing
 *
 */
public class NyyRuntimeException extends RuntimeException{
	
	private static final long serialVersionUID = 3100326186979666730L;

	public NyyRuntimeException() {
        super();
    }

    public NyyRuntimeException(String message) {
        super(message);
    }
    
    public NyyRuntimeException(Throwable cause) {
        super(cause);
    }

    public NyyRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
