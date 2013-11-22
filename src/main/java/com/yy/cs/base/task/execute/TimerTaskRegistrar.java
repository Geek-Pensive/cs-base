package com.yy.cs.base.task.execute;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.yy.cs.base.task.TimerTask;
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

	public TaskContext getTaskContext(String id) {
		HandlingRunnable handlingRunnable = handlings.get(id);
		return handlingRunnable.getContext();
	}
	
	public Map<String,TaskContext> getAllTaskContext() {
		Map<String , TaskContext> contexts= new HashMap<String,TaskContext>();
		for (Entry<String, HandlingRunnable> entry : this.handlings.entrySet()) {
			contexts.put(entry.getKey(), entry.getValue().getContext());
		}
		return contexts;
	}
	
	private void scheduleTasks() {
		if (this.cronTaskMap != null) {
			for (Entry<String, TimerTask> entry : cronTaskMap.entrySet()) {
				TimerTask task = entry.getValue();
				Trigger trigger = new CronTrigger(task.getCron());
				handlings.put(entry.getKey(), this.taskScheduler.localSchedule(task, trigger));
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
