package com.yy.cs.client.jedis;

/*
 * Copyright (c) 2012 duowan.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with duowan.com.
 */

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * @author hongyuan
 * @author haoqing </br> Jedis 线程池管理类
 */
public class JedisPoolManager {

	private static final Logger log = LoggerFactory
			.getLogger(JedisPoolManager.class);

	private static final ThreadLocal<JedisPool> currentJedisPool = new ThreadLocal<JedisPool>();

	private static final ThreadLocal<JedisPool> currentReadJedisPool = new ThreadLocal<JedisPool>();
	
	private static final ThreadLocal<JedisPool> currentWriteJedisPool = new ThreadLocal<JedisPool>();
	
	private List<JedisPool> jedisPools;
	
	private List<JedisPool> jedisWritePools;
	
	private List<JedisPool> jedisReadPools;

	private int jedisPoolsSize;
	
	private int jedisWritePoolsSize;
	
	private int jedisReadPoolsSize;

	private AtomicInteger atomitCount = new AtomicInteger(0);
	private AtomicInteger writeAtomitCount = new AtomicInteger(0);
	private AtomicInteger readAtomitCount = new AtomicInteger(0);
	
	/**
	 * 从 jedisWritePools轮询获取jedis连接 
	 * @return
	 */
	public Jedis getWriteJedis(){
		int currentIndex = writeAtomitCount.getAndIncrement();
		currentIndex = currentIndex % jedisWritePoolsSize;
		JedisPool jedisPool = jedisWritePools.get(currentIndex);
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (jedis.isConnected()) {
				currentWriteJedisPool.set(jedisPool);
				return jedis;
			} else {
				jedisPool.returnBrokenResource(jedis);
				log.error("Get writed jedis connection from writed pool but not connected.");
			}
		} catch (JedisConnectionException e) {
			log.error("Get writed jedis connection from writed pool list index:{}",
					currentIndex, e);
			if (jedis != null) {
				log.warn("Return broken resource:" + jedis);
				jedisPool.returnBrokenResource(jedis);
			}
		}
		return null;
	}
	
	/**
	 * 从 jedisReadPools读库随机获取jedis连接 
	 * @return
	 */
	public Jedis getReadJedis(){
		int currentIndex = readAtomitCount.getAndIncrement();
		currentIndex = currentIndex % jedisReadPoolsSize;
		JedisPool jedisPool = jedisReadPools.get(currentIndex);
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (jedis.isConnected()) {
				currentReadJedisPool.set(jedisPool);
				return jedis;
			} else {
				jedisPool.returnBrokenResource(jedis);
				log.error("Get read jedis connection from read-only pool but not connected.");
			}
		} catch (JedisConnectionException e) {
			log.error("Get read jedis connection from read-only pool list index:{}",
					currentIndex, e);
			if (jedis != null) {
				log.warn("Return broken resource:" + jedis);
				jedisPool.returnBrokenResource(jedis);
			}
		}
		return null;
	}
	
	
	/**
	 * 从jedisPools中随机获取jedis连接
	 * @return
	 */
	public Jedis getJedis() {
		int currentIndex = atomitCount.getAndIncrement();
		currentIndex = currentIndex % jedisPoolsSize;
		JedisPool jedisPool = jedisPools.get(currentIndex);
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (jedis.isConnected()) {
				currentJedisPool.set(jedisPool);
				return jedis;
			} else {
				jedisPool.returnBrokenResource(jedis);
				log.error("Get jedis connection from pool but not connected.");
			}
		} catch (JedisConnectionException e) {
			log.error("Get jedis connection from pool list index:{}",
					currentIndex, e);
			if (jedis != null) {
				log.warn("Return broken resource:" + jedis);
				jedisPool.returnBrokenResource(jedis);
			}
		}
		return null;
	}

	/**
	 * 归还Jedis连接
	 * 
	 * @param jedis
	 */
	public void returnJedis(Jedis jedis) {
		JedisPool jedisPool = currentJedisPool.get();
		currentJedisPool.remove();
		if (jedisPool != null) {
			jedisPool.returnResource(jedis);
		}
	}
	
	
	/**
	 * 归还readJedis连接
	 * 
	 * @param jedis
	 */
	public void returnReadJedis(Jedis jedis) {
		JedisPool jedisPool = currentReadJedisPool.get();
		currentReadJedisPool.remove();
		if (jedisPool != null) {
			jedisPool.returnResource(jedis);
		}
	}
	
	
	
	/**
	 * 归还writeJedis连接
	 * 
	 * @param jedis
	 */
	public void returnWriteJedis(Jedis jedis) {
		JedisPool jedisPool = currentWriteJedisPool.get();
		currentWriteJedisPool.remove();
		if (jedisPool != null) {
			jedisPool.returnResource(jedis);
		}
	}
	
	

	public void setJedisPools(List<JedisPool> jedisPools) {
		this.jedisPools = jedisPools;
		this.jedisPoolsSize = jedisPools.size();
	}

	public List<JedisPool> getJedisPools() {
		return jedisPools;
	}

	public List<JedisPool> getJedisWritePools() {
		return jedisWritePools;
	}

	public void setJedisWritePools(List<JedisPool> jedisWritePools) {
		this.jedisWritePools = jedisWritePools;
		this.jedisWritePoolsSize = jedisWritePools.size();
	}

	public List<JedisPool> getJedisReadPools() {
		return jedisReadPools;
	}

	public void setJedisReadPools(List<JedisPool> jedisReadPools) {
		this.jedisReadPools = jedisReadPools;
		this.jedisReadPoolsSize = jedisReadPools.size();
	}
}
