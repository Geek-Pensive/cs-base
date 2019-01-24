package com.yy.cs.base.redis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * 方便获取从库的host port
 * @author tanghengde
 *
 */
public class CustomJedisPool extends JedisPool {

    private final HostAndPort hostAndPort;

    public CustomJedisPool(final JedisPoolConfig poolConfig, final HostAndPort hostAndPort, final int timeout) {
        this(poolConfig, hostAndPort, timeout, null);
    }

    public CustomJedisPool(final JedisPoolConfig poolConfig, final HostAndPort hostAndPort, final int timeout, final String password) {
        super(poolConfig, hostAndPort.getHost(), hostAndPort.getPort(), timeout, password, Protocol.DEFAULT_DATABASE, null);
        this.hostAndPort = hostAndPort;
    }

    public HostAndPort getHostAndPort() {
        return hostAndPort;
    }

}
