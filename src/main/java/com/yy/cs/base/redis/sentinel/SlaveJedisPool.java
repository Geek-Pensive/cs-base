package com.yy.cs.base.redis.sentinel;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

/**
 * 方便获取从库的host port
 * @author tanghengde
 *
 */
public class SlaveJedisPool extends JedisPool {

    private final HostAndPort hostAndPort;

    public SlaveJedisPool(final GenericObjectPoolConfig poolConfig, final HostAndPort hostAndPort, final int timeout) {
        super(poolConfig, hostAndPort.getHost(), hostAndPort.getPort(), timeout, null, Protocol.DEFAULT_DATABASE, null);
        this.hostAndPort = hostAndPort;

    }

    public HostAndPort getHostAndPort() {
        return hostAndPort;
    }

}
