package com.yy.cs.base.redis;

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
        this.checkPeriod = checkPeriod;
        this.fullCheckPeriod = fullCheckPeriod;
        this.lastReInitTime = System.currentTimeMillis();// 初始化时间
    }

    @Override
    public void run() {
        if (null == factory) {
            return;
        }
        try {
            boolean masterStatus = check(factory.getRedisMasterPool(), factory.getMasterServerSize());
            boolean slaveStatus = check(factory.getRedisSlavePool(), factory.getSlaveServerSize());
            if (masterStatus || slaveStatus) {
                log.warn(Json.ObjToStr(factory.getRedisServers()) + " going to reinit ,cause by [masterFailed="
                        + masterStatus + ",slavesFailed=" + slaveStatus + "]");
                factory.init();
                lastReInitTime = System.currentTimeMillis();
                return;
            }
            if ((System.currentTimeMillis() - lastReInitTime) < fullCheckPeriod) {
                return;
            }

            int masterCount = factory.getMasterServerSize();
            int slaveCount = factory.getSlaveServerSize();

            int serverCount = masterCount;
            if (factory.getRedisMasterPool() == factory.getRedisSlavePool()) {
                // 没有设置从库的时候，会把主库当做从库，这里判断下引用是否一样
                slaveCount = 0;
            }
            serverCount += slaveCount;
            if (serverCount != factory.getRealServersCount()) {// 从库或者主库down了,上次重新初始化的时候，没有成功初始化从库的情况下，需要再次检查并初始化
                log.warn(Json.ObjToStr(factory.getRedisServers()) + " going to reinit ,cause by [SUM (masterCount="
                        + masterCount + ",slaveCount=" + slaveCount + ") != " + factory.getRealServersCount() + "]");

                factory.init();
                lastReInitTime = System.currentTimeMillis();
            }
        } catch (Throwable e) {
            log.error("RedisHealthChecking error for " + Json.ObjToStr(factory.getRedisServers()), e);
        }
    }

    private boolean check(List<CustomJedisPool> pools, int size) {
        boolean reInit = false;
        if (null == pools && size == 0) {
            return reInit;
        }
        if (pools.size() == 0 && size == 0) {
            return reInit;
        }
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
