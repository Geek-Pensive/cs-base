package com.yy.cs.base.task.log;

import java.util.List;

/**
 * task 日志处理，可将日志记录的文件，或者上报
 */
public interface TaskLogHandler {

    /**
     * 处理 taskLog
     * @param log task log 的相关信息
     * @param taskBizLogs  本次 task 产生的业务日志
     */
    void dealWithTaskLog(TaskLog log, List<TaskBizLog> taskBizLogs);
}
