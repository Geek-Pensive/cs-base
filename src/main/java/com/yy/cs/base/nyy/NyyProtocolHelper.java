package com.yy.cs.base.nyy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.commons.codec.digest.DigestUtils;

import com.yy.cs.base.task.context.Constants.Enc;
import com.yy.cs.base.task.context.Constants.Param;
import com.yy.cs.base.task.context.Constants.Symbol;
import com.yy.cs.base.thrift.exception.CsNyySecurityException;

public class NyyProtocolHelper {

	/**
	 * 组装nyy协议的get url </br> 比如
	 * http://a.com/abc?appId=1&sign=x&data=%7B%22k1%22:%22v1%22%7D
	 * 
	 * @param uri
	 * @param appId
	 * @param sign
	 * @param data
	 *            这里不对data进行encode
	 * @return
	 */
	public static String assembleNyyGetUrl(String uri, String appId, String sign, String data) {
		return uri + Symbol.QUESTION_MARK + Param.APPID + Symbol.EQUAL + appId + Symbol.AND + Param.SIGN + Symbol.EQUAL + sign + Symbol.AND
				+ Param.DATA + Symbol.EQUAL + data;
	}

	/**
	 * encode data 后并组装nyy协议的get url </br> 比如
	 * http://a.com/abc?appId=1&sign=x&data=%7B%22k1%22:%22v1%22%7D
	 * 
	 * @param uri
	 * @param appId
	 * @param sign
	 * @param data
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String encodeDateAssembleNyyGetUrl(String uri, String appId, String sign, String data) throws UnsupportedEncodingException {
		String encodedData = URLEncoder.encode(data, Enc.UTF_8);
		return assembleNyyGetUrl(uri, appId, sign, encodedData);
	}

	/**
	 * 
	 * @param key
	 * @param sign
	 * @param data
	 */
	public static void verifySha256Sign(String key, String sign, String data) {
		String expect = genSha256(key, data);
		if (!sign.equals(expect)) {
			throw new CsNyySecurityException("sha256 sign not match!, expect=" + expect + ", sign=" + sign + ", key=" + key + ", data=" + data);
		}
	}

	/**
	 * 
	 * @param key
	 * @param data
	 * @return
	 */
	public static String genSha256(String key, String data) {
		String strForGen = "data=" + data + "&key=" + key;
		String sign = DigestUtils.sha256Hex(strForGen.getBytes(Charset.forName("UTF-8")));
		return sign;
	}

}
