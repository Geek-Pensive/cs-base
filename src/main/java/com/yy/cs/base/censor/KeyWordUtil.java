package com.yy.cs.base.censor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;

import com.yy.cs.base.http.CSHttpClient;
import com.yy.cs.base.http.HttpClientException;
import com.yy.cs.base.task.thread.NamedThreadFactory;

/**
 *	反低俗内容检查工具类。
 *	调用了的http://do.yy.duowan.com提供的反低俗内容。
 *	5分钟定时刷新缓存内容。
 *
 */
public class KeyWordUtil {

	private Log logger = LogFactory.getLog(this.getClass());
	
	private String HIGH_KEYWORD_LIST_URL = "http://do.yy.duowan.com/HighKWordlist.txt";
	private String NORMAL_KEYWORD_LIST_URL = "http://do.yy.duowan.com/NormalKWordlist.txt";
	private String LOW_KEYWORD_LIST_URL = "http://do.yy.duowan.com/LowKWordlist.txt";
	
	private long interval = 5 * 1000 * 60;
	
	private static Map<KeywordType, String> keywordMap = new HashMap<KeywordType, String>();
	
	private static KeyWordUtil keywordUtil = new  KeyWordUtil();
	
	ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1,new NamedThreadFactory("KeyWordUtil",true));
	
	private final CSHttpClient httpClient;
		
	public static KeyWordUtil getInstance() {
		return keywordUtil;
	}

	private  KeyWordUtil() {
		scheduledExecutor.scheduleAtFixedRate(new Task(), 1000, interval, TimeUnit.MILLISECONDS) ;
		httpClient = new CSHttpClient();
	}
	
	private String getKeyword(KeywordType type) {
		String keyword = keywordMap.get(type);
		return keyword;
	}
	
	/**
	 * 检查关键是否,为反低俗内容关键字
	 * 
	 * @param types 关键字类型
	 * @param text 检查内容
	 * @return 是反低俗内容：true
	 */
	public boolean isCensored(String text, KeywordType[] types) {
		
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
	 * @throws HttpClientException 
	 * @throws IOException 
	 */
	public boolean checkKeyword(String word, KeywordType type) {
		String keywords = getKeyword(type);
		if(keywords == null || "".equals(keywords)){
			autoLoadKeyword();
			keywords = getKeyword(type);
		}
		boolean isKeyword = false;
		String[] keywordArr = String.valueOf(keywords).split("\n");
		String stext = word.trim();
		String keyword = "";
		for (String key : keywordArr) {
			if(key == null ||  key.isEmpty()){
				continue;
			}
			keyword = key.trim();
			if (stext.contains(keyword)) {
				isKeyword = true;
				break;
			}
		}
		return isKeyword;
	}
	
	/**
	 * 检查关键是否,为反低俗内容关键字
	 * 
	 * @param text 检查内容
	 * @return 是反低俗内容：true
	 */
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
		HttpGet get = new HttpGet(txtURL);
		byte[] bytes = httpClient.executeMethodAndReturnByteArray(get);
		if (bytes == null) {
			return null;
		}
		boolean isGzip = matchesGZ(bytes, bytes.length);
		if (isGzip) {
			bytes = unGZip(bytes);
		}
		return decode(bytes);
	}
	
	/**
	 * unGZip解压缩方法
	 * @throws IOException 
	 */
    public byte[] unGZip(byte[] data) throws IOException {
		byte[] b = null;
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		GZIPInputStream gzip = new GZIPInputStream(bis);
		byte[] buf = new byte[1024];
		int num = -1;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((num = gzip.read(buf, 0, buf.length)) != -1) {
			baos.write(buf, 0, num);
		}
		b = baos.toByteArray();
		baos.flush();
		baos.close();
		gzip.close();
		bis.close();
		return b;
    }
    
	 /**
     * Checks if the signature matches what is expected for a .gz file.
     *
     * @param signature the bytes to check
     * @param length    the number of bytes to check
     * @return          true if this is a .gz stream, false otherwise
     *
     */
    private boolean matchesGZ(byte[] signature, int length) {

        if (length < 2) {
            return false;
        }
        if (signature[0] != 31) {
            return false;
        }

        if (signature[1] != -117) {
            return false;
        }

        return true;
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


}
