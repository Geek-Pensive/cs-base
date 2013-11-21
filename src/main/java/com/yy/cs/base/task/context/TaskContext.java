
package com.yy.cs.base.task.context;

import java.util.Date;


public class TaskContext{

	private volatile Date nextScheduledExecutionTime;    //下次执行时间

	private volatile Date lastStartTime;		//最近一次执行开始时间

	private volatile Date lastCompletionTime;   //最近一次执行完成时间
	
	private volatile Throwable t;
	
	private volatile Date exceptionTime;
	
	public TaskContext() {
	}
 
	public TaskContext(Date nextScheduledExecutionTime, Date lastStartTime, Date lastCompletionTime) {
		this.nextScheduledExecutionTime = nextScheduledExecutionTime;
		this.lastStartTime = lastStartTime;
		this.lastCompletionTime = lastCompletionTime;
	}
	public void update(Date nextScheduledExecutionTime, Date lastStartTime, Date lastCompletionTime) {
		this.nextScheduledExecutionTime = nextScheduledExecutionTime;
		this.lastStartTime = lastStartTime;
		this.lastCompletionTime = lastCompletionTime;
	}
	public void updateExecuteTime(Date lastStartTime, Date lastCompletionTime) {
		this.lastStartTime = lastStartTime;
		this.lastCompletionTime = lastCompletionTime;
	}
	
	public void updateNextTime(Date nextScheduledExecutionTime) {
		this.nextScheduledExecutionTime = nextScheduledExecutionTime;
	}
	
	public void updateException(Date exceptionTime, Throwable t){
		this.exceptionTime = exceptionTime;
		this.t = t;
	}
	
	public Date nextScheduledExecutionTime() {
		return this.nextScheduledExecutionTime;
	}

	public Date lastStartTime() {
		return this.lastStartTime;
	}

	public Date lastCompletionTime() {
		return this.lastCompletionTime;
	}
	public Throwable getT() {
		return t;
	}

	public Date getExceptionTime() {
		return exceptionTime;
	}

	public void setExceptionTime(Date exceptionTime) {
		this.exceptionTime = exceptionTime;
	}

	public void setT(Throwable t) {
		this.t = t;
	}
}
