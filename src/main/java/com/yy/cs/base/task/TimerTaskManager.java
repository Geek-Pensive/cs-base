package com.yy.cs.base.task;

import java.util.HashMap;
import java.util.Map;

import com.yy.cs.base.status.CsStatus;
import com.yy.cs.base.task.context.Constants.MonitorType;
import com.yy.cs.base.task.execute.TimerTaskRegistrar;
import com.yy.cs.base.task.thread.TaskScheduler;
import com.yy.cs.base.task.thread.ThreadPoolTaskScheduler;



public class TimerTaskManager {
	
	 private int poolSize = 2;
	 
	 private Map<String,TimerTask> timerTasks;
     
     private TimerTaskRegistrar registrar;
     
     private volatile boolean  isStart = false;
     
     private String monitorfile;
     
     private MonitorType monitorType;
     
     public String getMonitorfile() {
		return monitorfile;
     }

	public void setMonitorfile(String monitorfile) {
		this.monitorfile = monitorfile;
	}
	
	public MonitorType getMonitorType() {
		return monitorType;
	}

	public void setMonitorType(MonitorType monitorType) {
		this.monitorType = monitorType;
	}

	public void addTimerTask(String id,TimerTask task){
    	 if(timerTasks ==  null){
    		 timerTasks = new HashMap<String,TimerTask>();
    	 }
    	 timerTasks.put(id, task);
     }
     
     public void addTimerTask(TimerTask task){
    	 this.addTimerTask(task.getClass().getName(),task);
     }
     
     public void start(){
    	 if(!isStart){
    		 isStart = true;
    		 TaskScheduler taskScheduler = new ThreadPoolTaskScheduler(poolSize);
    		 registrar = new TimerTaskRegistrar();
    		 registrar.parse(timerTasks);
    		 registrar.start(taskScheduler);
    		 registrar.addMonitorTask(monitorfile, monitorType);
    	 }
     }
     
     public void destroy(){
    	 if(isStart){
    		 registrar.destroy();
    		 isStart = false;
    	 }
     }
     public CsStatus getCsStatus(){
    	 return registrar.getCsStatus();
     }
     
//     public TaskStatus getTaskStatus(String id){
//    	 TaskContext context = registrar.getTaskContext(id);
//    	 if(context ==  null){
//    		 return null;
//    	 }
//    	 return new TaskStatus(id, context.nextScheduledExecutionTime(),context.lastStartTime(),
//					context.lastCompletionTime(),
//					context.getExceptionTime(),context.executeAddress(),context.getT());
//     }
     

	public Map<String, TimerTask> getTimerTasks() {
		return timerTasks;
	}


	public void setTimerTasks(Map<String, TimerTask> timerTasks) {
		this.timerTasks = timerTasks;
	}
	
	
	
	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		if(poolSize < 1){
			throw new IllegalArgumentException("TimerTaskManage poolSize Not less than 1"); 
		}
		this.poolSize = poolSize;
	}

	 
}
