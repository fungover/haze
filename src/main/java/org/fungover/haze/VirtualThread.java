package org.fungover.haze;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VirtualThread extends Thread {

	private String databaseKey;
	private String databaseValue;
	private Map<String, String> database;
	//TODO Add Command variable


	@Override
	public void run() {
		Lock lock = new ReentrantLock();
		lock.lock();
		try {
			//TODO Switch on command
			this.database.put(databaseKey, databaseValue);
		} finally {
			lock.unlock();
		}


	}


	public void startNewVirtualThread(String databaseKey, String databaseValue, Map<String, String> database) {
		//TODO Add command perimeter
		this.databaseKey = databaseKey;
		this.databaseValue = databaseValue;
		this.database = database;
		startVirtualThread(this::run);

	}

	public Map<String, String> getDatabase() {
		return database;
	}

	public VirtualThread(Map<String, String> database) {
		this.database = database;
	}
}
