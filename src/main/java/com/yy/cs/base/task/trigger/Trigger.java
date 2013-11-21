package com.yy.cs.base.task.trigger;

import java.util.Date;

import com.yy.cs.base.task.context.TaskContext;


public interface Trigger {

	Date nextExecutionTime(TaskContext triggerContext);

}
