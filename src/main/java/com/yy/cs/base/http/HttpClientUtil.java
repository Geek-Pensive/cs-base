package com.yy.cs.base.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.yy.cs.base.task.trigger.StringUtils;

/**
 *  HttpClient常用工具类
 */
public class HttpClientUtil {
	
//	 private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);
	
	public static final String HTTP_HEADER_XRP = "X-Real-IP";
	
	public static final String HTTP_HEADER_XFF = "X-Forwarded-For";
	
	public static final String DEFAULT_CHARSET = "UTF-8";
	
	public static final char DEFAULT_EQ = '=';
	
	public static final char DEFAULT_AND = '&';
	
	/**
	 * 通过map对象构造post请求参数。
	 * @param params k-v关系的请求参数。
	 * @return List<NameValuePair>
	 */
    public static List<NameValuePair> toNameValuePairs(Map<String, String> params) {

        ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
        Set<Entry<String, String>> entries = params.entrySet();
        for (Entry<String, String> entry : entries) {

            NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
            list.add(nameValuePair);
        }
        return list;
    }

    /**
	 * 通过map对象构造get请求参数。
	 * @param params k-v关系的请求参数。
	 * @return get请求的QueryString格式的字符串
	 */
    public static String toQueryString(Map<String, String> params) throws UnsupportedEncodingException {

        ArrayList<String> p = new ArrayList<String>();
        for (Entry<String, String> entry : params.entrySet()) {
            p.add(encode(entry.getKey())  + DEFAULT_EQ + encode(entry.getValue()));
        }
        StringBuffer sb = new StringBuffer(); 
        for(int i = 0; i<p.size(); i++){
        	sb.append(p.get(i));
        	if(i != p.size()-1){
        		sb.append(DEFAULT_AND);
        	}
        }
        return sb.toString();
    }
    
    /**
     * 字符串按照utf-8编码
     * @param value 需要编码的字符串
     * @return 编码字符串
     */
    public static String encode(String value) {
        if (value == null || value.length() == 0) { 
            return "";
        }
        try {
            return URLEncoder.encode(value, DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    /**
     * 字符串按照utf-8解码
     * @param value 需要解码的字符串
     * @return 解码字符串
     */
    public static String decode(String value) {
        if (value == null || value.length() == 0) { 
            return "";
        }
        try {
            return URLDecoder.decode(value, DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * 获取request的ip地址
     * <br>
     * 优先级1：从X-Forwarded-For中取
     * <br>
     * 优先级2：从X-Real-IP中取
     * <br>
     * 优先级3：直接获取RemoteAddr
     * @param request HttpServletRequest对象
     * @return ip地址
     */
    public static String getRequestorIp(HttpServletRequest request) {

        // 优先级1：从X-Forwarded-For中取，有被伪造的风险
        // 适用于多级nginx代理的形式
        String xff = request.getHeader(HTTP_HEADER_XFF);
        if (!StringUtils.isEmpty(xff)) {
            if (xff.indexOf(",") < 0) {
                return xff;
            }
            int pos = xff.indexOf(",");
            if (pos == -1) {
                return xff;
            }
            return xff.substring(0, pos);
        }
        
        // 优先级2：从X-Real-IP中取，有多重代理时，这个地址可能不准确，但一般不会被伪造
        String ip = request.getHeader(HTTP_HEADER_XRP);
        if (!StringUtils.isEmpty(ip)) {
            return ip;
        }
        // 优先级3：直接获取RemoteAddr，有反向代理时这个地址会不准确
        return request.getRemoteAddr();
    }
    
}
