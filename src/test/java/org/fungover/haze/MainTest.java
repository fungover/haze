package org.fungover.haze;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class MainTest {
    HazeDatabase hazeDatabase = new HazeDatabase();
    HazeList hazeList = new HazeList(hazeDatabase);

    @Test
    void callingExecuteCommandWithValidNonExistingInputReturnsColonOne() {
        assertThat(Main.executeCommand(hazeDatabase, List.of("SETNX", "1", "This is a value"), hazeList)).isEqualTo(":1\r\n");
    }

    @Test
    void callingExecuteCommandWithInvalidInputStringReturnsErrorMessage() {
        assertThat(Main.executeCommand(hazeDatabase, List.of(""), hazeList)).isEqualTo("-ERR unknown command\r\n");
    }

    @Test
    void executeCommandCanHandleCommandsInBothUpperAndLowerCase() {
        Main.executeCommand(hazeDatabase, List.of("sEtNx", "1", "This is a value"), hazeList);
        assertThat(Main.executeCommand(hazeDatabase, List.of("sEtNx", "1", "This is also a value"), hazeList)).isEqualTo(":0\r\n");
    }

    @Test
    void callExecuteCommandWithAnEmptyPingShouldReturnPong() {
        assertThat(Main.executeCommand(hazeDatabase, List.of("Ping"), hazeList)).isEqualTo("+PONG\r\n");
    }

    @Test
    void callExecuteCommandWithPingAndMessageShouldReturnTheMessageAsBulkString() {
        assertThat(Main.executeCommand(hazeDatabase, List.of("Ping", "test message"), hazeList)).isEqualTo("$12\r\ntest message\r\n");
    }

    @Test
    void callExecuteCommandWithGetAndKeyShouldReturnTheValueAsBulkString() {
        Main.executeCommand(hazeDatabase, List.of("SET", "theKey", "theValue"), hazeList);
        assertThat(Main.executeCommand(hazeDatabase, List.of("GET", "theKey"), hazeList)).isEqualTo("$8\r\ntheValue\r\n");
    }

    @Test
    void callExecuteCommandWithDelAndExistingKeyShouldReturnOne() {
        Main.executeCommand(hazeDatabase, List.of("SET", "theKey", "theValue"), hazeList);
        assertThat(Main.executeCommand(hazeDatabase, List.of("Del", "theKey"), hazeList)).isEqualTo(":1\r\n");
    }

    @Test
    void callExecuteCommandWithExistsAndOneExistingKeyShouldReturnOne() {
        Main.executeCommand(hazeDatabase, List.of("SET", "theKey", "theValue"), hazeList);
        assertThat(Main.executeCommand(hazeDatabase, List.of("Exists", "theKey", "secondKey"), hazeList)).isEqualTo(":1\r\n");
    }

    @Test
    void callExecuteCommandWithSaveShouldReturnOK() {
        Main.executeCommand(hazeDatabase, List.of("SAVE"), hazeList);
        assertThat(SaveFile.writeOnFile(hazeDatabase.copy())).isEqualTo("+OK\r\n");
    }

    @ParameterizedTest
    @ValueSource(strings = {"SET", "SETNX"})
    void callingExecuteCommandWithWrongNumberOfArgumentsResultsInErrorMessage(String command) {
        assertThat(Main.executeCommand(hazeDatabase, List.of(command, "key"), hazeList))
                .isEqualTo("-ERR wrong number of arguments for command\r\n");
    }

    @ParameterizedTest
    @ValueSource(strings = {"LPUSH", "RPUSH"})
    void callingExecuteCommandWithListOfSizeOneReturnsOne(String command) {
        assertThat(Main.executeCommand(hazeDatabase, List.of(command, "key", "1"), hazeList))
                .isEqualTo(":1\r\n");
    }

    @ParameterizedTest
    @CsvSource({"LPUSH, LPOP", "RPUSH, RPOP"})
    void callingExecuteCommandWithListOfSizeOneReturnsBulkString(String firstCommand, String secondCommand) {
        Main.executeCommand(hazeDatabase, List.of(firstCommand, "key", "hello"), hazeList);
        assertThat(Main.executeCommand(hazeDatabase, List.of(secondCommand, "key"), hazeList))
                .isEqualTo("$5\r\nhello\r\n");
    }

    @Test
    void callExecuteCommandWithLLENShouldReturnThree() {
        Main.executeCommand(hazeDatabase, List.of("LPUSH", "key", "1", "2", "3"), hazeList);
        assertThat(Main.executeCommand(hazeDatabase, List.of("LLEN", "key"), hazeList)).isEqualTo(":3\r\n");
    }

    @Test
    void callExecuteCommandWithLMOVEShouldReturnErrorMessageWhenListIsEmpty() {
        assertThat(Main.executeCommand(hazeDatabase, List.of("LMOVE", "key"), hazeList)).isEqualTo("-The source list is empty.\r\n");
    }

    @Test
    void callExecuteCommandWithLTRIMShouldReturnErrorMessageWhenKeyDoesNotExist() {
        assertThat(Main.executeCommand(hazeDatabase, List.of("LTRIM", "key", "2", "3"), hazeList)).isEqualTo("-The key is not present in the database.\r\n");
    }
}
