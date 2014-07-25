package com.yy.cs.base.task.execute;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.status.CsStatus;
import com.yy.cs.base.status.StatusCode;
import com.yy.cs.base.task.TimerTask;
import com.yy.cs.base.task.context.Constants;
import com.yy.cs.base.task.context.Constants.MonitorType;
import com.yy.cs.base.task.context.MonitorTask;
import com.yy.cs.base.task.context.TaskContext;
import com.yy.cs.base.task.thread.TaskScheduler;
import com.yy.cs.base.task.trigger.CronTrigger;
import com.yy.cs.base.task.trigger.Trigger;

/**
 * 
 * Task的注册类，存放了所以的注册task的信息
 *
 */
public class TimerTaskRegistrar {
	
	private static final Logger logger = LoggerFactory.getLogger(TimerTaskRegistrar.class);
	
	private TaskScheduler taskScheduler;

	Map<String, TimerTask> cronTaskMap = new HashMap<String, TimerTask>();
	
	Map<String, TimerTask> clusterTaskMap = new HashMap<String, TimerTask>();

	private final Map<String , HandlingRunnable> handlings = new HashMap<String , HandlingRunnable>();
	
	public Map<String, HandlingRunnable> getHandlings() {
		return handlings;
	}
	/**
	 * 将Task解析到cronTaskMap中
	 * @param timerTasks 
	 * 		task的Map集合
	 */
	public void parse(Map<String,TimerTask> timerTasks) {
		Set<Entry<String, TimerTask>> tasks = timerTasks.entrySet();
		for (Entry<String, TimerTask> e : tasks) {
			TimerTask task =  e.getValue();
			String id =  e.getKey();
			if (task == null) {
				continue;
			}
			if(task.getId() == null || "".equals(task.getId())){
				task.setId(id);
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

	/**
	 * 执行本地的调度任务 和 redis注册的调度任务
	 * @param taskScheduler 
	 * 		任务调度器
	 */
	public void start(TaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
		scheduleTasks();
	}

//	public TaskContext getTaskContext(String id) {
//		HandlingRunnable handlingRunnable = handlings.get(id);
//		return handlingRunnable.getContext();
//	}
	/**
	 * 获取任务执行的状态
	 * @return
	 * 		任务执行完成后的状态
	 */
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
			subStatus.additionInfo(Constants.LAST_EXCEPTION_TIME, context.getExceptionTime());
			subStatus.additionInfo(Constants.THROWABLE, context.getT());
			boolean istimeout = entry.getValue().isTimeout();
			subStatus.additionInfo(Constants.TIMEOUT, istimeout);
			if(istimeout){
				subStatus.setCode(StatusCode.WRONG);
			}
			if(context.getT() != null){
				subStatus.setCode(StatusCode.FAIL);
			}
			csStatus.addSubCsStatus(subStatus);
		}
		csStatus.setName("TimerTaskManager");
		return csStatus;
	}
	/**
	 * 添加任务执行监听，并将任务执行日志信息写入文件
	 * @param monitorfile
	 * 		文件路径
	 * @param type
	 * 		记录监听信息方式{@link MonitorType}，将信息记录到日志或者 HTML文件中 
	 */
	public void addMonitorTask(final String monitorfile,final MonitorType type){
		this.taskScheduler.scheduleWithFixedDelay(new Runnable() {
			MonitorTask monitor = new MonitorTask(monitorfile,type);
			public void run() {
                try {
                	monitor.writeTaskFile(getCsStatus());
                } catch (Throwable t) { // 防御性容错
                	logger.error("monitorTask  expection: " + t.getMessage(), t);
                }
			}
		}, 5 * 1000, 3 * 1000, TimeUnit.MILLISECONDS);
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
	/**
	 * 销毁任务的执行
	 */
	public void destroy() {
		for (Entry<String, HandlingRunnable> entry : this.handlings.entrySet()) {
			entry.getValue().cancel(true);
		}
		this.taskScheduler.shutdown();
	}
}
