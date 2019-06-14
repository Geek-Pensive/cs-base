package com.yy.cs.base.redis.sentinel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.yy.cs.base.task.trigger.StringUtils;
import org.slf4j.LoggerFactory;

import com.yy.cs.base.json.Json;
import com.yy.cs.base.redis.RedisUtils;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class CustomJedisSentinelPool extends JedisPool {

    protected static final org.slf4j.Logger log = LoggerFactory.getLogger(CustomJedisSentinelPool.class);

    protected String masterName;
    
    protected Set<String> sentinels;
    
    protected JedisPoolConfig poolConfig;

    protected int timeout = Protocol.DEFAULT_TIMEOUT;

    protected String password;

    protected int database = Protocol.DEFAULT_DATABASE;

    protected static final String MASTER_PREFIX = "master";

    protected static final String SLAVE_PREFIX = "slave";

    protected Set<MasterListener> masterListeners = new HashSet<MasterListener>();

    protected SlavesChecker slaveChecker = null;;

    protected Map<String, ArrayList<HostAndPort>> sentinelsMap = new ConcurrentHashMap<>();

    protected Executor executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private volatile HostAndPort currentHostMaster;

    protected CopyOnWriteArrayList<SlaveJedisPool> availableSlaves = new CopyOnWriteArrayList<>();

    protected CopyOnWriteArrayList<HostAndPort> unavailableSlaves = new CopyOnWriteArrayList<>();

    private AtomicLong lastLoadTimestamp = new AtomicLong();

    public CustomJedisSentinelPool(String masterName, Set<String> sentinels, final JedisPoolConfig poolConfig) {
        this(masterName, sentinels, poolConfig, Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE);
    }

    public CustomJedisSentinelPool(String masterName, Set<String> sentinels) {
        this(masterName, sentinels, new JedisPoolConfig(), Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE);
    }

    public CustomJedisSentinelPool(String masterName, Set<String> sentinels, String password) {
        this(masterName, sentinels, new JedisPoolConfig(), Protocol.DEFAULT_TIMEOUT, password);
    }

    public CustomJedisSentinelPool(String masterName, Set<String> sentinels, final JedisPoolConfig poolConfig,
                                   int timeout, final String password) {
        this(masterName, sentinels, poolConfig, timeout, password, Protocol.DEFAULT_DATABASE);
    }

    public CustomJedisSentinelPool(String masterName, Set<String> sentinels, final JedisPoolConfig poolConfig,
                                   final int timeout) {
        this(masterName, sentinels, poolConfig, timeout, null, Protocol.DEFAULT_DATABASE);
    }

    public CustomJedisSentinelPool(String masterName, Set<String> sentinels, final JedisPoolConfig poolConfig,
                                   final String password) {
        this(masterName, sentinels, poolConfig, Protocol.DEFAULT_TIMEOUT, password);
    }

    public CustomJedisSentinelPool(String masterName, Set<String> sentinels, final JedisPoolConfig poolConfig,
                                   int timeout, final String password, final int database) {
        this.masterName = masterName;
        this.sentinels = sentinels;
        this.poolConfig = poolConfig;
        this.timeout = timeout;
        this.password = password;
        this.database = database;
        initSentinels(sentinels, masterName, timeout);
    }

    @Override
    public Jedis getResource() {
        Jedis jedis = super.getResource();
        jedis.setDataSource(this);
        return jedis;
    }

    public void returnBrokenResource(final Jedis resource) {
        if (resource != null) {
            returnBrokenResourceObject(resource);
        }
    }

    public void returnResource(final Jedis resource) {
        if (resource != null) {
            resource.resetState();
            returnResourceObject(resource);
        }
    }

    public void destroy() {
        for (MasterListener m : masterListeners) {
            m.shutdown();
        }
        if (null != slaveChecker) {
            slaveChecker.shutdown();
        }

        if (availableSlaves != null && availableSlaves.size() > 0) {
            for (JedisPool pool : availableSlaves) {
                pool.close();
            }
            availableSlaves.clear();
        }
        if (null != unavailableSlaves) {
            unavailableSlaves.clear();
        }
        log.info("CustomJedisSentinelPool destroy...");

        super.destroy(); // close myself
    }

    public HostAndPort getCurrentHostMaster() {
        return currentHostMaster;
    }

    private void initMasterPool(HostAndPort master) {
        // 覆写equals，避免重复初始化master pool
        if (!master.equals(currentHostMaster)) {
            ArrayList<HostAndPort> ls = new ArrayList<>();
            ls.add(master);
            sentinelsMap.put(MASTER_PREFIX, ls);
            currentHostMaster = master;
            log.info("Created JedisPool to master at " + master);
            // 创建master pool(创建之前会先close)
            initPool(poolConfig, new JedisFactory(master.getHost(), master.getPort(), timeout, password, database));
        }
    }

    private void initSalvePools(ArrayList<HostAndPort> slaves) {
        try {
            lock.writeLock().lock();
            boolean isSame = false;
            ArrayList<HostAndPort> list = sentinelsMap.get(SLAVE_PREFIX);
            if (list != null && slaves != null && slaves.containsAll(list) && list.containsAll(slaves)) {
                isSame = true;
            }
            if (isSame) {
                return;
            } else {
                sentinelsMap.put(SLAVE_PREFIX, slaves);
            }
            
            // 先初始化新的从连接池
            CopyOnWriteArrayList<SlaveJedisPool> oldAvailableSlaves = availableSlaves;
            CopyOnWriteArrayList<HostAndPort> oldUnavailableSlaves = unavailableSlaves;
            CopyOnWriteArrayList<SlaveJedisPool> newAvailableSlaves = new CopyOnWriteArrayList<>();
            CopyOnWriteArrayList<HostAndPort> newUnavailableSlaves = new CopyOnWriteArrayList<>();
            for (HostAndPort hap : slaves) {
                if (RedisUtils.isAvailable(hap.getHost(), hap.getPort(), timeout)) {
                    newAvailableSlaves.add(createSlaveJedisPool(hap));
                    log.info("reload new slave jedisPool host:{},port:{}", hap.getHost(), hap.getPort());
                } else {
                    newUnavailableSlaves.add(hap);
                    log.warn("reload failed slave jedisPool host:{},port:{}", hap.getHost(), hap.getPort());
                }
            }
            availableSlaves = newAvailableSlaves;
            unavailableSlaves = newUnavailableSlaves;
            
            // availableSlaves的每一次新增、修改（删除除外）都进行一次fillPriority
            fillPriorityAndSortSlaveJedisPool();
            
            // 再销毁老的从连接池
            if (oldAvailableSlaves != null && oldAvailableSlaves.size() > 0) {
                for (JedisPool pool : oldAvailableSlaves) {
                    log.info("remove and destroy old slave jedisPool :{}", pool);
                    pool.close();
                }
                oldAvailableSlaves.clear();
            }
            if (oldUnavailableSlaves != null && oldUnavailableSlaves.size() > 0) {
                oldUnavailableSlaves.clear();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private SlaveJedisPool createSlaveJedisPool(HostAndPort hap) {
        return new SlaveJedisPool(poolConfig, hap, timeout);
    }
    
    protected void fillPriorityAndSortSlaveJedisPool() {
        // TODO empty implementation
    }

    public JedisPool getReaderPool() {
        try {
            lock.readLock().lock();
            int size = availableSlaves.size();
            if (size > 0) {
                return availableSlaves.get(nextBalanceIndex(size));
            } else {
                log.info("error: none slave pool can be aquired");
                return null;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 返回一个下次要访问的slave索引序号
     */
    protected int nextBalanceIndex(int size) {
        return ThreadLocalRandom.current().nextInt(size);
    }

    protected Map<String, ArrayList<HostAndPort>> initSentinels(Set<String> sentinels, final String masterName,
                                                                int timeout) {
        Map<String, ArrayList<HostAndPort>> map = new HashMap<String, ArrayList<HostAndPort>>();
        HostAndPort master = null;
        boolean running = true;
        log.info("start initSentinels masterName:{}, sentinels:{}...", masterName, sentinels);
        outer: while (running) {
            log.info("Trying to find master from available Sentinels...");
            for (String sentinel : sentinels) {
                final HostAndPort hap = toHostAndPort(Arrays.asList(sentinel.split(":")));
                log.debug("Connecting to Sentinel " + hap);
                try {
                    @SuppressWarnings("resource")
                    Jedis jedis = new Jedis(hap.getHost(), hap.getPort(), timeout);
                    if (master == null) {
                        master = toHostAndPort(jedis.sentinelGetMasterAddrByName(masterName));
                        log.info("found Redis master at " + master);
                        ArrayList<HostAndPort> ls = new ArrayList<>();
                        ls.add(master);
                        sentinelsMap.put(MASTER_PREFIX, ls);
                        // 初始化masterPool
                        initMasterPool(master);
                        // 获取从服务器列表
                        ArrayList<HostAndPort> slaves = new ArrayList<>();
                        for (Map<String, String> slave : jedis.sentinelSlaves(masterName)) {
                            String runid = slave.get("runid");
                            //可能是伪装的redis replicator, 没有填充该参数
                            if (StringUtils.isEmpty(runid)) {
                                continue;
                            }
                            HostAndPort _slave = new HostAndPort(slave.get("ip"), Integer.parseInt(slave.get("port")));
                            slaves.add(_slave);
                            log.debug("Found Redis Slave: " + Json.ObjToStr(slave));
                        }
                        // 初始化slavePool
                        initSalvePools(slaves);
                        lastLoadTimestamp.set(System.currentTimeMillis());

                        jedisClose(jedis);
                        break outer;
                    }
                } catch (JedisConnectionException e) {
                    log.warn("Cannot connect to sentinel running @ " + hap + ". Trying next one.");
                }
            }
            try {
                log.error("All sentinels down, cannot determine where is " + masterName
                        + " master is running... sleeping 1000ms.");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("Redis master running at " + master + ", starting Sentinel listeners...");
        for (String sentinel : sentinels) {
            final HostAndPort hap = toHostAndPort(Arrays.asList(sentinel.split(":")));
            MasterListener masterListener = new MasterListener(masterName, hap.getHost(), hap.getPort());
            masterListeners.add(masterListener);
            masterListener.setDaemon(true);
            masterListener.start();
        }

        slaveChecker = new SlavesChecker();
        slaveChecker.setDaemon(true);
        slaveChecker.start();

        return map;
    }

    private void reloadSlavePools(Jedis jedis, String masterName) {
        ArrayList<HostAndPort> slaves = new ArrayList<>();
        for (Map<String, String> slave : jedis.sentinelSlaves(masterName)) {
            String runid = slave.get("runid");
            //可能是伪装的redis replicator, 没有填充该参数
            if (StringUtils.isEmpty(runid)) {
                continue;
            }
            HostAndPort _slave = new HostAndPort(slave.get("ip"), Integer.parseInt(slave.get("port")));
            slaves.add(_slave);
            log.debug("reloadSlavePools: found Redis Slave: " + Json.ObjToStr(slave));
        }
        initSalvePools(slaves);

        jedisClose(jedis);
    }

    private HostAndPort toHostAndPort(List<String> getMasterAddrByNameResult) {
        String host = getMasterAddrByNameResult.get(0);
        int port = Integer.parseInt(getMasterAddrByNameResult.get(1));
        return new HostAndPort(host, port);
    }

    protected class SlavesChecker extends Thread {

        protected AtomicBoolean running = new AtomicBoolean(false);

        @Override
        public void run() {
            running.set(true);
            while (running.get()) {
                try {
                    TimeUnit.SECONDS.sleep(30);// 写死30s？
                } catch (InterruptedException e) {
                    shutdown();
                    return;
                } catch (Exception e) {

                }
                long lastUpdate = lastLoadTimestamp.get();
                List<SlaveJedisPool> newUnavailable = new ArrayList<>();
                List<HostAndPort> newAvailable = new ArrayList<>();
                lock.readLock().lock();
                try {
                    if (availableSlaves.isEmpty() && unavailableSlaves.isEmpty()) {
                        continue;
                    }
                    for (SlaveJedisPool jp : availableSlaves) {
                        try (Jedis j = jp.getResource();) {
                            if (!RedisUtils.ping(j)) {
                                newUnavailable.add(jp);
                            }
                        } catch (Exception e) {
                            newUnavailable.add(jp);
                        }
                    }

                    for (HostAndPort hap : unavailableSlaves) {
                        if (RedisUtils.isAvailable(hap.getHost(), hap.getPort(), timeout)) {
                            newAvailable.add(hap);
                        }
                    }
                } finally {
                    lock.readLock().unlock();
                }

                if (newUnavailable.size() > 0 || newAvailable.size() > 0) {
                    lock.writeLock().lock();
                    if (lastUpdate != lastLoadTimestamp.get()) {
                        continue;
                    }
                    try {
                        if (!newUnavailable.isEmpty()) {
                            for (SlaveJedisPool jp : newUnavailable) {
                                unavailableSlaves.add(jp.getHostAndPort());
                                availableSlaves.remove(jp);
                                if (log.isDebugEnabled()) {
                                    log.debug(" remove unavailable jedis slave pool " + jp.getHostAndPort());
                                }
                            }
                        }

                        if (!newAvailable.isEmpty()) {
                            for (HostAndPort hap : newAvailable) {
                                availableSlaves.add(createSlaveJedisPool(hap));
                                unavailableSlaves.remove(hap);
                                if (log.isDebugEnabled()) {
                                    log.debug(" add available jedis slave pool " + hap);
                                }
                            }
                            // availableSlaves的每一次新增、修改（删除除外）都进行一次fillPriority
                            fillPriorityAndSortSlaveJedisPool();
                        }

                    } finally {
                        lock.writeLock().unlock();
                    }
                }
                
                if (log.isDebugEnabled()) {
                    StringBuilder sb = new StringBuilder("masterName:" + masterName + " availableSlaves: ");
                    for (SlaveJedisPool pool : availableSlaves) {
                        sb.append(pool.getHostAndPort())
                                .append("(Priority=")
                                .append(pool.getPriority())
                                .append("), ");
                    }
                    log.debug(sb.toString());
                    
                    sb = new StringBuilder("masterName:" + masterName + " unavailableSlaves: ");
                    for (HostAndPort hostAndPort : unavailableSlaves) {
                        sb.append(hostAndPort);
                    }
                    log.debug(sb.toString());
                }
            }
        }

        public void shutdown() {
            try {
                log.info("Shutting down SlaveChecker ");
                running.set(false);
            } catch (Exception e) {
                log.error("Caught exception while shutting down: " + e.getMessage());
            }
        }

    }

    protected class MasterListener extends Thread {

        protected String masterName;
        protected String host;
        protected int port;
        protected long subscribeRetryWaitTimeMillis = 5000;
        protected Jedis j;
        protected AtomicBoolean running = new AtomicBoolean(false);

        protected MasterListener() {
        }

        public MasterListener(String masterName, String host, int port) {
            this.masterName = masterName;
            this.host = host;
            this.port = port;
        }

        public MasterListener(String masterName, String host, int port, long subscribeRetryWaitTimeMillis) {
            this(masterName, host, port);
            this.subscribeRetryWaitTimeMillis = subscribeRetryWaitTimeMillis;
        }

        /**
         * 消息例子：
                        主切换：
            Sentinel 221.228.86.201:26382 published: (channel:+switch-master, message: mymaster 58.215.180.218 6380 58.215.180.219 6381).
                        主挂：
            Sentinel 58.215.180.219:26381 published: (channel:+sdown, message: master mymaster 58.215.180.219 6381).
                        从挂：
            Sentinel 58.215.180.219:26381 published: (channel:+sdown, message: slave 58.215.180.219:6381 58.215.180.219 6381 @ mymaster 58.215.180.218 6380).
                        从恢复：
            Sentinel 58.215.180.219:26381 published: (channel:-sdown, message: slave 58.215.180.219:6381 58.215.180.219 6381 @ mymaster 58.215.180.218 6380).
                        从增加：
            Sentinel 221.228.86.201:26382 published: (channel:+slave, message: slave 58.215.180.218:6381 58.215.180.218 6381 @ mymaster 221.228.86.201 6382).
         */
        public void run() {
            running.set(true);
            while (running.get()) {
                try {
                    j = new Jedis(host, port);
                    j.subscribe(new JedisPubSubAdapter() {
                        @Override
                        public void onMessage(String channel, String message) {
                            log.info("Sentinel " + host + ":" + port + " published: (channel:" + channel + ", message: " + message + ").");
                            if("+switch-master".equals(channel)){   //主切换
                                String[] messages = message.split(" ");
                                if (messages.length > 3) {
                                    if (masterName.equals(messages[0])) {
                                        String oldMasterIp = messages[1];
                                        String oldMasterPort = messages[2];
                                        String newMasterIp = messages[3];
                                        String newMasterPort = messages[4];
                                        
                                        HostAndPort hostAddress = toHostAndPort(
                                                Arrays.asList(newMasterIp, newMasterPort));
                                        log.info("switch master {} from [{}] to [{}]", messages[0], toHostAndPort(
                                                Arrays.asList(oldMasterIp, oldMasterPort)), hostAddress);
                                        initMasterPool(hostAddress);
                                        executorService.execute(new Runnable() {
                                            public void run() {
                                                reloadSlavePools(new Jedis(host, port), masterName);
                                            };
                                        });
                                    } else {
                                        log.info("Ignoring message on +switch-master for master name " + messages[0]
                                                + ", our master name is " + masterName);
                                    }
                                } else {
                                    log.error("Invalid message received on Sentinel  host:" + port
                                            + " on channel +switch-master: " + message);
                                }
                            } else if ("-sdown".equals(channel) || "+sdown".equals(channel)) {
                                String[] messages = message.split(" ");
                                if(messages.length == 8){
                                    if("slave".equals(messages[0])){    // 只处理从挂，主挂会触发切换事件
                                        if(masterName.equals(messages[5])){
                                            reloadSlavePools(new Jedis(host, port), masterName);
                                        }else{
                                            log.info("Ignoring message on -/+sdown for master name {}, our master name is {}!", messages[5], masterName);
                                        }
                                    }
                                }
                            } else if ("+slave".equals(channel)) {
                                String[] messages = message.split(" ");
                                if(messages.length == 8){
                                    if("slave".equals(messages[0])){    // 从增加
                                        if(masterName.equals(messages[5])){
                                            reloadSlavePools(new Jedis(host, port), masterName);
                                        }else{
                                            log.info("Ignoring message on +slave for master name {}, our master name is {}!", messages[5], masterName);
                                        }
                                    }
                                }
                            }
                        }
                    }, "+switch-master", "+sdown", "-sdown", "+slave");
                } catch (JedisConnectionException e) {
                    if (running.get()) {
                        log.error("Lost connection to Sentinel at " + host + ":" + port
                                + ". Sleeping 5000ms and retrying.", e);
                        try {
                            Thread.sleep(subscribeRetryWaitTimeMillis);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        log.info("Unsubscribing from Sentinel at " + host + ":" + port);
                    }
                } finally {
                    jedisClose(j);
                }
            }
        }

        public void shutdown() {
            try {
                log.info("Shutting down listener on " + host + ":" + port);
                running.set(false);
                // This isn't good, the Jedis object is not thread safe
                j.disconnect();
            } catch (Exception e) {
                log.error("Caught exception while shutting down: " + e.getMessage());
            }
        }
    }


    private void jedisClose(Jedis jedis) {
        try {
            if (jedis != null) {
                jedis.close();
            }
        } catch (Exception e) {
            log.error("jedis close fail", e);
        }
    }
}