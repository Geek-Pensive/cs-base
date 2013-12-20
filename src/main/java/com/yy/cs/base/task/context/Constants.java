package com.yy.cs.base.task.context;


public class Constants {
	
	private Constants(){}
	
	public final static String  TASK_ID   = "taskId";

	public final static String  NEXT_EXECUTE_TIME = "nextScheduledExecutionTime";    //下次执行时间

	public final static String  LAST_START_TIME = "lastStartTime";		//最近一次执行开始时间

	public final static String  LAST_COMPLETION_TIME = "lastCompletionTime";   //最近一次执行完成时间

	public final static String EXECUTE_ADDRESS = "executeAddress";   //最近一次执行的地址
	 
	public final static String LAST_EXCEPTION_TIME = "lastExceptionTime";   //最近一次异常执行时间

	public final static String  THROWABLE = "throwable";
	
	public final static String  TIMEOUT = "timeout";
	
	public interface Param {
		public static String APPID = "appId";
		public static String DATA = "data";
		public static String SIGN = "sign";
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
	
	
}
