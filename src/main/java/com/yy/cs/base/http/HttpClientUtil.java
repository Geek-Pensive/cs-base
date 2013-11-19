package com.yy.cs.base.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class HttpClientUtil {
	
    public static List<NameValuePair> toNameValuePairs(Map<String, String> params) {

        ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
        Set<Entry<String, String>> entries = params.entrySet();
        for (Entry<String, String> entry : entries) {

            NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
            list.add(nameValuePair);
        }
        return list;
    }

    public static String toQueryString(Map<String, String> params) throws UnsupportedEncodingException {

        ArrayList<String> p = new ArrayList<String>();
        for (Entry<String, String> entry : params.entrySet()) {
            p.add(encode(entry.getKey())  + "=" + encode(entry.getValue()));
        }

        return StringUtils.join(p, '&');
    }
    
    public static String encode(String value) {
        if (value == null || value.length() == 0) { 
            return "";
        }
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public static String decode(String value) {
        if (value == null || value.length() == 0) { 
            return "";
        }
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
