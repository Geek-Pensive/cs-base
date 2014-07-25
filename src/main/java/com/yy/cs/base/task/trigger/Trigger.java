package com.yy.cs.base.task.trigger;

import java.util.Date;

import com.yy.cs.base.task.context.TaskContext;

/**
 * 
 *  计算下任务下一次执行
 * 
 */
public interface Trigger {
	/**
	 * 下一次任务执行的时间
	 * @param triggerContext  任务执行的上下文
	 * @return
	 * 		Date 执行时间
	 */
	Date nextExecutionTime(TaskContext triggerContext);

}
