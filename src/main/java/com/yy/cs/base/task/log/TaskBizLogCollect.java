package com.yy.cs.base.task.log;

import org.slf4j.helpers.MessageFormatter;

import java.util.ArrayList;
import java.util.List;

public class TaskBizLogCollect{

    private List<TaskBizLog> logs = new ArrayList<>();

    public void debug(String log) {
        logs.add(new TaskBizLog(TaskBizLog.TaskBizLogLevel.DEBUG,log));
    }

    public void debug(String format,Object... objects) {
        String formatMsg = MessageFormatter.arrayFormat(format, objects).getMessage();
        logs.add(new TaskBizLog(TaskBizLog.TaskBizLogLevel.DEBUG,formatMsg));
    }

    public void info(String log) {
        logs.add(new TaskBizLog(TaskBizLog.TaskBizLogLevel.INFO,log));
    }

    public void info(String format,Object... objects) {
        String formatMsg = MessageFormatter.arrayFormat(format, objects).getMessage();
        logs.add(new TaskBizLog(TaskBizLog.TaskBizLogLevel.INFO,formatMsg));
    }

    public void warn(String log) {
        logs.add(new TaskBizLog(TaskBizLog.TaskBizLogLevel.WARN,log));
    }

    public void warn(String format,Object... objects) {
        String formatMsg = MessageFormatter.arrayFormat(format, objects).getMessage();
        logs.add(new TaskBizLog(TaskBizLog.TaskBizLogLevel.WARN,formatMsg));
    }

    public void error(String log) {
        logs.add(new TaskBizLog(TaskBizLog.TaskBizLogLevel.ERROR,log));
    }

    public void error(String format,Object... objects) {
        String formatMsg = MessageFormatter.arrayFormat(format, objects).getMessage();
        logs.add(new TaskBizLog(TaskBizLog.TaskBizLogLevel.ERROR,formatMsg));
    }

    public List<TaskBizLog> getLogs() {
        return logs;
    }
}
