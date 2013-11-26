package com.yy.cs.base.task.execute;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import com.yy.cs.base.task.Task;
import com.yy.cs.base.task.context.TaskContext;


public abstract class HandlingRunnable implements Runnable,ScheduledFuture<Object>{

	protected final Task task;
	
	protected final TaskContext context = new TaskContext();

	protected final Object triggerContextMonitor = new Object();
	
	protected Date scheduledExecutionTime;
	
	protected ScheduledFuture<?> currentFuture;
	
	public HandlingRunnable(Task task) {
		if(task == null){
			throw new IllegalArgumentException("task must not be null");
		}
		this.task = task;
	}
	
	public TaskContext getContext() {
		return context;
	}
	
	public abstract HandlingRunnable schedule();
	
	@Override
	public void run(){
		try {
			this.task.execute();
		}catch (Throwable ex) {
			this.context.updateException(new Date(),ex);
		}
	}

	
	@Override
	public String toString() {
		return "HandlingRunnable for " + this.task;
	}

}
