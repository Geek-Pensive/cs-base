package com.yy.cs.base.task;

import java.util.Date;

public class TimerTaskTest extends TimerTask {

	@Override
	public void execute() {
		System.out.println(new Date() + "-----7777777777777777------execute()---TimerTaskTest--------");
	}
	
}
