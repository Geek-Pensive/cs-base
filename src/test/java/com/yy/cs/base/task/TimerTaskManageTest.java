package com.yy.cs.base.task;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.yy.cs.base.status.CsStatus;
import com.yy.cs.base.task.execute.TaskExceptionHandler;

public class TimerTaskManageTest {
	
	public static TimerTaskManager timerTaskManage = new TimerTaskManager();
	@Before
    public void before() {
//		ApplicationContext context = new ClassPathXmlApplicationContext("spring-task.xml");
//		timerTaskManage =  (TimerTaskManager) context.getBean("taskManage");
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
				CsStatus t = timerTaskManage.getCsStatus();
//				System.out.println(t.toString());
//				WebServiceFileUtil w = new WebServiceFileUtil();
//				w.writeTaskFile(t);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    
    
    @Test
    public void testTimerTaskManag() {
    	TimerTaskTest time = new TimerTaskTest();
    	time.setCron("*/30 * * * * *");
    	timerTaskManage.addTimerTask(time);
    	timerTaskManage.start();
    	timerTaskManage.getCsStatus();
    		for(int i=0; i<20;i++){
        		try {
        			Thread.sleep(8000);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
        	}
//    		for(TaskStatus t : timerTaskManage.getAllTaskStatus()){
//				System.out.println(t.toString());;
//			}
//    		System.out.println("---------destroy---------------");
//    		timerTaskManage.destroy();
//    		for(TaskStatus t : timerTaskManage.getAllTaskStatus()){
//				System.out.println(t.toString());;
//			}
    }
    
    public static void main(String[] args) {
        TimerTaskTest time = new TimerTaskTest();
        time.setCron("*/15 * * * * *");
        timerTaskManage.addTimerTask(time);
        
        TaskExceptionHandler taskExceptionHandler = new TaskExceptionHandler() {
            
            @Override
            public void handle(Task task, Throwable ex) {
                System.out.println("taskId:" + task.getId() + ", exceptionTime:" + new Date() + ", execute error:" + ex);
            }
        };
        timerTaskManage.setTaskExceptionHandler(taskExceptionHandler);
        
        timerTaskManage.start();
        timerTaskManage.getCsStatus();
        
            for(int i=0; i<20;i++){
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
    }
}
