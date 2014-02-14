package com.yy.cs.base.redis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yy.cs.base.thrift.exception.CsRedisRuntimeException;


public class RedisUtils {
	
	/**
	 * 从jedis返回的info信息中获取connected client 数量
	 * </br>
	 * 如果info为空，或者匹配信息不到，则返回 0
	 * @param info
	 * @return 
	 */
	public static int getConnectedClientNum(String info){
		if(info == null || "".equals(info)){
			return 0;
		}
		Pattern p = Pattern.compile("connected_clients:(\\d+)");
		Matcher m = p.matcher(info);
		if(m.find()){
			return Integer.valueOf(m.group(1));
		}
		return 0;
	}

	/**
	 * 从jedis返回的info信息中分析 该redis实例是否是 master
	 * </br>
	 * 如果info为空，或者匹配信息不到，则返回  false
	 * @param info
	 * @return
	 */
	public static boolean isMaster(String info){
		if(info == null || "".equals(info)){
			return false;
		}
		Pattern p = Pattern.compile("role:(\\w+)");
		Matcher m = p.matcher(info);
		if(m.find()){
			String isMaster = (m.group(1));
			return "master".equals(isMaster) ? true : false;
		}
		return false;
	}

	/**
	 * @param serverInfo  格式如  ip:port:password:timeout
	 * @return 
	 */
	public static String[] parseServerInfo(String serverInfo){
		if(serverInfo == null || "".equals(serverInfo)){
			throw new CsRedisRuntimeException("invalid param, param couldn't be blank");
		}
		String [] str = serverInfo.split(":");
		for(int i = 0;i < str.length; i++){
		    if("".equals(str[i])){
		        str[i] = null;
		    }
		}
		if(str.length < 2){
			throw new CsRedisRuntimeException("invalid param, param should like ip:port:password:timeout, if not exists password, you can use ip:port::");
		}
		String [] result = new String[4];
		result[0] = str[0];
		result[1] = str[1];
		if(str.length == 3){
			result[2] = str[2];
		}
		if(str.length == 4){
			result[2] = str[2];
			result[3] = str[3];
		}
		return result;
	}
	
	public static void main(String []args){
		String [] strArray = parseServerInfo("172.19.103.105:6331::1000");
		for(int i = 0; i < strArray.length; i++){
			System.out.println(strArray[2] == null);
			System.out.println(i + " " + strArray[i]);
		}
	}
	
}
