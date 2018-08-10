package com.yy.cs.base.redis.sentinel;

import com.yy.cs.base.hostgroup.HostGroupCmdbLocator;
import com.yy.cs.base.hostgroup.HostGroupLocator;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.concurrent.locks.LockSupport;

/**
 * 就近读redis从库
 * <p>对于redis slave，如果slave的ip于本机ip处于相同机房（groupId相同），则优先读取
 *
 * @author: zhl
 * @since: 2018/08/07
 * @email: zhanhailin1@yy.com
 * @see HostGroupLocator
 **/
public class NearbyJedisSentinelPool extends CustomJedisSentinelPool {

    protected HostGroupLocator hostGroupLocator;

    /**
     * 本机分组id，优先读相同分组id的redis从
     */
    protected String localGroupId;

    /**
     * 是否只从最高优先级的{@link #availableSlaves}拿资源
     * <p>举例，如果此值为<code>true</code>，availableSlaves有5个节点，每个节点优先级分别为[1,1,1,0,-1]，
     * 则{@link #getReaderPool()}只会在优先级最高的三个节点中挑选。
     * <p>如果为<code>false</code>，则无视优先级，从5个节点中随机挑选
     */
    private boolean pickHighestSlaves;

    /**
     * 同机房优先级
     */
    public static final int SAME_GROUP_PRIORITY = 1;

    /**
     * 不同机房优先级
     */
    public static final int NOT_SAME_GROUP_PRIORITY = 0;

    @Override
    protected Map<String, ArrayList<HostAndPort>> initSentinels(Set<String> sentinels, String masterName, int timeout) {
        initNearbyProperties();
        return super.initSentinels(sentinels, masterName, timeout);
    }

    @Override
    protected int nextBalanceIndex(int size) {
        if (pickHighestSlaves && !HostGroupLocator.DEFAULT_GROUP.equals(localGroupId)) {
            size = getHighestSize(size);
        }
        return super.nextBalanceIndex(size);
    }

    private int getHighestSize(int size) {
        if (size <= 1) {
            return size;
        }
        int highestSize = 1;
        int highestPriority = availableSlaves.get(0).getPriority();
        //slave不会太多，一般就几个，因此不使用额外的一个字段记录
        for (; highestSize < size && availableSlaves.get(highestSize).getPriority() == highestPriority; ++highestSize) {
        }
        return highestSize;
    }

    protected void initNearbyProperties() {

        pickHighestSlaves = true;

        if (hostGroupLocator == null) {
            setHostGroupLocator(new HostGroupCmdbLocator());
        }
        if (localGroupId == null) {
            localGroupId = hostGroupLocator.getGroup();
        }
    }

    @Override
    protected SlaveJedisPool createSlaveJedisPool(HostAndPort hap) {
        final SlaveJedisPool pool = super.createSlaveJedisPool(hap);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                fillPriority(pool);
            }
        });
        return pool;
    }

    private void fillPriority(SlaveJedisPool pool) {

        if (!pickHighestSlaves || HostGroupLocator.DEFAULT_GROUP.equals(localGroupId)) {
            return;
        }

        String slaveGroup = hostGroupLocator.getGroup(pool.getHostAndPort().getHost());
        int priority = StringUtils.equals(slaveGroup, localGroupId) ? SAME_GROUP_PRIORITY : NOT_SAME_GROUP_PRIORITY;
        if (priority != pool.getPriority()) {
            pool.setPriority(priority);
            //存在极小可能pool还没添加到availableSlaves中
            if (waitForPoolAdded(pool)) {
                sortSlaveJedisPool();
            }
        }
    }

    /**
     * 等待pool被加入到从库列表中
     * @return 线程中断时返回false，否则返回true
     */
    private boolean waitForPoolAdded(SlaveJedisPool pool) {
        if (!containSlavePool(pool)) {
            Thread.yield();
            if (!containSlavePool(pool)) {
                LockSupport.parkUntil(System.currentTimeMillis() + 300);
                if (Thread.currentThread().isInterrupted()) {
                    log.warn("[NearbyJedisSentinelPool] Thread has bean interrupted.");
                    return false;
                }
                if (!containSlavePool(pool)) {
                    log.warn("[NearbyJedisSentinelPool] {} It has not been added to the availableSlaves yet", pool.getHostAndPort());
                    //警告但不阻止
                    return true;
                }
            }
        }
        return true;
    }

    private boolean containSlavePool(SlaveJedisPool pool) {
        return availableSlaves.contains(pool) || unavailableSlaves.contains(pool);
    }

    private void sortSlaveJedisPool() {
        lock.writeLock().lock();
        try {
            Collections.sort(availableSlaves);
            if (log.isInfoEnabled()) {
                StringBuilder sb = new StringBuilder("[NearbyJedisSentinelPool] Sorted SlaveJedisPool: ");
                for (SlaveJedisPool pool : availableSlaves) {
                    sb.append(pool.getHostAndPort())
                            .append("(Priority=")
                            .append(pool.getPriority())
                            .append("), ");
                }
                log.info(sb.toString());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public HostGroupLocator getHostGroupLocator() {
        return hostGroupLocator;
    }

    public void setHostGroupLocator(HostGroupLocator hostGroupLocator) {
        this.hostGroupLocator = hostGroupLocator;
    }

    public String getLocalGroupId() {
        return localGroupId;
    }

    public void setLocalGroupId(String localGroupId) {
        this.localGroupId = localGroupId;
    }

    public boolean isPickHighestSlaves() {
        return pickHighestSlaves;
    }

    public void setPickHighestSlaves(boolean pickHighestSlaves) {
        if (pickHighestSlaves && this.pickHighestSlaves == false && !availableSlaves.isEmpty()) {
            //旧值为false且新值为true，排一次序
            for (SlaveJedisPool pool : availableSlaves) {
                String slaveGroup = hostGroupLocator.getGroup(pool.getHostAndPort().getHost());
                int priority = StringUtils.equals(slaveGroup, localGroupId) ? SAME_GROUP_PRIORITY : NOT_SAME_GROUP_PRIORITY;
                pool.setPriority(priority);
            }
            sortSlaveJedisPool();
        }
        this.pickHighestSlaves = pickHighestSlaves;
    }

    public NearbyJedisSentinelPool(String masterName, Set<String> sentinels, JedisPoolConfig poolConfig) {
        super(masterName, sentinels, poolConfig);
    }

    public NearbyJedisSentinelPool(String masterName, Set<String> sentinels) {
        super(masterName, sentinels);
    }

    public NearbyJedisSentinelPool(String masterName, Set<String> sentinels, String password) {
        super(masterName, sentinels, password);
    }

    public NearbyJedisSentinelPool(String masterName, Set<String> sentinels, JedisPoolConfig poolConfig, int timeout, String password) {
        super(masterName, sentinels, poolConfig, timeout, password);
    }

    public NearbyJedisSentinelPool(String masterName, Set<String> sentinels, JedisPoolConfig poolConfig, int timeout) {
        super(masterName, sentinels, poolConfig, timeout);
    }

    public NearbyJedisSentinelPool(String masterName, Set<String> sentinels, JedisPoolConfig poolConfig, String password) {
        super(masterName, sentinels, poolConfig, password);
    }

    public NearbyJedisSentinelPool(String masterName, Set<String> sentinels, JedisPoolConfig poolConfig, int timeout, String password, int database) {
        super(masterName, sentinels, poolConfig, timeout, password, database);
    }
}
