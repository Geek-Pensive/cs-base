package com.yy.cs.base.task.log;

import org.slf4j.Logger;

public class TaskBizLog {

    private TaskBizLogLevel taskBizLogLevel;

    private String logMsg;

    public TaskBizLog(TaskBizLogLevel taskBizLogLevel, String logMsg) {
        this.taskBizLogLevel = taskBizLogLevel;
        this.logMsg = logMsg;
    }

    public TaskBizLogLevel getTaskBizLogLevel() {
        return taskBizLogLevel;
    }

    public void setTaskBizLogLevel(TaskBizLogLevel taskBizLogLevel) {
        this.taskBizLogLevel = taskBizLogLevel;
    }

    public String getLogMsg() {
        return logMsg;
    }

    public void setLogMsg(String logMsg) {
        this.logMsg = logMsg;
    }

    @Override
    public String toString() {
        return "TaskBizLog{" +
                "taskBizLogLevel=" + taskBizLogLevel +
                ", logMsg='" + logMsg + '\'' +
                '}';
    }

    public void log(Logger logger){
    		if(logger == null) {
    			return;
    		}
    		if(TaskBizLogLevel.DEBUG == getTaskBizLogLevel()) {
    			logger.debug("task biz log:{}", getLogMsg());
    		}
    		if(TaskBizLogLevel.INFO  == getTaskBizLogLevel()) {
    			logger.info("task biz log:{}", getLogMsg());
    		}
    		if(TaskBizLogLevel.WARN  == getTaskBizLogLevel()) {
    			logger.warn("task biz log:{}", getLogMsg());
    		}
    		if(TaskBizLogLevel.ERROR == getTaskBizLogLevel()) {
    			logger.error("task biz log:{}", getLogMsg());
    		}
    }

    public enum  TaskBizLogLevel {
        DEBUG(10000),INFO(20000), WARN(30000),ERROR(40000);
        private int level;

        TaskBizLogLevel(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }
}
