package com.yy.cs.base.redis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yy.cs.base.nyy.exception.NyyRuntimeException;

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
		Pattern p = Pattern.compile("connected_slaves:(\\d+)");
		Matcher m = p.matcher(info);
		if(m.find()){
			int num = Integer.valueOf(m.group(1));
			return num != 0? true : false;
		}
		return false;
	}

	/**
	 * @param serverInfo  格式如  ip:port:password:timeout
	 * @return 
	 */
	public static String[] parseServerInfo(String serverInfo){
		if(serverInfo == null || "".equals(serverInfo)){
			throw new NyyRuntimeException("invalid param, param couldn't be blank");
		}
		String [] str = serverInfo.split(":");
		if(str.length < 2){
			throw new NyyRuntimeException("invalid param, param should like ip:port:password:timeout, if not exists password, you can use ip:port::");
		}
		return str;
	}
	
	public static void main(String []args){
		String [] strArray = parseServerInfo("172.19.103.105:6331::");
		for(int i = 0; i < strArray.length; i++){
			System.out.println(i + " " + strArray[i]);
		}
	}
	
}
