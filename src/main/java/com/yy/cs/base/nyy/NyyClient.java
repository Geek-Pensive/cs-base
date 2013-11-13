package com.yy.cs.base.nyy;

import java.util.Map;

import com.yy.cs.base.nyy.exception.NyyException;

public interface NyyClient {
	
	
	/**
     * @param message
     * @throws NyyException
     */
    public String send(Map<String,String> message);

    
    /**
     * @param object
     * @param clazz
     * @throws NyyException
     */
    public String send(Object object);
    
    
//    /**
//     * @param string
//     * @throws NyyException
//     */
//    public Map<String,String> parseToMap(String nyy) throws NyyException;
//    
//    
//    /**
//     * @param string
//     * @param clazz
//     * @throws NyyException
//     */
//    public  Object  parseToObject(String nyy,Class<?> clazz) throws NyyException;
}
