package org.fungover.haze;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RespInputParserTest {
    RespInputParser respInputParser = new RespInputParser();
    @Test
    void testReadInputStreamBulk() throws IOException {
        String inputString = "*3\r\n$5\r\nHello\r\n$5\r\nWorld\r\n$5\r\nRedis\r\n";
        List<String> expectedOutput = Arrays.asList("Hello", "World", "Redis");

        BufferedReader input = new BufferedReader(new StringReader(inputString));
        List<String> actualOutput = new ArrayList<>();
        respInputParser.readInputStream(input, actualOutput, input.readLine());

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void testReadInputStreamSimple() throws IOException {
        String inputString = "+OK\r\n";
        List<String> expectedOutput = Arrays.asList("+OK");

        BufferedReader input = new BufferedReader(new StringReader(inputString));
        List<String> actualOutput = new ArrayList<>();
        respInputParser.readInputStream(input, actualOutput, input.readLine());

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void testReadInputStreamError() throws IOException {
        String inputString = "-ERR unknown command\r\n";
        List<String> expectedOutput = Arrays.asList("-ERR", "unknown", "command");

        BufferedReader input = new BufferedReader(new StringReader(inputString));
        List<String> actualOutput = new ArrayList<>();
        respInputParser.readInputStream(input, actualOutput, input.readLine());

        assertEquals(expectedOutput, actualOutput);
    }

}
