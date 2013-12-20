package com.yy.cs.base.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Json {

	private static ObjectMapper mapper = new ObjectMapper();
	private static final Logger LOG = LoggerFactory.getLogger(Json.class);

	static {
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // 忽略不存在的属性
	}

	/**
	 * 通过JSON字符串生成对象
	 * 
	 * @param jsonStr
	 *            JSON字符串
	 * @param type
	 *            返回值的类型
	 * @exception e
	 *                对象为空时，底层抛出异常时，均会封装成RuntimeException抛出
	 * @return T 指定对象
	 */
	public static <T> T strToObj(String jsonStr, Class<T> type) {
		try {
			return mapper.readValue(jsonStr, type);
		} catch (Exception e) {
			String msg = String.format("Failed to parse json %s", jsonStr);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * 生成对象对应的JSON字符串.
	 * 
	 * @param obj
	 *            对象实例
	 * @exception e
	 *                对象为空时，底层抛出异常时，均会封装成RuntimeException抛出
	 * @return 返回生成的字符串
	 */
	public static String ObjToStr(Object obj) {
		if (obj == null) {
			throw new RuntimeException("Failed to map object, which is null");
		}
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			String msg = String.format("Failed to map object {}", obj);
			throw new RuntimeException(msg, e);
		}
	}
}
