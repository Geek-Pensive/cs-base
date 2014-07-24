package com.yy.cs.base.task;

import com.yy.cs.base.status.CsStatus;
/**
 * 任务
 * @author duowan-PC
 *
 */
public interface Task {
	
	/**
	 * 执行任务
	 * @throws Exception
	 */
	 void execute() throws Exception;
	/**
	 * 获取执行任务的Id
	 * @return
	 * 		任务Id
	 */
	 String getId();
	 /**
	  * 获取执行返回状态
	  * @return
	  * 	任务状态 {@link CsStatus}
	  */
	 CsStatus getCsStatus();
}
