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

/**
 * 管理redis的连接池工厂类，redis连接的参数设置
 * 
 */
public class RedisClientFactory extends JedisPoolConfigAdapter {

    private static final Logger log = LoggerFactory.getLogger(RedisClientFactory.class);

    private volatile List<JedisPool> redisMasterPool = new ArrayList<JedisPool>();

    private volatile List<JedisPool> redisSlavePool = new ArrayList<JedisPool>();

    private int totalServersSize;

    private int masterServerSize;

    private int slaveServerSize;

    private ReentrantLock lock = new ReentrantLock();

    private AtomicInteger atomitMasterCount = new AtomicInteger(0);
    private AtomicInteger atomitSlaveCount = new AtomicInteger(0);

    private List<String> redisServers;

    /**
     * 构造器函数
     * 
     * @param redisServers
     *            redis服务器地址列表 ip:port:username:password
     */
    public RedisClientFactory(List<String> redisServers) {
        super();
        if (redisServers == null || redisServers.size() == 0) {
            throw new CsRedisRuntimeException("redisServers couldn't be null");
        }
        this.redisServers = redisServers;
        this.totalServersSize = redisServers.size();
        init();
    }

    public RedisClientFactory() {

    }

    /**
     * 从redisMasterPool中随机获取pool
     * 
     * @return
     *         Master的jedisPool资源池
     */
    public JedisPool getMasterPool() {

        if (masterServerSize <= 0) {
            return getSlavePool();
        }
        int currentIndex = atomitMasterCount.getAndIncrement();
        if (masterServerSize > 0) {
            currentIndex = currentIndex % masterServerSize;
        }
        JedisPool jedisPool = redisMasterPool.get(currentIndex);
        return jedisPool;
    }

    /**
     * 从redisSlavePool中随机获取pool,当前pool无法获取jedis连接时，切换到其他的Jedispool
     * 
     * @return
     *         Slave的jedisPool资源池
     */
    public JedisPool getSlavePool() {
        int currentIndex = atomitSlaveCount.getAndIncrement();
        currentIndex = currentIndex % slaveServerSize;
        JedisPool jedisPool = redisSlavePool.get(currentIndex);
        return jedisPool;
    }

    public void setRedisServers(List<String> redisServers) {
        if (redisServers == null || redisServers.size() == 0) {
            throw new CsRedisRuntimeException("redisServers couldn't be null");
        }
        this.redisServers = redisServers;
        this.totalServersSize = redisServers.size();
    }

    /**
     * 初始化
     */
    public void init() {

        if (this.totalServersSize == 0) {
            throw new IllegalArgumentException("redisServer is invalidly config,please correctly set Redis Servers.");
        }

        /**
         * 在Master池上操作时,如果异常会重新init.操作master有可能会被并发。
         */
        if (lock.isLocked()) {
            return;
        }
        lock.lock();
        try {
            JedisPool pool = null;
            Jedis jedis = null;
            List<JedisPool> newMasterPool = new ArrayList<JedisPool>();
            List<JedisPool> newRslavePool = new ArrayList<JedisPool>();
            for (int i = 0; i < totalServersSize; i++) {
                String[] strArray = RedisUtils.parseServerInfo(redisServers.get(i));
                String ip = strArray[0];
                int port = Integer.valueOf(strArray[1]);
                String password = strArray[2];
                int timeout = strArray[3] != null && !"".equals(strArray[3].trim()) ? Integer.valueOf(strArray[3])
                        : 10000;// 默认是10秒
                try {
                    pool = RedisUtils.getJedisPool(this.config, ip, port, timeout, password);
                    jedis = pool.getResource();
                } catch (Exception e) {
                    log.warn("[" + ip + ":" + port + "]" + e.getMessage(), e);
                    if (jedis != null) {
                        pool.returnBrokenResource(jedis);
                    }
                    continue;
                }
                try {
                    String info = jedis.info();
                    boolean isMaster = RedisUtils.isMaster(info);
                    // 主实例
                    if (isMaster == true) {
                        newMasterPool.add(pool);
                        // 从实例
                    } else {
                        newRslavePool.add(pool);
                    }
                } finally {
                    // 释放
                    pool.returnResource(jedis);
                }
            }
            List<JedisPool> oldMasterPool = redisMasterPool;
            List<JedisPool> oldRslavePool = redisSlavePool;
            redisMasterPool = newMasterPool;
            redisSlavePool = newRslavePool;
            // 如果没有master 避免用户直接获取master进行操作导致错误
            if (redisMasterPool.size() == 0) {
                redisMasterPool = redisSlavePool;
            }
            // 如果没有slave 避免用户直接获取slave进行操作导致错误
            if (redisSlavePool.size() == 0) {
                redisSlavePool = redisMasterPool;
            }
            if (null != oldMasterPool && oldMasterPool.size() > 0) {
                destroy(oldMasterPool);
            }
            if (null != oldRslavePool && oldRslavePool.size() > 0) {
                destroy(oldRslavePool);
            }
            this.masterServerSize = redisMasterPool.size();
            this.slaveServerSize = redisSlavePool.size();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 销毁Jedis资源池
     */
    public void destroy(List<JedisPool> pool) {
        if (pool != null && pool.size() != 0) {
            for (JedisPool p : pool) {
                try {
                    p.destroy();
                } catch (Throwable e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 销毁操作
     */
    public void destroy() {
        if (redisMasterPool != null && redisMasterPool.size() != 0) {
            for (JedisPool p : redisMasterPool) {
                p.destroy();
            }
        }
        if (redisSlavePool != null && redisSlavePool.size() != 0) {
            for (JedisPool p : redisSlavePool) {
                p.destroy();
            }
        }
    }

}
