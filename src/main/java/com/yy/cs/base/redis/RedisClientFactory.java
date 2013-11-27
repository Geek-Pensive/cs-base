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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.yy.cs.base.nyy.exception.NyyRuntimeException;

/**
 * @author hongyuan
 * @author haoqing </br> redis client线程池工厂类
 */
public class RedisClientFactory extends JedisPoolConfig{

	private static final Logger log = LoggerFactory
			.getLogger(RedisClientFactory.class);

	private List<JedisPool> redisMasterPool = new ArrayList<JedisPool>();
	
	private List<JedisPool> redisSlavePool = new ArrayList<JedisPool>();
	
	private int totalServersSize;
	
	private int masterServerSize;
	
	private int slaveServerSize;

	private AtomicInteger atomitMasterCount = new AtomicInteger(0);
	private AtomicInteger atomitSlaveCount = new AtomicInteger(0);
	
	
	private List<String> redisServers;
	
	/**
	 * 从redisMasterPool中随机获取pool
	 * @return  
	 */
	public JedisPool getMasterPool() {
		int currentIndex = atomitMasterCount.getAndIncrement();
		currentIndex = currentIndex % masterServerSize;
		JedisPool jedisPool = redisMasterPool.get(currentIndex);
		return jedisPool;
	}

	
	/**
	 * 从redisSlavePool中随机获取pool
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
			throw new NyyRuntimeException("redisServers couldn't be null");
		}
		this.redisServers = redisServers;
		this.totalServersSize = redisServers.size();
	}
	
	/**
	 * 初始化
	 */
	public void init(){
		try{
			for(int i = 0; i < totalServersSize; i++){
				String [] strArray = RedisUtils.parseServerInfo(redisServers.get(i));
				String ip = strArray[0];
				int port = Integer.valueOf(strArray[1]);
				String password = strArray[2];
				int timeout = strArray[3] != null? Integer.valueOf(strArray[3]) : 10000;//默认是10秒
				JedisPool pool = new JedisPool(this, ip, port,timeout, password);
				//check master or slave
				Jedis jedis = pool.getResource();
				String info = jedis.info();
				boolean isMaster = RedisUtils.isMaster(info);
				//主实例
				if(isMaster == true){
					redisMasterPool.add(pool);
				//从实例
				}else{
					redisSlavePool.add(pool);
				}
				//释放
				pool.returnResource(jedis);
			}
			//如果没有master 避免用户直接获取master进行操作导致错误
			if(redisMasterPool.size() == 0){
				redisMasterPool = redisSlavePool;
			}
			//如果没有slave 避免用户直接获取slave进行操作导致错误
			if(redisSlavePool.size() == 0){
				redisSlavePool = redisMasterPool;
			}
		}catch(Exception e){
			log.error("occur error in init, error = {}", e);
		}
		this.masterServerSize = redisMasterPool.size();
		this.slaveServerSize = redisSlavePool.size();
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
