package com.yy.cs.base.censor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yy.cs.base.http.HttpClientException;
import com.yy.cs.base.http.HttpClientUtil;

public class KeyWordUtil {

	private Log logger = LogFactory.getLog(this.getClass());
	
	private String HIGH_KEYWORD_LIST_URL = "http://do.yy.duowan.com/HighKWordlist.txt";
	private String NORMAL_KEYWORD_LIST_URL = "http://do.yy.duowan.com/NormalKWordlist.txt";
	private String LOW_KEYWORD_LIST_URL = "http://do.yy.duowan.com/LowKWordlist.txt";
	
	private long interval = 5 * 1000l* 60;
	
	private static Map<KeywordType, String> keywordMap = new HashMap<KeywordType, String>();
	
	//private static ExecutorService executor = Executors.newFixedThreadPool(1);
	
	private static KeyWordUtil keywordUtil = new  KeyWordUtil();
	
	 ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

	public static class KeyWordUtilFactory{
		
		public static KeyWordUtil getInstance(){			
			return keywordUtil ; 
		}
	}
	
	private  KeyWordUtil() {
		scheduledExecutor.scheduleAtFixedRate(new Task(), 1000, interval, TimeUnit.MILLISECONDS) ; 
	}
	
	private String getKeyword(KeywordType type) {
		String keyword = keywordMap.get(type);
		return keyword;
	}

	public boolean isCensored(String text, KeywordType... types) {
		for (KeywordType type : types) {
			if (checkKeyword(text, type)) {
				return true;
			}
		}
		return false;
	}
  
	
	
	/**
	 * 检查是否为关键字
	 * 
	 * @param word
	 * @param type
	 * @return 如果是关键字则返回true,否则返回false
	 */
	private boolean checkKeyword(String word, KeywordType type) {
		String keywords = getKeyword(type);
		boolean isKeyword = false;
		String[] keywordArr = String.valueOf(keywords).split("\n");
		String stext = word.trim();
		String keyword = "";
		for (String key : keywordArr) {
			if(key == null ||  key.isEmpty()){
				continue;
			}
			if (stext.contains(keyword)) {
				isKeyword = true;
				break;
			}
		}
		// logger.debug("isKeyword(" + stext + ") return " + isKeyword +
		// "   keyword:" + keyword);
		return isKeyword;
	}

	public boolean isCensored(String text) {
		return isCensored(text, KeywordType.values());
	}

	/**
	 * 从固定数据分流服务器获取关键字
	 * 
	 * @param type
	 * @return
	 * @throws IOException
	 * @throws HttpClientException
	 */

	private String loadKeyword(KeywordType type) throws IOException,
			HttpClientException {
		String txtURL = getKeywordURL(type);
		byte[] bytes =HttpClientUtil.getUrlAsBytes(txtURL);
		//byte[] bytes =HttpUtil.getUrlAsString(txtURL).getBytes();
		if (bytes == null) {
			return null;
		}
		boolean isGzip = GZIPUtil.matches(bytes, bytes.length);
		if (isGzip) {
			bytes = GZIPUtil.unGZip(bytes);
		}
		return decode(bytes);
	}

	/**
	 * 对加密过的字符进行解码
	 * 
	 * @param source
	 * @return
	 * @throws IOException
	 */
	private String decode(byte[] bytes) throws IOException {
		byte[] temp = Base64.decodeBase64(bytes);
		String result = new String(temp, "utf-16");
		return result;
	}

	/**
	 * 根据敏感字类型取得URL
	 * 
	 * @param type
	 * @return
	 */
	private String getKeywordURL(KeywordType type) {
		String url = null;
		switch (type) {
		case HIGH:
			url = HIGH_KEYWORD_LIST_URL;
			break;
		case NORMAL:
			url = NORMAL_KEYWORD_LIST_URL;
			break;
		case LOW:
			url = LOW_KEYWORD_LIST_URL;
			break;
		default:
			logger.warn("illegal KeywordType param !") ; 
			break;
		}
		return url;
	}

	private void autoLoadKeyword() {
		 //long start = System.currentTimeMillis();
		for (KeywordType type : KeywordType.values()) {
			try {
				String keyword = this.loadKeyword(type);
				// logger.info("loading keyword by type:" + type);
				if (keyword != null && ! keyword.isEmpty()) {
					keywordMap.put(type, keyword);
				}
			} catch (Exception e) {
				logger.error("load keyword error,type=[" + type + "] message="
						+ e.getMessage());
			}
		}
		 //long times = System.currentTimeMillis() - start;
		 //logger.info("load keyword finished : " + times + " ms");
		
	}

	private class Task implements Runnable{
		public void run() {
				try {
					autoLoadKeyword();
				} catch (Exception e) {
					logger.error("autoLoadKeyword error!" + e.getMessage());
				}
				
			}
	}
	
	
	public enum KeywordType {
		HIGH(1, "A类敏感字符"), NORMAL(2, "B类敏感字符"), LOW(3, "C类敏感字符");
		private Integer value;
		private String desc;

		public Integer getValue() {
			return value;
		}

		public String getDesc() {
			return desc;
		}
		private KeywordType(int value, String desc) {
			this.value = value;
			this.desc = desc;
		}
		/**
		 * 根据代号返回关键字类型对象
		 * @param value
		 *            代号
		 * @return
		 */
		public static KeywordType getKeywordType(Integer value) {
			if (value == null)
				return null;
			for (KeywordType type : KeywordType.values()) {
				if (value.equals(type.value))
					return type;
			}
			return null;
		}

	}

	public static void main(String[] args) {
		KeyWordUtil kw = new KeyWordUtil();
		kw.autoLoadKeyword();
		for (KeywordType key : keywordMap.keySet()) {
			System.out.println("key:" + key + ",length:"
					+ keywordMap.get(key).replace("\n\t", "|"));
		}
		System.out.println(kw.isCensored("色情"));
	}

}
