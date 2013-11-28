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
}
