package integration.com.yy.cs.base.json;

import org.junit.Test;

import com.yy.cs.base.json.Json;
import com.yy.cs.base.json.JsonTest.TObj;

public class JsonTest {
	public static String key = "kkkey";
	public static Long value = -998L;
	public static Long count = 1000 * 1000 * 10L;

	@Test
	public void testStrToObjPerformance() {// Intel i5 (linux): ~0.86M TPS
		long start = System.currentTimeMillis();

		for (long i = 0; i < count; i++) {
			String jsonStr = "{\"key\":\"" + key + i + "\",\"value\":" + value
					+ i + "}";
			Json.strToObj(jsonStr, TObj.class);
		}
		long time = System.currentTimeMillis() - start;
		System.out.println("StrToObjPerformance. Time cost (ms): " + time
				+ ", count: " + count + ", tps: " + count * 1000 / time);
	}

	@Test
	public void testObjToStrPerformance() { // Intel i5 (linux): ~1.82M TPS
		long start = System.currentTimeMillis();
		TObj tObj = new TObj();

		for (long i = 0; i < count; i++) {
			tObj.setKey(key + i);
			tObj.setValue(value + i);
			Json.ObjToStr(tObj);
		}
		long time = System.currentTimeMillis() - start;
		System.out.println("ObjToStrPerformance. Time cost (ms): " + time
				+ ", count: " + count + ", tps: " + count * 1000 / time);
	}
}
