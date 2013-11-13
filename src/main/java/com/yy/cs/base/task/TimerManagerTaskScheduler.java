package com.yy.cs.base.task;

import com.yy.cs.base.task.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import com.yy.cs.base.task.poolthread.NamedThreadFactory;

public class TimerManagerTaskScheduler {
	
    private static final ScheduledExecutorService scheduledExecutorService = 
    		Executors.newScheduledThreadPool(2, new NamedThreadFactory("CS-Base-Timer", true));

    private static final ConcurrentMap<String,ScheduledFuture<?>> scheduledFutures = 
    		new ConcurrentHashMap<String, ScheduledFuture<?>>();
    
    
    public static void addTimerTask(final TimerTask t){
    	Runnable r = new Runnable(){
    		public void run(){
    			//log
    			t.execute();
    			//log
    		};
    	};
    	r.run();
    	ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(r, 0, 0, null);
    	scheduledFutures.putIfAbsent(scheduledFuture.hashCode()+"", scheduledFuture);
    	
    }
}
