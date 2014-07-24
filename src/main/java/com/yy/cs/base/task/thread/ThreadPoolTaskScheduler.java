package com.yy.cs.base.task.thread;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.yy.cs.base.task.ClusterConfig;
import com.yy.cs.base.task.Task;
import com.yy.cs.base.task.execute.ClusterTriggerRunnable;
import com.yy.cs.base.task.execute.HandlingRunnable;
import com.yy.cs.base.task.execute.LocalTriggerRunnable;
import com.yy.cs.base.task.trigger.Trigger;

/**
 * 基于线程池的任务调度器
 * @author duowan-PC
 *
 */
public class ThreadPoolTaskScheduler implements TaskScheduler  {


	private final ScheduledExecutorService scheduledExecutor;
	/**
	 * 构造器,默认线程池大小为2
	 */
	public  ThreadPoolTaskScheduler() {
		this(2);
	}
	/**
	 * 构造器,默认线程池大小为2。
	 * @param poolSize
	 * 		线程池大小, 当poolSize<2时，默认初始化2个线程的线程池
	 */
	public  ThreadPoolTaskScheduler(int poolSize) {
		if(poolSize < 2){
			poolSize = 2;
		}
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
	
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
			long initialDelay, long delay, TimeUnit unit) {
		return scheduledExecutor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
	}

}
