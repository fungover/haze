package org.fungover.haze;

import java.util.HashMap;
import java.util.Map;

public class RedisServer {

	private Map<String,String> database;

	public RedisServer() {
		database = new HashMap<>();
	}

	public String set(String key, String value) {
		database.put(key,value);

		return "+OK\r\n";
	}

	public String get(String key) {
		if (database.containsKey(key)) {
			return "$" + database.get(key).length() + "\r\n" + database.get(key) + "\r\n";
		} else {
			return "$-1\r\n";
		}
	}

}
