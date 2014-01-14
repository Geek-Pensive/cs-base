package com.yy.cs.base.task.execute;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.yy.cs.base.task.TimerTaskTest;
import com.yy.cs.base.task.trigger.CronTrigger;
import com.yy.cs.base.task.trigger.Trigger;

public class LocalTriggerRunnableTest {

	public TimerTaskTest timetask;
	public Trigger trigger;
	public ScheduledExecutorService executor;
	public final int poolSize = 2;
	
	Date testExcTime;
	Date testCompTime;
	Date testlastStartTime;
	Date nextScheduledExecutionTime;
	Throwable t;
	
	@Before
	public void init(){
		RunnableInit rInit = new RunnableInit();
		timetask = new TimerTaskTest();
		timetask.setCron("*/5 * * * * *");
		trigger = new CronTrigger(timetask.getCron());
		executor = rInit.setExecutor(executor,poolSize);
	}
	
	/**
	 * 测试run方法，判断跑一次run方法，应该得到的各个字段返回值是否符合逻辑
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testLocalRun() throws InterruptedException, ExecutionException, TimeoutException{
		HandlingRunnable lRunnable = new LocalTriggerRunnable(timetask, trigger, executor).schedule();
		lRunnable.get(10000, TimeUnit.MILLISECONDS);
		testExcTime = lRunnable.context.getExceptionTime();
		testCompTime = lRunnable.context.lastCompletionTime();
		testlastStartTime = lRunnable.context.lastStartTime();
		nextScheduledExecutionTime = lRunnable.context
				.nextScheduledExecutionTime();
		t = lRunnable.context.getT();
		Assert.assertNull("任务无异常，异常时间为null",testExcTime);
		// Assert.assertTrue(testExcTime.compareTo(testCompTime)<=0);
		Assert.assertTrue(testCompTime.compareTo(testlastStartTime) >= 0);
		Assert.assertTrue(testlastStartTime.getSeconds() % 5 == 0
				&& nextScheduledExecutionTime.getSeconds() % 5 == 0);
		Assert.assertNull("任务无异常，异常t为null",t);
		// Assert.assertNotNull(t);
	}
	
	@Test
	public void testRunBlankTrigger(){
		trigger = null;
		try {
			HandlingRunnable lRunnable = new LocalTriggerRunnable(timetask, trigger,
					executor).schedule();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Assert.assertEquals("trigger must not be null", e.getMessage());
		}
	}
}
