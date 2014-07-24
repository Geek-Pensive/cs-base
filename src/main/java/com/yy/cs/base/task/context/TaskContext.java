
package com.yy.cs.base.task.context;

import java.util.Date;

/**
 * 任务执行上下文,封装任务执行的开始、结束、下一次执行的时间。出现异常的时间、任务执行的路径等信息
 * @author duowan-PC
 *
 */
public class TaskContext{

	private volatile Date nextScheduledExecutionTime;    //下次执行时间

	private volatile Date lastStartTime;		//最近一次执行开始时间

	private volatile Date lastCompletionTime;   //最近一次执行完成时间
	
	private volatile Throwable t;
	
	private volatile Date exceptionTime;
	
	private volatile String executeAddress;
	/**
	 * 无参构造函数
	 */
	public TaskContext() {
	}
	
	/**
	 * 构造函数
	 * @param nextScheduledExecutionTime
	 * 		下一次执行任务的时间
	 * @param lastStartTime
	 * 		上一次执行开始的时间
	 * @param lastCompletionTime
	 * 		上一次执行结束的时间
	 */
	public TaskContext(Date nextScheduledExecutionTime, Date lastStartTime, Date lastCompletionTime) {
		this.nextScheduledExecutionTime = nextScheduledExecutionTime;
		this.lastStartTime = lastStartTime;
		this.lastCompletionTime = lastCompletionTime;
	}
	/**
	 * 
	 * @param executeAddress
	 */
	public void updateExecuteAddress(String executeAddress) {
		this.executeAddress = executeAddress;
	}
	/**
	 * 更新任务执行信息
	 * @param nextScheduledExecutionTime
	 * 		下一次执行时间
	 * @param lastStartTime
	 * 		上一次执行开始时间
	 * @param lastCompletionTime
	 * 		上一次执行完成时间
	 */
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

	public String executeAddress() {
		return executeAddress;
	}
	
}
