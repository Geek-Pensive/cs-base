package com.yy.cs.base.nyy;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.yy.cs.base.nyy.config.NyyConfig;



public class NyyTest {
	
	
	NyyClient nyyClient;
	@Before
    public void before() {
		NyyConfig nyyConfig  = new NyyConfig("http://127.0.0.1:8080/TestNyy/","appId","key") ;
		nyyClient = nyyConfig.getNyyClient();
    }

    @After
    public void after() {
       
    }

     
    @Test
    public void testNyyClient() {
    	Map<String,String> m = new HashMap<String,String>();
    	m.put("test", "te阿斯顿达大厦st");
    	System.out.println(nyyClient.send(m));;
    }
}
