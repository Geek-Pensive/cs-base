package com.yy.cs.base.task;

import java.util.Date;

public abstract class TimerTask implements Task{
	
	private Date executeDate;
	
	public  void setExecuteTime(Date executeDate){
		this.executeDate = executeDate;
	}
	
	public  Date getExecuteTime(){
		return this.executeDate;
	}
	public TimerTask(){
		TimerTaskManagerScheduler.addTimerTask(this);
	}
}
