package com.yy.cs.base.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

import com.yy.cs.base.thrift.exception.CsRedisRuntimeException;

/**
 * redis client 的封装类
 * @author haoqing
 * 
 */
	
public class RedisClient {
	
	private static final Logger log = LoggerFactory
			.getLogger(RedisClient.class) ; 
	private RedisClientFactory factory;
	
	public RedisClientFactory getFactory() {
		return factory;
	}

	public void setFactory(RedisClientFactory factory) {
		this.factory = factory;
	}
	
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
	public String setAndReturn(int dbIndex, final String key, String value){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = jedisPool.getResource();
			
			//如果为0,则不需通信表明select db0
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.set(key, value);
		}catch(Exception e){
			factory.reload();
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			throw new CsRedisRuntimeException("jedis set fail", e);
		}finally{
		    if(jedis != null){
		        jedisPool.returnResource(jedis);
		    }
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
	public String setAndReturn(final String key, String value){
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
	public String setAndReturn(int dbIndex, final String key, String value, int seconds){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = jedisPool.getResource();

			//如果为0,则不需通信表明select db0
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.setex(key, seconds, value);
		}catch(Exception e){
			factory.reload();
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			throw new CsRedisRuntimeException("jedis set fail", e);
		}finally{
		    if(jedis != null){
                jedisPool.returnResource(jedis);
            }
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
	public String setAndReturn(final String key, String value, int seconds){
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
	public String setAndReturn(int dbIndex, final byte[] key, byte[] value, int seconds){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
		    jedisPool = getJedisMasterPool();
			jedis = jedisPool.getResource();

			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.setex(key, seconds, value);
		}catch(Exception e){
			factory.reload();
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			throw new CsRedisRuntimeException("jedis set fail", e);
		}finally{
		    if(jedis != null){
                jedisPool.returnResource(jedis);
            }
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
	public String setAndReturn(final byte[] key, byte[] value, int seconds){
		return setAndReturn(0, key, value, seconds);
	}
	
	/**
	 * 获取info信息
	 * @return
	 */
	public String infoAndReturn(){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = jedisPool.getResource();
			
			return jedis.info();
		}catch(Exception e){
			factory.reload();
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			throw new CsRedisRuntimeException("jedis info fail", e);
		}finally{
		    if(jedis != null){
                jedisPool.returnResource(jedis);
            }
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
	public String getAndReturn(int dbIndex, final String key){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisSlavePool();
			jedis = jedisPool.getResource();
			
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.get(key);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			throw new CsRedisRuntimeException("jedis get fail", e);
		}finally{
		    if(jedis != null){
                jedisPool.returnResource(jedis);
            }
		}
	}
	
	/**
	 * 执行get操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param key
	 * @return
	 */
	public String getAndReturn(final String key){
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
			jedis = jedisPool.getResource();
			
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.set(key, value);
		}catch(Exception e){
			factory.reload();
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			throw new CsRedisRuntimeException("jedis set fail", e);
		}finally{
		    if(jedis != null){
                jedisPool.returnResource(jedis);
            }
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
			jedis = jedisPool.getResource();
			
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.get(key);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			throw new CsRedisRuntimeException("jedis get fail", e);
		}finally{
		    if(jedis != null){
                jedisPool.returnResource(jedis);
            }
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
			jedis = jedisPool.getResource();
			
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.mset(keysvalues);
		}catch(Exception e){
			factory.reload();
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			throw new CsRedisRuntimeException(e.getMessage(), e);
		}finally{
		    if(jedis != null){
                jedisPool.returnResource(jedis);
            }
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
			  jedisPool = getJedisSlavePool();
			  jedis = jedisPool.getResource();
			
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.mget(keys);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			throw new CsRedisRuntimeException("jedis mget fail", e);
		}finally{
		    if(jedis != null){
                jedisPool.returnResource(jedis);
            }
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
	
	
	/**
     * 执行mset操作，然后释放client连接
     * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
     * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
     *@param dbIndex
     * @param key
     * @param value
     * @return  Status code reply Basically +OK as MSET can't fail
     */
    public String msetAndReturn(int dbIndex, byte[]... keysvalues){
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try{
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();
        	
            if(dbIndex != 0){
                jedis.select(dbIndex);
            }
            return jedis.mset(keysvalues);
        }catch(Exception e){
        	factory.reload();
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis mset fail", e);
        }finally{
            if(jedis != null){
                jedisPool.returnResource(jedis);
            }
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
    public String msetAndReturn(byte[]... keysvalues){
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
    public List<byte[]> mgetAndReturn(int dbIndex, byte[]... keys){
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try{
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();
            
            if(dbIndex != 0){
                jedis.select(dbIndex);
            }
            return jedis.mget(keys);
        }catch(Exception e){
        	factory.reload();
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis mget fail", e);
        }finally{
            if(jedis != null){
                jedisPool.returnResource(jedis);
            }
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
    public List<byte[]> mgetAndReturn(byte[]... keys){
        return mgetAndReturn(0, keys);
    }
    
	/**
	 * 
	 */
    /******* Set Operation **********/

    public Set<String> smembers(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisSlavePool();
            jedis = jedisPool.getResource();

            return jedis.smembers(key);
        } catch (Exception e) {
        	factory.reload();
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis get fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /**
     * redis SADD 操作，向名为key的set中添加一个或者多个value
     * @param key
     * @param values
     * @return
     */
    public Long sadd(String key, String... values) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();
        	
            return jedis.sadd(key, values);
        } catch (Exception e) {
        	factory.reload();
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    
    /**
     * redis的 SREM 操作，移除set(名称为key)中的一个或多个value
     * @param key
     * @param values
     * @return
     */
    public Long srem(String key, String... values) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();

            return jedis.srem(key, values);
        } catch (Exception e) {
        	factory.reload();
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /**
     * Redis 的SCARD 操作，返回当前set中的value个数。
     * 集合的基数。
	 * 当 key 不存在时，返回 0 
     * @param key
     * @return
     */
    public Long scard(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisSlavePool();
            jedis = jedisPool.getResource();

            return jedis.scard(key);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis get fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    /**
     * SISMEMBER key member 判断 member 元素是否集合 key 的成员。
     * 如果 member 元素是集合的成员，返回 1 。
     * 如果 member 元素不是集合的成员，或 key 不存在，返回 0 。
     * @param key
     * @param value
     * @return
     */
    public Boolean sismember(String key, String value) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisSlavePool();
            jedis = jedisPool.getResource();
             
              return jedis.sismember(key, value);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis get fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /******* Hash Operation **********/
    
    /**
     * 将哈希表 key 中的域 field 的值设为 value 。如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。
     * 如果域 field 已经存在于哈希表中，旧值将被覆盖。
     * @param key
     * @param field
     * @param value
     * @return
     *       如果 field 是哈希表中的一个新建域，并且值设置成功，返回 1 。
     *       如果哈希表中域 field 已经存在且旧值已被新值覆盖，返回 0 。
     */
    public Long hset(String key, String field, String value) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
              jedisPool = getJedisMasterPool();
              jedis = jedisPool.getResource();
              
              return jedis.hset(key, field, value);
        } catch (Exception e) {
        	factory.reload();
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    /**
     * 同时将多个 field-value (域-值)对设置到哈希表 key 中。此命令会覆盖哈希表中已存在的域。
     * 如果 key 不存在，一个空哈希表被创建并执行 HMSET 操作。
     * @param key
     * @param value
     * @return
     *      如果命令执行成功，返回 OK 。
	 *	         当 key 不是哈希表(hash)类型时，返回一个错误。
     */
    public String hmset(String key, Map<String, String> value) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
        	
             jedisPool = getJedisMasterPool();
             jedis = jedisPool.getResource();
              return jedis.hmset(key, value);
        } catch (Exception e) {
        	factory.reload();
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    
    /**
     * 返回哈希表 key 中给定域 field 的值。
     * @param key
     * @param field
     * @return
     *    给定域的值。当给定域不存在或是给定 key 不存在时，返回 nil 。
     */
    public String hget(String key, String field) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
	          jedisPool = getJedisSlavePool();
	          jedis = jedisPool.getResource();
        	
              return jedis.hget(key, field);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis get fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    /**
     * 返回哈希表 key 中，一个或多个给定域的值。
     * 如果给定的域不存在于哈希表，那么返回一个 nil 值。
     * 因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
     * @param key
     * @param fields
     * @return
     *     一个包含多个给定域的关联值的表，表值的排列顺序和给定域参数的请求顺序一样
     */
    public List<String> hmget(String key, String... fields) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
             jedisPool = getJedisSlavePool();
             jedis = jedisPool.getResource();

             return jedis.hmget(key, fields);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis get fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /*** common operation ***/

    public Long remove(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
             jedisPool = getJedisMasterPool();
             jedis = jedisPool.getResource();
            return jedis.del(key);
        } catch (Exception e) {
        	factory.reload();
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    
    /**
     * 将 key 改名为 newkey 。当 key 和 newkey 相同，或者 key 不存在时，返回一个错误。
     * 当 newkey 已经存在时， RENAME 命令将覆盖旧值。
     * @param oldkey
     * @param newkey
     * @return
     *    改名成功时提示 OK ，失败时候返回一个错误。
     */
    public String rename(String oldkey,String newkey){
    	
    	Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();
              
            return jedis.rename(oldkey, newkey);
        } catch (Exception e) {
        	factory.reload();
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    
    
    public boolean exists(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisSlavePool();
            jedis = jedisPool.getResource();
	         
            return jedis.exists(key);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis get fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    
    public String ping(){
    	Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisSlavePool();
            jedis = jedisPool.getResource();
           
            return jedis.ping() ; 
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis get fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    
    /*** advanced operation ***/

    public void watch(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();

            jedis.watch(key);
        } catch (Exception e) {
        	factory.reload();
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    public List<Object> doTransaction(TransactionAction transactionAction) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();
        	
            Transaction transaction = jedis.multi();
            transactionAction.execute(transaction);
            return transaction.exec();
        } catch (Exception e) {
        	factory.reload();
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    public void doPipline(PiplineAction piplineAction) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();
            
            Pipeline pipline = jedis.pipelined();
            piplineAction.execute(pipline);
            pipline.sync();
        } catch (Exception e) {
        	factory.reload();
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    public List<Object> doPiplineAndReturn(PiplineAction piplineAction) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();
        	
            Pipeline pipline = jedis.pipelined();
            piplineAction.execute(pipline);
            return pipline.syncAndReturnAll();
        } catch (Exception e) {
        	factory.reload();
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
	
    
    
    
    public interface PiplineAction {
        void execute(Pipeline pipline);
    }

    public interface TransactionAction {
        void execute(Transaction transaction);
    }
	
}
