package com.yy.cs.base.nyy.remoting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.yy.cs.base.nyy.common.ConfigUtil;
import com.yy.cs.base.nyy.remoting.http.HttpInvoker;
import com.yy.cs.base.nyy.remoting.thrift.ThriftInvoker;

public class ProtocolFactoryTest {
	
	
	@Before
    public void before() {
		
//		NyyConfig nyyConfig  = new NyyConfig("http://127.0.0.1:8080/TestNyy/","appId","key") ;
//		nyyClient = nyyConfig.getNyyClient();
    }

    @After
    public void after() {
       
    }
     
    @Test
    public void testHttpProtocol() {
    	ConfigUtil config = ConfigUtil.valueOf("http://127.0.0.1:8080/NyyTest");
    	assertEquals(ProtocolFactory.getInvoker(config).getClass(),HttpInvoker.class);
    }
    
    @Test
    public void testThriftProtocol() {
    	ConfigUtil config = ConfigUtil.valueOf("thrift://127.0.0.1:8080/NyyTest");
    	assertEquals(ProtocolFactory.getInvoker(config).getClass(),ThriftInvoker.class);
    }
    
    @Test
    public void testErrorProtocol() {
    	ConfigUtil config = ConfigUtil.valueOf("error://127.0.0.1:8080/NyyTest");
    	Exception ex = null;
    	try{
    		ProtocolFactory.getInvoker(config);
    	}catch(Exception e){
    		ex = e;
    	}
    	assertTrue(ex instanceof IllegalStateException);
    }
}
