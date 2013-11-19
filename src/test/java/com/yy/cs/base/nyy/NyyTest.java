package com.yy.cs.base.nyy;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.yy.cs.base.nyy.config.NyyClientFactory;
import com.yy.cs.base.nyy.config.NyyConfig;



public class NyyTest {
	
	
	NyyClient nyyClient;
	@Before
    public void before() {
		NyyConfig c = new NyyConfig("http://www.baidu.com/","appId","key");
		NyyClientFactory fa = new NyyClientFactory();
		nyyClient = fa.getNyyClient(c);
    }
	/**
	 * 
	 */
    @After
    public void after() {
       
    }

     
    @Test
    public void testHttpNyyClient() {
    	Map<String,String> m = new HashMap<String,String>();
    	m.put("test", "te阿斯顿达大厦st");
    	System.out.println(nyyClient.send(m));;
    }
    @Test
    public void testThrithNyyClient() {
    	NyyConfig c = new NyyConfig("thrift://127.0.0.1:8181");
		NyyClientFactory fa = new NyyClientFactory();
		nyyClient = fa.getNyyClient(c);
    	Map<String,String> m = new HashMap<String,String>();
    	m.put("test", "test");
    	nyyClient.send(m);
    	System.out.println();;
    }
}
