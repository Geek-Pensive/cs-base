package com.yy.cs.base.redis;

/*
 * Copyright (c) 2012 duowan.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with duowan.com.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.yy.cs.base.thrift.exception.CsRedisRuntimeException;

/**
 * @author hongyuan
 * @author haoqing </br> redis client线程池工厂类
 */
public class RedisClientFactory extends JedisPoolConfig{

	private static final Logger log = LoggerFactory
			.getLogger(RedisClientFactory.class);

	private volatile List<JedisPool> redisMasterPool = new ArrayList<JedisPool>();
	
	private volatile List<JedisPool> redisSlavePool = new ArrayList<JedisPool>();
	
	private int totalServersSize;
	
	private int masterServerSize;
	
	private int slaveServerSize;
	
	private ReentrantLock lock = new  ReentrantLock();
	
	private AtomicInteger atomitMasterCount = new AtomicInteger(0);
	private AtomicInteger atomitSlaveCount = new AtomicInteger(0);
	
	
	private List<String> redisServers;
	
	/**
	 * 从redisMasterPool中随机获取pool
	 * @return  
	 */
	public JedisPool getMasterPool() {
		
		if(masterServerSize <= 0){
			return getSlavePool();
		}
		int currentIndex = atomitMasterCount.getAndIncrement();
		currentIndex = currentIndex % masterServerSize;
		JedisPool jedisPool = redisMasterPool.get(currentIndex);
		return jedisPool;
	}

	
	/**
	 * 从redisSlavePool中随机获取pool,当前pool无法获取jedis连接时，切换到其他的Jedispool
	 * @return 
	 */
	public JedisPool getSlavePool() {
		int currentIndex = atomitSlaveCount.getAndIncrement();
		currentIndex = currentIndex % slaveServerSize;
		JedisPool jedisPool = redisSlavePool.get(currentIndex);
		return jedisPool;
	}



	public void setRedisServers(List<String> redisServers) {
		if(redisServers == null || redisServers.size() ==0){
			throw new CsRedisRuntimeException("redisServers couldn't be null");
		}
		this.redisServers = redisServers;
		this.totalServersSize = redisServers.size();
	}
	
	/**
	 * 初始化
	 */
	public void init() {
		/**
		 * 在Master池上操作时,如果异常会重新init.操作master有可能会被并发。
		 */
		if(lock.isLocked()){
			return;
		}
		lock.lock();
		try {
			JedisPool pool = null;
			Jedis jedis = null;
			List<JedisPool> newMasterPool = new ArrayList<JedisPool>();
			List<JedisPool> newRslavePool = new ArrayList<JedisPool>();
			for (int i = 0; i < totalServersSize; i++) {
				String[] strArray = RedisUtils.parseServerInfo(redisServers
						.get(i));
				String ip = strArray[0];
				int port = Integer.valueOf(strArray[1]);
				String password = strArray[2];
				int timeout = strArray[3] != null ? Integer
						.valueOf(strArray[3]) : 10000;// 默认是10秒
				pool = new JedisPool(this, ip, port, timeout, password);
				try {
					jedis = pool.getResource();
				} catch (Exception e) {
					log.warn(e.getMessage(), e);
					if (jedis != null) {
						pool.returnBrokenResource(jedis);
					}
					continue;
				}
				String info = jedis.info();
				boolean isMaster = RedisUtils.isMaster(info);
				// 主实例
				if (isMaster == true) {
					newMasterPool.add(pool);
					// 从实例
				} else {
					newRslavePool.add(pool);
				}
				// 释放
				pool.returnResource(jedis);
			}
			if (newMasterPool.size() != 0) {
				List<JedisPool> oldMasterPool = redisMasterPool;
				redisMasterPool = newMasterPool;
				this.masterServerSize = redisMasterPool.size();
				destroy(oldMasterPool);
			}
			if (newRslavePool.size() != 0) {
				List<JedisPool> oldRslavePool = redisSlavePool;
				redisSlavePool = newRslavePool;
				this.slaveServerSize = redisSlavePool.size();
				destroy(oldRslavePool);
			}
		} finally {
			lock.unlock();
		}
	}	
	
	/**
	 * 销毁操作
	 */
	public void destroy(List<JedisPool> pool){
		if(pool != null && pool.size() != 0){
			for(JedisPool p : pool){
				try {
					p.destroy();
		        } catch (Throwable e) {
		            log.warn(e.getMessage(),e);
		        }
			}
		}
	}
	
	/**
	 * 销毁操作
	 */
	public void destroy(){
		if(redisMasterPool != null && redisMasterPool.size() != 0){
			for(JedisPool p : redisMasterPool){
				p.destroy();
			}
		}
		if(redisSlavePool != null && redisSlavePool.size() != 0){
			for(JedisPool p : redisSlavePool){
				p.destroy();
			}
		}
	}
	
 }
