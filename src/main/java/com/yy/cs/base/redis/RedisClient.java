package com.yy.cs.base.redis;

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.yy.cs.base.thrift.exception.CsRedisRuntimeException;

/**
 * redis client 的封装类
 * @author haoqing
 *
 */
public class RedisClient {
	
	private RedisClientFactory factory;
	
	/**
	 * 从redisMasterPool中随机获取pool
	 * @return
	 */
	public JedisPool getJedisMasterPool() {
		return factory.getMasterPool();
	}
	/**
	 * 从redisSlavePool中随机获取pool
	 * @return
	 */
	public JedisPool getJedisSlavePool() {
		return factory.getSlavePool();
	}

	/**
	 * 执行set操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param dbIndex  redis db index
	 * @param key
	 * @param value
	 * @return
	 */
	public synchronized String setAndReturn(int dbIndex, final String key, String value){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = getJedisMasterPool().getResource();
			//如果为0,则不需通信表明select db0
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.set(key, value);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			throw new CsRedisRuntimeException("jedis set fail", e);
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
	 * @return
	 */
	public synchronized String setAndReturn(final String key, String value){
		return setAndReturn(0, key, value);
	}
	
	/**
	 * 执行set操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param key
	 * @param value
	 * @param dbIndex redis db index
	 * @param seconds  有效时间
	 * @return
	 */
	public synchronized String setAndReturn(int dbIndex, final String key, String value, int seconds){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = getJedisMasterPool().getResource();
			//如果为0,则不需通信表明select db0
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.setex(key, seconds, value);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			throw new CsRedisRuntimeException("jedis set fail", e);
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
		return setAndReturn(0, key, value, seconds);
	}
	
	/**
	 * 执行set操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param key
	 * @param value
	 * @param dbIndex redis db index
	 * @param seconds  有效时间
	 * @return
	 */
	public synchronized String setAndReturn(int dbIndex, final byte[] key, byte[] value, int seconds){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = getJedisMasterPool().getResource();
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.setex(key, seconds, value);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			throw new CsRedisRuntimeException("jedis set fail", e);
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
		return setAndReturn(0, key, value, seconds);
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
			throw new CsRedisRuntimeException("jedis info fail", e);
		}finally{
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 执行get操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param dbIndex
	 * @param key
	 * @return
	 */
	public synchronized String getAndReturn(int dbIndex, final String key){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisSlavePool();
			jedis = getJedisSlavePool().getResource();
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.get(key);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			throw new CsRedisRuntimeException("jedis get fail", e);
		}finally{
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 执行get操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param key
	 * @return
	 */
	public synchronized String getAndReturn(final String key){
		return getAndReturn(0, key);
	}
	
	/**
	 * 执行set操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 *@param dbIndex
	 * @param key
	 * @param value
	 * @return
	 */
	public String setAndReturn(int dbIndex, final byte[] key, byte[] value){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = getJedisMasterPool().getResource();
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.set(key, value);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			throw new CsRedisRuntimeException("jedis set fail", e);
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
	 * @return
	 */
	public String setAndReturn(final byte[] key, byte[] value){
		return setAndReturn(0, key, value);
	}
	
	/**
	 * 执行get操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param dbIndex
	 * @param key
	 * @return
	 */
	public byte[] getAndReturn(int dbIndex, final byte[] key){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisSlavePool();
			jedis = getJedisSlavePool().getResource();
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.get(key);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			throw new CsRedisRuntimeException("jedis get fail", e);
		}finally{
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 执行get操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param key
	 * @return
	 */
	public byte[] getAndReturn(final byte[] key){
		return getAndReturn(0,key);
	}
	
	
	
	
	/**
	 * 执行mset操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 *@param dbIndex
	 * @param key
	 * @param value
	 * @return  Status code reply Basically +OK as MSET can't fail
	 */
	public String msetAndReturn(int dbIndex, String... keysvalues){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = getJedisMasterPool().getResource();
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.mset(keysvalues);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			throw new CsRedisRuntimeException("jedis mset fail", e);
		}finally{
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 执行mset操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param key
	 * @param value
	 * @return  Status code reply Basically +OK as MSET can't fail
	 */
	public String msetAndReturn(String... keysvalues){
		return msetAndReturn(0, keysvalues);
	}
	
	/**
	 * 执行mget操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 *@param dbIndex
	 * @param key
	 * @param value
	 * @return  
	 */
	public List<String> mgetAndReturn(int dbIndex, String... keys){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = getJedisMasterPool().getResource();
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.mget(keys);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			throw new CsRedisRuntimeException("jedis mget fail", e);
		}finally{
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 执行mset操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param key
	 * @param value
	 * @return  
	 */
	public List<String> mgetAndReturn(String... keys){
		return mgetAndReturn(0, keys);
	}
	
	
	

	public RedisClientFactory getFactory() {
		return factory;
	}

	public void setFactory(RedisClientFactory factory) {
		this.factory = factory;
	}
	
}
