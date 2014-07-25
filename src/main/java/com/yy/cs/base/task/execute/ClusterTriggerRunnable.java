package com.yy.cs.base.task.execute;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.task.ClusterConfig;
import com.yy.cs.base.task.Task;
import com.yy.cs.base.task.execute.lock.RedisTaskLock;
import com.yy.cs.base.task.execute.lock.TaskLock;
import com.yy.cs.base.task.trigger.Trigger;

 
/**
 *  执行集中式task、cron表达式的Runnable实现
 *
 */
public class ClusterTriggerRunnable extends HandlingRunnable {

	private static final Logger logger = LoggerFactory.getLogger(ClusterTriggerRunnable.class);
	
	private final Trigger trigger;
	
	private final ScheduledExecutorService executor;
	
	private final TaskLock taskLock;
	/**
	 * 
	 * @param task
	 * 		需执行任务
	 * @param trigger
	 * 		任务触发器
	 * @param executor
	 * 		任务执行器
	 * @param clusterConfig
	 * 		<p>任务执行配置信息，主要是当前任务的执行信息。clusterConfig {@link ClusterConfig } 需要初始化Redis,配置redis服务器address地址 
	 * 		
	 */
	public ClusterTriggerRunnable(Task task, Trigger trigger, ScheduledExecutorService executor,ClusterConfig clusterConfig) {
		super(task,trigger);
		if(trigger == null){
			throw new IllegalArgumentException("trigger must not be null");
		}
		if(clusterConfig == null || clusterConfig.getRedisClient() == null){
			throw new IllegalArgumentException("clusterConfig must not be null");
		}
		this.trigger = trigger;
		this.executor = executor;
//		if(clusterConfig.getExpireLockTime() > 0){
//			taskLock = new RedisTaskLock(clusterConfig.getRedisPoolManager(),clusterConfig.getExpireLockTime());
//		}else{
		taskLock = new RedisTaskLock(clusterConfig.getRedisClient());
//		}
	}


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
		
		//取task的锁
		if(taskLock.lock(task.getId(), this.scheduledExecutionTime.getTime())){
			super.run();
		}
		this.context.updateExecuteAddress(taskLock.getExecuteAddress(task.getId(),this.scheduledExecutionTime.getTime()));
		//完成当前任务后,调度下一次任务的执行
		synchronized (this.triggerContextMonitor) {
			if (!this.currentFuture.isCancelled()) {
				schedule();
			}
		}
		logger.info("completion run cluster task id:" + task.getId());
	}
	

}
