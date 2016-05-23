package com.yy.cs.base.task.log;

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
