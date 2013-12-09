package com.yy.cs.base.thrift.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.yy.cs.base.thrift.invoker.Invoker;


public class ThriftInvocationHandler  implements InvocationHandler {
	
	
	private final Invoker invoker;
	
    public ThriftInvocationHandler(Invoker invoker){
        this.invoker = invoker;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    	String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return invoker.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return invoker.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return invoker.equals(args[0]);
        }
        return invoker.invoke(new Invocation(method, args));
    }

}
