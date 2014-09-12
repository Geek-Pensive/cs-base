package com.yy.cs.base.task.execute;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.status.CsStatus;
import com.yy.cs.base.status.StatusCode;
import com.yy.cs.base.task.Task;
import com.yy.cs.base.task.context.Constants;
import com.yy.cs.base.task.context.TaskContext;
import com.yy.cs.base.task.trigger.Trigger;

/**
 * 任务处理的Handling 抽象类，实现了{@link Runnable},{@link ScheduledFuture}接口
 *
 */
public abstract class HandlingRunnable implements Runnable,ScheduledFuture<Object>{
	
	private static final Logger logger = LoggerFactory.getLogger(HandlingRunnable.class);
	
	/**
	 * 任务
	 */
	protected final Task task;
	/**
	 * 触发器
	 */
	protected final Trigger trigger;
	/**
	 * 任务上下文
	 */
	protected final TaskContext context = new TaskContext();
	/**
	 * 任务上下文监听对象
	 */
	protected final TaskContext contextMonitor = new TaskContext();
	/**
	 * 锁
	 */
	protected final Object triggerContextMonitor = new Object();
	/**
	 * 任务执行时间
	 */
	protected Date scheduledExecutionTime;
	
	/**
	 * 任务执行后的返回结果future
	 */
	protected ScheduledFuture<?> currentFuture;
	
	/**
	 * 构造器函数
	 * @param task
	 * 		任务 {@link Task}
	 * @param trigger
	 * 		任务触发器 {@link Trigger}
	 */
	public HandlingRunnable(Task task,Trigger trigger) {
		if(task == null){
			throw new IllegalArgumentException("task must not be null");
		}
		this.task = task;
		this.trigger = trigger;
	}
	/**
	 * 获取任务上下文对象
	 * @return
	 * 		当前执行任务的任务上下文对象 {@link TaskContext}
	 */
	public TaskContext getContext() {
		return context;
	}
	
	/**
	 * 调度一次要执行的任务,更新任务执行信息，返回任务本身
	 * @return
	 * 		当前执行任务本身
	 */
	public abstract HandlingRunnable schedule();
	
	
	private Date startTime;
	
	@Override
	public void run(){
		synchronized (this.triggerContextMonitor) {
			startTime = new Date();
		}
		try {
			logger.info(startTime + ", start run task id:" + task.getId());
			this.task.execute();
			this.context.updateException(null,null);
		}catch (Throwable ex) {
			logger.error(this.task.getId() + "  "+ ex.getMessage(), ex);
			this.context.updateException(new Date(),ex);
		}finally{
			this.context.updateExecuteTime(startTime, new Date());
			synchronized (this.triggerContextMonitor) {
				startTime = null;
			}
			CsStatus status =  new CsStatus();
            status.setName(task.getId());
            TaskContext context = this.getContext();
            status.additionInfo(Constants.TASK_ID, task.getId());
            status.additionInfo(Constants.NEXT_EXECUTE_TIME, context.nextScheduledExecutionTime());
            status.additionInfo(Constants.LAST_START_TIME, context.lastStartTime());
            status.additionInfo(Constants.LAST_COMPLETION_TIME, context.lastCompletionTime());
            status.additionInfo(Constants.EXECUTE_ADDRESS, context.executeAddress());
            status.additionInfo(Constants.LAST_EXCEPTION_TIME, context.getExceptionTime());
            status.additionInfo(Constants.THROWABLE, context.getT());
            boolean istimeout =this.isTimeout();
            status.additionInfo(Constants.TIMEOUT, istimeout);
            if(istimeout){
                status.setCode(StatusCode.WRONG);
            }
            if(context.getT() != null){
                status.setCode(StatusCode.FAIL);
            }
            task.setCsStatus(status);
		}
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		synchronized (this.triggerContextMonitor) {
			return this.currentFuture.cancel(mayInterruptIfRunning);
		}
	}

	@Override
	public boolean isCancelled() {
		synchronized (this.triggerContextMonitor) {
			return this.currentFuture.isCancelled();
		}
	}

	@Override
	public boolean isDone() {
		synchronized (this.triggerContextMonitor) {
			return this.currentFuture.isDone();
		}
	}

	@Override
	public Object get() throws InterruptedException, ExecutionException {
		ScheduledFuture<?> curr;
		synchronized (this.triggerContextMonitor) {
			curr = this.currentFuture;
		}
		return curr.get();
	}

	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		ScheduledFuture<?> curr;
		synchronized (this.triggerContextMonitor) {
			curr = this.currentFuture;
		}
		return curr.get(timeout, unit);
	}

	@Override
	public long getDelay(TimeUnit unit) {
		ScheduledFuture<?> curr;
		synchronized (this.triggerContextMonitor) {
			curr = this.currentFuture;
		}
		return curr.getDelay(unit);
	}

	@Override
	public int compareTo(Delayed other) {
		if (this == other) {
			return 0;
		}
		long diff = getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
		return (diff == 0 ? 0 : ((diff < 0)? -1 : 1));
	}
	@Override
	public String toString() {
		return "HandlingRunnable for " + this.task;
	}
	/**
	 * 判断当前时间是否已经超出下一次任务执行的时间。如果超出了执行时间则任务失效返回true
	 * ,反之返回false
	 * @return
	 * 		boolean
	 */
	public boolean isTimeout(){
		Date nexdDate;
		synchronized (this.triggerContextMonitor) {
			if (startTime == null) {
				return false;
			}
			contextMonitor.updateExecuteTime(startTime, startTime);
			nexdDate = trigger.nextExecutionTime(contextMonitor);
		}
		return System.currentTimeMillis() > nexdDate.getTime() ? true : false;
	}
}
