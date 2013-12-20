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

		try {
			System.out.println(Json.ObjToStr(null));
			fail("should fail for null object");
		} catch (Exception e) {
			// expect exception here
		}

		String jsonStr = Json.ObjToStr(tObj);

		assertTrue(jsonStr.contains(key));
		assertTrue(jsonStr.contains(value.toString()));
	}

	@Test
	public void testStrToObj() {
		try {
			Json.strToObj(null, TObj.class);
			fail("should fail for null string");
		} catch (Exception e) {
			// expect exception here
		}

		try {
			Json.strToObj(" ", TObj.class);
			fail("should fail for empty string");
		} catch (Exception e) {
			// expect exception here
		}

		// normal parse
		String jsonStr = "{\"key\":\"" + key + "\",\"value\":" + value + "}";
		TObj result = Json.strToObj(jsonStr, TObj.class);
		assertEquals(key, result.getKey());
		assertEquals(value, result.getValue());

		// compatible with extra no exist properties
		String jsonStrWithExtras = "{\"key\":\"" + key + "\",\"value\":"
				+ value + ",\"notExistValue\":" + value + "}";
		result = Json.strToObj(jsonStrWithExtras, TObj.class);
		assertEquals(key, result.getKey());
		assertEquals(value, result.getValue());
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
