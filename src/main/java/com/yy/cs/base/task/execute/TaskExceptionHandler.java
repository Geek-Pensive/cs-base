package com.yy.cs.base.task.execute;

import com.yy.cs.base.task.Task;

public interface TaskExceptionHandler {

    void handle(Task task,Throwable ex);
}
