package com.yy.cs.base.nyy;

import java.nio.charset.Charset;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.thrift.exception.CsNyySecurityException;

/**
 * 
 * @author haoqing
 *
 */
public class NyySecureHelper {

	private static Logger LOG = LoggerFactory.getLogger(NyySecureHelper.class);
	
	/**
	 * nyy hash算法采用sha256
	 * </br>验证sign是否符合正确
	 * @param key
	 * @param sign
	 * @param data
	 */
	public static void verifySha256Sign(String key, String sign, String data) {
		String expect = genSha256(key, data);
		if (!sign.equals(expect)) {
			LOG.info("sha256 sign not match! expect=" + expect + ", sign=" + sign + ", data=" + data);
			throw new CsNyySecurityException("sha256 sign not match!");
		}
	}

	/**
	 * nyy hash算法采用sha256
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
