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
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.yy.cs.base.nyy.exception.NyyRuntimeException;

/**
 * @author hongyuan
 * @author haoqing </br> redis client线程池管理类
 */
public class RedisPoolManager {

	private static final Logger log = LoggerFactory
			.getLogger(RedisPoolManager.class);

	private JedisPoolConfig poolConfig;
	
	private List<JedisPool> redisMasterPool = new ArrayList<JedisPool>();
	
	private List<JedisPool> redisSlavePool = new ArrayList<JedisPool>();
	
	private int totalServersSize;
	
	private int masterServerSize;
	
	private int slaveServerSize;

	private AtomicInteger atomitMasterCount = new AtomicInteger(0);
	private AtomicInteger atomitSlaveCount = new AtomicInteger(0);
	
	public final static JedisPoolConfig DEFAULT_CONFIG = new JedisPoolConfig(){
		{
		setMaxActive(300);
		setMaxIdle(100);
		setMaxWait(50);
		}
	};
	
	private List<String> redisServers;
	
	/**
	 * 从redisMasterPool中随机获取jedis连接
	 * @return   如果出现异常，则返回null
	 */
	public RedisClient getMasterJedis() {
		int currentIndex = atomitMasterCount.getAndIncrement();
		currentIndex = currentIndex % masterServerSize;
		JedisPool jedisPool = redisMasterPool.get(currentIndex);
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (jedis.isConnected()) {
				RedisClient redisClient = new RedisClient();
				redisClient.setJedis(jedis);
				redisClient.setJedisPool(jedisPool);
				return redisClient;
			} else {
				jedisPool.returnBrokenResource(jedis);
				log.error("Get jedis connection from pool but not connected.");
			}
		} catch (JedisConnectionException e) {
			log.error("Get jedis connection from redisMasterPool list index:{}",
					currentIndex, e);
			if (jedis != null) {
				log.warn("Return broken resource:" + jedis);
				jedisPool.returnBrokenResource(jedis);
			}
		}
		return null;
	}

	
	/**
	 * 从redisSlavePool中随机获取jedis连接
	 * @return  如果出现异常，则返回null
	 */
	public RedisClient getSlaveJedis() {
		int currentIndex = atomitSlaveCount.getAndIncrement();
		currentIndex = currentIndex % slaveServerSize;
		JedisPool jedisPool = redisSlavePool.get(currentIndex);
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (jedis.isConnected()) {
				RedisClient redisClient = new RedisClient();
				redisClient.setJedis(jedis);
				redisClient.setJedisPool(jedisPool);
				return redisClient;
			} else {
				jedisPool.returnBrokenResource(jedis);
				log.error("Get jedis connection from pool but not connected.");
			}
		} catch (JedisConnectionException e) {
			log.error("Get jedis connection from redisMasterPool list index:{}",
					currentIndex, e);
			if (jedis != null) {
				log.warn("Return broken resource:" + jedis);
				jedisPool.returnBrokenResource(jedis);
			}
		}
		return null;
	}

	

	/**
	 * 设置poolConfig, 如果没有poolConfig，将使用默认的poolConfig
	 * @param poolConfig
	 */
	public void setPoolConfig(JedisPoolConfig poolConfig) {
		if(poolConfig != null){
			this.poolConfig = poolConfig;
		}
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
		if(this.poolConfig == null){
			poolConfig = DEFAULT_CONFIG;
		}
		try{
			for(int i = 0; i < totalServersSize; i++){
				String [] strArray = RedisUtils.parseServerInfo(redisServers.get(i));
				String ip = strArray[0];
				int port = Integer.valueOf(strArray[1]);
				String password = strArray.length >= 3 ? strArray[2] : null;
				int timeout = strArray.length == 4 ? Integer.valueOf(strArray[3]) : 2000;//默认是2秒
				JedisPool pool = new JedisPool(this.poolConfig, ip, port,timeout, password);
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
