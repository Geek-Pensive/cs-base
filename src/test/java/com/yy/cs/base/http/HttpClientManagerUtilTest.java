package com.yy.cs.base.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HttpClientManagerUtilTest {
	
	
	CSHttpClient  http;
	@Before
    public void before() {
		CSHttpClientFactory f = new CSHttpClientFactory();
		http = new CSHttpClient(f);
    }
	/**
	 * 
	 */
    @After
    public void after() {
       
    }

     
    @Test
    public void testExecuteMethod() throws  HttpClientException {
    	HttpGet get = new HttpGet("http://www.baidu.com/");
    	String s = http.executeMethod(get);
    	System.out.println(s);
    	org.junit.Assert.assertNotNull(s); 
    }
    
    
    @Test
    public void testGetResponseStream() throws  HttpClientException {
    	HttpGet get = new HttpGet("http://www.baidu.com/");
    	String s = http.executeMethod(get);
    	System.out.println(s);
    	org.junit.Assert.assertNotNull(s); 
    }
    
    
    @Test
    public void testDoGet() throws  HttpClientException {
    	String s = http.doGet("http://www.baidu.com/");
    	System.out.println(s);
    	org.junit.Assert.assertNotNull(s); 
    }
    
    @Test
    public void testOK() throws  HttpClientException {
    	org.junit.Assert.assertTrue(http.isGetOK("http://www.baidu.com/")); 
    }
    
    @Test
    public void testDoPost() throws  HttpClientException {
    	Map<String,String> m = new HashMap<String,String>();
    	m.put("Email", "dada@gmail.com");
    	m.put("Passwd", "12321312");
    	String s = http.doPost("https://accounts.google.com/ServiceLoginAuth", m);
    	System.out.println(s);
    	org.junit.Assert.assertNotNull(s); 
    }
}
