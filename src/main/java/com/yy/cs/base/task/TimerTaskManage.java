package com.yy.cs.base.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.yy.cs.base.task.context.TaskContext;
import com.yy.cs.base.task.execute.TimerTaskRegistrar;
import com.yy.cs.base.task.thread.TaskScheduler;
import com.yy.cs.base.task.thread.ThreadPoolTaskScheduler;



public class TimerTaskManage {
	
	 private int poolSize = 2;
	 
	 private Map<String,TimerTask> timerTasks;
     
     private TimerTaskRegistrar registrar;
     
     private volatile boolean  isStart = false;
     
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
    	 }
     }
     
     public void destroy(){
    	 if(isStart){
    		 registrar.destroy();
    	 }
     }
     public List<TaskStatus> getAllTaskStatus(){
    	 List<TaskStatus> status = new ArrayList<TaskStatus>(); 
    	 for(Entry<String,TaskContext> entry : registrar.getAllTaskContext().entrySet()){
    		 TaskContext context = entry.getValue();
    		 status.add(new TaskStatus(entry.getKey(), context.nextScheduledExecutionTime(),context.lastStartTime(),
					context.lastCompletionTime(),
					context.getExceptionTime(),context.getT()));
    	 }
    	 return status;
     }
     
     public TaskStatus getTaskStatus(String id){
    	 TaskContext context = registrar.getTaskContext(id);
    	 if(context ==  null){
    		 return null;
    	 }
    	 return new TaskStatus(id, context.nextScheduledExecutionTime(),context.lastStartTime(),
					context.lastCompletionTime(),
					context.getExceptionTime(),context.getT());
     }
     

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
		this.poolSize = poolSize;
	}

	 
}
