package com.yy.cs.base.task.log;

/**
 * Created by peiquan on 2016/5/22.
 */
public class ConsoleTaskLogHandler implements TaskLogHandler {

    @Override
    public void dealWithTaskLog(TaskLog log) {
        System.out.println(log);
    }
}
