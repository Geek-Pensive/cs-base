package com.yy.cs.base.redis;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * 工具类
 *
 */
public class RedisUtils {

    /**
     * 从jedis返回的info信息中获取connected client 数量
     * </br>
     * 如果info为空，或者匹配信息不到，则返回 0
     * 
     * @param info
     *            redis服务器的相关配置信息
     * @return
     *         connected client 数量
     */
    public static int getConnectedClientNum(String info) {
        if (info == null || "".equals(info)) {
            return 0;
        }
        Pattern p = Pattern.compile("connected_clients:(\\d+)");
        Matcher m = p.matcher(info);
        if (m.find()) {
            return Integer.valueOf(m.group(1));
        }
        return 0;
    }

    /**
     * 从jedis返回的info信息中分析 该redis实例是否是 master
     * </br>
     * 如果info为空，或者匹配信息不到，则返回 false
     * 
     * @param info
     *            redis服务器的相关配置信息
     * @return
     *         boolean
     */
    public static boolean isMaster(String info) {
        if (info == null || "".equals(info)) {
            return false;
        }
        Pattern p = Pattern.compile("role:(\\w+)");
        Matcher m = p.matcher(info);
        if (m.find()) {
            String isMaster = (m.group(1));
            return "master".equals(isMaster) ? true : false;
        }
        return false;
    }

    /**
     * @param serverInfo
     *            格式如 ip:port:password:timeout
     * @return
     *         String数组，分别为ip,port，password,timeout
     */
    public static String[] parseServerInfo(String serverInfo) {
        if (serverInfo == null || "".equals(serverInfo)) {
            throw new CsRedisRuntimeException("invalid param, param couldn't be blank");
        }
        String[] str = serverInfo.split(":");
        for (int i = 0; i < str.length; i++) {
            if ("".equals(str[i])) {
                str[i] = null;
            }
        }
        if (str.length < 2) {
            throw new CsRedisRuntimeException(
                    "invalid param, param should like ip:port:password:timeout, if not exists password, you can use ip:port::");
        }
        String[] result = new String[4];
        result[0] = str[0];
        result[1] = str[1];
        if (str.length == 3) {
            result[2] = str[2];
        }
        if (str.length == 4) {
            result[2] = str[2];
            result[3] = str[3];
        }
        return result;
    }

    /**
     * 判断jedis依赖的commons-pool版本。
     * jedis2.4.2后，使用的是commons-pool2通过特征字段获取commons-pool的版本，默认是返回1。commons-pool2返回2
     *
     * @return
     */
    public static int versionOfCommonsPool() {
        int version = 1;
        Class<?> jp = JedisPoolConfig.class;
        try {
            jp.getMethod("getMaxActive");
        } catch (NoSuchMethodException e) {
            version = 2;
        }
        return version;
    }

    public static void main(String[] args) {
        System.out.println(versionOfCommonsPool());
    }

}
