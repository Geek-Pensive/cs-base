package com.yy.cs.base.censor;

import com.yy.cs.base.censor.impl.CensorWordsImpl;
import com.yy.cs.base.censor.impl.StandardWordsFilterImpl;
import com.yy.cs.base.http.CSHttpClient;
import com.yy.cs.base.http.HttpClientException;
import com.yy.cs.base.task.thread.NamedThreadFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

/**
 * 从http://do.yy.duowan.com获取检查反低俗关键子列表
 * <br>
 * 缓存了关键字，5分钟定时刷新数据
 */
public class KeyWordUtil {

	private Log logger = LogFactory.getLog(this.getClass());
	
	private String HIGH_KEYWORD_LIST_URL = "http://do.yy.duowan.com/HighKWordlist.txt";
	private String NORMAL_KEYWORD_LIST_URL = "http://do.yy.duowan.com/NormalKWordlist.txt";
	private String LOW_KEYWORD_LIST_URL = "http://do.yy.duowan.com/LowKWordlist.txt";

	private long interval = 5 * 1000 * 60;

	private static Map<KeywordType, CensorWords> keywordMap = new HashMap<KeywordType, CensorWords>();
	
	private static KeyWordUtil keywordUtil = new  KeyWordUtil();
	
	private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1,new NamedThreadFactory("KeyWordUtil",true));
	
	private final CSHttpClient httpClient;
	/**
	 * 获取KeyWordUtil对象实例的方法
	 * @return 
	 * 		KeyWordUtil 
	 */
	public static KeyWordUtil getInstance() {
		return keywordUtil;
	}

	private  KeyWordUtil() {
		scheduledExecutor.scheduleAtFixedRate(new Task(), 1000, interval, TimeUnit.MILLISECONDS) ;
		httpClient = new CSHttpClient();
	}
	
	private CensorWords getKeyword(KeywordType type) {
        CensorWords keyword = keywordMap.get(type);
		return keyword;
	}
	
	/**
	 * 检查关键是否,为反低俗内容关键字
	 * 
	 * @param types 关键字类型
	 * @param text 检查内容
	 * @return 
	 * 		boolean  是反低俗内容返回true，反之返回false
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
	 * 检查是否为关键字是否属于给定的KeywordType类型
	 * 
	 * @param word 关键字
	 * @param type 关键字类型
	 * @return 
	 * 		boolean 如果是关键字则返回true,否则返回false
	 */
	public boolean checkKeyword(String word, KeywordType type) {
        CensorWords cw = getKeyword(type);
		if(cw == null || "".equals(cw)){
			autoLoadKeyword();
            cw = getKeyword(type);
		}
		return cw.isCensor(word);
	}
	
	/**
	 * 检查关键是否为反低俗内容关键字
	 * 
	 * @param text 检查内容
	 * @return boolean 类型
	 */
	public boolean isCensored(String text) {
		return isCensored(text, KeywordType.values());
	}

	/**
	 * 从固定数据分流服务器获取关键字
	 * 
	 * @param type 关键字类型
	 * @return String 获取
	 * @throws IOException
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
	 * 
	 * @param data 压缩的数据内容
	 * @return byte[] 解缩后的数据，返回字节数组
	 * @throws IOException
	 */
	private byte[] unGZip(byte[] data) throws IOException {
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
	 * @param bytes
	 * @return String
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
	 * 		String url
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
                List<String> ls = new LinkedList<String>();
                String[] ar = keyword.split("\n");
                for (String s: ar) {
                    ls.add(s.trim());
                }
                CensorWords cw = CensorWordsImpl.build(ls, new StandardWordsFilterImpl());
				if (keyword != null && ! keyword.isEmpty()) {
					keywordMap.put(type, cw);
				}
			} catch (Exception e) {
				logger.error("load keyword error,type=[" + type + "] message="
						+ e.getMessage());
			}
		}
		 //long times = System.currentTimeMillis() - start;
		 //logger.info("load keyword finished : " + times + " ms");
		
	}
	/**
	 * 执行获取过滤关键字的任务
	 *
	 */
	private class Task implements Runnable{
		public void run() {
				try {
					autoLoadKeyword();
				} catch (Exception e) {
					logger.error("autoLoadKeyword error!" + e.getMessage());
				}
				
			}
	}
	
	/**
	 * 关键字类型
	 *
	 */
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
		 * 		type
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
