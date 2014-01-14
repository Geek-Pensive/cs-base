package com.yy.cs.base.task.execute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.yy.cs.base.redis.RedisClient;
import com.yy.cs.base.redis.RedisClientFactory;
import com.yy.cs.base.task.ClusterConfig;
import com.yy.cs.base.task.TimerTaskTest;
import com.yy.cs.base.task.thread.NamedThreadFactory;
import com.yy.cs.base.task.trigger.CronTrigger;
import com.yy.cs.base.task.trigger.Trigger;

public class ClusterTriggerRunnableTest {

	public TimerTaskTest timetask;
	public Trigger trigger;
	public ScheduledExecutorService executor;
	public ClusterConfig cluster;
	public RedisClient redisClient;
	public final int poolSize = 2;

	String testAddr;
	Date testExcTime;
	Date testCompTime;
	Date testlastStartTime;
	Date nextScheduledExecutionTime;
	Throwable t;

	@Before
	public void init() {
		RunnableInit rInit = new RunnableInit();
		redisClient = new RedisClient();
		rInit.setRedis(redisClient);
		timetask = new TimerTaskTest();
		cluster = new ClusterConfig();
		rInit.addTimeTask(timetask,cluster,redisClient);
		trigger = new CronTrigger(timetask.getCron());
		executor = rInit.setExecutor(executor,poolSize);
	}

	/**
	 * 测试ClusterTriggerRunnable的run方法 测试所用task是一个抛出空指针异常的task 1.异常时间 早于等于完成时间
	 * 2.完成时间晚于等于开始时间 3.开始时间，下次执行时间能被五整除 4.异常Throwable ！= null 5.执行地址非空
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testRun() throws InterruptedException, ExecutionException,
			TimeoutException {

		HandlingRunnable cRunnable = new ClusterTriggerRunnable(timetask, trigger,
				executor, cluster).schedule();
		Assert.assertNotNull("scheduledExecutionTime不为空，可得到非空HandlingRunnable",cRunnable.scheduledExecutionTime);
		cRunnable.get(10000, TimeUnit.MILLISECONDS);
//		cRunnable.isTimeout();
		testAddr = cRunnable.context.executeAddress();
		testExcTime = cRunnable.context.getExceptionTime();
		testCompTime = cRunnable.context.lastCompletionTime();
		testlastStartTime = cRunnable.context.lastStartTime();
		nextScheduledExecutionTime = cRunnable.context
				.nextScheduledExecutionTime();
		t = cRunnable.context.getT();
		Assert.assertNull("任务无异常，异常时间为null",testExcTime);
		// Assert.assertTrue(testExcTime.compareTo(testCompTime)<=0);
		Assert.assertTrue(testCompTime.compareTo(testlastStartTime) >= 0);
		Assert.assertTrue(testlastStartTime.getSeconds() % 5 == 0
				&& nextScheduledExecutionTime.getSeconds() % 5 == 0);
		Assert.assertNull("任务无异常，异常t为null",t);
		// Assert.assertNotNull(t);
		Assert.assertNotNull(testAddr);
	}

	@Test
	public void testRunBlankTrigger(){
		trigger = null;
		try {
			HandlingRunnable cRunnable = new ClusterTriggerRunnable(timetask, trigger,
					executor, cluster).schedule();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Assert.assertEquals("trigger must not be null", e.getMessage());
		}
	}
	
	@Test
	public void testRunBlankClusterConfig(){
		cluster = null;
		try {
			HandlingRunnable cRunnable = new ClusterTriggerRunnable(timetask, trigger,
					executor, cluster);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Assert.assertEquals("clusterConfig must not be null", e.getMessage());
		}
	}
	
	@Test
	public void testRunBlankRedisClient(){
		cluster.setRedisClient(null);
		try {
			HandlingRunnable cRunnable = new ClusterTriggerRunnable(timetask, trigger,
					executor, cluster);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Assert.assertEquals("clusterConfig must not be null", e.getMessage());
		}
	}
	
	
	public class Daemon implements Runnable {

		public void run() {
			for (long i = 0; i < 9999999L; i++) {
				System.out.println("守护线程第" + i + "次执行！");
				try {
					Thread.sleep(7);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class Common extends Thread {

		public void run() {
			for (int i = 0; i < 10; i++) {
				System.out.println("线程1第" + i + "次执行！");
				try {
					Thread.sleep(7);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args) {
		ClusterTriggerRunnableTest crtt = new ClusterTriggerRunnableTest();
		Thread t1 = crtt.new Common();
		Thread t2 = new Thread(crtt.new Daemon());
		t2.setDaemon(true); // 设置为守护线程
		t2.start();
		t1.start();
	}
}
