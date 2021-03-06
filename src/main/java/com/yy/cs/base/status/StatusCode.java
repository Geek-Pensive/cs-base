package com.yy.cs.base.status;

/**
 * 返回请求状态枚举,主要包括SUCCESS 、FAIL、WRONG三种
 *
 */
public enum StatusCode {
	
	SUCCCESS(0),FAIL(1),WRONG(2);
	private int code;
	
	/**
	 * 状态码
	 * @param code
	 */
	StatusCode(int code){
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
