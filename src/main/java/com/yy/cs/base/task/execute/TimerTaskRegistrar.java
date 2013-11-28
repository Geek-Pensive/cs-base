package com.yy.cs.base.task.execute;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.yy.cs.base.status.CsStatus;
import com.yy.cs.base.task.TimerTask;
import com.yy.cs.base.task.context.Constants;
import com.yy.cs.base.task.context.TaskContext;
import com.yy.cs.base.task.thread.TaskScheduler;
import com.yy.cs.base.task.trigger.CronTrigger;
import com.yy.cs.base.task.trigger.Trigger;

public class TimerTaskRegistrar {
	
	
	private TaskScheduler taskScheduler;

	Map<String, TimerTask> cronTaskMap = new HashMap<String, TimerTask>();
	
	Map<String, TimerTask> clusterTaskMap = new HashMap<String, TimerTask>();

	private final Map<String , HandlingRunnable> handlings = new HashMap<String , HandlingRunnable>();
	
	public void parse(Map<String,TimerTask> timerTasks) {
		Set<Entry<String, TimerTask>> tasks = timerTasks.entrySet();
		for (Entry<String, TimerTask> e : tasks) {
			TimerTask task =  e.getValue();
			String id =  e.getKey();
			if (task == null) {
				continue;
			}
			if(task.getCluster() != null){
				clusterTaskMap.put(id, task);
				continue;
			} 
			cronTaskMap.put(id, task);	
		}
	}
	 
	public TaskScheduler getScheduler() {
		return this.taskScheduler;
	}

	 
	public void start(TaskScheduler taskScheduler ) {
		this.taskScheduler = taskScheduler;
		scheduleTasks();
	}

//	public TaskContext getTaskContext(String id) {
//		HandlingRunnable handlingRunnable = handlings.get(id);
//		return handlingRunnable.getContext();
//	}
	
	public CsStatus getCsStatus() {
		CsStatus csStatus = new CsStatus();
		for (Entry<String, HandlingRunnable> entry : this.handlings.entrySet()) {
			
			CsStatus subStatus = entry.getValue().task.getCsStatus();
			if(subStatus == null){
				subStatus = new CsStatus();
			}
			if(subStatus.getName() == null || "".equals(subStatus.getName())){
				subStatus.setName(entry.getKey());
			}
			TaskContext context = entry.getValue().getContext();
			subStatus.additionInfo(Constants.TASK_ID, entry.getKey());
			subStatus.additionInfo(Constants.NEXT_EXECUTE_TIME, context.nextScheduledExecutionTime());
			subStatus.additionInfo(Constants.LAST_START_TIME, context.lastStartTime());
			subStatus.additionInfo(Constants.LAST_COMPLETION_TIME, context.lastCompletionTime());
			subStatus.additionInfo(Constants.EXECUTE_ADDRESS, context.executeAddress());
			subStatus.additionInfo(Constants.LAST_EXCEPTION_TIME, context.lastCompletionTime());
			subStatus.additionInfo(Constants.THROWABLE, context.getT());
			csStatus.addSubCsStatus(subStatus);
		}
		return csStatus;
	}
	
	private void scheduleTasks() {
		if (this.cronTaskMap != null) {
			for (Entry<String, TimerTask> entry : cronTaskMap.entrySet()) {
				TimerTask task = entry.getValue();
				Trigger trigger = new CronTrigger(task.getCron());
				handlings.put(entry.getKey(), this.taskScheduler.localSchedule(task, trigger));
			}
		}
		if (this.clusterTaskMap != null) {
			for (Entry<String, TimerTask> entry : clusterTaskMap.entrySet()) {
				TimerTask task = entry.getValue();
				Trigger trigger = new CronTrigger(task.getCron());
				handlings.put(entry.getKey(), this.taskScheduler.clusterSchedule(task, trigger,task.getCluster()));
			}
		}
		
	}

	public void destroy() {
		for (Entry<String, HandlingRunnable> entry : this.handlings.entrySet()) {
			entry.getValue().cancel(true);
			handlings.remove(entry.getKey());
		}
		this.taskScheduler.shutdown();
	}
}
