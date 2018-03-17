package com.yy.cs.base.task.log;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.task.Task;
import com.yy.cs.base.task.log.metrics.TaskMetricsClient;

public class MetricsAndLogHandler implements TaskLogHandler{

	private Logger logger = LoggerFactory.getLogger(MetricsAndLogHandler.class);

	private boolean activeMetrics = true;
	private boolean activeLog     = true;
	private String metricsAppName = "";

	@Override
	public void dealWithTaskLog(TaskManagerInfo taskManagerInfo, Task task, TaskLog log, List<TaskBizLog> taskBizLogs) {
		if(StringUtils.isBlank(metricsAppName) || log == null) {
			logger.error("[dealWithTaskLog] metricsAppName or log should not be blank or it will do nothing!");
			return;
		}
		try{
			if(activeMetrics) {
				handleMetricsReport(log);
			}
			if(activeLog) {
				handleLogPrint(log, taskBizLogs);
			}
		}catch(Exception e) {
			logger.error("[dealWithTaskLog] error ,please check!"
					+ "activeMetrics:{}, activeLog:{}, metricsAppName:{},"
					+ " taskManagerInfo:{}, task:{}, log:{}, taskBizLogs:{}",
					activeMetrics, activeLog, metricsAppName, 
					taskManagerInfo, task, log, taskBizLogs, e);
		}
	}

	private void handleLogPrint(TaskLog log, List<TaskBizLog> taskBizLogs) {
		logger.info("[handleLogPrint] taskLog:{}", log);
		if(taskBizLogs == null || taskBizLogs.isEmpty()) {
			logger.info("[handleLogPrint] TaskBizLogs is empty.");
			return;
		}
		for(TaskBizLog taskBizLog : taskBizLogs) {
			taskBizLog.log(logger);
		}
	}

	private void handleMetricsReport(TaskLog log) {
		if(StringUtils.isBlank(log.getTaskKey())) {
			logger.error("[handleMetricsReport] TaskLog key is blank and it will not report to metrics.");
			return;
		}
		if(log.getStartTime() == null || log.getCompletionTime() == null) {
			logger.error("[handleMetricsReport] TaskLog startTime or completionTime is null and it will not report to metrics");
			return;
		}
		if(log.getStatusCode() == null) {
			logger.error("[handleMetricsReport] TaskLog statusCode is null and it will not report to metrics");
			return;
		}
		String uri = log.getTaskKey();
		int code = log.getStatusCode().getCode(); // 0:成功, 1:有异常, 2:超时。目前是默认映射关系无需再转换
		long time = log.getCompletionTime().getTime() - log.getStartTime().getTime();
		logger.info("[handleMetricsReport] Task metrics data report.uri:{}, code:{}, time:{}ms", uri, code, time);
		TaskMetricsClient.reportDataInner(uri, code, time);
	}

	public boolean isActiveMetrics() {
		return activeMetrics;
	}

	public void setActiveMetrics(boolean activeMetrics) {
		this.activeMetrics = activeMetrics;
	}

	public boolean isActiveLog() {
		return activeLog;
	}

	public void setActiveLog(boolean activeLog) {
		this.activeLog = activeLog;
	}

	public String getMetricsAppName() {
		return metricsAppName;
	}

	public void setMetricsAppName(String metricsAppName) {
		this.metricsAppName = metricsAppName;
		//init TaskMetricsClient
		TaskMetricsClient.init(getMetricsAppName());
	}



}
