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
	 *            s * JSON字符串
	 * @param type
	 *            返回值的类型
	 * @return 如果能够封装为指定对象，则返回该值，否则返回null
	 */
	public static <T> T strToObj(String jsonStr, Class<T> type) {
		if (jsonStr == null || jsonStr.isEmpty()) {
			return null;
		}

		try {
			return mapper.readValue(jsonStr, type);
		} catch (Exception e) {
			LOG.warn("Failed to parse json {}", jsonStr, e);
		}
		return null;
	}

	/**
	 * 生成JSON字符串.
	 * 
	 * @param obj
	 *            对象实例
	 * @return 返回生成的字符串
	 */
	public static String ObjToStr(Object obj) {
		if (obj == null) {
			return null;
		}

		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			LOG.warn("Failed to map object {}", obj, e);
		}
		return null;
	}
}
