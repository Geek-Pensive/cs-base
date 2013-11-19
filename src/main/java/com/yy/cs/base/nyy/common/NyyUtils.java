package com.yy.cs.base.nyy.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NyyUtils {
	
	 private static final Logger LOG = LoggerFactory.getLogger(NyyUtils.class);
	
	private NyyUtils(){}
	
//	public static void main(String[] args) {
//		System.out.println(getSign("key","data"));
//	}
	
	public static String getSign(String key,String data){
		String target = "";
		if(key != null){
			target += key;
		}
		if(data != null){
			target += data;
		}
		return toMD5HexString(target);
	} 
	
	public static String toMD5HexString(final String target) {
		String result = target;
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			result = byteToHexString(md.digest(target.getBytes("utf-8")));
		} catch (NoSuchAlgorithmException e) {
			// rare case, so we won't throw out exception here
			LOG.warn("No MD5 algorithm provided by jre.", e);
		} catch (UnsupportedEncodingException e) {
			// rare case, so we won't throw out exception here
			LOG.warn(
					"Unsupported encoding exception meet when encrypting input to md5",
					e);
		}
		return result;
	}

	public static String byteToHexString(byte[] byteArray) {
        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append(0).append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }
}
