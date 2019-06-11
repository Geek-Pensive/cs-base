package com.yy.cs.base.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.yy.cs.base.hostgroup.HostGroupLocator;
import com.yy.cs.base.redis.sentinel.NearbyJedisSentinelPool;
import com.yy.cs.base.redis.sentinel.RedisSentinelFactory;

import redis.clients.jedis.Jedis;

/**
 * 
 *
 */
public class RedisSentinelTest {

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
		
		redisClientFactory.setUseNearbySlave(true);
		
		redisClientFactory.init();
		
		// 重置hostGroupLocators，用于本地测试模拟
		List<HostGroupLocator> hostGroupLocators = new ArrayList<>();
		hostGroupLocators.add(new HostGroupLocator() {
            
            @Override
            public String getGroup(String host, String defaultGroup) {
                return getGroup(host);
            }
            
            @Override
            public String getGroup(String host) {
                if("58.215.180.219".equals(host)){
                    return "1";
                }
                return "2";
            }
            
            @Override
            public String getGroup() {
                return "1";
            }
        });
		((NearbyJedisSentinelPool)redisClientFactory.getMasterPool()).setHostGroupLocators(hostGroupLocators);
		((NearbyJedisSentinelPool)redisClientFactory.getMasterPool()).setPickHighestSlaves(false);
		((NearbyJedisSentinelPool)redisClientFactory.getMasterPool()).setPickHighestSlaves(true);

	}

	@Test
	public void testSetnxpx() {
	    
	    try (Jedis jedis = redisClientFactory.getSlavePool().getResource()) {
            System.out.println("------->" + jedis.getClient().getHost() + ":" + jedis.getClient().getPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
	    
        synchronized (RedisSentinelTest.class) {
            while (true) {
                try {
                    RedisSentinelTest.class.wait();
                } catch (InterruptedException e) {
                }
            }
        }
	}
}
