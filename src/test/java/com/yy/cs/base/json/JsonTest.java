package com.yy.cs.base.json;

import static org.junit.Assert.*;

import org.junit.Test;

public class JsonTest {
	public static String key = "kkkey";
	public static Long value = -998L;

	@Test
	public void testObjToStr() {
		TObj tObj = new TObj();
		tObj.setKey(key);
		tObj.setValue(value);

		String jsonStr = Json.ObjToStr(tObj);

		assertTrue(jsonStr.contains(key));
		assertTrue(jsonStr.contains(value.toString()));
	}

	@Test
	public void testStrToObj() {
		String jsonStr = "{\"key\":\"" + key + "\",\"value\":" + value + "}";

		TObj result = Json.strToObj(jsonStr, TObj.class);

		assertEquals(key, result.getKey());
		assertEquals(value, result.getValue());
	}

	@Test
	public void testStrToObjPerformance() {
		long start = System.currentTimeMillis();

		long i;
		for (i = 0; i < 1000 * 1000 * 10; i++) {
			String jsonStr = "{\"key\":\"" + key + i + "\",\"value\":" + value
					+ i + "}";
			Json.strToObj(jsonStr, TObj.class);
		}
		long time = System.currentTimeMillis() - start;
		System.out.println("StrToObjPerformance. Time cost (ms): " + time
				+ ", count: " + i + ", tps: " + i * 1000 / time);
	}

	@Test
	public void testObjToStrPerformance() {
		long start = System.currentTimeMillis();
		TObj tObj = new TObj();

		long i;
		for (i = 0; i < 1000 * 1000 * 10; i++) {
			tObj.setKey(key + i);
			tObj.setValue(value + i);
			Json.ObjToStr(tObj);
		}
		long time = System.currentTimeMillis() - start;
		System.out.println("ObjToStrPerformance. Time cost (ms): " + time
				+ ", count: " + i + ", tps: " + i * 1000 / time);
	}

	public static class TObj {
		private String key;
		private Long value;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Long getValue() {
			return value;
		}

		public void setValue(Long value) {
			this.value = value;
		}
	}
}
