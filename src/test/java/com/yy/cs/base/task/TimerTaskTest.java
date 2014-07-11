package com.yy.cs.base.task;

import java.util.Date;
import java.util.List;

import com.yy.cs.base.status.CsStatus;

public class TimerTaskTest extends TimerTask {
	
	private Date date;
	@Override
	public void execute() throws Exception{
		Thread.sleep(1000);
		System.out.println(new Date()
				+ "-----------execute()---TimerTaskTest--------");
//		date.compareTo(date);
	}

	
	
	public static void main(String[] agrs) {
		TimerTaskManager timerTaskManager = new TimerTaskManager();
		TimerTaskTest time = new TimerTaskTest();
		time.setCron("*/30 * * * * *");
		timerTaskManager.addTimerTask(time);
		timerTaskManager.start();
		CsStatus status = timerTaskManager.getCsStatus();
	}
}
