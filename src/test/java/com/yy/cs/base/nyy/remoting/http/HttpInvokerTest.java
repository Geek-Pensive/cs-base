package com.yy.cs.base.nyy.remoting.http;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.yy.cs.base.nyy.common.ConfigUtil;
import com.yy.cs.base.nyy.exception.NyyException;
import com.yy.cs.base.nyy.proxy.Invocation;
import com.yy.cs.base.nyy.proxy.RemotingInvocation;
import com.yy.cs.base.nyy.remoting.Invoker;
import com.yy.cs.base.nyy.remoting.ProtocolFactory;
import com.yy.cs.base.nyy.remoting.Result;

public class HttpInvokerTest {

	ConfigUtil params = ConfigUtil.valueOf("http://www.baidu.com/");
	
	Invoker invoker; 
	@Before
    public void before() {
		invoker = ProtocolFactory.getInvoker(params);
    }

    @After
    public void after() {
       
    }

     
    @Test
    public void testNyyClient() {
    	Invocation invocation = new RemotingInvocation("","","");
    	try {
    		for(int i = 0;;){
    			
    			Result result = invoker.invoke(invocation);
    			System.out.println(++i);
    		}
//    		String str = (String) result.recreate();
//    		Assert.assertNotNull(str);
		} catch (NyyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	synchronized(HttpInvokerTest.class){
    		try {
    			for(;;){
    				HttpInvokerTest.class.wait();	
    			}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
}
