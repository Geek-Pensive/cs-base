package com.yy.cs.base.nyy.common;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigUtilTest {
	
	
	@Before
    public void before() {
		
    }

    @After
    public void after() {
       
    }

     
    @Test
    public void testValueOf() {
        String uri = "http://www.baidu.com/testNyy/test";
        ConfigUtil url = ConfigUtil.valueOf(uri);
        assertEquals("www.baidu.com", url.getAddress());
        assertEquals("testNyy/test", url.getPath());
        assertEquals(0, url.getPort());
        assertEquals("http", url.getProtocol());
        assertEquals("www.baidu.com", url.getHost());
        assertEquals("http://www.baidu.com/testNyy/test",url.toString());
    }
    
    
    
    @Test
    public void testDecode() {
        String str = "测试1234567890zxcvbnmasdfghjklpoiuytrewq-=!@#$%^&*()_+{}\"<:>?";
        System.out.println(ConfigUtil.encode(str));
        assertEquals(str,ConfigUtil.decode(ConfigUtil.encode(str)));
    }

    
    @Test
    public void testEncode() {
    }
}
