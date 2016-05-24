package com.yy.cs.base.task.log;

import com.yy.cs.base.task.Task;

import java.util.List;

public class TaskManagerInfo {

    private String appId;
    private int poolSize;
    private List<Task> taskList;

    public TaskManagerInfo(String appId, int poolSize, List<Task> taskList) {
        this.appId = appId;
        this.poolSize = poolSize;
        this.taskList = taskList;
    }

    public String getAppId() {
        return appId;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public List<Task> getTaskList() {
        return taskList;
    }
}
