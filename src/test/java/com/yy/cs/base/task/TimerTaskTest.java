package com.yy.cs.base.task;

import java.util.Date;

public class TimerTaskTest extends TimerTask {

	@Override
	public void execute() {
		System.out.println(new Date() + "-----------execute()---TimerTaskTest--------");
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
