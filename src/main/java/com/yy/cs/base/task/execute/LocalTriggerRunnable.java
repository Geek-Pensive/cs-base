package com.yy.cs.base.task.execute;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.task.Task;
import com.yy.cs.base.task.trigger.Trigger;

 
/**
 *  执行本地task、cron表达式Runnable实现
 *
 */
public class LocalTriggerRunnable extends HandlingRunnable {
	
	private static final Logger logger = LoggerFactory.getLogger(LocalTriggerRunnable.class);
	
//	private final Trigger trigger;
	
	private final ScheduledExecutorService executor;
	
	public LocalTriggerRunnable(Task task, Trigger trigger, ScheduledExecutorService executor) {
		super(task,trigger);
		if(trigger == null){
			throw new IllegalArgumentException("trigger must not be null");
		}
		this.executor = executor;
	}
	
	/**
	 * 提交一次要执行的任务,更新任务执行信息，返回任务本身
	 */
	public HandlingRunnable schedule() {
		synchronized (this.triggerContextMonitor) {
			this.scheduledExecutionTime = this.trigger.nextExecutionTime(this.context);
			this.context.updateNextTime(scheduledExecutionTime);
			if (this.scheduledExecutionTime == null) {
				return null;
			}
			long initialDelay = this.scheduledExecutionTime.getTime() - System.currentTimeMillis();
			this.currentFuture = this.executor.schedule(this, initialDelay, TimeUnit.MILLISECONDS);
			return this;
		}
	}
	
	@Override
	public void run() {
		super.run();
		synchronized (this.triggerContextMonitor) {
			if (!this.currentFuture.isCancelled()) {
				schedule();
			}
		}
		logger.info("completion run local task id:" + task.getId());
	}
	

}
