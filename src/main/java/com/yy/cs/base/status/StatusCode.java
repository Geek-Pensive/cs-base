package com.yy.cs.base.status;

public enum StatusCode {
	
	SUCCCESS(0),FAIL(1),WRONG(2);
	private int code;
	
	StatusCode(int code){
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
