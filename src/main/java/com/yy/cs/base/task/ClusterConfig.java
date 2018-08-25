package com.yy.cs.base.task;

import com.yy.cs.base.redis.RedisClient;

/**
 * 
 * 集中式task的属性配置对象,需要注入 {@link RedisClient}
 *
 */
public class ClusterConfig {

	private RedisClient redisClient;
	
//	private RedisPoolManager redisPoolManager;
	
	private int expireLockTime = -1;	//默认没有设置:-1
	
	public RedisClient getRedisClient() {
		return this.redisClient;
	}

	public void setRedisClient(RedisClient redisClient) {
		this.redisClient = redisClient;
	}

	public ClusterConfig(){
		
	}
	/**
	 * 
	 * @param redisClient
	 */
	public ClusterConfig(RedisClient redisClient){
		this.redisClient = redisClient ; 
	}
	public int getExpireLockTime() {
		return expireLockTime;
	}

	public void setExpireLockTime(int expireLockTime) {
		this.expireLockTime = expireLockTime;
	}

	@Override
	public String toString() {
		return "ClusterConfig{" +
				"redisClient=" + redisClient +
				", expireLockTime=" + expireLockTime +
				'}';
	}

	//	public RedisPoolManager getRedisPoolManager() {
//		return redisPoolManager;
//	}
//
//	public void setRedisPoolManager(RedisPoolManager redisPoolManager) {
//		this.redisPoolManager = redisPoolManager;
//	}

}
