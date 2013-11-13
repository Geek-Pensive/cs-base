package com.yy.cs.base.nyy.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.yy.cs.base.nyy.common.JacksonUtils;
import com.yy.cs.base.nyy.common.NyyUtils;
import com.yy.cs.base.nyy.config.AbstractConfig;
import com.yy.cs.base.nyy.remoting.Invoker;


public class InvokerInvocationHandler  implements InvocationHandler {
	
	private final Invoker  invoker;
	private final AbstractConfig config;
	
	
    public InvokerInvocationHandler(Invoker handler,AbstractConfig config){
        this.invoker = handler;
        this.config = config;
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
        String data = ""; 
        if(args != null && args.length == 1 ){
        	data = JacksonUtils.toJson(args[0]);
        }else if(args != null && args.length > 1){
        	data = JacksonUtils.toJson(args);
        }
        return invoker.invoke(new RemotingInvocation(config.getAppId(),NyyUtils.getSign(config.getKey(), data),data)).recreate();
    }

    
}
