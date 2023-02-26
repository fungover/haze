package org.fungover.haze;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HazeList {

    static final String NIL_RESPONSE = "$5\r\n(nil)\r\n";
    static final String EMPTY_ARRAY_RESPONSE = "*0\r\n";
    static final String LEFT = "LEFT";
    static final String RIGHT = "RIGHT";
    HazeDatabase hazeDatabase;

    public HazeList(HazeDatabase hazeDatabase) {
        this.hazeDatabase = hazeDatabase;
    }

    public String lPush(List<String> inputList) {
        String key = getKey(inputList);

        List<String> newList = inputList.stream()
                .skip(2)
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.reverse(newList);

        String oldListAsString = hazeDatabase.containsKey(key) ? hazeDatabase.getValue(key) : "";
        String newListAsString = listValueAsString(newList);

        if (!oldListAsString.isEmpty())
            newListAsString += "\r\n";

        hazeDatabase.addValue(key, newListAsString + oldListAsString);

        int currentSize = getValueAsList(hazeDatabase.getValue(key)).size();

        return ":" + currentSize + "\r\n";
    }

    public String rPush(List<String> inputList) {
        String key = getKey(inputList);

        String currentValuesAsString = hazeDatabase.getValue(key);
        List<String> currentValues = currentValuesAsString != null ? getValueAsList(currentValuesAsString) : new ArrayList<>();

        List<String> newInputs = inputList.stream()
                .skip(2)
                .collect(Collectors.toCollection(ArrayList::new));

        currentValues.addAll(newInputs);

        String newListAsString = listValueAsString(currentValues);
        hazeDatabase.addValue(key, newListAsString);

        return ":" + currentValues.size() + "\r\n";
    }

    //OVERLOAD
    public String lPop(String key) {

        if (!hazeDatabase.containsKey(key))
            return NIL_RESPONSE;

        List<String> list = getValueAsList(hazeDatabase.getValue(key));

        String firstElement = list.remove(0);
        String newValue = listValueAsString(list);
        hazeDatabase.addValue(key, newValue);

        return "$" + firstElement.length() + "\r\n" + firstElement + "\r\n";
    }

    //OVERLOAD
    @SuppressWarnings("squid:S5413")
    public String lPop(String key, int count) {

        if (!hazeDatabase.containsKey(key))
            return NIL_RESPONSE;

        List<String> list = getValueAsList(hazeDatabase.getValue(key));

        int actualCount = Math.min(count, list.size());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("*").append(actualCount).append("\r\n");

        for (int i = 0; i < actualCount; i++) {
            String element = list.remove(0);
            stringBuilder.append("$").append(element.length()).append("\r\n").append(element).append("\r\n");
        }

        hazeDatabase.addValue(key, listValueAsString(list));
        return stringBuilder.toString();
    }

    //OVERLOAD
    public String rPop(String key) {

        if (!hazeDatabase.containsKey(key))
            return NIL_RESPONSE;

        List<String> list = getValueAsList(hazeDatabase.getValue(key));

        int lastIndex = list.size() - 1;
        String lastElement = list.remove(lastIndex);
        String newValue = listValueAsString(list);
        hazeDatabase.addValue(key, newValue);

        return "$" + lastElement.length() + "\r\n" + lastElement + "\r\n";
    }

    //OVERLOAD
    @SuppressWarnings("squid:S5413")
    public String rPop(String key, int count) {

        if (!hazeDatabase.containsKey(key))
            return NIL_RESPONSE;

        List<String> list = getValueAsList(hazeDatabase.getValue(key));

        int actualCount = Math.min(count, list.size());
        if (actualCount == 0)
            return EMPTY_ARRAY_RESPONSE;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("*").append(actualCount).append("\r\n");

        for (int i = 0; i < actualCount; i++) {
            String lastElement = list.remove(list.size()-1);
            stringBuilder.append("$").append(lastElement.length()).append("\r\n").append(lastElement).append("\r\n");
        }

        hazeDatabase.addValue(key, listValueAsString(list));
        return stringBuilder.toString();
    }

    public String lLen(List<String> inputList) {
        String key = getKey(inputList);
        String value = hazeDatabase.getValue(key);
        if (value == null || value.length()==0)
            return ":0\r\n";
        else {
            List<String> list = getValueAsList(value);
            return ":" + list.size() + "\r\n";
        }
    }

    public String lMove(List<String> inputList) {

        String source = getKey(inputList);
        if (inputList.size() != 5)
            return "-ERR wrong number of arguments for command.\r\n";

        List<String> position = inputList.subList(2, inputList.size());
        String destination = position.get(0);
        String whereFrom = position.get(1).toUpperCase();
        String whereTo = position.get(2).toUpperCase();
        String sourceValue = hazeDatabase.getValue(source);
        String destinationValue = hazeDatabase.getValue(destination);

        if (sourceValue == null || destinationValue == null)
            return "-One or both keys is missing.\r\n";
        else if (sourceValue.isEmpty())
            return "-The source list is empty.\r\n";

        List<String> sourceList = getValueAsList(sourceValue);
        List<String> destinationList = getValueAsList(destinationValue);
        String value;

        if (whereFrom.equals(LEFT) && whereTo.equals(LEFT)) {
            value = sourceList.remove(0);
            destinationList.add(0, value);
        }
        else if (whereFrom.equals(LEFT) && whereTo.equals(RIGHT)) {
            value = sourceList.remove(0);
            destinationList.add(value);
        }
        else if (whereFrom.equals(RIGHT) && whereTo.equals(LEFT)) {
            value = sourceList.remove(sourceList.size() - 1);
            destinationList.add(0, value);
        }
        else if (whereFrom.equals(RIGHT) && whereTo.equals(RIGHT)) {
            value = sourceList.remove(sourceList.size() - 1);
            destinationList.add(value);
        }
        else
            return "-Invalid input for FROM and WHERE.\r\n";

        hazeDatabase.addValue(source, listValueAsString(sourceList));
        hazeDatabase.addValue(destination, listValueAsString(destinationList));

        return "$" + value.length() + "\r\n" + value + "\r\n";
    }

    public String lTrim(String key, int start, int stop) {
        if (!hazeDatabase.containsKey(key)) {
            return "-The key is not present in the database.\r\n";
        }
        try {
            List<String> list = getValueAsList(hazeDatabase.getValue(key));
            List<String> subList = new ArrayList<>(list.subList(start, stop + 1));
            String newCsv = listValueAsString(subList);
            hazeDatabase.addValue(key, newCsv);
            return "+OK\r\n";
        }
        catch (IndexOutOfBoundsException e) {
            return "-The inputs are outside the range of the list.\r\n";
        }
    }

    public String callLPop(List<String> inputList) {
        String key = getKey(inputList);
        List<String> count = inputList.subList(2, inputList.size());

        if (!count.isEmpty())
            return lPop(key, HazeList.parser(inputList.get(2)));
        else
            return lPop(key);
    }

    public String callRPop(List<String> inputList) {
        String key = getKey(inputList);
        List<String> count = inputList.subList(2, inputList.size());

        if (!count.isEmpty())
            return rPop(key, HazeList.parser(inputList.get(2)));
        else
            return rPop(key);
    }

    public String callLtrim(List<String> inputList) {
        String key = getKey(inputList);

        if (inputList.size() != 4)
            return "-Wrong number of arguments for LTRIM\r\n";

        int start;
        int stop;
        try {
            start = Integer.parseInt(inputList.get(2));
            stop = Integer.parseInt(inputList.get(3));
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
    @SuppressWarnings("squid:S6204")
    public static List<String> getValueAsList(String textToSplit) {
        return Stream.of(textToSplit.split("\r\n", -1))
                .collect(Collectors.toList());
    }

    public static String listValueAsString(List<String> list) {
        return String.join("\r\n", list);
    }

    private static String getKey(List<String> inputList) {
        String key = null;
        if (inputList.size() > 1)
            key = inputList.get(1);
        return key;
    }
}
