package com.yy.cs.base.task;

import com.yy.cs.base.status.CsStatus;
import com.yy.cs.base.task.log.TaskBizLogCollect;

/**
 * 
 * 任务接口
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
	 * 获取 quatz 表达式
	 * @return
     */
	 String getCron();

	 /**
	  * 获取当前task的运行状态
	  * @return
	  * 	任务状态 {@link CsStatus}
	  */
	 CsStatus getCsStatus();
	 
	 /**
      * 设置当前task的运行状态
      * @return
      *     任务状态 {@link CsStatus}
      */
	 void setCsStatus(CsStatus csStatus);

	TaskBizLogCollect getBizLogger();
}
