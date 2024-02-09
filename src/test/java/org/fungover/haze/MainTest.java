package org.fungover.haze;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fungover.haze.Main.printThreadDebug;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


class MainTest {
    HazeDatabase database = new HazeDatabase();
    HazeList hazeList = new HazeList(database);


    @Test
    void callingExecuteCommandWithValidNonExistingInputReturnsColonOne() {
        assertThat(Main.executeCommand(database, List.of("SETNX", "1", "This is a value"), hazeList)).isEqualTo(":1\r\n");
    }

    @Test
    void callingExecuteCommandWithInvalidInputStringReturnsErrorMessage() {
        assertThat(Main.executeCommand(database, List.of(""), hazeList)).isEqualTo("-ERR no command provided\r\n");

    }

    @Test
    void executeCommandCanHandleCommandsInBothUpperAndLowerCase() {
        Main.executeCommand(database, List.of("sEtNx", "1", "This is a value"), hazeList);
        assertThat(Main.executeCommand(database, List.of("sEtNx", "1", "This is also a value"), hazeList)).isEqualTo(":0\r\n");
    }

    @Test
    void callExecuteCommandWithAnEmptyPingShouldReturnPong() {
        assertThat(Main.executeCommand(database, List.of("Ping"), hazeList)).isEqualTo("+PONG\r\n");
    }

    @Test
    void callExecuteCommandWithPingAndMessageShouldReturnTheMessageAsBulkString() {
        assertThat(Main.executeCommand(database, List.of("Ping", "test message"), hazeList)).isEqualTo("$12\r\ntest message\r\n");
    }

    @Test
    void callExecuteCommandWithGetAndKeyShouldReturnTheValueAsBulkString() {
        Main.executeCommand(database, List.of("SET", "theKey", "theValue"), hazeList);
        assertThat(Main.executeCommand(database, List.of("GET", "theKey"), hazeList)).isEqualTo("$8\r\ntheValue\r\n");
    }

    @Test
    void callExecuteCommandWithDelAndExistingKeyShouldReturnOne() {
        Main.executeCommand(database, List.of("SET", "theKey", "theValue"), hazeList);
        assertThat(Main.executeCommand(database, List.of("Del", "theKey"), hazeList)).isEqualTo(":1\r\n");
    }

    @Test
    void callExecuteCommandWithExistsAndOneExistingKeyShouldReturnOne() {
        Main.executeCommand(database, List.of("SET", "theKey", "theValue"), hazeList);
        assertThat(Main.executeCommand(database, List.of("Exists", "theKey", "secondKey"), hazeList)).isEqualTo(":1\r\n");
    }

    @Test
    void callExecuteCommandWithSaveShouldReturnOK() {
        Main.executeCommand(database, List.of("SAVE"), hazeList);
        assertThat(SaveFile.writeOnFile(database.copy())).isEqualTo("+OK\r\n");
    }

    @ParameterizedTest
    @ValueSource(strings = {"SET", "SETNX"})
    void callingExecuteCommandWithWrongNumberOfArgumentsResultsInErrorMessage(String command) {
        assertThat(Main.executeCommand(database, List.of(command, "key"), hazeList))
                .isEqualTo("-ERR wrong number of arguments for command\r\n");
    }

    @ParameterizedTest
    @ValueSource(strings = {"LPUSH", "RPUSH"})
    void callingExecuteCommandWithListOfSizeOneReturnsOne(String command) {
        assertThat(Main.executeCommand(database, List.of(command, "key", "1"), hazeList))
                .isEqualTo(":1\r\n");
    }

    @ParameterizedTest
    @CsvSource({"LPUSH, LPOP", "RPUSH, RPOP"})
    void callingExecuteCommandWithListOfSizeOneReturnsBulkString(String firstCommand, String secondCommand) {
        Main.executeCommand(database, List.of(firstCommand, "key", "hello"), hazeList);
        assertThat(Main.executeCommand(database, List.of(secondCommand, "key"), hazeList))
                .isEqualTo("$5\r\nhello\r\n");
    }

    @Test
    void callExecuteCommandWithLLENShouldReturnThree() {
        Main.executeCommand(database, List.of("LPUSH", "key", "1", "2", "3"), hazeList);
        assertThat(Main.executeCommand(database, List.of("LLEN", "key"), hazeList)).isEqualTo(":3\r\n");
    }

    @Test
    void callExecuteCommandWithLMOVEShouldReturnErrorMessageWhenListIsEmpty() {
        assertThat(Main.executeCommand(database, List.of("LMOVE", "key"), hazeList)).isEqualTo("-ERR wrong number of arguments for command.\r\n");
    }

    @Test
    void callExecuteCommandWithLTRIMShouldReturnErrorMessageWhenKeyDoesNotExist() {
        assertThat(Main.executeCommand(database, List.of("LTRIM", "key", "2", "3"), hazeList)).isEqualTo("-The key is not present in the database.\r\n");
    }


    @Test
    void testExecuteCommandEmptyFirstCommand() {


        List<String> inputList = List.of("", "command2", "command3");


        String result = Main.executeCommand(database, inputList, hazeList);


        Assertions.assertEquals("-ERR no command provided\r\n", result);
    }

    @Test
    void testPrintThreadDebug() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        printThreadDebug();

        assertFalse(outContent.toString().contains("ThreadID"));
        assertFalse(outContent.toString().contains("Is virtual Thread"));
    }
}
