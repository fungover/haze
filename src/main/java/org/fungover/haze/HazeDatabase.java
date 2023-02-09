package org.fungover.haze;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HazeDatabase {

	private Map<String, String> database;
	private Lock lock;

	public HazeDatabase() {
		this.database = new HashMap<>();
		this.lock = new ReentrantLock();
	}


	public String set(String key, String value) {
		lock.lock();
		try {
			//add code for setting value when when key exists
		} finally {
			lock.unlock();
		}
		return "";
	}

	public String get(String key) {
		lock.lock();
		try {
			//return value when key is passed
		} finally {
			lock.unlock();
		}
		return "";
	}

	public String delete(String key) {
		lock.lock();
		try {
			//remove key when it is passed, ignores if there is no key
		} finally {
			lock.unlock();

		}
		return "";
	}

	public String exists(String key) {
		lock.lock();
		try {
			//gets key as parameter and returns an integer representing how many keys exists
		} finally {
			lock.unlock();

		}
		return "";
	}

	public String setNX(String key) {
		lock.lock();
		try {
			//sets value if key does not exists, if there is a key this operation is ignored.
		} finally {
			lock.unlock();

		}
		return "";
	}
}
