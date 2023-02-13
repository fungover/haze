package org.fungover.haze;

import java.util.*;

public class HazeList {

    final Map<String, List<String>> database;

    public HazeList() {
        this.database = new HashMap<>();
    }

    public String LPUSH(String key, String... values) {
        List<String> list = database.computeIfAbsent(key, k -> new ArrayList<>());
        List<String> tempList = Arrays.asList(values);
        Collections.reverse(tempList);
        list.addAll(0, tempList);
        return ":" + list.size() + "\r\n";
    }

    public String RPUSH(String key, String... values) {
        List<String> list = database.computeIfAbsent(key, k -> new ArrayList<>());
        list.addAll(Arrays.asList(values));
        return ":" + list.size() + "\r\n";
    }

    //OVERLOAD
    public String LPOP(String key) {
        if (!database.containsKey(key) || database.get(key).isEmpty())
            return "$5\r\n(nil)\r\n";
        int lengthOfValue = database.get(key).get(0).length();
        return "$" + lengthOfValue + "\r\n" +database.get(key).remove(0) + "\r\n";
    }

    //OVERLOAD
    public String LPOP(String key, int count) {
        if (!database.containsKey(key) || database.get(key).isEmpty())
            return "$5\r\n(nil)\r\n";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("*").append(count).append("\r\n");

        for (int i = 0; i < count; i++) {
            if (database.get(key).isEmpty())
                return stringBuilder.toString();

            String value = database.get(key).remove(0);
            stringBuilder.append("$").append(value.length()).append("\r\n").append(value).append("\r\n");
        }
        return stringBuilder.toString();
    }

    //OVERLOAD
    public String RPOP(String key) {
        if (!database.containsKey(key) || database.get(key).isEmpty())
            return "$5\r\n(nil)\r\n";
        int lengthOfValue = database.get(key).get(0).length();
        int lastIndex = database.get(key).size()-1;
        return "$" + lengthOfValue + "\r\n" + database.get(key).remove(lastIndex) + "\r\n";
    }

    //OVERLOAD
    public String RPOP(String key, int count) {
        if (!database.containsKey(key) || database.get(key).isEmpty())
            return "$5\r\n(nil)\r\n";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("*").append(count).append("\r\n");

        for (int i = 0; i < count; i++) {
            if (database.get(key).isEmpty())
                return stringBuilder.toString();

            int lastIndex = database.get(key).size()-1;
            String value = database.get(key).remove(lastIndex);
            stringBuilder.append("$").append(value.length()).append("\r\n").append(value).append("\r\n");
        }
        return stringBuilder.toString();
    }

    public String LLEN(String key) {
        if (database.get(key) == null)
            return ":0\r\n";
        return ":" + database.get(key).size() + "\r\n";
    }


    public String LMOVE(String source, String destination, String whereFrom, String whereTo) {

        if (database.get(source) == null || database.get(destination) == null)
            return "-One or both keys do not contain lists.\r\n";
        else if (database.get(source).isEmpty())
            return "-The source list is empty.\r\n";

        String value;

        if (whereFrom.equals("LEFT") && whereTo.equals("LEFT")) {
            value = database.get(source).remove(0);
            database.get(destination).add(0, value);
        }
        else if (whereFrom.equals("LEFT") && whereTo.equals("RIGHT")) {
            value = database.get(source).remove(0);
            database.get(destination).add(value);
        }
        else if (whereFrom.equals("RIGHT") && whereTo.equals("LEFT")) {
            value = database.get(source).remove(database.get(source).size() - 1);
            database.get(destination).add(0, value);
        }
        else if (whereFrom.equals("RIGHT") && whereTo.equals("RIGHT")) {
            value = database.get(source).remove(database.get(source).size() - 1);
            database.get(destination).add(value);
        }
        else
            return "-Invalid input for FROM and WHERE.\r\n\"";

        return "$" + value.length() + "\r\n" + value + "\r\n";
    }


    public String LTRIM(String key, int start, int stop) {
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

    @Override
    public String toString() {
        return "HazeList{" +
                "database=" + database +
                '}';
    }
}
