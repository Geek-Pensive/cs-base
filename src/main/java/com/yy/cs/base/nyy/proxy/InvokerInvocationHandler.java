package com.yy.cs.base.nyy.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.yy.cs.base.nyy.common.ConfigUtil;
import com.yy.cs.base.nyy.common.Constants;
import com.yy.cs.base.nyy.common.JacksonUtils;
import com.yy.cs.base.nyy.common.NyyUtils;
import com.yy.cs.base.nyy.remoting.Invoker;


public class InvokerInvocationHandler  implements InvocationHandler {
	
	private final Invoker  invoker;
	
	
    public InvokerInvocationHandler(Invoker invoker){
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
        ConfigUtil config = invoker.getConfigUtil();
        String data = ""; 
        if(args != null && args.length == 1 ){
        	data = JacksonUtils.toJson(args[0]);
        }else if(args != null && args.length > 1){
        	data = JacksonUtils.toJson(args);
        }
        String sign = NyyUtils.getSign(config.getParamete(Constants.KEY),
				data);
        data = ConfigUtil.encode(data);
		return invoker.invoke(
				new RemotingInvocation(config.getParamete(Constants.APPID),
						sign, data)).recreate();
    }

    
}
