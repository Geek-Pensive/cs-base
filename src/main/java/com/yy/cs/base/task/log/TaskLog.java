package com.yy.cs.base.task.log;

import com.yy.cs.base.status.StatusCode;

import java.util.Date;

public class TaskLog {

    /**
     * task 标识
     */
    private String taskKey;
    /**
     * 本地 task 计划开始执行的时间
     */
    private Date scheduledExecutionTime;
    /** task 实际开始执行的时间 */
    private Date startTime;
    /** task 实际结束执行的时间 */
    private Date completionTime;
    /** 是否超时 */
    private Boolean isTimeOut;
    /** task 状态码 */
    private StatusCode statusCode;

    public TaskLog() {

    }

    private TaskLog(Builder builder) {
        setTaskKey(builder.taskKey);
        setScheduledExecutionTime(builder.scheduledExecutionTime);
        setStartTime(builder.startTime);
        setCompletionTime(builder.completionTime);
        isTimeOut = builder.isTimeOut;
        setStatusCode(builder.statusCode);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(TaskLog copy) {
        Builder builder = new Builder();
        builder.taskKey = copy.taskKey;
        builder.scheduledExecutionTime = copy.scheduledExecutionTime;
        builder.startTime = copy.startTime;
        builder.completionTime = copy.completionTime;
        builder.isTimeOut = copy.isTimeOut;
        builder.statusCode = copy.statusCode;
        return builder;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public Date getScheduledExecutionTime() {
        return scheduledExecutionTime;
    }

    public void setScheduledExecutionTime(Date scheduledExecutionTime) {
        this.scheduledExecutionTime = scheduledExecutionTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Date completionTime) {
        this.completionTime = completionTime;
    }

    public Boolean getTimeOut() {
        return isTimeOut;
    }

    public void setTimeOut(Boolean timeOut) {
        isTimeOut = timeOut;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "TaskLog{" +
                "taskKey='" + taskKey + '\'' +
                ", scheduledExecutionTime=" + scheduledExecutionTime +
                ", startTime=" + startTime +
                ", completionTime=" + completionTime +
                ", isTimeOut=" + isTimeOut +
                ", statusCode=" + statusCode +
                '}';
    }

    public static final class Builder {
        private String taskKey;
        private Date scheduledExecutionTime;
        private Date startTime;
        private Date completionTime;
        private Boolean isTimeOut;
        private StatusCode statusCode;

        private Builder() {
        }

        public Builder taskKey(String val) {
            taskKey = val;
            return this;
        }

        public Builder scheduledExecutionTime(Date val) {
            scheduledExecutionTime = val;
            return this;
        }

        public Builder startTime(Date val) {
            startTime = val;
            return this;
        }

        public Builder completionTime(Date val) {
            completionTime = val;
            return this;
        }

        public Builder isTimeOut(Boolean val) {
            isTimeOut = val;
            return this;
        }

        public Builder statusCode(StatusCode val) {
            statusCode = val;
            return this;
        }

        public TaskLog build() {
            return new TaskLog(this);
        }
    }
}
