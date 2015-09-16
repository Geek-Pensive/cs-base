package com.yy.cs.base.task.thread;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.task.ClusterConfig;
import com.yy.cs.base.task.Task;
import com.yy.cs.base.task.TimerTask;
import com.yy.cs.base.task.execute.ClusterTriggerRunnable;
import com.yy.cs.base.task.execute.HandlingRunnable;
import com.yy.cs.base.task.execute.LocalTriggerRunnable;
import com.yy.cs.base.task.execute.TimerTaskRegistrar;
import com.yy.cs.base.task.trigger.Trigger;

/**
 * 基于线程池的任务调度器
 *
 */
public class ThreadPoolTaskScheduler implements TaskScheduler  {

	private final static Logger LOG = LoggerFactory.getLogger(ThreadPoolTaskScheduler.class);
	
	private TimerTaskRegistrar register;
	
	private final ScheduledExecutorService scheduledExecutor;
	
	private Lock lock = new ReentrantLock();
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
		try{
			lock.lock();
			LocalTriggerRunnable triggerRunnable = new LocalTriggerRunnable(task,trigger,scheduledExecutor);
			HandlingRunnable r = this.register.getHandlings().get(task.getId());
			if( r != null ){
				TimerTask newTimerTask = (TimerTask) task;
				TimerTask oldTimerTask = (TimerTask) r.getTask();
				if (newTimerTask.getId().equals(oldTimerTask.getId())
						&& oldTimerTask.getCron().equalsIgnoreCase(
								newTimerTask.getCron())) {
					return r;
				}else{
					//停止任务
					if(r.isCancelled() || r.cancel(false)){
						this.register.getHandlings().remove(triggerRunnable.getTask().getId());
						//触发新的任务
						return triggerRunnable.schedule();
					}else{
						LOG.error("fail to update and execute new local TimerTask :{},and new task:{},trigger "
								+ "/ old: task ",task,trigger,r.getTask(),r.getTrigger());
						return r;
					}
				}	
			}else{
				return triggerRunnable.schedule();
			}
		}finally{
			lock.unlock();
		}
	}
	
	public HandlingRunnable clusterSchedule(Task task, Trigger trigger,
			ClusterConfig config) {
		try {
			lock.lock();
			ClusterTriggerRunnable triggerRunnable = new ClusterTriggerRunnable(
					task, trigger, scheduledExecutor, config);
			HandlingRunnable r = this.register.getHandlings().get(task.getId());
			if (r != null) {
				TimerTask newTimerTask = (TimerTask) task;
				TimerTask oldTimerTask = (TimerTask) r.getTask();
				//如果task id 和 cron表达式相同则表明时同一个任务，则不需要重新起任务
				if (newTimerTask.getId().equals(oldTimerTask.getId())
						&& oldTimerTask.getCron().equalsIgnoreCase(
								newTimerTask.getCron())) {
					return r;
				} else {
					// 停止任务
					if (r.isCancelled() || r.cancel(false)) {
						this.register.getHandlings().remove(
								triggerRunnable.getTask().getId());
						// 触发新的任务
						return triggerRunnable.schedule();
					} else {
						LOG.error(
								"fail to update and execute new cluster TimerTask :{},and new task:{},trigger "
										+ "/ old: task ", task, trigger,
								r.getTask(), r.getTrigger());
						return r;
					}
				}
			} else {
				return triggerRunnable.schedule();
			}
		} finally {
			lock.unlock();
		}
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
	
	public TimerTaskRegistrar getRegister() {
		return register;
	}

	@Override
	public void setTaskRegister(TimerTaskRegistrar register) {
		this.register = register;
	}


}
