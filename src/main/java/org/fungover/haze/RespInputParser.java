package org.fungover.haze;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RespInputParser {
  public static void readInputStream(BufferedReader input, List<String> inputList, String firstReading) throws IOException {
        if (firstReading.startsWith("*")) {

            Log4j2.debug("readInputStream: " + input + " " + inputList + " " + firstReading);
            int size = Integer.parseInt(firstReading.substring(1)) * 2;
            for (int i = 0; i < size; i++) {
                String temp = input.readLine();
                if (!temp.contains("$"))
                    inputList.add(temp);
            }
        } else {
            String[] separated = firstReading.split("\\s");
            inputList.addAll(Arrays.asList(separated));
        }
    }
}
