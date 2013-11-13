package com.yy.cs.base.nyy.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JacksonUtils {
	
    private static final Logger LOG = LoggerFactory.getLogger(JacksonUtils.class);

    private static ObjectMapper mapper = new ObjectMapper();
    static{
    	mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    }
    
    
    /**
     * 通过JSON字符串生成对象
     * 
     * @param json JSON字符串
     * @param type 返回值的类型
     * @return 如果能够封装为指定对象，则返回该值，否则返回null
     */
    public static <T> T fromJson(String json, Class<T> type) {
        T result = null;
        try {
            if (json != null && !"".equals(json)) {
                result = mapper.readValue(json, type);
            }
        } catch (JsonParseException e) {
            LOG.warn("fromJson() with JsonParseException.", e);
        } catch (JsonMappingException e) {
            LOG.warn("fromJson() with JsonMappingException.", e);
        } catch (IOException e) {
            LOG.warn("fromJson() with IOException.", e);
        }
        return result;
    }

    /**
     * 生成JSON字符串.生成字符串会自动进行HTML转义及不可直接被解释.
     * 
     * @param obj 对象实例
     * @return 返回生成的字符串
     */
    public static String toJson(Object obj) {
        String result = null;
        try {
            if (obj != null) {
                result = mapper.writeValueAsString(obj);
            }
        } catch (JsonParseException e) {
            LOG.warn("fromJson() with JsonParseException.", e);
        } catch (JsonMappingException e) {
            LOG.warn("fromJson() with JsonMappingException.", e);
        } catch (IOException e) {
            LOG.warn("fromJson() with IOException.", e);
        }
        return result;
    }
    
    public static void main(String[] args) {
		
    	Map<String,String>  m =  new HashMap<String,String>();
    	m.put("12344", "ASD达大厦 阿斯顿");
    	m.put("12134", "dasdadfa");
    	m.put("12厦 阿斯434", "dasdadfa");
    	m.put("121434", "dasdadfa");
    	m.put("12354", "dasdadfa");
    	m.put("124334", "dasdadfa");
    	m.put("12834", "dasdadfa");
//    	RemotingResult a =new RemotingResult("dadsada12s");
//    	RemotingResult b =new RemotingResult("dadsadas5");
//    	RemotingResult c =new RemotingResult("dadsada4s");
//    	RemotingResult d =new RemotingResult("dadsadas3");
//    	RemotingResult e =new RemotingResult("dadsadas1");
//    	Object[] ob = new Object[]{a};
    	
    	System.out.println(JacksonUtils.toJson(m));
    	
    	
	}
}
