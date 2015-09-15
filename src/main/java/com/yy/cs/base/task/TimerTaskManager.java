package com.yy.cs.base.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.status.CsStatus;
import com.yy.cs.base.task.context.Constants.MonitorType;
import com.yy.cs.base.task.execute.HandlingRunnable;
import com.yy.cs.base.task.execute.TimerTaskRegistrar;
import com.yy.cs.base.task.thread.TaskScheduler;
import com.yy.cs.base.task.thread.ThreadPoolTaskScheduler;
import com.yy.cs.base.task.trigger.StringUtils;


/**
 * 定时执行任务管理器，所有需要执行的任务，都需要提交到此类中
 *
 */
public class TimerTaskManager {
	
	private static Logger LOG = LoggerFactory.getLogger(TimerTaskManager.class);
	
	 private int poolSize = 2;
	 
	 private ConcurrentMap<String,TimerTask> timerTasks = new ConcurrentHashMap<String,TimerTask>();
     
     private TimerTaskRegistrar registrar = new TimerTaskRegistrar();
     
     private AtomicBoolean  isStart = new AtomicBoolean(false);
     
     private String monitorfile;
     
     private MonitorType monitorType;
     
     private TaskScheduler taskScheduler;
     
	public String getMonitorfile() {
		return monitorfile;
     }
     /**
      * set监控文件的存放路径，文件的全路径
      * @param monitorfile 监控文件的存放路径
      */
	 public void setMonitorfile(String monitorfile) {
		 this.monitorfile = monitorfile;
	 }
	
	 public MonitorType getMonitorType() {
		 return monitorType;
	 }
	 /**
	  * 监控文件的格式，支持HTML和log格式
	  * @param monitorType
	  */
	 public void setMonitorType(MonitorType monitorType) {
		 this.monitorType = monitorType;
	 }
	 
	 /**
	  * 增加一个Task任务
	  * @param id  任务id
	  * @param task Task任务
	  */
	 public void addTimerTask(String id,TimerTask task){
    	 registrar.addTimerTask(id, task);
    	 timerTasks.put(id, task);
     }
     
     public void addTimerTask(TimerTask task){
    	 if(StringUtils.isEmpty(task.getId())){
    		 task.setId(task.getClass().getName());
    	 }
    	 this.addTimerTask(task.getId(),task);
     }
     /**
      *  启动任务的调度器,执行任务,默认初始化包含两个线程的任务执行器。
      *  并将执行任务过程中的信息通过检测器记录到日志或者html文件中
      */
     public void start(){
    	 if(isStart.compareAndSet(false, true)){
    		 taskScheduler = new ThreadPoolTaskScheduler(poolSize);
    		 taskScheduler.setTaskRegister(registrar);
    		 registrar.start(taskScheduler);
    		 registrar.addMonitorTask(monitorfile, monitorType);
    	 }else{
    		 LOG.warn("the same TimerTaskManager instance should not start twice");
    	 }
     }
     
     /**
      * 销毁定时任务器
      */
     public void destroy(){
    	 if(isStart.compareAndSet(true, false)){
    		 registrar.destroy();
    		 timerTasks.clear();
    	 }
     }
     /**
      * 获取任务的执行状态
      * @return
      * 	任务状态对象
      */
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

	/**
	 * 增加Task任务集合
	 * @param timerTasks <taskid TimerTask>的集合关系映射
	 */
	public void setTimerTasks(ConcurrentMap<String, TimerTask> timerTasks) {
		this.timerTasks = timerTasks;
	}
	
	
	public int getPoolSize() {
		return poolSize;
	}
	
	/**
	 * 设置任务池的线程数量
	 * @param poolSize 池数量
	 */
	public void setPoolSize(int poolSize) {
		if(poolSize < 1){
			throw new IllegalArgumentException(" poolSize Not less than 1"); 
		}
		this.poolSize = poolSize;
	}

	public TaskScheduler getTaskScheduler(){
		return this.taskScheduler;
	}
	
	public boolean stopTask(String taskId,boolean mayInterrupted){
		try{
			HandlingRunnable runingTask = this.registrar.getHandlings().get(taskId);
			if(runingTask.isCancelled()){
				this.registrar.getHandlings().remove(taskId);
				
				timerTasks.remove(taskId);
				return true;
			}else{
				boolean b = runingTask.cancel(mayInterrupted);
				if(b){
					this.registrar.getHandlings().remove(taskId);
					timerTasks.remove(taskId);
				}
				return b;
		   }
		}catch(Exception e){
			return false;
		}
	}
	
	
	 
}
