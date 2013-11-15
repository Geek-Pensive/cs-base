package com.yy.cs.base.client.jedis;

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

import com.yy.cs.client.jedis.JedisPoolManager;

/**
 * 
 * @author haoqing
 * JedisPoolManager 的覆盖测试用例
 *
 */
public class JedisPoolManagerTest {

	JedisPoolManager jedisPoolManager;
	
	@Before
	public void init(){
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"jedis-application.xml");
        jedisPoolManager = (JedisPoolManager) context.getBean("jedisPoolManager");
	}

	/**
	 * 测试轮询从 jedisPool中获取jedis实例
	 */
	@Test
	public void testRoundrobinGetJedis(){
		for(int i = 0; i < 100; i ++){
			Jedis jedis = jedisPoolManager.getJedis();
			String str = String.valueOf(System.currentTimeMillis());
			jedis.set(str,str);
			Jedis readJedis = jedisPoolManager.getReadJedis();
			Jedis writeJedis = jedisPoolManager.getWriteJedis();
			//如果i为偶数则命中jedisPools 中的第一个配置，如果为奇数，则命中jedisPools的第二个配置
			if(i % 2 == 0){
				Assert.assertEquals(str,readJedis.get(str));
				Assert.assertFalse(str.equals(writeJedis.get(str)));
			}else{
				Assert.assertEquals(str,writeJedis.get(str));
				Assert.assertFalse(str.equals(readJedis.get(str)));
			}
			//释放jedis
			jedisPoolManager.returnJedis(jedis);
			jedisPoolManager.returnReadJedis(readJedis);
			jedisPoolManager.returnWriteJedis(writeJedis);
		}
	}
	
	
	
	/**
	 * 测试returnJedis 和 连接数
	 */
	@Test
	public void testConectedClientsAndReturnJedis(){
		Jedis jedis = jedisPoolManager.getReadJedis();
		int clientNum1 = getConnectedClientNum(jedis.info());
		Jedis jedis2 = jedisPoolManager.getReadJedis();
		int clientNum2 = getConnectedClientNum(jedis2.info());
		Assert.assertEquals(1, clientNum2 - clientNum1);
		List<Jedis> list = new ArrayList<Jedis>();
		Jedis lastJedsi = jedis2;
		for(int i = 0; i <= 100; i++){ //如果不能正常returnJedis的话，将导致抛出异常
			Jedis j = jedisPoolManager.getReadJedis();
			jedisPoolManager.returnReadJedis(lastJedsi);
			lastJedsi = j;
			list.add(j);
		}
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
		//配置要与 JedisPoolManagerTest一致,连接同一台机器
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxActive(300);
		config.setMaxIdle(100);
		config.setMaxWait(50);
		JedisPool pool;
		pool = new JedisPool(config, "172.19.108.117", 6380);
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
			jedis = jedisPoolManager.getReadJedis();
			jedis.set(String.valueOf(i), String.valueOf(i));
			// do redis opt by instance
			jedisPoolManager.returnReadJedis(jedis);
		}
		endTime = System.currentTimeMillis();
		System.out.println("test 1000 time cost "+ (endTime -  beginTime) + " in JedisPoolManagerTest");
	}
	
	/**
	 * 从jedis返回的info信息中获取connected client 数量
	 * @param info
	 * @return
	 */
	public static int getConnectedClientNum(String info){
		if(info == null || "".equals(info)){
			return 0;
		}
		Pattern p = Pattern.compile("connected_clients:(\\d+)");
		Matcher m = p.matcher(info);
		if(m.find()){
			return Integer.valueOf(m.group(1));
		}
		return 0;
	}
}
