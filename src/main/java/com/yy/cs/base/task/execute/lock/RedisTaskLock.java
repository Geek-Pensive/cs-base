package com.yy.cs.base.task.execute.lock;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.yy.cs.base.redis.RedisClient;

/**
 * 执行redis任务锁对象
 *
 */
public class RedisTaskLock implements TaskLock {
	
	private static final Logger logger = LoggerFactory.getLogger(RedisTaskLock.class);
	
	private final RedisClient redisClient; 
	
	private static final int defaultExpire = 7 * 24 * 60 * 60;   //失效时间7天
	
	private static final String defaultPrefix = "cs-base-task-";   //默认锁5 * 60秒
	
	private static final String defaultSplit = "&";
	
	private final int expire;   //失效时间3 * 60秒
	/**
	 * 构造器函数
	 * @param redisClient
	 * 		{@link RedisClient} redis的java客户端jedis封装类
	 */
	public RedisTaskLock(RedisClient redisClient) {
		this(redisClient,defaultExpire);
	}
	/**
	 * 构造器函数
	 * @param redisClient
	 * 		redisClient对象
	 * @param expire
	 * 		锁失效时间
	 */
	public RedisTaskLock(RedisClient redisClient, int expire) {
		super();
		this.redisClient = redisClient;
		this.expire = expire;
	}
	
	@Override
	public boolean lock(String id, long value) {
		JedisPool pool = null;
		Jedis	jedis  = null;
		boolean result = false;
		try{
			pool = redisClient.getJedisMasterPool();
			jedis =	pool.getResource();
			String key = defaultPrefix + id + defaultSplit + value;
			String v = getLocalAddress() + defaultSplit + value;  
			long setnx = jedis.setnx(key, v);
			//如果等于1 加锁成功
			result = setnx == 1 ? true : false;
	        if(result) {
	        	//设置超时时间
	        	jedis.expire(key, expire);
	        	return result;
	        }
		}catch(Throwable t){
			//防御性容错，如果异常直接返回false
			logger.error(" task id:" + id + " value:" +value , t);
		}
		finally{
			if(pool != null && jedis != null){
				pool.returnResource(jedis);
			}
		}
		return result;
	}

	 
	@Override
	public String getExecuteAddress(String id,long value) {
		
		JedisPool pool = null;
		Jedis	jedis  = null;
		String result = null;
		try{
			pool = redisClient.getJedisMasterPool();
			jedis =	pool.getResource();
			String key = defaultPrefix + id + defaultSplit + value;
			String s = jedis.get(key);
			result = s.substring(0,s.lastIndexOf(defaultSplit));
		}catch(Throwable t){
			//防御性容错，如果异常直接返回false
			logger.error(" task id:" + id + " value:" +value , t);
		}
		finally{
			if(pool != null && jedis != null){
				pool.returnResource(jedis);
			}
		}
		return result;
	}
	/**
	 * 获取本地的ip地址
	 * @return
	 * 		本地地址字符串
	 */
	public static String getLocalAddress() {
    	try {
			return InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			logger.error("",e);
		}
		return "127.0.0.1";
    }
    
//    public static void main(String[] args) {
//    	String s = "qaz/127.0.0.1&31312";
//    	Long old = Long.valueOf(s.substring(s.lastIndexOf(defaultSplit)+1));
//    	System.out.println(s.substring(0,s.lastIndexOf(defaultSplit)));
//	}
	
	
	
	
	
	
	
	
//	public RedisClient getRedisClient() {
//		return redisClient;
//	}
//
//	public void setRedisClient(RedisClient redisClient) {
//		this.redisClient = redisClient;
//	}

}
