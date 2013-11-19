package com.yy.cs.base.nyy.proxy.jdk;

import com.yy.cs.base.nyy.proxy.InvokerInvocationHandler;
import com.yy.cs.base.nyy.proxy.Proxy;
import com.yy.cs.base.nyy.remoting.Invoker;

public class JDKProxy implements Proxy {

	@SuppressWarnings("unchecked")
	public <T> T getProxy(Invoker invoker, Class<?>[] interfaces) {
		return (T) java.lang.reflect.Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), interfaces,
				new InvokerInvocationHandler(invoker));
	}

	
}
