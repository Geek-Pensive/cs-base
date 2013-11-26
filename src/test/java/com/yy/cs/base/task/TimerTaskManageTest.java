package com.yy.cs.base.task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TimerTaskManageTest {
	
	TimerTaskManage timerTaskManage;
	@Before
    public void before() {
//		timerTaskManage = new TimerTaskManage();
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-task.xml");
		timerTaskManage =  (TimerTaskManage) context.getBean("taskManage");
    }
	/**
	 * 
	 */
    @After
    public void after() {
    	timerTaskManage.destroy();
    }
    
    
    @Test
    public void testSpringTimerTaskManag() {
	    for(; ;){
			try {
				Thread.sleep(8000);
				for(TaskStatus t : timerTaskManage.getAllTaskStatus()){
					System.out.println(t.toString());;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    
    
//    @Test
    public void testTimerTaskManag() {
    	TimerTaskTest time = new TimerTaskTest();
    	time.setCron("*/30 * * * * *");
    	timerTaskManage.addTimerTask(time);
    	timerTaskManage.start();
    	
    		for(int i=0; i<20;i++){
        		try {
        			Thread.sleep(8000);
        			for(TaskStatus t : timerTaskManage.getAllTaskStatus()){
        				System.out.println(t.toString());;
        			}
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
        	}
    		for(TaskStatus t : timerTaskManage.getAllTaskStatus()){
				System.out.println(t.toString());;
			}
    		System.out.println("---------destroy---------------");
    		timerTaskManage.destroy();
    		for(TaskStatus t : timerTaskManage.getAllTaskStatus()){
				System.out.println(t.toString());;
			}
    }
}
