package com.yy.cs.base.redis;

/**
 * 操作Redis时的运行是异常
 *
 */
public class CsRedisRuntimeException extends RuntimeException{
	
	private static final long serialVersionUID = 3100326186979666730L;

	public CsRedisRuntimeException() {
        super();
    }

    public CsRedisRuntimeException(String message) {
        super(message);
    }
    
    public CsRedisRuntimeException(Throwable cause) {
        super(cause);
    }

    public CsRedisRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
