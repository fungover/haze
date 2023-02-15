package org.fungover.haze;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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
            if (database.containsKey(key)) {
                var value = database.get(key);
                return "$" + value.length() + "\r\n" + value + "\r\n";
            } else return "$-1\r\n";
        } finally {
            lock.unlock();
        }
    }

    public String delete(List<String> keys) {
        var counter = new AtomicInteger(0);
        lock.lock();
        try {
            keys.forEach(key -> {
                if (database.containsKey(key)) {
                    database.remove(key);
                    counter.getAndIncrement();
                }
            });
            return ":" + counter + "\r\n";
        } finally {
            lock.unlock();
        }
    }

    public String exists(List<String> keys) {
        lock.lock();
        int numberOfKeys = 0;
        try {
            for (String i : key) {
                if (database.containsKey(i)) {
                    numberOfKeys++;
                }
            }
        } finally {
            lock.unlock();

        }
        return ":" + numberOfKeys + "\r\n";
    }

    public String setNX(String key, String value) {
        String replyWhenKeyNotSet = ":0\r\n";
        String replyWhenKeySet = ":1\r\n";
        lock.lock();
        try {
            if (database.containsKey(key))
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
