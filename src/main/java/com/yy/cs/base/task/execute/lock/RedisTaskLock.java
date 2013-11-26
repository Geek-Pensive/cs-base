package com.yy.cs.base.task.execute.lock;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.yy.cs.base.redis.RedisClient;
import com.yy.cs.base.redis.RedisPoolManager;

public class RedisTaskLock implements TaskLock {
	
	private final RedisPoolManager redisClient; 
	
	private static final int defaultExpire = 3 * 60;   //默认锁3 * 60秒
	
	private static final String defaultPrefix = "cs-base-task-";   //默认锁5 * 60秒
	
	private static final String defaultSplit = "&";
	
	private final int expire;   //默认锁3 * 60秒
	
	public RedisTaskLock(RedisPoolManager redisClient) {
		this(redisClient,defaultExpire);
	}
	
	public RedisTaskLock(RedisPoolManager redisClient, int expire) {
		super();
		this.redisClient = redisClient;
		this.expire = expire;
	}
	
	@Override
	public boolean lock(String id, long value) {
		RedisClient client = redisClient.getMasterJedis();
		boolean result;
		String key = defaultPrefix+id;
		String v = getLocalAddress()+defaultSplit+value;  
		long setnx = client.getNativeJedis().setnx(key, v);
		//如果等于1 加锁成功
		result = setnx == 1 ? true : false;
        if(result) {
        	//设置超时时间
        	client.getNativeJedis().expire(key, expire);
        	return result;
        } else {
            //如果没有加锁成功，检查是否是死锁，如果是死锁，则释放
        	String s = client.getNativeJedis().get(key);
        	Long old = Long.valueOf(s.substring(s.lastIndexOf(defaultSplit)+1));
        	//如果当前执行点大于被锁时间点，释放该锁
        	if(value > old.longValue()){
        		String oldValue = client.getNativeJedis().getSet(key, v);
        		if(oldValue == null || !oldValue.equals(v)){
        			client.getNativeJedis().expire(key, expire);
        			result = true;
        		}
        	}
        }
        client.returnSelf();
		return result;
	}

	 
	public boolean unLock(String id, long value) {
	//	redisClient.getNativeJedis().del(id);
		return false;
	}

	@Override
	public String getExecuteAddress(String id) {
		RedisClient client = redisClient.getMasterJedis();
		String key = defaultPrefix+id;
		String s = client.getNativeJedis().get(key);
		client.returnSelf();
		return s.substring(0,s.lastIndexOf(defaultSplit));
	}

	public static String getLocalAddress() {
    	try {
			return InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
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
