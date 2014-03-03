package integration.com.yy.cs.base.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	private static ObjectMapper mapper = new ObjectMapper();
	@Test
	public void testObjToStr() throws JsonParseException, JsonMappingException, IOException { // Intel i5 (linux): ~1.82M TPS
		
		String str = "[{\"target\": \"stats.timers.client-update-server.database.time.refreshing.success.115_238_171_223.mean_90\", \"datapoints\": [[3361.0, 1393491600], [3999.0, 1393491660]]}]";
		List<Bean> lsit = mapper.readValue(str, new TypeReference<List<Bean>>(){});
		System.out.println(lsit);
		
		
	}
	public void testStrToObja(){
		String str = "[{\"target\": \"stats.timers.client-update-server.database.time.refreshing.success.115_238_171_223.mean_90\", \"datapoints\": [[3361.0, 1393491600], [3999.0, 1393491660]]}]";
		List<Bean> lsit = Json.strToObj(str,new TypeReference<List<Bean>>(){});
		System.out.println(lsit);
	}
	
	
}

class Bean{
	
	String target;
	
	List<List<String>> datapoints;

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public List<List<String>> getDatapoints() {
		return datapoints;
	}

	public void setDatapoints(List<List<String>> datapoints) {
		this.datapoints = datapoints;
	}

	@Override
	public String toString() {
		return "Bean [target=" + target + ", datapoints=" + datapoints + "]";
	}
	
	
}
