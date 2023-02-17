package org.fungover.haze;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class MainTest {
    HazeDatabase database = new HazeDatabase();
    HazeList hazeList = new HazeList();


    @Test
    void callingExecuteCommandWithValidNonExistingInputReturnsColonOne() {
        assertThat(Main.executeCommand(database, List.of("SETNX", "1", "This is a value"),hazeList)).isEqualTo(":1\r\n");
    }

    @Test
    void callingExecuteCommandWithInvalidInputStringReturnsErrorMessage() {
        assertThat(Main.executeCommand(database, List.of(""),hazeList)).isEqualTo("-ERR unknown command\r\n");
    }

    @Test
    void executeCommandCanHandleCommandsInBothUpperAndLowerCase() {
        Main.executeCommand(database, List.of("sEtNx", "1", "This is a value"),hazeList);
        assertThat(Main.executeCommand(database, List.of("sEtNx", "1", "This is also a value"),hazeList)).isEqualTo(":0\r\n");
    }

    @Test
    void callExecuteCommandWithAnEmptyPingShouldReturnPong() {
        assertThat(Main.executeCommand(database, List.of("Ping"),hazeList)).isEqualTo("+PONG\r\n");
    }

    @Test
    void callExecuteCommandWithPingAndMessageShouldReturnTheMessage() {
        assertThat(Main.executeCommand(database, List.of("Ping", "test message"),hazeList))
                .isEqualTo("$12\r\ntest message\r\n");

    void callExecuteCommandWithPingAndMessageShouldReturnTheMessageAsBulkString() {
        assertThat(Main.executeCommand(database, List.of("Ping", "test message"))).isEqualTo("$12\r\ntest message\r\n");
    }

    @Test
    void callExecuteCommandWithGetAndKeyShouldReturnTheValueAsBulkString() {
        Main.executeCommand(database, List.of("SET", "theKey", "theValue"));
        assertThat(Main.executeCommand(database, List.of("GET", "theKey"))).isEqualTo("$8\r\ntheValue\r\n");
    }

    @Test
    void callExecuteCommandWithDelAndExistingKeyShouldReturnOne() {
        Main.executeCommand(database, List.of("SET", "theKey", "theValue"));
        assertThat(Main.executeCommand(database, List.of("Del", "theKey"))).isEqualTo(":1\r\n");
    }

    @Test
    void callExecuteCommandWithExistsAndOneExistingKeyShouldReturnOne() {
        Main.executeCommand(database, List.of("SET", "theKey", "theValue"));
        assertThat(Main.executeCommand(database, List.of("Exists", "theKey", "secondKey"))).isEqualTo(":1\r\n");
    }

    @Test
    void callExecuteCommandWithSaveShouldReturnOK() {
        Main.executeCommand(database, List.of("SAVE"));
        assertThat(SaveFile.writeOnFile(database.copy())).isEqualTo("+OK\r\n");
    }

    @ParameterizedTest
    @ValueSource(strings = {"SET", "SETNX"})
    void callingExecuteCommandWithWrongNumberOfArgumentsResultsInErrorMessage(String command) {
        assertThat(Main.executeCommand(database, List.of(command, "key")))
                .isEqualTo("-ERR wrong number of arguments for command\r\n");

    }
    
    @Test
    void listToArraySkipFirstTwoShouldReturnThirdValue(){
        List <String> testList = new ArrayList<>();
        testList.add("Value1");
        testList.add("Value2");
        testList.add("Value3");
        String[] testArray = Main.listToArraySkipFirstTwo(testList);
        assertThat(testArray[0]).isEqualTo("Value3");

    }
}
