package com.yy.cs.base.task;

import java.util.Date;

public class TaskStatus {
	
	private String id;

	private   Date nextScheduledExecutionTime;    //下次执行时间

	private   Date lastStartTime;		//最近一次执行开始时间

	private   Date lastCompletionTime;   //最近一次执行完成时间

	private   String executeAddress;   //最近一次执行的地址
	 
	private   Date lastExceptionTime;   //最近一次异常执行时间

	private   Throwable t;
	
	public TaskStatus() {
	}
	
	public TaskStatus(String id, Date nextScheduledExecutionTime,
			Date lastStartTime, Date lastCompletionTime,
			Date lastExceptionTime, String executeAddress, Throwable t) {
		super();
		this.id = id;
		this.nextScheduledExecutionTime = nextScheduledExecutionTime;
		this.lastStartTime = lastStartTime;
		this.lastCompletionTime = lastCompletionTime;
		this.lastExceptionTime = lastExceptionTime;
		this.executeAddress = executeAddress;
		this.t = t;
	}
	 
	public TaskStatus(Date nextScheduledExecutionTime, Date lastStartTime, Date lastCompletionTime) {
		this.nextScheduledExecutionTime = nextScheduledExecutionTime;
		this.lastStartTime = lastStartTime;
		this.lastCompletionTime = lastCompletionTime;
	}


	public TaskStatus(String id,Date nextScheduledExecutionTime, Date lastStartTime,
			Date lastCompletionTime, Date lastExceptionTime, Throwable t) {
		super();
		this.nextScheduledExecutionTime = nextScheduledExecutionTime;
		this.lastStartTime = lastStartTime;
		this.lastCompletionTime = lastCompletionTime;
		this.lastExceptionTime = lastExceptionTime;
		this.t = t;
		this.id = id;
	}


	public void setLastStartTime(Date lastStartTime) {
		this.lastStartTime = lastStartTime;
	}


	public void setLastCompletionTime(Date lastCompletionTime) {
		this.lastCompletionTime = lastCompletionTime;
	}


	public void setLastExceptionTime(Date lastExceptionTime) {
		this.lastExceptionTime = lastExceptionTime;
	}


	public Date getNextScheduledExecutionTime() {
		return nextScheduledExecutionTime;
	}


	public void setNextScheduledExecutionTime(Date nextScheduledExecutionTime) {
		this.nextScheduledExecutionTime = nextScheduledExecutionTime;
	}


	public Throwable getT() {
		return t;
	}


	public void setT(Throwable t) {
		this.t = t;
	}


	public Date getLastStartTime() {
		return lastStartTime;
	}


	public Date getLastCompletionTime() {
		return lastCompletionTime;
	}


	public Date getLastExceptionTime() {
		return lastExceptionTime;
	}


	public Date lastCompletionTime() {
		return this.lastCompletionTime;
	}
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}
	
	public String getExecuteAddress() {
		return executeAddress;
	}


	public void setExecuteAddress(String executeAddress) {
		this.executeAddress = executeAddress;
	}


	@Override
	public String toString() {
		return "TaskStatus [id=" + id + ", nextScheduledExecutionTime="
				+ nextScheduledExecutionTime + ", lastStartTime="
				+ lastStartTime + ", lastCompletionTime=" + lastCompletionTime
				+ ", executeAddress=" + executeAddress
				+ ", lastExceptionTime=" + lastExceptionTime
				+ ", t=" + t + "]";
	}
	
}
