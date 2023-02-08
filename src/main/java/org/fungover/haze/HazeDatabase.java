package org.fungover.haze;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HazeDatabase {

	private static Map<String, String> database;
	private static Lock lock;

	public HazeDatabase() {
		this.database = new HashMap<>();
		this.lock = new ReentrantLock();
	}


	private String set() {
		lock.lock();
		try {
			//add code for setting value when when key exists
		} finally {
			lock.unlock();
		}
		return "";
	}

	private String get() {
		lock.lock();
		try {
			//return value when key is passed
		} finally {
			lock.unlock();
		}
		return "";
	}

	private String delete() {
		lock.lock();
		try {
			//remove key when it is passed, ignores if there is no key
		} finally {
			lock.unlock();

		}
		return "";
	}

	private String exists() {
		lock.lock();
		try {
			//gets key as parameter and returns an integer representing how many keys exists
		} finally {
			lock.unlock();

		}
		return "";
	}

	private String setNX() {
		lock.lock();
		try {
			//sets value if key does not exists, if there is a key this operation is ignored.
		} finally {
			lock.unlock();

		}
		return "";
	}
}
