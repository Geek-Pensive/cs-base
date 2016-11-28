package com.yy.cs.base.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.yy.cs.base.redis.RedisClient;
import com.yy.cs.base.redis.RedisClientFactory;
import com.yy.cs.base.status.CsStatus;
import com.yy.cs.base.task.context.Constants;

public class TimerTaskTest extends TimerTask {
	
	private Date date;
	@Override
	public void execute() throws Exception{
		Thread.sleep(5000);
		System.out.println(new Date()
				+ "-----------execute()---TimerTaskTest--------");
//		date.compareTo(date);
		
		throw new RuntimeException("error....");
	}

	
	@Test
	public  void test() throws InterruptedException{
		RedisClient redisClient = new RedisClient();
		setRedis(redisClient);
		ClusterConfig config = new ClusterConfig();
		config.setRedisClient(redisClient);
		config.setExpireLockTime(5);
		
		TimerTaskTest time = new TimerTaskTest();
		time.setId("1");
		time.setCluster(config);
		time.setCron("*/10 * * * * *");
		TimerTaskTest time2 = new TimerTaskTest();
		
		time2.setId("1");
		time2.setCluster(config);
		time2.setCron("*/10 * * * * *");
		
		TimerTaskManager timerTaskManager = new TimerTaskManager();
		TimerTaskManager timerTaskManager2 = new TimerTaskManager();
		
		timerTaskManager.addTimerTask(time);
		timerTaskManager2.addTimerTask(time2);
		
		timerTaskManager.start();
		timerTaskManager2.start();
		Thread.sleep(20000);
		CsStatus status = timerTaskManager.getCsStatus();
		System.out.println(status.getAdditionInfo(Constants.LAST_START_TIME));
		System.out.println(status.getAdditionInfo(Constants.NEXT_EXECUTE_TIME));
		
	}
	
	public static void setRedis(RedisClient redisClient) {
		RedisClientFactory redisClientFactory = new RedisClientFactory();
		List<String> list = new ArrayList<String>();
		// 这里是业务要连接的redis
		list.add("127.0.0.1:6380::");
		list.add("127.0.0.1:6381::");
		redisClientFactory.setRedisServers(list);
		redisClientFactory.init();
		redisClient.setFactory(redisClientFactory);
	}
}
