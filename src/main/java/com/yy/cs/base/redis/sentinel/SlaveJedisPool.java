package com.yy.cs.base.redis.sentinel;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * 方便获取从库的host port
 * @author tanghengde
 *
 */
public class SlaveJedisPool extends JedisPool implements Comparable<SlaveJedisPool> {

    private final HostAndPort hostAndPort;

    /**
     * 优先级，数字越大优先级越高，默认为0，相同机房的优先级为1
     */
    private int priority = 0;

    public SlaveJedisPool(final JedisPoolConfig poolConfig, final HostAndPort hostAndPort, final int timeout) {
        super(poolConfig, hostAndPort.getHost(), hostAndPort.getPort(), timeout, null, Protocol.DEFAULT_DATABASE, null);
        this.hostAndPort = hostAndPort;

    }

    public HostAndPort getHostAndPort() {
        return hostAndPort;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(SlaveJedisPool o) {
        return o.getPriority() - this.getPriority();
    }
}
