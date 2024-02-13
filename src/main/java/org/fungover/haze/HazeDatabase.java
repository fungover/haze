package org.fungover.haze;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HazeDatabase {
    private static final String ARGUMENT_ERROR = "-ERR wrong number of arguments for command\r\n";
    private static final String NO_SUCH_KEY_ERROR = "-ERR no such key\r\n";
    private final Map<String, String> database;
    private final Lock lock;

    public HazeDatabase() {
        this.database = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public String set(List<String> inputList) {
        if (inputList.size() != 3)
            return ARGUMENT_ERROR;
        String key = inputList.get(1);
        String value = inputList.get(2);
        lock.lock();
        try {
            database.put(key, value);
        } finally {
            lock.unlock();
        }
        return "+OK\r\n";
    }

    public String get(List<String> inputList) {
        if (inputList.size() != 2)
            return ARGUMENT_ERROR;
        lock.lock();
        try {
            if (database.containsKey(inputList.get(1))) {
                var value = database.get(inputList.get(1));
                return "$" + value.length() + "\r\n" + value + "\r\n";
            } else return "$-1\r\n";
        } finally {
            lock.unlock();
        }
    }

    public String delete(List<String> keys) {
        if (keys.isEmpty())
            throw new IllegalArgumentException("No keys provided");

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

    public String getAndDelete(List<String> inputList) {
        if (inputList.size() != 2)
            return ARGUMENT_ERROR;
        lock.lock();
        String key = inputList.get(1);
        var value = "";
        try {
            if (database.containsKey(key)) {
                value = database.get(inputList.get(1));
                database.remove(key);
                return value;
            } else return NO_SUCH_KEY_ERROR;
        } finally {
            lock.unlock();
        }
    }

    public String exists(List<String> keys) {
        if (keys.isEmpty())
            return ":0\r\n";

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
            return ARGUMENT_ERROR;
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
        if (messageList == null || messageList.isEmpty()) {
            throw new IllegalArgumentException("No message provided");
        } else if (messageList.size() > 2) {
            throw new IllegalArgumentException("Too many arguments for PING command");
        }

        if (messageList.size() == 1)
            return "+PONG\r\n";
        else return "$" + (messageList.get(1)).length() + "\r\n" + messageList.get(1) + "\r\n";
    }

    public String getValue(String key) {
        lock.lock();
        try {
            return database.get(key);
        }
        finally {
            lock.unlock();
        }
    }

    public boolean containsKey(String key) {
        lock.lock();
        try {
            return database.containsKey(key);
        }
        finally {
            lock.unlock();
        }
    }

    public void addValue(String key, String value) {
        lock.lock();
        try {
            database.put(key, value);
        }
        finally {
            lock.unlock();
        }
    }

    public String increaseValue(List<String> inputList) {
        lock.lock();
        String key = inputList.get(1);
        try {
            if (!database.containsKey(key)) {
                return NO_SUCH_KEY_ERROR;
            }
            String value = database.get(key);
            try {
                long longValue = Long.parseLong(value);
                longValue++;
                database.put(key, String.valueOf(longValue));
                return ":" + longValue + "\r\n";
            } catch (NumberFormatException e) {
                return "-WRONGTYPE value is not an integer or out of range\r\n";
            }
        } finally {
            lock.unlock();
        }
    }

    public String decreaseValue(List<String> inputList) {
        lock.lock();
        String key = inputList.get(1);
        try {
            if (!database.containsKey(key)) {
                return NO_SUCH_KEY_ERROR;
            }
            String value = database.get(key);
            try {
                long longValue = Long.parseLong(value);
                longValue--;
                database.put(key, String.valueOf(longValue));
                return ":" + longValue + "\r\n";
            } catch (NumberFormatException e) {
                return "-WRONGTYPE value is not an integer or out of range\r\n";
            }
        } finally {
            lock.unlock();
        }
    }

}
