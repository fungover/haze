package org.fungover.haze;

import java.util.*;
import java.util.concurrent.locks.*;

public class HazeList {

    static final String NIL_RESPONSE = "$5\r\n(nil)\r\n";
    static final String LEFT = "LEFT";
    static final String RIGHT = "RIGHT";
    final Map<String, List<String>> database;
    final ReentrantLock lock;

    public HazeList() {
        this.database = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public String lPush(String key, String... values) {
        lock.lock();
        try {
            List<String> list = database.computeIfAbsent(key, k -> new ArrayList<>());
            List<String> tempList = Arrays.asList(values);
            Collections.reverse(tempList);
            list.addAll(0, tempList);
            return ":" + list.size() + "\r\n";
        }
        finally {
            lock.unlock();
        }
    }

    public String rPush(String key, String... values) {
        lock.lock();
        try {
            List<String> list = database.computeIfAbsent(key, k -> new ArrayList<>());
            list.addAll(Arrays.asList(values));
            return ":" + list.size() + "\r\n";
        }
        finally {
            lock.unlock();
        }
    }

    //OVERLOAD
    public String lPop(String key) {
        lock.lock();
        try {
            if (!database.containsKey(key) || database.get(key).isEmpty())
                return NIL_RESPONSE;
            int lengthOfValue = database.get(key).get(0).length();
            return "$" + lengthOfValue + "\r\n" +database.get(key).remove(0) + "\r\n";
        }
        finally {
            lock.unlock();
        }
    }

    //OVERLOAD
    @java.lang.SuppressWarnings("squid:S5413")
    public String lPop(String key, int count) {
        lock.lock();
        try {
            if (!database.containsKey(key) || database.get(key).isEmpty())
                return NIL_RESPONSE;

            int actualCount = getActualCount(key, count);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("*").append(actualCount).append("\r\n");

            for (int i = 0; i < actualCount; i++) {
                String value = database.get(key).remove(0);
                stringBuilder.append("$").append(value.length()).append("\r\n").append(value).append("\r\n");
            }
            return stringBuilder.toString();
        }
        finally {
            lock.unlock();
        }
    }

    private int getActualCount(String key, int count) {
        List<String> values = database.get(key);
        values.removeIf(String::isEmpty);
        return Math.min(count, values.size());
    }

    //OVERLOAD
    public String rPop(String key) {
        lock.lock();
        try {
            if (!database.containsKey(key) || database.get(key).isEmpty())
                return NIL_RESPONSE;
            int lengthOfValue = database.get(key).get(0).length();
            int lastIndex = database.get(key).size()-1;
            return "$" + lengthOfValue + "\r\n" + database.get(key).remove(lastIndex) + "\r\n";
        }
        finally {
            lock.unlock();
        }
    }

    //OVERLOAD
    @java.lang.SuppressWarnings("squid:S5413")
    public String rPop(String key, int count) {
        lock.lock();
        try {
            if (!database.containsKey(key) || database.get(key).isEmpty())
                return NIL_RESPONSE;

            int actualCount = getActualCount(key, count);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("*").append(actualCount).append("\r\n");

            for (int i = 0; i < actualCount; i++) {
                int lastIndex = database.get(key).size()-1;
                String value = database.get(key).remove(lastIndex);
                stringBuilder.append("$").append(value.length()).append("\r\n").append(value).append("\r\n");
            }
            return stringBuilder.toString();
        }
        finally {
            lock.unlock();
        }
    }


    public String lLen(String key) {
        lock.lock();
        try {
            if (database.get(key) == null)
                return ":0\r\n";
            return ":" + database.get(key).size() + "\r\n";
        }
        finally {
            lock.unlock();
        }
    }


    public String lMove(String source, String destination, String whereFrom, String whereTo) {
        System.out.println(source);
        System.out.println(destination);
        System.out.println(whereFrom);
        System.out.println(whereTo);

        lock.lock();
        try {
            if (database.get(source) == null || database.get(destination) == null)
                return "-One or both keys is missing.\r\n";
            else if (database.get(source).isEmpty())
                return "-The source list is empty.\r\n";

            String value;

            if (whereFrom.equals(LEFT) && whereTo.equals(LEFT)) {
                value = database.get(source).remove(0);
                database.get(destination).add(0, value);
            }
            else if (whereFrom.equals(LEFT) && whereTo.equals(RIGHT)) {
                value = database.get(source).remove(0);
                database.get(destination).add(value);
            }
            else if (whereFrom.equals(RIGHT) && whereTo.equals(LEFT)) {
                value = database.get(source).remove(database.get(source).size() - 1);
                database.get(destination).add(0, value);
            }
            else if (whereFrom.equals(RIGHT) && whereTo.equals(RIGHT)) {
                value = database.get(source).remove(database.get(source).size() - 1);
                database.get(destination).add(value);
            }
            else
                return "-Invalid input for FROM and WHERE.\r\n";

            return "$" + value.length() + "\r\n" + value + "\r\n";
        }
        finally {
            lock.unlock();
        }
    }


    public String lTrim(String key, int start, int stop) {
        lock.lock();
        try {
            if (!database.containsKey(key))
                return("-The key is not present in the database.\r\n");

            try {
                database.put(key, new ArrayList<>(database.get(key).subList(start, stop+1)));
                return "+OK\r\n";
            }
            catch (IndexOutOfBoundsException e) {
                return "-The inputs are outside the range of the list.\r\n";
            }
        }
        finally {
            lock.unlock();
        }
    }


    @Override
    public String toString() {

        return "HazeList{" +
                "database=" + database +
                '}';
    }

    public String callLPop(String key, String... count) {
        if (count.length > 0)
            return lPop(key, HazeList.parser(count[0]));
        else
            return lPop(key);
    }

    public String callRpop(String key, String... count) {
        if (count.length > 0)
            return rPop(key, HazeList.parser(count[0]));
        else
            return rPop(key);
    }

    public String callLtrim(String key, String... args) {

        if (args.length != 2)
            return "-Wrong number of arguments for LTRIM\r\n";

        int start, stop;
        try {
            start = Integer.parseInt(args[0]);
            stop = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e) {
            return "-Value is not an integer or out of range\r\n";
        }
        return lTrim(key, start, stop);
    }

    public static int parser(String inputString) {
        //Do not call this when zero messes up your algorithm with a bad parse.
        try {
            return Integer.parseInt(inputString);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }
}
