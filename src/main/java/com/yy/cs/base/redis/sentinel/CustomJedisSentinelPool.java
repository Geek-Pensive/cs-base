package com.yy.cs.base.redis.sentinel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
//import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Pool;

import com.yy.cs.base.json.Json;

public class CustomJedisSentinelPool extends Pool<Jedis> {
	
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(CustomJedisSentinelPool.class);

    protected GenericObjectPoolConfig poolConfig;

    protected int timeout = Protocol.DEFAULT_TIMEOUT;

    protected String password;

    protected int database = Protocol.DEFAULT_DATABASE;
    
    protected static final String MASTER_PREFIX = "master";
    
    protected static final String SLAVE_PREFIX = "slave";

    protected Set<MasterListener> masterListeners = new HashSet<MasterListener>();
    
    protected Map <String,ArrayList<HostAndPort>> sentinelsMap = new ConcurrentHashMap<>();

    private Executor executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    private volatile HostAndPort currentHostMaster;
    
    private CopyOnWriteArrayList<JedisPool> jedisPools = new CopyOnWriteArrayList<>();
    
    public CustomJedisSentinelPool(String masterName, Set<String> sentinels,
	    final GenericObjectPoolConfig poolConfig) {
	this(masterName, sentinels, poolConfig, Protocol.DEFAULT_TIMEOUT, null,
		Protocol.DEFAULT_DATABASE);
    }

    public CustomJedisSentinelPool(String masterName, Set<String> sentinels) {
	this(masterName, sentinels, new GenericObjectPoolConfig(),
		Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE);
    }

    public CustomJedisSentinelPool(String masterName, Set<String> sentinels,
	    String password) {
	this(masterName, sentinels, new GenericObjectPoolConfig(),
		Protocol.DEFAULT_TIMEOUT, password);
    }

    public CustomJedisSentinelPool(String masterName, Set<String> sentinels,
	    final GenericObjectPoolConfig poolConfig, int timeout,
	    final String password) {
	this(masterName, sentinels, poolConfig, timeout, password,
		Protocol.DEFAULT_DATABASE);
    }

    public CustomJedisSentinelPool(String masterName, Set<String> sentinels,
	    final GenericObjectPoolConfig poolConfig, final int timeout) {
	this(masterName, sentinels, poolConfig, timeout, null,
		Protocol.DEFAULT_DATABASE);
    }

    public CustomJedisSentinelPool(String masterName, Set<String> sentinels,
	    final GenericObjectPoolConfig poolConfig, final String password) {
	this(masterName, sentinels, poolConfig, Protocol.DEFAULT_TIMEOUT,
		password);
    }

    public CustomJedisSentinelPool(String masterName, Set<String> sentinels,
	    final GenericObjectPoolConfig poolConfig, int timeout,
	    final String password, final int database) {
		this.poolConfig = poolConfig;
		this.timeout = timeout;
		this.password = password;
		this.database = database;
		initSentinels(sentinels, masterName,timeout);
    }

    @Override
    public Jedis getResource() {
        Jedis jedis = super.getResource();
        jedis.setDataSource(this);
        return jedis;
    }
    
    public void returnBrokenResource(final Jedis resource) {
        if(resource != null){
            returnBrokenResourceObject(resource);
        }
    }

    public void returnResource(final Jedis resource) {
        if(resource != null){
            resource.resetState();
            returnResourceObject(resource);
        }
    }

    public void destroy() {
		for (MasterListener m : masterListeners) {
		    m.shutdown();
		}
		super.destroy();
		if(jedisPools != null && jedisPools.size() > 0 ){
    		for(JedisPool pool : jedisPools){
    			pool.close();
    		}
    		jedisPools.clear();
    	}
		log.info("CustomJedisSentinelPool destroy...");
    }

    public HostAndPort getCurrentHostMaster() {
    	return currentHostMaster;
    }
    private void initMasterPool(HostAndPort master) {
    	//覆写equals，避免重复初始化master pool
		if (!master.equals(currentHostMaster)) {
			ArrayList<HostAndPort> ls = new ArrayList<>();
			ls.add(master);
			sentinelsMap.put(MASTER_PREFIX, ls);
		    currentHostMaster = master;
		    log.info("Created JedisPool to master at " + master);
		    //创建master pool
		    initPool(poolConfig,new JedisFactory(master.getHost(), master.getPort(),timeout, password, database));
		}
    }
    
    private void initSalvePools(ArrayList<HostAndPort> slaves){
    	try{
    		lock.writeLock().lock();
    		boolean isSame = false;
        	ArrayList<HostAndPort> list = sentinelsMap.get(SLAVE_PREFIX);
        	if(list != null && slaves != null  
        		 && slaves.containsAll(list) 
        		 && list.containsAll(slaves)){
        		isSame = true;
        	}
        	if(isSame){
        		return ;
        	}else{
        		sentinelsMap.put(SLAVE_PREFIX, slaves);
        	}
    		if(jedisPools != null && jedisPools.size() > 0 ){
        		for(JedisPool pool : jedisPools){
        			log.info("remove and destroy old jedisPool :{}",pool);
        			pool.close();
        		}
        		jedisPools.clear();
        	}
        	for (HostAndPort hap : slaves) {
    			jedisPools.add(new JedisPool(poolConfig, hap.getHost(), hap.getPort(),timeout));
    			log.info("reload new jedisPool host:{},port:{}",hap.getHost(),hap.getPort());
    		}	
    	}finally{
    		lock.writeLock().unlock();
    	}
    }
    
    public JedisPool getReaderPool(){
    	try{
    		lock.readLock().lock();
    		int size = jedisPools.size();
    		if( size > 0 ){
    			return jedisPools.get(random.nextInt(size));
    		}else{
    			log.info("error: none slave pool can be aquired");
    			return null;
    		}
    	}finally{
    		lock.readLock().unlock();
    	}
    }
    
    private static final Random random = new Random();
    
    private Map<String,ArrayList<HostAndPort>> initSentinels(Set<String> sentinels,
	    final String masterName,int timeout) {
    	Map<String,ArrayList<HostAndPort>> map = new HashMap<String,ArrayList<HostAndPort>>();
		HostAndPort master = null;
		boolean running = true;
		 log.info("start initSentinels...");
		outer: while (running) {
		    log.info("Trying to find master from available Sentinels...");
		    for (String sentinel : sentinels) {
				final HostAndPort hap = toHostAndPort(Arrays.asList(sentinel.split(":")));
				log.debug("Connecting to Sentinel " + hap);
				try {
					    @SuppressWarnings("resource")
						Jedis jedis = new Jedis(hap.getHost(), hap.getPort(),timeout);
					    if (master == null) {
						master = toHostAndPort(jedis.sentinelGetMasterAddrByName(masterName));
						log.info("found Redis master at " + master);
						ArrayList<HostAndPort> ls = new ArrayList<>();
						ls.add(master);
						sentinelsMap.put(MASTER_PREFIX, ls);
						//初始化masterPool
						initMasterPool(master);
						// 获取从服务器列表
						ArrayList<HostAndPort> slaves = new ArrayList<>();
					    for (Map<String, String> slave : jedis.sentinelSlaves(masterName)) {
					    	HostAndPort _slave = toHostAndPort(Arrays.asList(slave.get("name").split(":")));
					    	slaves.add(_slave);
					    	log.info("Found Redis Slave: " + Json.ObjToStr(slave));
					    }
						//初始化slavePool
						initSalvePools(slaves);
						jedis.close();
						break outer;
				    }
				} catch (JedisConnectionException e) {
					    log.warn("Cannot connect to sentinel running @ " + hap+ ". Trying next one.");
				}
		    }
		    try {
				log.error("All sentinels down, cannot determine where is "+ masterName + " master is running... sleeping 1000ms.");
					Thread.sleep(1000);
			    } catch (InterruptedException e) {
			    	e.printStackTrace();
		    }
		}
		log.info("Redis master running at " + master + ", starting Sentinel listeners...");
		for (String sentinel : sentinels) {
		    final HostAndPort hap = toHostAndPort(Arrays.asList(sentinel.split(":")));
		    MasterListener masterListener = new MasterListener(masterName,hap.getHost(), hap.getPort());
		    masterListeners.add(masterListener);
		    masterListener.setDaemon(true);
		    masterListener.start();
		}
	
		return map;
    }

    private void reloadSlavePools(Jedis jedis,String masterName){
    	ArrayList<HostAndPort> slaves = new ArrayList<>();
	    for (Map<String, String> slave : jedis.sentinelSlaves(masterName)) {
	    	HostAndPort _slave = toHostAndPort(Arrays.asList(slave.get("name").split(":")));
	    	slaves.add(_slave);
	    	log.info("reloadSlavePools: found Redis Slave: " + Json.ObjToStr(slave));
	    }
	    sentinelsMap.put(SLAVE_PREFIX, slaves);
	    initSalvePools(slaves);
	    jedis.close();
    }
    
    private HostAndPort toHostAndPort(List<String> getMasterAddrByNameResult) {
		String host = getMasterAddrByNameResult.get(0);
		int port = Integer.parseInt(getMasterAddrByNameResult.get(1));
		return new HostAndPort(host, port);
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

	public MasterListener(String masterName, String host, int port,
		long subscribeRetryWaitTimeMillis) {
	    this(masterName, host, port);
	    this.subscribeRetryWaitTimeMillis = subscribeRetryWaitTimeMillis;
	}

	public void run() {
	    running.set(true);
	    while (running.get()) {
		try {
				j = new Jedis(host, port);
				j.subscribe(new JedisPubSubAdapter() {
				@Override
				public void onMessage(String channel, String message) {
					    log.info("Sentinel " + host + ":" + port+ " published: " + message + ".");
					    String[] switchMasterMsg = message.split(" ");
					    if (switchMasterMsg.length > 3) {
							if (masterName.equals(switchMasterMsg[0])) {
								HostAndPort hostAddress = toHostAndPort(Arrays.asList(switchMasterMsg[3],switchMasterMsg[4]));
								log.info("switch master and init pool at :{}",hostAddress);
								initMasterPool(hostAddress);
								executorService.execute(new Runnable(){
									public void run() {
										reloadSlavePools(new Jedis(host, port), masterName);
									};
								});
							} else {
							    log.info("Ignoring message on +switch-master for master name "
								    + switchMasterMsg[0]
								    + ", our master name is "
								    + masterName);
							}
					    } else {
					    	log.error("Invalid message received on Sentinel  host:"+ port+ " on channel +switch-master: "+ message);
					    }
					}
			   }, "+switch-master");
			} catch (JedisConnectionException e) {
			    if (running.get()) {
					log.error("Lost connection to Sentinel at " + host
						+ ":" + port
						+ ". Sleeping 5000ms and retrying.");
					try {
					    Thread.sleep(subscribeRetryWaitTimeMillis);
					} catch (InterruptedException e1) {
					    e1.printStackTrace();
					}
			    } else {
			    	log.info("Unsubscribing from Sentinel at " + host + ":"+ port);
			    }
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
			log.error("Caught exception while shutting down: "
				+ e.getMessage());
	    }
	}
    }

}