package com.yy.cs.base.task.thread;


import com.yy.cs.base.task.ClusterConfig;
import com.yy.cs.base.task.Task;
import com.yy.cs.base.task.execute.HandlingRunnable;
import com.yy.cs.base.task.execute.TimerTaskRegistrar;
import com.yy.cs.base.task.log.TaskLogHandler;
import com.yy.cs.base.task.trigger.Trigger;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
 
public interface TaskScheduler {

	 /**
	  * 
	  * @param task
	  * 		需要调度执行的任务,实现runnable接口或者继承Thread类
	  * @param trigger
	  * 		触发器
	  * @return
	  * 		下一个需要被调度执行的任务对象
	  */
	HandlingRunnable localSchedule(Task task, Trigger trigger);

	/**
	 * 
	 * @param task
	 * 		任务
	 * @param trigger 
	 * 		触发器     
	 * @param config
	 * 		任务集群配置信息 
	 * @return
	 * 		下一个需要被调度执行的任务对象 
	 * 		
	 */
	HandlingRunnable clusterSchedule(Task task, Trigger trigger,ClusterConfig config);
	
	/**
	 * 定期执行任务
	 * @param command
	 * 		需要定期执行的任务,实现Runnable接口
	 * @param initialDelay
	 * 		第一次执行的时间
	 * @param delay
	 * 		间隔执行的时间
	 * @param unit
	 * 		时间单位
	 * @return
	 * 		执行结果future
	 */
	ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit);
	
	/**
	 * 关闭当前调度任务
	 */
	void shutdown();
	
	void setTaskRegister(TimerTaskRegistrar register);

	void setTaskLogHandler(TaskLogHandler taskLogHandler);
}
