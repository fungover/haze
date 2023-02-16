package org.fungover.haze;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HazeDatabase {

    private final Map<String, String> database;
    private final Lock lock;

    public HazeDatabase() {
        this.database = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public String set(String key, String value) {
        lock.lock();
        try {
            //add code for setting value when key exists
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
            for (String i : keys) {
                if (database.containsKey(i)) {
                    numberOfKeys++;
                }
            }
        } finally {
            lock.unlock();

        }
        return ":" + numberOfKeys + "\r\n";
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

    public Map<String, String> copy() {
        Map<String, String> shallowCopy;
        lock.lock();
        try {
            shallowCopy = Map.copyOf(database);
        } finally {
            lock.unlock();
        }
        return shallowCopy;
    }
    
    public String ping(List<String> messageList) {

        if (messageList.size() == 1)
            return "+PONG\r\n";
        else return "$" + (messageList.get(1)).length() + "\r\n" + messageList.get(1) + "\r\n";
    }
}
