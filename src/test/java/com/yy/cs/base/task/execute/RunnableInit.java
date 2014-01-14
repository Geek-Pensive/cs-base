package com.yy.cs.base.task.execute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.yy.cs.base.redis.RedisClient;
import com.yy.cs.base.redis.RedisClientFactory;
import com.yy.cs.base.task.ClusterConfig;
import com.yy.cs.base.task.TimerTaskTest;
import com.yy.cs.base.task.thread.NamedThreadFactory;
import com.yy.cs.base.task.trigger.CronTrigger;
import com.yy.cs.base.task.trigger.Trigger;

public class RunnableInit {
	
	public void setRedis(RedisClient redisClient) {
		RedisClientFactory redisClientFactory = new RedisClientFactory();
		List<String> list = new ArrayList<String>();
		// 这里是业务要连接的redis
		list.add("172.19.103.105:6331::");
		list.add("172.19.103.105:6330::");
		list.add("172.19.103.105:6379:fdfs123:");
		redisClientFactory.setRedisServers(list);
		redisClientFactory.init();
		redisClient.setFactory(redisClientFactory);
	}

	public void addTimeTask(TimerTaskTest timetask,ClusterConfig cluster,RedisClient redisClient) {
		timetask.setCron("*/5 * * * * *");
		cluster.setRedisClient(redisClient);
		timetask.setCluster(cluster);
	}

	public ScheduledExecutorService setExecutor(ScheduledExecutorService executor,int poolSize) {
		executor = new ScheduledThreadPoolExecutor(poolSize,
				new NamedThreadFactory("cs-taks-pool"));
		return executor;
	}
}
