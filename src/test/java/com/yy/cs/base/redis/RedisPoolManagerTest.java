package com.yy.cs.base.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.yy.cs.base.redis.RedisClient;
import com.yy.cs.base.redis.RedisPoolManager;

/**
 * 
 * @author haoqing
 * JedisPoolManager 的覆盖测试用例
 *
 */
public class RedisPoolManagerTest {

	RedisPoolManager jedisPoolManager;
	
	@Before
	public void init(){
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"jedis-application.xml");
        jedisPoolManager = (RedisPoolManager) context.getBean("jedisPoolManager");
	}

	/**
	 * 测试轮询从 redisPool中获取jedis实例
	 */
	@Test
	public void testRoundrobinGetJedis(){
		System.out.println("测试轮询从 jedisPool中获取jedis实例  开始");
		for(int i = 0; i < 100; i ++){
			String currentTimestamp = String.valueOf(System.currentTimeMillis());
			RedisClient redisClient = jedisPoolManager.getMasterJedis();
			redisClient.setAndReturn(currentTimestamp,currentTimestamp);
			RedisClient redisClient1 = jedisPoolManager.getSlaveJedis();
			RedisClient redisClient2 = jedisPoolManager.getSlaveJedis();
			Assert.assertTrue(redisClient1.getJedis().getClient().getHost().equals(redisClient2.getJedis().getClient().getHost()));
			Assert.assertTrue(redisClient1.getJedis().getClient().getPort() != redisClient2.getJedis().getClient().getPort());
		}
		System.out.println("测试轮询从 jedisPool中获取jedis实例  通过");
	}
	
	
	
	/**
	 * 测试returnJedis 和 连接数
	 */
	@Test
	public void testConectedClientsAndReturnJedis(){
		System.out.println("测试returnJedis 和 连接数 开始");
		RedisClient redisClient = jedisPoolManager.getMasterJedis();
		int clientNum1 = RedisUtils.getConnectedClientNum(redisClient.getNativeJedis().info());
		RedisClient redisClient2 = jedisPoolManager.getMasterJedis();
		int clientNum2 = RedisUtils.getConnectedClientNum(redisClient2.getNativeJedis().info());
		Assert.assertEquals(1, clientNum2 - clientNum1);
		List<RedisClient> list = new ArrayList<RedisClient>();
		RedisClient lastJedis = redisClient2;
		for(int i = 0; i <= 1000; i++){ //如果不能正常returnJedis的话，将导致抛出异常
			RedisClient j = jedisPoolManager.getMasterJedis();
			lastJedis.returnSelf();
			lastJedis = j;
			list.add(j);
		}
		System.out.println("测试returnJedis 和 连接数 通过");
	}
	
	/**
	 * 对比JedisPoolManager与Jedis的数据
	 * </br>
	 * 测试结果，两者相差不大：比如：</br>
	 * test 1000 time cost 5744 in Jedis
	 * </br>
	 * test 1000 time cost 5422 in JedisPoolManagerTest
	 * </br>
	 * test 1000 time cost 5074 in Jedis
	 * </br>
	 * test 1000 time cost 5288 in JedisPoolManagerTest
	 */
	@After
	public void pressureTest(){
		System.out.println("对比JedisPoolManager与Jedis的数据  开始");
		//配置要与 JedisPoolManagerTest一致,连接同一台机器
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxActive(300);
		config.setMaxIdle(100);
		config.setMaxWait(50);
		JedisPool pool;
		pool = new JedisPool(config, "172.19.103.105", 6379,2000, "fdfs123");
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
			RedisClient r = jedisPoolManager.getMasterJedis();
			r.setAndReturn(String.valueOf(i), String.valueOf(i));
		}
		endTime = System.currentTimeMillis();
		System.out.println("test 1000 time cost "+ (endTime -  beginTime) + " in JedisPoolManagerTest");
		System.out.println("对比JedisPoolManager与Jedis的数据  结束");
	}
	
	
	
	
}
