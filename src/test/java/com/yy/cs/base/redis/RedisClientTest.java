package com.yy.cs.base.redis;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
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
		list.add("172.19.103.105:6331::");
		list.add("172.19.103.105:6330::");
		list.add("172.19.103.105:6379:fdfs123:");
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
		pool.returnResource(jedis);
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
	
	
	
	public static void main(String []args){
		
		RedisClientFactory redisClientFactory = new RedisClientFactory();
		List<String> list = new ArrayList<String>();
		//这里是业务要连接的redis
		list.add("172.19.103.105:6331::");
		list.add("172.19.103.105:6330::");
		list.add("172.19.103.105:6379:fdfs123:");
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
	
	
	
}
