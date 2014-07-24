package com.yy.cs.base.task.trigger;

import java.util.Date;

import com.yy.cs.base.task.context.TaskContext;


public interface Trigger {
	/**
	 * 下一次任务执行的时间
	 * @param triggerContext
	 * @return
	 * 		Date 执行时间
	 */
	Date nextExecutionTime(TaskContext triggerContext);

}
