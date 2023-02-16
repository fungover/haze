package org.fungover.haze;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MainTest {
    HazeDatabase database = new HazeDatabase();
    HazeList hazeList = new HazeList();

    @Test
    void callingExecuteCommandWithValidNonExistingInputReturnsColonOne() {
        assertThat(Main.executeCommand(database, hazeList, List.of("SETNX", "1", "This is a value"))).isEqualTo(":1\r\n");
    }

    @Test
    void callingExecuteCommandWithInvalidInputStringReturnsErrorMessage() {
        assertThat(Main.executeCommand(database, hazeList, List.of(""))).isEqualTo("-ERR unknown command\r\n");
    }

    @Test
    void executeCommandCanHandleCommandsInBothUpperAndLowerCase() {
        Main.executeCommand(database, hazeList, List.of("sEtNx", "1", "This is a value"));
        assertThat(Main.executeCommand(database, hazeList, List.of("sEtNx", "1", "This is also a value"))).isEqualTo(":0\r\n");
    }
}
