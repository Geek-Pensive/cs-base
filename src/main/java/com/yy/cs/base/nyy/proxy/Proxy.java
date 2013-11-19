package com.yy.cs.base.nyy.proxy;

import com.yy.cs.base.nyy.remoting.Invoker;

public interface Proxy {
	
	  <T> T getProxy(Invoker invoker, Class<?>[] interfaces);
}
