package com.yy.cs.base.redis;

import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.json.Json;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisClientFactoryHealthChecker extends TimerTask {

    private static final Logger log = LoggerFactory.getLogger(RedisClientFactoryHealthChecker.class);

    private RedisClientFactory factory;
    private long lastReInitTime = 0;
    public static final long CHECK_PERIOD = 30 * 1000;
    private long checkPeriod = CHECK_PERIOD;
    private long fullCheckPeriod = CHECK_PERIOD * 4;

    public RedisClientFactoryHealthChecker(RedisClientFactory factory, long checkPeriod, long fullCheckPeriod) {
        this.factory = factory;
    }

    @Override
    public void run() {
        if (null == factory) {
            return;
        }
        boolean masterStatus = check(factory.getRedisMasterPool(), factory.getMasterServerSize());
        boolean slaveStatus = check(factory.getRedisSlavePool(), factory.getSlaveServerSize());
        if (masterStatus || slaveStatus) {
            log.warn(Json.ObjToStr(factory.getRedisServers()) + " going to reinit ,cause by [masterFailed="
                    + masterStatus + ",slavesFailed=" + slaveStatus + "]");
            factory.init();
            lastReInitTime = System.currentTimeMillis();
        } else {
            if ((System.currentTimeMillis() - lastReInitTime) >= fullCheckPeriod
                    && (factory.getMasterServerSize() + factory.getSlaveServerSize()) != factory.getRedisServers()
                            .size()) {// 从库或者主库down了,上次重新初始化的时候，没有成功初始化从库的情况下，需要再次检查并初始化
                log.warn(Json.ObjToStr(factory.getRedisServers()) + " going to reinit ,cause by [SUM (masterCount="
                        + factory.getMasterServerSize() + ",slaveCount=" + factory.getSlaveServerSize() + ") != "
                        + factory.getRedisServers().size() + "]");

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
                    if (null != j) {
                        j.close();
                        j = null;
                    }
                } catch (Exception e1) {
                    return true;
                }
            } finally {
                if (j != null) {
                    j.close();
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

    public long getCheckPeriod() {
        return checkPeriod;
    }

    public long getFullCheckPeriod() {
        return fullCheckPeriod;
    }

    public void setCheckPeriod(long checkPeriod) {
        this.checkPeriod = checkPeriod;
    }

    public void setFullCheckPeriod(long fullCheckPeriod) {
        this.fullCheckPeriod = fullCheckPeriod;
    }

}
