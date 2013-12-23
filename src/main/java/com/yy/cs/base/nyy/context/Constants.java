package com.yy.cs.base.nyy.context;

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
	
	public interface Param {
		public static String APPID = "appId";
		public static String DATA = "data";
		public static String SIGN = "sign";
		public static String NYY = "nyy";
	}

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
	 *
	 */
	public static class NyyObject{
		private String appId;
		private String data;
		private String sign;
		public String getAppId() {
			return appId;
		}
		public void setAppId(String appId) {
			this.appId = appId;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
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

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}
