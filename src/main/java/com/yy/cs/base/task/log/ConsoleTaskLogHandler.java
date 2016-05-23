package com.yy.cs.base.task.log;

import com.yy.cs.base.task.Task;

import java.util.List;

/**
 * Created by peiquan on 2016/5/22.
 */
public class ConsoleTaskLogHandler implements TaskLogHandler {

    @Override
    public void dealWithTaskLog(Task task,TaskLog log, List<TaskBizLog> taskBizLogs) {
        System.out.println(task.getId() + log);
        System.out.println(taskBizLogs);
    }
}
