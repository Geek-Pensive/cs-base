package com.yy.cs.base.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.yy.cs.base.redis.RedisClient.TransactionAction;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * 
 * @author haoqing
 * RedisClientTest 的覆盖测试用例
 *
 */
public class RedisClientTest {

	RedisClient redisClient;
	
	@Before
	public void init(){
		
		RedisClientFactory redisClientFactory = new RedisClientFactory();
		List<String> list = new ArrayList<String>();
		//这里是业务要连接的redis
//		list.add("172.19.103.105:6331::");
//		list.add("172.19.103.105:6330::");
//		list.add("172.19.103.105:6379:fdfs123:");
		
		list.add("127.0.0.1:6380::");
		list.add("127.0.0.1:6381::");
		
		redisClientFactory.setRedisServers(list);
		redisClientFactory.init();
		redisClient = new RedisClient();
		redisClient.setFactory(redisClientFactory);
		
		
	}

	/**
	 * 测试轮询从 redisPool中获取jedis实例
	 */
	@Test
	public void testRoundrobinGetJedis(){
		System.out.println("测试轮询从 jedisPool中获取jedis实例  开始");
		for(int i = 0; i < 100; i ++){
			String currentTimestamp = String.valueOf(System.currentTimeMillis());
			redisClient.setAndReturn(currentTimestamp,currentTimestamp);
			String currentTimestamp2 = redisClient.getAndReturn(currentTimestamp);
			Assert.assertEquals(currentTimestamp, currentTimestamp2);
			JedisPool masterPool = redisClient.getJedisMasterPool();
			Jedis masterJedis = masterPool.getResource();
			JedisPool slavePool = redisClient.getJedisSlavePool();
			Jedis slaveJedis = slavePool.getResource();
			Assert.assertTrue(masterJedis.getClient().getHost().equals(slaveJedis.getClient().getHost()));
			Assert.assertTrue(masterJedis.getClient().getPort() != slaveJedis.getClient().getPort());
			masterPool.returnResource(masterJedis);
			slavePool.returnResource(slaveJedis);
		}
		System.out.println("测试轮询从 jedisPool中获取jedis实例  通过");
	}
	
	
	
	/**
	 * 测试returnJedis 和 连接数
	 */
	@Test
	public void testConectedClientsAndReturnJedis(){
		System.out.println("测试returnJedis 和 连接数 开始");
		JedisPool masterPool = redisClient.getJedisMasterPool();
		Jedis masterJedis = masterPool.getResource();
		masterPool.returnResource(masterJedis);
		for(int i = 0; i <= 10000; i++){ //如果不能正常returnJedis的话，将导致抛出异常
			redisClient.infoAndReturn();
		}
		System.out.println("测试returnJedis 和 连接数 通过");
	}
	
	/**
	 * 对比RedisClient与Jedis的数据
	 * </br>
	 * 测试结果，两者相差不大：比如：</br>
	 * test 1000 time cost 953 in Jedis
	 * </br>
	 * test 1000 time cost 807 in RedisClient
	 * </br>
	 * test 1000 time cost 1170 in Jedis
	 * </br>
	 * test 1000 time cost 823 in RedisClient
	 */
	@Test
	public void pressureTest(){
		System.out.println("对比RedisClient与Jedis的数据  开始");
		//配置要与 JedisPoolManagerTest一致,连接同一台机器
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxActive(300);
		config.setMaxIdle(100);
		config.setMaxWait(50);
		JedisPool pool;
		pool = new JedisPool(config, "172.19.103.105", 6379,10000, "fdfs123");
		Jedis jedis = null;
		long beginTime = System.currentTimeMillis();
		for(int i = 0; i < 1000; i++){
			boolean borrowOrOprSuccess = true;
			try {
				jedis = pool.getResource();
				jedis.set(String.valueOf(i), String.valueOf(i));
				// do redis opt by instance
			} catch (JedisConnectionException e) {
				borrowOrOprSuccess = false;
				if (jedis != null)
					pool.returnBrokenResource(jedis);
	 
			} finally {
				if (borrowOrOprSuccess)
					pool.returnResource(jedis);
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("test 1000 time cost "+ (endTime -  beginTime) + " in Jedis");
		
		beginTime = System.currentTimeMillis();
		for(int i = 0; i < 1000; i++){
			redisClient.setAndReturn(String.valueOf(i), String.valueOf(i));
		}
		endTime = System.currentTimeMillis();
		System.out.println("test 1000 time cost "+ (endTime -  beginTime) + " in RedisClient");
		System.out.println("对比RedisClient与Jedis的数据  结束");
	}
	
	/**
	 * 对比RedisClient每次操作不returnResource的数据
	 * </br>
	 * 每次操作不returnResource test 100000 time cost 113906 in Jedis 
	 * </br>
	 * 每次操作returnResource test 100000 time cost 116360 in RedisClient
	 */
	@Test
	public void pressureIfReturnSourceOrNotTest(){
		System.out.println("对比RedisClient每次操作不returnResource的数据  开始");
		//配置要与 JedisPoolManagerTest一致,连接同一台机器
		long beginTime = System.currentTimeMillis();
		JedisPool pool = redisClient.getJedisMasterPool();
		Jedis jedis = pool.getResource();
		for(int i = 0; i < 100000; i++){
			try {
				jedis.set(String.valueOf(i), String.valueOf(i));
				// do redis opt by instance
			} catch (JedisConnectionException e) {
			} finally {
			}
		}
		//pool.returnResource(jedis);
		long endTime = System.currentTimeMillis();
		System.out.println("test 100000 time cost "+ (endTime -  beginTime) + " in Jedis without returnResouce once operate");
		
		beginTime = System.currentTimeMillis();
		for(int i = 0; i < 100000; i++){
			redisClient.setAndReturn(String.valueOf(i), String.valueOf(i));
		}
		endTime = System.currentTimeMillis();
		System.out.println("test 100000 time cost "+ (endTime -  beginTime) + " in RedisClient with returnResouce once operate");
		System.out.println("对比RedisClient每次操作不returnResource的数据  结束");
	}
	
	
	@Test 
	public void msetAndReturnTest(){
		String keys = redisClient.msetAndReturn("k1","v1","k2","v2") ; 
		System.out.println(keys);
		List<String> str = redisClient.mgetAndReturn("k1","k2","k3") ;
		for(String s : str){
			System.out.println(s);
		}
	}
	
	@Test
	public void setAndReturnTest(){
		byte [] key = "key".getBytes() ; 
		byte [] value = "value".getBytes() ; 
		String keys = redisClient.setAndReturn(key, value) ;
		System.out.println(keys);
		System.out.println(new String(redisClient.getAndReturn(key))); 
		
	}
	/************SET OPERATION TEST**********/
	@Test
	public void saddTest(){
		//向set中添加值,返回set
		String key  = "sadd" ; 
		redisClient.sadd(key, "a","b","c") ; 
		Set<String> set = redisClient.smembers(key) ;
		Iterator<String> it = set.iterator() ; 
		while(it.hasNext()){
			String s = it.next() ; 
			System.out.println(s);
		}
	}
	
	@Test
	public void sremTest(){
		//remove set中的值
		//
		String key  = "sremTest" ; 
		redisClient.sadd(key, "c","java","c++") ; 
		System.out.println(redisClient.srem(key, "non-exists-language")) ; 
		System.out.println(redisClient.srem(key, "c","c++")) ; 
	}
	
	@Test
	public void scardTest(){
		String key  = "scardTest" ; 
		redisClient.sadd(key, "c","java","c++") ; 
		System.out.println(redisClient.scard(key)); 
	}
	@Test
	public void sismemberTest(){
		String key  = "scardTest" ; 
		System.out.println(redisClient.sismember(key, "c") ); 
	}
	
	
	
	/***************HASH OPERATION TEST*****************/
	@Test
	public void hsetTest(){
		String HashName = "hsetTest1" ;
		String fieldName = "initKey" ; 
		String fieldValue = "initValue" ; 
		long t1 = redisClient.hset(HashName, fieldName, fieldValue) ; 
		Assert.assertEquals(1, t1) ;
		String fieldValue2 = "secondSetValue" ;
		redisClient.hset(HashName, fieldName, fieldValue2); 
		long t2 = redisClient.hset(HashName, fieldName, fieldValue2);
		Assert.assertEquals(0, t2) ;
		 
	}
	
	@Test
	public void hmsetTest(){
		String hashName = "hmsetTest1" ; 
		Map<String, String> map = new HashMap<String,String>();
		map.put("lg", "c java c++") ; 
		map.put("cpy", "yy qq") ; 
		String str = redisClient.hmset(hashName, map) ; 
		Assert.assertEquals("OK", str) ; 
		String s = redisClient.hget(hashName, "lg") ; 
		Assert.assertEquals("c java c++", s) ; 
		
	}
	
	@Test
	public void hmgetTest(){
		String hashName="hmgetTest" ; 
		Map<String, String> map = new HashMap<String,String>();
		map.put("lg", "c java c++") ; 
		map.put("cpy", "yy qq") ; 
		String str = redisClient.hmset(hashName, map) ; 
		Assert.assertEquals("OK", str) ; 
		List<String> list = redisClient.hmget(hashName, "lg","cpy") ; 
		for(String s : list){
			System.out.println(s);
		}
	}
	
	@Test
	public void doTransactionTest(){
		redisClient.doTransaction(new TransactionAction(){
			@Override
			public void execute(Transaction transaction) {
				transaction.set("doTransactionTest", "haha") ; 
			}
		}) ; 
		System.out.println(redisClient.getAndReturn("doTransactionTest")); 
	}
	
	
	
	
	public static void main(String []args) throws InterruptedException{
		
		RedisClientFactory redisClientFactory = new RedisClientFactory();
		List<String> list = new ArrayList<String>();
		//这里是业务要连接的redis
//		list.add("172.19.103.105:6331::");
//		list.add("172.19.103.105:6330::");
//		list.add("172.19.103.105:6379:fdfs123:");
		
		list.add("127.0.0.1:6380::");
		list.add("127.0.0.1:6381::");
		
		
		redisClientFactory.setRedisServers(list);
		redisClientFactory.init();
		RedisClient redisClient = new RedisClient();
		redisClient.setFactory(redisClientFactory);
		String str = redisClient.msetAndReturn("hello","hello1","world","world1");
		System.out.println(str);
		System.out.println(redisClient.mgetAndReturn("hello","hello1","world","world1"));
		/*JedisPool pool = redisClientFactory.getMasterPool();
		Jedis jedis = pool.getResource();
		System.out.println(RedisUtils.isMaster(jedis.info()));
		pool.returnResource(jedis);
		
		pool = redisClientFactory.getSlavePool();
		jedis = pool.getResource();
		System.out.println(RedisUtils.isMaster(jedis.info()));
		pool.returnResource(jedis);*/
		
	}
	/**
	 * 测试当master shutdown后，修改slave conf文件，reload RedisFactory 参数是否可以完成master/slave切换
	 * @throws InterruptedException
	 */
	@Test 
	public void testCall() throws InterruptedException{
		int i = 0 ; 
		while(++i<500){
			String str2 = null ; 
			try {
				 str2 = redisClient.msetAndReturn("hello","hello1","world","world1");
				 Thread.currentThread().sleep(1000);
			} catch (Exception e) {
				System.out.println("execute time = "+i+" seconds !"+e);
				Thread.currentThread().sleep(1000);
				continue ;
			}
			try {
				System.out.println(str2);
				System.out.println(redisClient.mgetAndReturn("hello","hello1","world","world1"));
			} catch (Exception e) {
				System.out.println(e);
				continue ; 
			}
			
			
		}
	}
	
	@Test 
	public void testGet() throws InterruptedException{
		int i = 0 ; 
		while(++i<2){
			List<String> str2 = null ; 
			try {
				 str2 = redisClient.mgetAndReturn("hello","world");
			} catch (Exception e) {
				Thread.currentThread().sleep(1000);
				e.printStackTrace();
				System.out.println("execute time = "+i+" seconds !");
				continue ;
			}
			System.out.println(str2);
			
		}
	}
	
	
	
}
