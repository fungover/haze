package org.fungover.haze;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class MainTest {
    HazeDatabase database = new HazeDatabase();

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
        assertThat(Main.executeCommand(database, List.of("Ping", "test message"))).isEqualTo("$12\r\ntest message\r\n");
    }

    @Test
    void callExecuteCommandWithGetAndKeyShouldReturnTheValue() {
        Main.executeCommand(database, List.of("SET", "theKey", "theValue"));
        assertThat(Main.executeCommand(database, List.of("GET", "theKey"))).isEqualTo("$8\r\ntheValue\r\n");
    }

    @Test
    void callExecuteCommandWithDelAndKeyShouldReturnOne() {
        Main.executeCommand(database, List.of("SET", "theKey", "theValue"));
        assertThat(Main.executeCommand(database, List.of("Del", "theKey"))).isEqualTo(":1\r\n");
    }

    @Test
    void callExecuteCommandWithExistsAndKeyShouldReturnOne() {
        Main.executeCommand(database, List.of("SET", "theKey", "theValue"));
        assertThat(Main.executeCommand(database, List.of("Exists", "theKey", "secondKey"))).isEqualTo(":1\r\n");
    }

    @Test
    void callExecuteCommandWithSaveAndKeyShouldReturnOne() {
        Main.executeCommand(database, List.of("SAVE"));
        assertThat(SaveFile.writeOnFile(database.copy())).isEqualTo("+OK\r\n");
    }
}
