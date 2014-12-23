package com.yy.cs.base.http;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;

public class HttpClientManagerUtilTest {
	
	
	CSHttpClient  http;
	@Before
    public void before() {
		CSHttpClientFactory f = new CSHttpClientFactory();
		http = new CSHttpClient(f);
    }

     
    @Test
    public void testExecuteMethod() throws  HttpClientException {
    	HttpGet get = new HttpGet("http://live.yy.com/api/queryGameliveUser3.php");
    	@SuppressWarnings("deprecation")
        String s = http.executeMethod(get);
    	System.out.println(s);
    	org.junit.Assert.assertNotNull(s); 
    }
    
    
//    @Test
//    public void testGetResponseStream() throws  HttpClientException {
//    	HttpGet get = new HttpGet("http://www.baidu.com/");
//    	String s = http.executeMethod(get);
//    	System.out.println(s);
//    	org.junit.Assert.assertNotNull(s); 
//    }
    
    
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
//    
//    @Test
//    public void testExecuteAndReturnString() throws Exception {
//        HttpGet httpGet = new HttpGet("http://www.baidu.com");
//        String str1 = http.executeMethodAndReturnString(httpGet);
//        System.out.println("str1 :\n" + str1);
//        assertNotNull(str1);
//        
//        HttpPost httpPost = new HttpPost("http://localhost:8080/TestAndStudy/testServlet");
//        String str2 = http.executeMethodAndReturnString(httpPost);
//        System.out.println("str2 :\n" + str2);
//        assertNotNull(str2);
//    }
//    
//    @Test
//    public void testExecuteAndReturnInputStream() throws Exception {
//        HttpGet httpGet = new HttpGet("http://www.baidu.com");
//        assertNotNull(http.executeMethodAndReturnInputStream(httpGet));
//        assertNotNull(http.executeMethodAndReturnInputStream(httpGet,new HashSet<Integer>(200)));
//        
//        HttpPost httpPost = new HttpPost("http://localhost:8080/TestAndStudy/testServlet");
//        assertNotNull(http.executeMethodAndReturnInputStream(httpPost));
//        assertNotNull(http.executeMethodAndReturnInputStream(httpPost,new HashSet<Integer>(200)));
//    }
//    
    @Test
    public void testGetResponseStream() throws Exception {
        assertNotNull(http.getResponseStream("http://localhost:8080/TestAndStudy/testServlet",new int[]{200}));
    }
    
}

