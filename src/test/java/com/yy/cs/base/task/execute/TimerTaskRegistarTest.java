package com.yy.cs.base.task.execute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import junit.framework.Assert;

import org.junit.Test;

import com.yy.cs.base.redis.RedisClient;
import com.yy.cs.base.redis.RedisClientFactory;
import com.yy.cs.base.status.CsStatus;
import com.yy.cs.base.task.ClusterConfig;
import com.yy.cs.base.task.TimerTask;
import com.yy.cs.base.task.TimerTaskManager;
import com.yy.cs.base.task.TimerTaskTest;
import com.yy.cs.base.task.thread.TaskScheduler;
import com.yy.cs.base.task.thread.ThreadPoolTaskScheduler;

public class TimerTaskRegistarTest {

	TimerTaskManager timerTaskManage = new TimerTaskManager();
	private TimerTaskRegistrar registrar;
	private Map<String, TimerTask> taskMap;
	private TimerTaskTest task_1;
	private TimerTaskTest task_2;
	private RedisClient redisClient;
	public ClusterConfig cluster;
	
	
	private int poolSize = 2;

	/**
	 * 初始化taskMap，设入一个cronTask和一个clusterTask
	 */
	public void getTaskMap() {
		task_1 = new TimerTaskTest();
		task_1.setCron("*/10 * * * * *");
		RunnableInit rInit = new RunnableInit();
		List<String> list = new ArrayList<String>();
		list.add("127.0.0.1:6380::");
		list.add("127.0.0.1:6381::");
		RedisClientFactory factory = new RedisClientFactory(list);
		factory.init();
		redisClient = new RedisClient(factory);
		redisClient.setFactory(factory);
		task_2 = new TimerTaskTest();
		cluster = new ClusterConfig();
		rInit.addTimeTask(task_2, cluster, redisClient);
		if (taskMap == null) {
			taskMap = new HashMap<String, TimerTask>();
		}
		taskMap.put(task_1.getClass().getName() + "_task_1", task_1);
		taskMap.put(task_2.getClass().getName() + "_task_2", task_2);
	}

	/**
	 * parse方法功能测试（task分类），判断调用parse方法后，
	 * taskMap中的任务是否正确分类分别装入cronTaskMap和clusterTaskMap
	 */
	@Test
	public void testParse() {
		getTaskMap();
		registrar = new TimerTaskRegistrar();
		registrar.parse(taskMap);
		Assert.assertTrue(registrar.cronTaskMap.containsValue(task_1));
		Assert.assertTrue(registrar.clusterTaskMap.containsValue(task_2));
	}

	/**
	 * scheduleTasks方法功能测试，分别执行分类后的两种类型任务,测试定时任务是否创建成功,
	 * 判断是否能正常取到定时任务currentFuture
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testScheduleTasks(){
		getTaskMap();
		registrar = new TimerTaskRegistrar();
		registrar.parse(taskMap);
		TaskScheduler taskScheduler = new ThreadPoolTaskScheduler(poolSize);
		registrar.start(taskScheduler);
//		Map<String, HandlingRunnable>  m = registrar.getHandlings();
//		for(Entry<String, TimerTask> en :taskMap.entrySet()){
//			 m.get(en.getKey()).get();
//		}
		HandlingRunnable hrtask_1 = registrar.getHandlings().get(task_1.getClass().getName() + "_task_1");
		HandlingRunnable hrtask_2 = registrar.getHandlings().get(task_2.getClass().getName() + "_task_2");
		Assert.assertNotNull(hrtask_1.currentFuture);
		Assert.assertNotNull(hrtask_2.currentFuture);
		registrar.destroy();
	}
	
	@Test
	public void testGetCsStatus() throws InterruptedException, ExecutionException{
		getTaskMap();
		registrar = new TimerTaskRegistrar();
		registrar.parse(taskMap);
		TaskScheduler taskScheduler = new ThreadPoolTaskScheduler(poolSize);
		registrar.start(taskScheduler);
		
		Map<String, HandlingRunnable>  m = registrar.getHandlings();
		for(Entry<String, TimerTask> en :taskMap.entrySet()){
			 m.get(en.getKey()).get();
		}
		CsStatus csStatus = registrar.getCsStatus();
		Assert.assertNotNull(csStatus);
		Assert.assertTrue(csStatus.getTotalNumber() == 3);
//		Assert.assertTrue(csStatus.getFailNumber() == 2);
		List<CsStatus> subCsStatus = csStatus.getSubCsStatus();
		Assert.assertTrue(subCsStatus.size() == 2);
		registrar.destroy();
	}

}
