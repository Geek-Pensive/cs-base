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
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

import com.yy.cs.base.thrift.exception.CsRedisRuntimeException;

/**
 * @author hongyuan
 * @author haoqing </br> redis client线程池工厂类
 */
public class RedisClientFactory extends JedisPoolConfig{

	private static final Logger log = LoggerFactory
			.getLogger(RedisClientFactory.class);

	private List<JedisPool> redisMasterPool = Collections.synchronizedList(new ArrayList<JedisPool>());
	
	private List<JedisPool> redisSlavePool = Collections.synchronizedList(new ArrayList<JedisPool>());
	
	private int totalServersSize;
	
	private int masterServerSize;
	
	private int slaveServerSize;
	
	private String testKey = "$jedisMasterTestKey" ;  

	private AtomicInteger atomitMasterCount = new AtomicInteger(0);
	private AtomicInteger atomitSlaveCount = new AtomicInteger(0);
	
	
	private List<String> redisServers;
	
	/**
	 * 从redisMasterPool中随机获取pool,检测当前的当前Master是否可连接,且发送一个数据测试Master是否可写,
	 * 不可写则抛出异常，当轮训到最后一个pool如果仍然不可写，则尝试初始化一次。
	 * @return  
	 */
	public JedisPool getMasterPool() {

		Jedis jedis  = null ; 
		JedisPool jedisPool  = null ; 
		for(int i = 0 ; i<masterServerSize ; i++){
			int currentIndex = atomitMasterCount.getAndIncrement();
			currentIndex = currentIndex % masterServerSize;
			jedisPool = redisMasterPool.get(currentIndex);
			try {
				jedis = jedisPool.getResource();
				return jedisPool;
			} catch (Exception e) {
				if(i == masterServerSize-1){
					//如果所有的MasterPool都不能获取jedis,则可能是Master宕机了,自动重新初始化一次,尝试下一次能够正确获取Pool
					reload() ; 
					 throw new JedisConnectionException(
			                    "Could not get a resource from the MasterPool, Master may has shutdwon", e);
				}
			}finally{
				if(jedis != null){
					jedisPool.returnResource(jedis) ; 
				}
			}
		}
		if( masterServerSize == 0 ){
			reload();
			log.error("there is no master redis server !") ; 
		}
		return jedisPool ; 
	}

	
	/**
	 * 从redisSlavePool中随机获取pool,当前pool无法获取jedis连接时，切换到其他的Jedispool
	 * @return 
	 */
	public JedisPool getSlavePool() {
		Jedis jedis  = null ; 
		JedisPool jedisPool  = null ; 
		for(int i = 0 ; i<slaveServerSize ; i++){
			int currentIndex = atomitSlaveCount.getAndIncrement();
			currentIndex = currentIndex % slaveServerSize;
			jedisPool = redisSlavePool.get(currentIndex);
			try {
				//检测当前的当前slave是否可连接，否则切换到其他的slave
				jedis = jedisPool.getResource();
				return jedisPool;
			} catch (Exception e) {
				if(i == slaveServerSize-1){
					//当所有的slave都不可获取时,尝试获取master
					jedisPool = getMasterPool() ; 
					log.warn("All slave servers may have been shutdown !",e) ; 
					if(jedisPool == null){
						//如果master/slave都不能获取jedis,则可能是网络问题,自动重新初始化一次。
						reload() ; 
						 throw new JedisConnectionException(
				                    "Could not get a resource from the Master or Slaves, " +
				                    "please check your network or your servers may have been shut dwon", e);
					}
				}
			}finally{
				if(jedis != null){
					jedisPool.returnResource(jedis) ; 
				}
			}
		}
		//slave切换到master后，可能导致0个slave,这时尝试从master获取jedispool
		if(jedisPool == null || slaveServerSize == 0){
			jedisPool = getMasterPool() ; 
			if(jedisPool == null){
				reload();
				log.error("redis server may has not startup!") ; 
			}
		}
		return jedisPool ; 
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
	public void init(){
		JedisPool pool = null;
		Jedis jedis = null;
			for(int i = 0; i < totalServersSize; i++){
				String [] strArray = RedisUtils.parseServerInfo(redisServers.get(i));
				String ip = strArray[0];
				int port = Integer.valueOf(strArray[1]);
				String password = strArray[2];
				int timeout = strArray[3] != null? Integer.valueOf(strArray[3]) : 10000;//默认是10秒
				pool = new JedisPool(this, ip, port,timeout, password);
				try {
					jedis = pool.getResource();
				} catch (Exception e) {
					log.warn(e.getMessage(), e) ; 
					if(jedis != null){
						pool.returnResource(jedis);
					}
					continue ; 
				}
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
	/**
	 * 重新加载master/slave信息
	 */
	public synchronized void  reload(){
			destroy() ; 
			redisMasterPool.clear() ; 
			redisSlavePool.clear() ; 
			atomitMasterCount = new AtomicInteger(0);
			atomitSlaveCount = new AtomicInteger(0);
			init();
	}
	
 }
