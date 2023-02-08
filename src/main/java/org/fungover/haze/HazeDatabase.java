package org.fungover.haze;

import java.util.HashMap;
import java.util.Map;

public class HazeDatabase {

	private static Map<String, String> database;

	public HazeDatabase() {
		this.database = new HashMap<>();
	}

	private String set() {
		//add code for setting value when when key exists
		return "";
	}

	private String get() {
		//return value when key is passed
		return "";
	}

	private String delete() {
		//remove key when it is passed, ignores if there is no key
		return "";
	}

	private String exists() {
		//gets key as parameter and returns an integer representing how many keys exists
		return "";
	}

	private String setNX() {
		//sets value if key does not exists, if there is a key this operation is ignored.
		return "";
	}
}
