package com.yy.cs.base.redis.sentinel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.LockSupport;

import org.apache.commons.lang3.StringUtils;

import com.yy.cs.base.hostgroup.HostAreaCmdbLocator;
import com.yy.cs.base.hostgroup.HostCityCmdbLocator;
import com.yy.cs.base.hostgroup.HostGroupCmdbLocator;
import com.yy.cs.base.hostgroup.HostGroupLocator;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;

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

    protected List<HostGroupLocator> hostGroupLocators;

    /**
     * 本机分组id，优先读相同分组id的redis从
     * 
     * 读优先级：同机房>同城市>同区域
     * 
     * 只能通过hostGroupLocators列表去获取本机的localGroupIds列表，所以不提供setter方法
     * 
     */
    protected List<String> localGroupIds;

    /**
     * 是否只从最高优先级的{@link #availableSlaves}拿资源
     * <p>举例，如果此值为<code>true</code>，availableSlaves有5个节点，每个节点优先级分别为[1,1,1,0,-1]，
     * 则{@link #getReaderPool()}只会在优先级最高的三个节点中挑选。
     * <p>如果为<code>false</code>，则无视优先级，从5个节点中随机挑选
     */
    private boolean pickHighestSlaves;
    
    /**
     * 默认优先级
     */
    public static final int DEFAULT_PRIORITY = 0;

    @Override
    protected Map<String, ArrayList<HostAndPort>> initSentinels(Set<String> sentinels, String masterName, int timeout) {
        initNearbyProperties();
        return super.initSentinels(sentinels, masterName, timeout);
    }

    @Override
    protected int nextBalanceIndex(int size) {
        if (pickHighestSlaves && null != localGroupIds && localGroupIds.size() > 0 && !HostGroupLocator.DEFAULT_GROUP.equals(localGroupIds.get(0))) {
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

        if (hostGroupLocators == null) {
            // 优先级：同机房>同城市>同区域
            List<HostGroupLocator> hostGroupLocatorsTmp = new ArrayList<>();
            hostGroupLocatorsTmp.add(HostGroupCmdbLocator.INSTANCE);
            hostGroupLocatorsTmp.add(HostCityCmdbLocator.INSTANCE);
            hostGroupLocatorsTmp.add(HostAreaCmdbLocator.INSTANCE);
            this.setHostGroupLocators(hostGroupLocatorsTmp);
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

        if (!pickHighestSlaves || null == localGroupIds || localGroupIds.size() == 0 || HostGroupLocator.DEFAULT_GROUP.equals(localGroupIds.get(0))) {
            return;
        }

        int priority = this.getPriority(pool.getHostAndPort().getHost());
        if (priority != pool.getPriority()) {
            pool.setPriority(priority);
            //存在极小可能pool还没添加到availableSlaves中
            if (waitForPoolAdded(pool)) {
                sortSlaveJedisPool();
            }
        }
    }
    
    /**
     * 从第一个hostGroupLocator开始对比远程和本地的group，相同则返回当前优先级
     * 最高优先级为hostGroupLocators.size()，依次减1
     * 
     * @param host
     * @return
     */
    private int getPriority(String host) {
        if(null != hostGroupLocators){
            for (int i = 0; i < hostGroupLocators.size(); i++) {
                String slaveGroup = hostGroupLocators.get(i).getGroup(host);
                if(StringUtils.equals(slaveGroup, localGroupIds.get(i))){
                    return hostGroupLocators.size() - i;
                }
            }
        }
        return DEFAULT_PRIORITY;
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

    public boolean isPickHighestSlaves() {
        return pickHighestSlaves;
    }

    public void setPickHighestSlaves(boolean pickHighestSlaves) {
        if (pickHighestSlaves && this.pickHighestSlaves == false && !availableSlaves.isEmpty()) {
            //旧值为false且新值为true，排一次序
            for (SlaveJedisPool pool : availableSlaves) {
                int priority = this.getPriority(pool.getHostAndPort().getHost());
                pool.setPriority(priority);
            }
            sortSlaveJedisPool();
        }
        this.pickHighestSlaves = pickHighestSlaves;
    }
    
    public List<HostGroupLocator> getHostGroupLocators() {
        return hostGroupLocators;
    }

    public void setHostGroupLocators(List<HostGroupLocator> hostGroupLocators) {
        this.hostGroupLocators = hostGroupLocators;

        // 根据hostGroupLocators获取本地localGroupIds
        if(null != hostGroupLocators){
            List<String> localGroupIdsTmp = new ArrayList<>();
            for (HostGroupLocator hostGroupLocator : hostGroupLocators) {
                localGroupIdsTmp.add(hostGroupLocator.getGroup());
            }
            localGroupIds = localGroupIdsTmp;
        }
    }

    public List<String> getLocalGroupIds() {
        return localGroupIds;
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
