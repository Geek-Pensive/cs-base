package com.yy.cs.base.thrift.proxy;

import java.io.Serializable;
import java.lang.reflect.Method;


public class Invocation implements Serializable {

    private static final long serialVersionUID = -4355285085441097045L;
    
    
    private final Method method;
    
    private final Object[] args;
    
    
	public Invocation(Method method, Object[] args) {
		super();
		this.method = method;
		this.args = args;
	}


	public Method getMethod() {
		return method;
	}

	
	public Object[] getParameters() {
		return args;
	}

}