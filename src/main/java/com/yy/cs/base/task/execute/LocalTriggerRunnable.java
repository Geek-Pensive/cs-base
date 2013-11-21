package com.yy.cs.base.task.execute;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.yy.cs.base.task.Task;
import com.yy.cs.base.task.trigger.Trigger;

 
 
public class LocalTriggerRunnable extends HandlingRunnable {

	private final Trigger trigger;
	
	private final ScheduledExecutorService executor;

	public LocalTriggerRunnable(Task task, Trigger trigger, ScheduledExecutorService executor) {
		super(task);
		if(trigger == null){
			new IllegalArgumentException("trigger must not be null");
		}
		this.trigger = trigger;
		this.executor = executor;
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
		Date startTime = new Date();
		super.run();
		Date completionTime = new Date();
		synchronized (this.triggerContextMonitor) {
			this.context.updateExecuteTime(startTime, completionTime);
			if (!this.currentFuture.isCancelled()) {
				schedule();
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

}
