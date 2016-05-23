package com.yy.cs.base.task.log;

import java.util.List;

/**
 * Created by peiquan on 2016/5/22.
 */
public class ConsoleTaskLogHandler implements TaskLogHandler {

    @Override
    public void dealWithTaskLog(TaskLog log, List<TaskBizLog> taskBizLogs) {
        System.out.println(log);
        System.out.println(taskBizLogs);
    }
}
