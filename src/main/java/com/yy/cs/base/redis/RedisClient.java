package com.yy.cs.base.redis;

import com.yy.cs.base.nyy.exception.NyyRuntimeException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis client 的封装类
 * @author haoqing
 *
 */
public class RedisClient {
	
	private Jedis jedis;
	private JedisPool jedisPool;
	
	public Jedis getJedis() {
		return jedis;
	}

	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}

	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	/**
	 * 执行set操作，然后释放client连接
	 * </br>注意如果client连接释放后，不能再使用该client
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getNativeJedis 方法
	 * </br>并在多次调用后，释放该连接可调用 returnSelf 方法 
	 * @see #getNativeJedis()
	 * @see #returnSelf() 
	 * @param key
	 * @param value
	 * @return
	 */
	public String setAndReturn(final String key, String value){
		try{
			return jedis.set(key, value);
		}catch(Exception e){
			throw new NyyRuntimeException("jedis set fail", e);
		}finally{
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 执行get操作，然后释放client连接
	 * </br>注意如果client连接释放后，不能再使用该client
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getNativeJedis 方法
	 * </br>并在多次调用后，释放该连接可调用 returnSelf 方法 
	 * @see #getNativeJedis()
	 * @see #returnSelf() 
	 * @param key
	 * @param value
	 * @return
	 */
	public String getAndReturn(final String key){
		try{
			return jedis.get(key);
		}catch(Exception e){
			throw new NyyRuntimeException("jedis get fail", e);
		}finally{
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 执行set操作，然后释放client连接
	 * </br>注意如果client连接释放后，不能再使用该client
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getNativeJedis 方法
	 * </br>并在多次调用后，释放该连接可调用 returnSelf 方法 
	 * @see #getNativeJedis()
	 * @see #returnSelf() 
	 * @param key
	 * @param value
	 * @return
	 */
	public String setAndReturn(final byte[] key, byte[] value){
		try{
			return jedis.set(key, value);
		}catch(Exception e){
			throw new NyyRuntimeException("jedis set fail", e);
		}finally{
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 执行get操作，然后释放client连接
	 * </br>注意如果client连接释放后，不能再使用该client
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getNativeJedis 方法
	 * </br>并在多次调用后，释放该连接可调用 returnSelf 方法 
	 * @see #getNativeJedis()
	 * @see #returnSelf() 
	 * @param key
	 * @param value
	 * @return
	 */
	public byte[] getAndReturn(final byte[] key){
		try{
			return jedis.get(key);
		}catch(Exception e){
			throw new NyyRuntimeException("jedis get fail", e);
		}finally{
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 获取原生的Jedis 连接
	 * @return 
	 */
	public Jedis getNativeJedis(){
		return this.jedis;
	}
	
	/**
	 * 释放Jedis 连接
	 * @return 
	 */
	public void returnSelf(){
		jedisPool.returnResource(jedis);
	}
	
	/**
	 * 释放Jedis broken连接
	 * @return 
	 */
	public void returnBrokenSelf(){
		jedisPool.returnBrokenResource(jedis);
	}
}
