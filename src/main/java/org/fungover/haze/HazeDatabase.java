package org.fungover.haze;

import java.util.HashMap;
import java.util.List;
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
            database.put(key, value);

        } finally {
            lock.unlock();
        }
        return "+OK\r\n";
    }

    public String get(String key) {
        lock.lock();
        try {
            //return value when key is passed
            if (database.containsKey(key)) {
                return getString(key);
            }
        } finally {
            lock.unlock();
        }
        return "$-1\r\n";
    }
    public String getString(String key) {
        return "$" + database.get(key).length() + "\r\n" + database.get(key) + "\r\n";
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

	public String setNX(List<String> inputList) {
        if (inputList.size() != 3)
            return "-ERR wrong number of arguments for command\r\n";
        String key = inputList.get(1);
        String value = inputList.get(2);

        String replyWhenKeyNotSet = ":0\r\n";
		    String replyWhenKeySet = ":1\r\n";
		    lock.lock();
		    try {
			      if(database.containsKey(key))
				        return replyWhenKeyNotSet;
			      else {
				        database.put(key, value);
				        return replyWhenKeySet;
			      }
		    } finally {
			      lock.unlock();
		    }
	  }
}
