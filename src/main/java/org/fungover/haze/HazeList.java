package org.fungover.haze;

import java.util.*;

public class HazeList {
    @Override
    public String toString() {
        return "HazeList{" +
                "database=" + database +
                '}';
    }

    private final Map<String, List<String>> database;

    public HazeList() {
        this.database = new HashMap<>();
    }


    public String LPUSH(String key, String... values) {
        List<String> list = database.computeIfAbsent(key, k -> new ArrayList<>());
        List<String> tempList = Arrays.asList(values);
        Collections.reverse(tempList);
        list.addAll(0, tempList);
        return list.size() + "\n";
    }

    public String RPUSH(String key, String... values) {
        List<String> list = database.computeIfAbsent(key, k -> new ArrayList<>());
        list.addAll(Arrays.asList(values));
        return list.size() + "\n";
    }

    public String LPOP(String key, int count) {
        if (count <= 0)
            return "";
        if (!database.containsKey(key) || database.get(key).isEmpty())
            return "(nil)\n";
        String result = database.get(key).remove(0) + "\n";
        return result + LPOP(key, count -1);
    }

    public String RPOP(String key, int count) {
        if (count <= 0)
            return "";
        if (!database.containsKey(key) || database.get(key).isEmpty()) {
            database.remove(key);
            return "(nil)\n";
        }
        String result = database.get(key).remove(database.get(key).size()-1) + "\n";
        return result + RPOP(key, count -1);
    }

    public String LLEN(String key) {
        if (database.get(key) == null)
            return "0\n";
        return database.get(key).size() + "\n";
    }

    public String LMOVE(String source, String destination, String whereFrom, String whereTo) {

        if (database.get(source) == null || database.get(destination) == null)
            return "One or both keys do not contain lists.\n";
        else if (database.get(source).isEmpty())
            return "The source list is empty.\n";

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
            return "Invalid input for FROM and WHERE.\n";

        return value+"\n";
    }


    public String LTRIM(String key, int start, int stop) {
        if (!database.containsKey(key))
            return("The key is not present in the database.\n");

        try {
            database.put(key, new ArrayList<>(database.get(key).subList(start, stop+1)));
            return "OK\n";
        }
        catch (IndexOutOfBoundsException e) {
            return "The inputs are outside the range of the list.\n";
        }
    }
}




