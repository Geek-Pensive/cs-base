package com.yy.cs.base.task.thread;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.yy.cs.base.task.ClusterConfig;
import com.yy.cs.base.task.Task;
import com.yy.cs.base.task.execute.ClusterTriggerRunnable;
import com.yy.cs.base.task.execute.HandlingRunnable;
import com.yy.cs.base.task.execute.LocalTriggerRunnable;
import com.yy.cs.base.task.trigger.Trigger;


public class ThreadPoolTaskScheduler implements TaskScheduler  {


	private final ScheduledExecutorService scheduledExecutor;
	
	public  ThreadPoolTaskScheduler() {
		this(1);
	}
	
	public  ThreadPoolTaskScheduler(int poolSize) {
		this.scheduledExecutor =  new ScheduledThreadPoolExecutor(poolSize, new NamedThreadFactory("cs-taks-pool"));
	}

	public HandlingRunnable localSchedule(Task task, Trigger trigger) {
		return new LocalTriggerRunnable(task,trigger,scheduledExecutor).schedule();
	}

	public HandlingRunnable clusterSchedule(Task task, Trigger trigger,ClusterConfig config) {
		return new ClusterTriggerRunnable(task,trigger,scheduledExecutor,config).schedule();
	}

	public void shutdown() {
		if(!scheduledExecutor.isShutdown()){
			scheduledExecutor.shutdown();
		}
		
	}

}
