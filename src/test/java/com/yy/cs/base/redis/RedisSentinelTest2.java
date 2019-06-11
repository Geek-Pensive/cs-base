package com.yy.cs.base.redis;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.yy.cs.base.redis.sentinel.RedisSentinelFactory;

import redis.clients.jedis.Jedis;

/**
 * 
 *
 */
public class RedisSentinelTest2 {

    RedisSentinelFactory redisClientFactory;
    
	@Before
	public void init(){
		
		redisClientFactory = new RedisSentinelFactory();

		HashSet<String> servers = new HashSet<>();
		servers.add("58.215.180.218:26380");
		servers.add("58.215.180.219:26381");
		servers.add("221.228.86.201:26382");
		redisClientFactory.setServers(servers);
		redisClientFactory.setMasterName("mymaster");
		
		redisClientFactory.init();
	}

	@Test
	public void testSetnxpx() {
	    
	    try (Jedis jedis = redisClientFactory.getMasterPool().getResource()) {
	        System.out.println("master:------->" + jedis.getClient().getHost() + ":" + jedis.getClient().getPort());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    try (Jedis jedis = redisClientFactory.getSlavePool().getResource()) {
            System.out.println("slave:------->" + jedis.getClient().getHost() + ":" + jedis.getClient().getPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
	    
        synchronized (RedisSentinelTest2.class) {
            while (true) {
                try {
                    RedisSentinelTest2.class.wait();
                } catch (InterruptedException e) {
                }
            }
        }
	}
}
