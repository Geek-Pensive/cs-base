package com.yy.cs.base.task.log;

/**
 * task 日志处理，可将日志记录的文件，或者上报
 */
public interface TaskLogHandler {

    /**
     * 处理 taskLog
     */
    void dealWithTaskLog(TaskLog log);
}
