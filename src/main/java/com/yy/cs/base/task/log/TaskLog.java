package com.yy.cs.base.task.log;

import com.yy.cs.base.status.StatusCode;

import java.util.Date;

public class TaskLog {

    private String taskKey;
    private Date nextScheduledExecutionTime;
    private Date lastStartTime;
    private Date lastCompletionTime;
    private Date exceptionTime;
    private Throwable throwable;
    private Boolean isTimeOut;
    private StatusCode statusCode;

    public TaskLog() {

    }
    private TaskLog(Builder builder) {
        setTaskKey(builder.taskKey);
        setNextScheduledExecutionTime(builder.nextScheduledExecutionTime);
        setLastStartTime(builder.lastStartTime);
        setLastCompletionTime(builder.lastCompletionTime);
        setExceptionTime(builder.exceptionTime);
        setThrowable(builder.throwable);
        isTimeOut = builder.isTimeOut;
        setStatusCode(builder.statusCode);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(TaskLog copy) {
        Builder builder = new Builder();
        builder.taskKey = copy.taskKey;
        builder.nextScheduledExecutionTime = copy.nextScheduledExecutionTime;
        builder.lastStartTime = copy.lastStartTime;
        builder.lastCompletionTime = copy.lastCompletionTime;
        builder.exceptionTime = copy.exceptionTime;
        builder.throwable = copy.throwable;
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

    public Date getNextScheduledExecutionTime() {
        return nextScheduledExecutionTime;
    }

    public void setNextScheduledExecutionTime(Date nextScheduledExecutionTime) {
        this.nextScheduledExecutionTime = nextScheduledExecutionTime;
    }

    public Date getLastStartTime() {
        return lastStartTime;
    }

    public void setLastStartTime(Date lastStartTime) {
        this.lastStartTime = lastStartTime;
    }

    public Date getLastCompletionTime() {
        return lastCompletionTime;
    }

    public void setLastCompletionTime(Date lastCompletionTime) {
        this.lastCompletionTime = lastCompletionTime;
    }

    public Date getExceptionTime() {
        return exceptionTime;
    }

    public void setExceptionTime(Date exceptionTime) {
        this.exceptionTime = exceptionTime;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
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
                ", nextScheduledExecutionTime=" + nextScheduledExecutionTime +
                ", lastStartTime=" + lastStartTime +
                ", lastCompletionTime=" + lastCompletionTime +
                ", exceptionTime=" + exceptionTime +
                ", throwable=" + throwable +
                ", isTimeOut=" + isTimeOut +
                ", statusCode=" + statusCode +
                '}';
    }

    public static final class Builder {
        private String taskKey;
        private Date nextScheduledExecutionTime;
        private Date lastStartTime;
        private Date lastCompletionTime;
        private Date exceptionTime;
        private Throwable throwable;
        private Boolean isTimeOut;
        private StatusCode statusCode;

        private Builder() {
        }

        public Builder taskKey(String val) {
            taskKey = val;
            return this;
        }

        public Builder nextScheduledExecutionTime(Date val) {
            nextScheduledExecutionTime = val;
            return this;
        }

        public Builder lastStartTime(Date val) {
            lastStartTime = val;
            return this;
        }

        public Builder lastCompletionTime(Date val) {
            lastCompletionTime = val;
            return this;
        }

        public Builder exceptionTime(Date val) {
            exceptionTime = val;
            return this;
        }

        public Builder throwable(Throwable val) {
            throwable = val;
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
