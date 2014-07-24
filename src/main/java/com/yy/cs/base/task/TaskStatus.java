package com.yy.cs.base.task;

import java.util.Date;

/**
 * 任务状态对象
 * <p>包括任务id、下一次执行时间、上一次开始执行时间、上一次执行完成时间、异常发生的时间等。
 * @author duowan-PC
 *
 */
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
	
	/**
	 * 构造器函数
	 * @param id
	 * 		任务的id标识
	 * @param nextScheduledExecutionTime
	 * 		下一次任务调度时间
	 * @param lastStartTime
	 * 		上一次任务调度执行开始时间
	 * @param lastCompletionTime
	 * 		上一次任务调度执行完成时间
	 * @param lastExceptionTime
	 * 		上一次任务调度发生异常的时间
	 * @param executeAddress
	 * 		任务执行的地址
	 * @param t
	 * 		异常
	 */
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
	/**
	 *  构造器函数
	 * @param nextScheduledExecutionTime
	 * 		下一次任务调度执行时间
	 * @param lastStartTime
	 * 		上一次任务调度执行开始时间
	 * @param lastCompletionTime
	 * 		上一次任务调度发生异常的时间
	 */
	public TaskStatus(Date nextScheduledExecutionTime, Date lastStartTime, Date lastCompletionTime) {
		this.nextScheduledExecutionTime = nextScheduledExecutionTime;
		this.lastStartTime = lastStartTime;
		this.lastCompletionTime = lastCompletionTime;
	}

	/**
	 * 构造器函数
	 * @param id
	 * 		任务的id标识
	 * @param nextScheduledExecutionTime
	 * 		下一次任务调度时间
	 * @param lastStartTime
	 * 		上一次任务调度执行开始时间
	 * @param lastCompletionTime
	 * 		上一次任务调度执行完成时间
	 * @param lastExceptionTime
	 * 		上一次任务调度发生异常的时间
	 * @param t
	 * 		异常
	 */
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

	/**
	 * 设置上一次任务执行开始时间
	 * @param lastStartTime
	 * 		上一次任务执行开始时间
	 */
	public void setLastStartTime(Date lastStartTime) {
		this.lastStartTime = lastStartTime;
	}

	/**
	 * 设置上一次任务结束时间
	 * @param lastCompletionTime
	 * 		上一次任务结束时间
	 */
	public void setLastCompletionTime(Date lastCompletionTime) {
		this.lastCompletionTime = lastCompletionTime;
	}

	/**
	 * 设置生一次任务执行发生异常的时间
	 * @param lastExceptionTime
	 * 		上一次任务执行发生异常的时间
	 */
	public void setLastExceptionTime(Date lastExceptionTime) {
		this.lastExceptionTime = lastExceptionTime;
	}

	/**
	 * 获取下一次任务调度执行的时间
	 * @return
	 * 		下一次任务调度执行的时间
	 */
	public Date getNextScheduledExecutionTime() {
		return nextScheduledExecutionTime;
	}

	/**
	 * 设置下一次任务调度执行的时间
	 * @param nextScheduledExecutionTime
	 * 		下一次任务调度执行的时间
	 */
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
