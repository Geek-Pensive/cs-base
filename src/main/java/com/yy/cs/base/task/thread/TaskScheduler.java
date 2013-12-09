package com.yy.cs.base.task.thread;


import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.yy.cs.base.task.ClusterConfig;
import com.yy.cs.base.task.Task;
import com.yy.cs.base.task.execute.HandlingRunnable;
import com.yy.cs.base.task.trigger.Trigger;
 
public interface TaskScheduler {

	 
	HandlingRunnable localSchedule(Task task, Trigger trigger);

 
	HandlingRunnable clusterSchedule(Task task, Trigger trigger,ClusterConfig config);
	
	ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit);
	
	void shutdown();
}
