package com.yy.cs.base.task.execute;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.task.Task;
import com.yy.cs.base.task.context.TaskContext;
import com.yy.cs.base.task.trigger.Trigger;


public abstract class HandlingRunnable implements Runnable,ScheduledFuture<Object>{
	
	private static final Logger logger = LoggerFactory.getLogger(HandlingRunnable.class);
	
	protected final Task task;
	
	protected final Trigger trigger;
	
	protected final TaskContext context = new TaskContext();
	
	protected final TaskContext contextMonitor = new TaskContext();

	protected final Object triggerContextMonitor = new Object();
	
	protected Date scheduledExecutionTime;
	
	protected ScheduledFuture<?> currentFuture;
	
	public HandlingRunnable(Task task,Trigger trigger) {
		if(task == null){
			throw new IllegalArgumentException("task must not be null");
		}
		this.task = task;
		this.trigger = trigger;
	}
	
	public TaskContext getContext() {
		return context;
	}
	
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
