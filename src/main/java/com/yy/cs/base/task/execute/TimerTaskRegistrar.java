package com.yy.cs.base.task.execute;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.status.CsStatus;
import com.yy.cs.base.status.StatusCode;
import com.yy.cs.base.task.TimerTask;
import com.yy.cs.base.task.context.Constants;
import com.yy.cs.base.task.context.Constants.MonitorType;
import com.yy.cs.base.task.context.MonitorTask;
import com.yy.cs.base.task.context.TaskContext;
import com.yy.cs.base.task.thread.NamedThreadFactory;
import com.yy.cs.base.task.thread.TaskScheduler;
import com.yy.cs.base.task.trigger.CronTrigger;
import com.yy.cs.base.task.trigger.Trigger;

/**
 * 
 * Task的注册类，存放了所以的注册task的信息
 *
 */
public class TimerTaskRegistrar {

    private static final Logger logger = LoggerFactory.getLogger(TimerTaskRegistrar.class);

    private TaskScheduler taskScheduler;

    private final ConcurrentHashMap<String, HandlingRunnable> handlings = new ConcurrentHashMap<String, HandlingRunnable>();

    private LinkedBlockingQueue<TimerTask> taskQueue = new LinkedBlockingQueue<>();

    private ExecutorService executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("task-scan", true));

    private ExecutorService dispatcherExecutor = Executors
            .newCachedThreadPool(new NamedThreadFactory("task-dispatch", true));

    public ConcurrentHashMap<String, HandlingRunnable> getHandlings() {
        return handlings;
    }

    public TaskScheduler getScheduler() {
        return this.taskScheduler;
    }

    /**
     * 执行本地的调度任务 和 redis注册的调度任务
     * 
     * @param taskScheduler
     *            任务调度器
     */
    public void start(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        scheduleTasks();
    }

    // public TaskContext getTaskContext(String id) {
    // HandlingRunnable handlingRunnable = handlings.get(id);
    // return handlingRunnable.getContext();
    // }
    /**
     * 获取任务执行的状态
     * 
     * @return
     *         任务执行完成后的状态
     */
    public CsStatus getCsStatus() {
        CsStatus csStatus = new CsStatus();
        for (Entry<String, HandlingRunnable> entry : this.handlings.entrySet()) {

            CsStatus subStatus = entry.getValue().task.getCsStatus();
            if (subStatus == null) {
                subStatus = new CsStatus();
            }
            if (subStatus.getName() == null || "".equals(subStatus.getName())) {
                subStatus.setName(entry.getKey());
            }
            TaskContext context = entry.getValue().getContext();
            subStatus.additionInfo(Constants.TASK_ID, entry.getKey());
            subStatus.additionInfo(Constants.NEXT_EXECUTE_TIME, context.nextScheduledExecutionTime());
            subStatus.additionInfo(Constants.LAST_START_TIME, context.lastStartTime());
            subStatus.additionInfo(Constants.LAST_COMPLETION_TIME, context.lastCompletionTime());
            subStatus.additionInfo(Constants.EXECUTE_ADDRESS, context.executeAddress());
            subStatus.additionInfo(Constants.LAST_EXCEPTION_TIME, context.getExceptionTime());
            subStatus.additionInfo(Constants.THROWABLE, context.getT());
            boolean istimeout = entry.getValue().isTimeout();
            subStatus.additionInfo(Constants.TIMEOUT, istimeout);
            if (istimeout) {
                subStatus.setCode(StatusCode.WRONG);
            }
            if (context.getT() != null) {
                subStatus.setCode(StatusCode.FAIL);
            }
            csStatus.addSubCsStatus(subStatus);
        }
        csStatus.setName("TimerTaskManager");
        return csStatus;
    }

    /**
     * 添加任务执行监听，并将任务执行日志信息写入文件
     * 
     * @param monitorfile
     *            文件路径
     * @param type
     *            记录监听信息方式{@link MonitorType}，将信息记录到日志或者 HTML文件中
     */
    public void addMonitorTask(final String monitorfile, final MonitorType type) {
        if (MonitorType.NONE == type) {
            return;
        }
        this.taskScheduler.scheduleWithFixedDelay(new Runnable() {
            MonitorTask monitor = new MonitorTask(monitorfile, type);

            public void run() {
                try {
                    monitor.writeTaskFile(getCsStatus());
                } catch (Throwable t) { // 防御性容错
                    logger.error("monitorTask  expection: " + t.getMessage(), t);
                }
            }
        }, 5 * 1000, 3 * 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 添加任务执行监听，并将任务执行日志信息写入文件.且定期清理生成的日志文件
     * 
     * @param monitorfile
     *            文件路径
     * @param type
     *            记录监听信息方式{@link MonitorType}，将信息记录到日志或者 HTML文件中
     * @param logSaveDays
     *            保存最近多少天的日志
     */
    public void addMonitorTask(final String monitorfile, final MonitorType type, final Integer logSaveDays) {
        if (MonitorType.NONE == type) {
            return;
        }
        this.taskScheduler.scheduleWithFixedDelay(new Runnable() {
            MonitorTask monitor = new MonitorTask(monitorfile, type);

            public void run() {
                try {
                    monitor.writeTaskFile(getCsStatus());
                } catch (Throwable t) { // 防御性容错
                    logger.error("monitorTask  expection: " + t.getMessage(), t);
                }
            }
        }, 5 * 1000, 3 * 1000, TimeUnit.MILLISECONDS);

        this.taskScheduler.scheduleWithFixedDelay(new Runnable() {
            MonitorTask monitor = new MonitorTask(monitorfile, type);

            public void run() {
                try {
                    monitor.deleteTaskLogFiles(logSaveDays);
                } catch (Throwable t) { // 防御性容错
                    logger.error("deleteTaskLogFiles  expection: " + t.getMessage(), t);
                }
            }
        }, 0, 1, TimeUnit.DAYS);
    }

    private void scheduleTasks() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            final TimerTask task = taskQueue.take();
                            dispatcherExecutor.execute(new Runnable() {
                                public void run() {
                                    handler(task);
                                };
                            });
                        } catch (Exception e) {
                            logger.error("shechule task fail ,error message:{},error:", e.getMessage(), e);
                        }
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    logger.error("shechule task fail ,error message:{},error:", e.getMessage(), e);
                    // don't need throw exception when the executor shutdown normally(such as destroy method is called)
                    // throw new RuntimeException(e);
                }

            }
        });
    }

    public void addTimerTask(String id, TimerTask task) {
        task.setId(id);
        taskQueue.add(task);
    }

    /**
     * 销毁任务的执行
     */
    public void destroy() {
        for (Entry<String, HandlingRunnable> entry : this.handlings.entrySet()) {
            if (!entry.getValue().isCancelled()) {
                entry.getValue().cancel(false);
                this.handlings.remove(entry.getKey());
            }
        }
        this.taskScheduler.shutdown();
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
        if (!dispatcherExecutor.isShutdown()) {
            dispatcherExecutor.shutdown();
        }
    }

    private void handler(TimerTask task) {
        Trigger trigger = new CronTrigger(task.getCron());
        if (task.getCluster() == null) {
            handlings.put(task.getId(), this.taskScheduler.localSchedule(task, trigger));
        } else {
            handlings.put(task.getId(), this.taskScheduler.clusterSchedule(task, trigger, task.getCluster()));
        }
    }

}
