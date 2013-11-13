package com.yy.cs.base.nyy.exception;

public class NyyException extends Exception {

    private static final long serialVersionUID = -9095702616213992961L;

    public NyyException() {
        super();
    }

    public NyyException(String message) {
        super(message);
    }
    
    public NyyException(Throwable cause) {
        super(cause);
    }

    public NyyException(String message, Throwable cause) {
        super(message, cause);
    }

}
