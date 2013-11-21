package com.yy.cs.base.task.thread;


import com.yy.cs.base.task.Task;
import com.yy.cs.base.task.execute.HandlingRunnable;
import com.yy.cs.base.task.trigger.Trigger;
 
public interface TaskScheduler {

	 
	HandlingRunnable localSchedule(Task task, Trigger trigger);

 
	HandlingRunnable clusterSchedule(Task task, Trigger trigger);

	void shutdown();
}
