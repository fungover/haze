package org.fungover.haze;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.fungover.haze.Main.printThreadDebug;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    HazeDatabase database = new HazeDatabase();
    List<String> inputList = new ArrayList<>();

    @Test
    void callingExecuteCommandWithValidNonExistingInputReturnsColonOne() {
        assertThat(Main.executeCommand(database, List.of("SETNX", "1", "This is a value"))).isEqualTo(":1\r\n");
    }

    @Test
    void callingExecuteCommandWithInvalidInputStringReturnsErrorMessage() {
        assertThat(Main.executeCommand(database, List.of(""))).isEqualTo("-ERR unknown command\r\n");
    }

    @Test
    void executeCommandCanHandleCommandsInBothUpperAndLowerCase() {
        Main.executeCommand(database, List.of("sEtNx", "1", "This is a value"));
        assertThat(Main.executeCommand(database, List.of("sEtNx", "1", "This is also a value"))).isEqualTo(":0\r\n");
    }

    @Test
    void callExecuteCommandWithAnEmptyPingShouldReturnPong() {
        assertThat(Main.executeCommand(database, List.of("Ping"))).isEqualTo("+PONG\r\n");
    }

    @Test
    void callExecuteCommandWithPingAndMessageShouldReturnTheMessage() {
        assertThat(Main.executeCommand(database, List.of("Ping", "test message")))
                .isEqualTo("$12\r\ntest message\r\n");
    }
    @Test
    void callExecuteCommandWithDelAndMessageShouldReturnNull (){
        inputList = new ArrayList<>();
        inputList.add("DEL");
        inputList.add("key1");
        var result= Main.executeCommand(database, inputList);
        assertThat(Main.executeCommand(database,inputList)).isEqualTo(":0\r\n");
    }

    @Test
    public void testPrintThreadDebug() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        printThreadDebug();

        assertFalse(outContent.toString().contains("ThreadID"));
        assertFalse(outContent.toString().contains("Is virtual Thread"));
    }
}
