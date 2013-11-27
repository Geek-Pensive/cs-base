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
	
	private RedisClientFactory factory;
	
	public JedisPool getJedisMasterPool() {
		return factory.getMasterPool();
	}
	
	public JedisPool getJedisSlavePool() {
		return factory.getSlavePool();
	}

	/**
	 * 执行set操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param key
	 * @param value
	 * @return
	 */
	public synchronized String setAndReturn(final String key, String value){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = getJedisMasterPool().getResource();
			return jedis.set(key, value);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			throw new NyyRuntimeException("jedis set fail", e);
		}finally{
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 执行set操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param key
	 * @param value
	 * @param seconds  有效时间
	 * @return
	 */
	public synchronized String setAndReturn(final String key, String value, int seconds){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = getJedisMasterPool().getResource();
			return jedis.setex(key, seconds, value);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			throw new NyyRuntimeException("jedis set fail", e);
		}finally{
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 执行set操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param key
	 * @param value
	 * @param seconds  有效时间
	 * @return
	 */
	public synchronized String setAndReturn(final byte[] key, byte[] value, int seconds){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = getJedisMasterPool().getResource();
			return jedis.setex(key, seconds, value);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			throw new NyyRuntimeException("jedis set fail", e);
		}finally{
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 获取info信息
	 * @return
	 */
	public synchronized String infoAndReturn(){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = getJedisMasterPool().getResource();
			return jedis.info();
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			throw new NyyRuntimeException("jedis info fail", e);
		}finally{
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 执行get操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param key
	 * @param value
	 * @return
	 */
	public synchronized String getAndReturn(final String key){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisSlavePool();
			jedis = getJedisSlavePool().getResource();
			return jedis.get(key);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			throw new NyyRuntimeException("jedis get fail", e);
		}finally{
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 执行set操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @see #getNativeJedis()
	 * @see #returnSelf() 
	 * @param key
	 * @param value
	 * @return
	 */
	public String setAndReturn(final byte[] key, byte[] value){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = getJedisMasterPool().getResource();
			return jedis.set(key, value);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			throw new NyyRuntimeException("jedis set fail", e);
		}finally{
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 执行get操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param key
	 * @param value
	 * @return
	 */
	public byte[] getAndReturn(final byte[] key){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisSlavePool();
			jedis = getJedisSlavePool().getResource();
			return jedis.get(key);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			throw new NyyRuntimeException("jedis get fail", e);
		}finally{
			jedisPool.returnResource(jedis);
		}
	}

	public RedisClientFactory getFactory() {
		return factory;
	}

	public void setFactory(RedisClientFactory factory) {
		this.factory = factory;
	}
	
}
