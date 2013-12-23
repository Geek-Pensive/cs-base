package com.yy.cs.base.nyy.helper;

import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import com.yy.cs.base.json.Json;
import com.yy.cs.base.nyy.context.Constants.Enc;
import com.yy.cs.base.nyy.context.Constants.NyyObject;
import com.yy.cs.base.nyy.context.Constants.Param;
import com.yy.cs.base.nyy.context.Constants.Symbol;
import com.yy.cs.base.thrift.exception.CsNyyRuntimeException;
import com.yy.cs.base.thrift.exception.CsNyySecurityException;

/**
 * nyy协议解析帮忙类
 * @author haoqing
 *
 */
public class NyyProtocolHelper {

	private static String NYY_JSON_FORMAT = "{\"appId\":%s,\"sign\":%s,\"data\":%s}";
	
	/**
	 * 组装nyy协议的get url  
	 * </br>
	 * </br>
	 * withNyyKey为false    http://a.com/abc?appId=1&sign=x&data=%7B%22k1%22:%22v1%22%7D
	 * </br></br>或者</br></br> 
	 *  withNyyKey为true    http://a.com/abc?nyy=appId%3D1%26sign%3Dx%26data%3D%7B%22k1%22%3A%22v1%22%7D
	 * @param uri  比如 http://a.com/abc
	 * @param appId
	 * @param sign
	 * @param data  这里data不需要事先进行encode
	 * @param withNyyKey 是否包含命名为nyy的key 
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String assembleNyyGetUrl(String uri, String appId, String sign, String data, boolean withNyyKey) throws UnsupportedEncodingException {
		String result = null;
		if(withNyyKey){
			String nyyJsonToEncode = String.format(NYY_JSON_FORMAT, appId, sign, data);
			String nyyJsonEncoded = URLEncoder.encode(nyyJsonToEncode, Enc.UTF_8);
			result = uri + Symbol.QUESTION_MARK + Param.NYY + Symbol.EQUAL + nyyJsonEncoded;
		}else{
			data = URLEncoder.encode(data, Enc.UTF_8);
			result = uri + Symbol.QUESTION_MARK + Param.APPID + Symbol.EQUAL + appId + Symbol.AND + Param.SIGN + Symbol.EQUAL + sign + Symbol.AND
					+ Param.DATA + Symbol.EQUAL + data;
		}
		return result;
	}
	
	/**
	 * 从request中获取post的数据</br>
	 * 比如 {"appId":1,"sign":"x","data":{"k1":"v1"}}
	 * @param request
	 * @return
	 */
	public static String getNyyPostContent(HttpServletRequest request){
		if(request == null){
			throw new CsNyySecurityException("request can't be null");
		}
		StringBuilder  sb = new StringBuilder();
		String line = null;
		try{
			BufferedReader br = request.getReader();
			while((line = br.readLine()) != null){
				sb.append(line);
			}
		}catch(Exception e){
			throw new CsNyyRuntimeException("request can't get a bufferedReader or readLine");
		}
		return sb.toString();
	}

	public static <T> T parseDataFromRespJson(String key, String respJson, Class<T> cls, boolean securityCheck){
		if(securityCheck){
			NyyObject nyyObject = Json.strToObj(respJson, NyyObject.class);
			NyySecureHelper.verifySha256Sign(key, nyyObject.getSign(), nyyObject.getData());
			return Json.strToObj(respJson, cls);
		}else{
			return Json.strToObj(respJson, cls);
		}
	}
	
}
