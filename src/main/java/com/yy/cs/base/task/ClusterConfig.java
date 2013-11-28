package com.yy.cs.base.task;

import com.yy.cs.base.redis.RedisClient;

public class ClusterConfig {

	private RedisClient redisClient;
	
//	private RedisPoolManager redisPoolManager;
	
//	private int expireLockTime = -1;	//默认没有设置:-1
	
	public RedisClient getRedisClient() {
		return this.redisClient;
	}

	public void setRedisClient(RedisClient redisClient) {
		this.redisClient = redisClient;
	}

//	public int getExpireLockTime() {
//		return expireLockTime;
//	}
//
//	public void setExpireLockTime(int expireLockTime) {
//		this.expireLockTime = expireLockTime;
//	}
	
//	public RedisPoolManager getRedisPoolManager() {
//		return redisPoolManager;
//	}
//
//	public void setRedisPoolManager(RedisPoolManager redisPoolManager) {
//		this.redisPoolManager = redisPoolManager;
//	}

}
