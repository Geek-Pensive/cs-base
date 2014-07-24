package com.yy.cs.base.nyy;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 
 * @author haoqing
 *
 */
public class Constants {

	@JsonView({NyyObject.class})
	private String appId;
	
	@JsonView({NyyObject.class})
	private String data;
	
	@JsonView({NyyObject.class})
	private String sign;
	
	public Constants(){
	}
	
	public Constants(String appId, String sign, String data){
		this.appId = appId;
		this.sign = sign;
		this.data = data;
	}
	/**
	 * nyy协议中参数的字段
	 * @author duowan-PC
	 *
	 */
	public interface Param {
		public static String APPID = "appId";
		public static String DATA = "data";
		public static String SIGN = "sign";
		public static String NYY = "nyy";
	}
	/**
	 * url中常用的分隔符号
	 * @author duowan-PC
	 *
	 */
	public interface Symbol {
		public static String SLASH = "/";
		public static String QUESTION_MARK = "?";
		public static String AND = "&";
		public static String EQUAL = "=";
	}
	
	public interface Enc {
		public static String UTF_8 = "utf-8";
	}
	
	/**
	 * 包含 appId key sign 三个字段
	 * @author haoqing
	 * @param <T>
	 *
	 */
	public static class NyyObject<T>{
		private String appId;
		private T data;
		private String sign;
		public String getAppId() {
			return appId;
		}
		public void setAppId(String appId) {
			this.appId = appId;
		}
		public T getData() {
			return data;
		}
		public void setData(T data) {
			this.data = data;
		}
		public String getSign() {
			return sign;
		}
		public void setSign(String sign) {
			this.sign = sign;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("NyyObject [appId=").append(appId).append(", data=").append(data).append(", sign=").append(sign).append("]");
			return builder.toString();
		}
	}
	/**
	 * 获得业务AppId值
	 * @return
	 * 	    业务AppId
	 */
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getData() {
		return data;
	}
	/**
	 * 设置nyy协议中的data字段的内容
	 * @param data
	 */
	public void setData(String data) {
		this.data = data;
	}
	
	public String getSign() {
		return sign;
	}
	/**
	 * 设置nyy协议中的sign加密信息
	 * @param sign
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}

}
