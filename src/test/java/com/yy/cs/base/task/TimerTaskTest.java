package com.yy.cs.base.task;

import java.util.Date;

public class TimerTaskTest extends TimerTask {
	
	private Date date;
	@Override
	public void execute() throws Exception{
		System.out.println(new Date() + "-----------execute()---TimerTaskTest--------");
	}
	
}
