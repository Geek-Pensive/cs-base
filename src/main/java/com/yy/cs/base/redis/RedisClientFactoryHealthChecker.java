package com.yy.cs.base.redis;

import java.util.List;
import java.util.TimerTask;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisClientFactoryHealthChecker extends TimerTask {

    private RedisClientFactory factory;
    private long lastReInitTime = 0;
    public static final long CHECK_PERIOD = 30 * 1000;

    public RedisClientFactoryHealthChecker() {

    }

    public RedisClientFactoryHealthChecker(RedisClientFactory factory) {
        this.factory = factory;
    }

    @Override
    public void run() {
        boolean masterStatus = check(factory.getRedisMasterPool(), factory.getMasterServerSize());
        boolean slaveStatus = check(factory.getRedisSlavePool(), factory.getSlaveServerSize());
        if (masterStatus || slaveStatus) {
            factory.init();
            lastReInitTime = System.currentTimeMillis();
        } else {
            if ((System.currentTimeMillis() - lastReInitTime) > 4 * CHECK_PERIOD
                    && (factory.getMasterServerSize() + factory.getSlaveServerSize()) != factory.getRedisServers()
                            .size()) {// 从库或者主库down了,上次重新初始化的时候，没有成功初始化从库的情况下，需要再次检查并初始化
                factory.init();
                lastReInitTime = System.currentTimeMillis();
            }
        }
    }

    private boolean check(List<JedisPool> pools, int size) {
        boolean reInit = false;
        int checkCount = 0;
        for (JedisPool jpool : pools) {
            Jedis j = null;
            try {
                j = jpool.getResource();
                String ping = j.ping();
                if (!"PONG".equals(ping)) {
                    return true;
                }
                checkCount++;
            } catch (Exception e) {
                try {
                    j = null;
                    jpool.returnBrokenResource(j);
                } catch (Exception e1) {
                    return true;
                }
            } finally {
                if (j != null) {
                    jpool.returnResource(j);
                }
            }
        }
        if (checkCount != size) {
            return true;
        }
        return reInit;
    }

    public RedisClientFactory getFactory() {
        return factory;
    }

    public void setFactory(RedisClientFactory factory) {
        this.factory = factory;
    }

}
