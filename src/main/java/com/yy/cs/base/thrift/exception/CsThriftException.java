package com.yy.cs.base.thrift.exception;

public class CsThriftException extends Exception {

    private static final long serialVersionUID = -9095702616213992961L;

    public CsThriftException() {
        super();
    }

    public CsThriftException(String message) {
        super(message);
    }
    
    public CsThriftException(Throwable cause) {
        super(cause);
    }

    public CsThriftException(String message, Throwable cause) {
        super(message, cause);
    }

}
