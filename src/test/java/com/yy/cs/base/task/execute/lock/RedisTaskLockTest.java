package com.yy.cs.base.task.execute.lock;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.yy.cs.base.redis.RedisClient;
import com.yy.cs.base.task.execute.RunnableInit;

public class RedisTaskLockTest {

	private RedisClient redisClient;
	private RunnableInit rinit;
	private RedisTaskLock rtLock;
	
	@Before
	public void init(){
		rinit = new  RunnableInit();
		redisClient = new RedisClient();
		rinit.setRedis(redisClient);
		rtLock = new RedisTaskLock(redisClient, 2);
	}
	
	/**
	 * 测试lock锁的功能，设置锁的超时时间为2秒，没调用一次lock方法，休眠1秒.则for循环中的结果应该是一次锁成功，一次锁失败。
	 * @throws InterruptedException
	 */
	@Test
	public void testLock() throws InterruptedException{
		for(int i = 1;i<5;i++){
			if(i%2 == 1){
				Assert.assertEquals(true, rtLock.lock("001", 123));
				System.out.println("true");
			}
			if(i%2 == 0){
				Assert.assertEquals(false, rtLock.lock("001", 123));
				System.out.println("false");
			}
			Thread.sleep(1000);
		}
	}
	
	/**
	 * 测试getExecuteAddress方法，其返回的结果应该等于调用getLocalAddress方法得到的本机主机名/本机Ip
	 */
	@Test
	public void testGetExecuteAddress(){
		rtLock.lock("002", 000);
		String exaddr = rtLock.getExecuteAddress("002", 000);
		Assert.assertEquals(RedisTaskLock.getLocalAddress(), exaddr);
	}
	
	@Test
	public void testGetLocalAddress(){
		Assert.assertNotNull(RedisTaskLock.getLocalAddress());
	}  
}
