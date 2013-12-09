package com.yy.cs.base.thrift.proxy;

import com.yy.cs.base.thrift.invoker.Invoker;


public class JDKProxy{

	@SuppressWarnings("unchecked")
	public <T> T getProxy(Invoker invoker, Class<?> interfaces) {
		return (T) java.lang.reflect.Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), new Class<?>[]{interfaces},
				new ThriftInvocationHandler(invoker));
	}

	
}
