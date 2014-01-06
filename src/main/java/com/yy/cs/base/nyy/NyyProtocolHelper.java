package com.yy.cs.base.nyy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import com.yy.cs.base.json.Json;
import com.yy.cs.base.nyy.Constants.Enc;
import com.yy.cs.base.nyy.Constants.Param;
import com.yy.cs.base.nyy.Constants.Symbol;
import com.yy.cs.base.thrift.exception.CsNyyRuntimeException;
import com.yy.cs.base.thrift.exception.CsNyySecurityException;

/**
 * nyy协议解析帮忙类
 * 
 * @author haoqing
 * 
 */
public class NyyProtocolHelper {

	private static String NYY_JSON_FORMAT = "{\"appId\":\"%s\",\"sign\":\"%s\",\"data\":%s}";

	private static Pattern appIdPattern = Pattern.compile("\"appId\":\"(.*?)\"");

	private static Pattern signPattern = Pattern.compile("\"sign\":\"(.*?)\"");

	/**
	 * 组装nyy协议的get url </br> </br> withNyyKey为false
	 * http://a.com/abc?appId=1&sign=x&data=%7B%22k1%22:%22v1%22%7D
	 * </br></br>或者</br></br> withNyyKey为true
	 * http://a.com/abc?nyy=appId%3D1%26sign
	 * %3Dx%26data%3D%7B%22k1%22%3A%22v1%22%7D
	 * 
	 * @param uri
	 *            比如 http://a.com/abc
	 * @param appId
	 * @param sign
	 * @param data
	 *            这里data不需要事先进行encode
	 * @param withNyyKey
	 *            是否包含命名为nyy的key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	protected static String assembleNyyGetUrl(String uri, String appId, String sign, String data, boolean withNyyKey)
			throws UnsupportedEncodingException {
		String result = null;
		if (withNyyKey) {
			String nyyJsonToEncode = String.format(NYY_JSON_FORMAT, appId, sign, data);
			String nyyJsonEncoded = URLEncoder.encode(nyyJsonToEncode, Enc.UTF_8);
			result = uri + Symbol.QUESTION_MARK + Param.NYY + Symbol.EQUAL + nyyJsonEncoded;
		} else {
			data = URLEncoder.encode(data, Enc.UTF_8);
			result = uri + Symbol.QUESTION_MARK + Param.APPID + Symbol.EQUAL + appId + Symbol.AND + Param.SIGN + Symbol.EQUAL + sign + Symbol.AND
					+ Param.DATA + Symbol.EQUAL + data;
		}
		return result;
	}

	/**
	 * 从request中获取post的数据</br> 比如 {"appId":1,"sign":"x","data":{"k1":"v1"}}
	 * 
	 * @param request
	 * @return
	 */
	protected static String getNyyPostMethodContent(HttpServletRequest request) {
		if (request == null) {
			throw new CsNyySecurityException("request can't be null");
		}
		StringBuilder sb = new StringBuilder();
		String line = null;
		BufferedReader br = null;
		try {
			br = request.getReader();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			throw new CsNyyRuntimeException("request can't get a bufferedReader or readLine", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 从request中获取get的数据</br> 兼容携带nyy参数或者不带nyy参数 最终返回类似
	 * {"appId":1,"sign":"x","data":{"k1":"v1"}}
	 * 
	 * @param request
	 * @return
	 */
	protected static String getNyyGetMethodContent(HttpServletRequest request) {
		if (request == null) {
			throw new CsNyySecurityException("request can't be null");
		}
		String nyyValue = request.getParameter(Constants.Param.NYY);
		// if not contains nyy
		if (nyyValue == null || nyyValue.isEmpty()) {
			String appId = request.getParameter(Constants.Param.APPID);
			String sign = request.getParameter(Constants.Param.SIGN);
			String data = request.getParameter(Constants.Param.DATA);
			nyyValue = String.format(NYY_JSON_FORMAT, appId, sign, data);
		}
		return nyyValue;
	}

	/**
	 * 获取nyy内容 </br>
	 * 支持get 或者 post的nyy协议
	 * @param request
	 * @return 类似 {"appId":1,"sign":"x","data":{"k1":"v1"}}
	 *         </br>如果method不是post或者get,则返回null
	 */
	public static String getNyyContent(HttpServletRequest request) {
		String content = null;
		// post
		if (HttpPost.METHOD_NAME.equals(request.getMethod())) {
			content = getNyyPostMethodContent(request);
			// get
		} else if (HttpGet.METHOD_NAME.equals(request.getMethod())) {
			content = getNyyGetMethodContent(request);
		}
		return content;
	}

	/**
	 * 从返回的json字符串中获取data字段
	 * 
	 * @param key
	 * @param respJson
	 * @param cls
	 * @param sha256HashCheck
	 * @return
	 */
	protected static <T> T parseDataFromRespJson(String key, String respJson, Class<T> cls, boolean sha256HashCheck) {
		if (sha256HashCheck) {
			sha256HashSecurityCheck(key, respJson);
			respJson = parseNyyJsonStr(respJson, Param.DATA);
			return Json.strToObj(respJson, cls);
		} else {
			respJson = parseNyyJsonStr(respJson, Param.DATA);
			return Json.strToObj(respJson, cls);
		}
	}

	/**
	 * sha256算法的hash校验</br> 如果验证不通过,将抛出 CsNyySecurityException 的
	 * RuntimeException
	 * 
	 * @param key   业务的key,如果不需要key,则需为""
	 * @param jsonStr  类似 {"appId":1,"sign":"x","data":{"k1":"v1"}}
	 */
	public static void sha256HashSecurityCheck(String key, String jsonStr) {
		String data = parseNyyJsonStr(jsonStr, Param.DATA);
		String sign = parseNyyJsonStr(jsonStr, Param.SIGN);
		if(key  == null){
			key = "";
		}
		NyySecureHelper.verifySha256Sign(key, sign, data);
	}

	/**
	 * 正则匹配对应的字段
	 * 
	 * @param nyyJsonStr
	 *            json格式的nyy字符串
	 * @param param
	 *            要匹配的字段，比如Constants.Param.APPID Constants.Param.SIGN Constants.Param.DATA
	 * @return
	 */
	public static String parseNyyJsonStr(String nyyJsonStr, String param) {
		String paramValue = null;
		if (Constants.Param.APPID.equals(param)) {
			Matcher m = appIdPattern.matcher(nyyJsonStr);
			if (m.find()) {
				paramValue = m.group(1);
			}
		} else if (Constants.Param.DATA.equals(param)) {
			paramValue = parseDataFromNyyJsonStr(nyyJsonStr);
		} else if (Constants.Param.SIGN.equals(param)) {
			Matcher m = signPattern.matcher(nyyJsonStr);
			if (m.find()) {
				paramValue = m.group(1);
			}
		}
		return paramValue;
	}

	/**
	 * 生成类似 {"appId":1,"sign":"x","data":{"k1":"v1"}} 返回给client
	 * 
	 * @param appId
	 * @param key  业务的key,如果不需要key,则需为""
	 * @param data  json格式的data
	 * @return
	 */
	public static String genRespJson(String appId, String key, String data) {
		if(key == null){
			key = "";
		}
		String sign = NyySecureHelper.genSha256(key, data);
		String resp = String.format(NYY_JSON_FORMAT, appId, sign, data);
		return resp;
	}

	/**
	 *  {"appId":"123","sign":"thisissign","data":{"k1":"v1","k2":"v2"}} 获取 data字段内容 {"k1":"v1","k2":"v2"} 
	 * @param str
	 * @return
	 */
	 protected static String parseDataFromNyyJsonStr(String str){
		str = str.substring(str.indexOf("{")+1);
	    str = str.substring(0,str.lastIndexOf("}"));
	    str = str.substring(str.indexOf("{"));
	    str = str.substring(0,str.lastIndexOf("}") +1 );
	    return str;
	 }
	 
	 protected static String formatNyyJson(String appId, String sign, String data){
		 return String.format(NYY_JSON_FORMAT, appId, sign, data);
	 }
}
