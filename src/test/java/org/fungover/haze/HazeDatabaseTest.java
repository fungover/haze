package org.fungover.haze;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HazeDatabaseTest {

    HazeDatabase testDatabase = new HazeDatabase();

    @Test
    void testSetNxReturnZeroWhenExistingKeyAreUsedWithDifferentValue() {
        testDatabase.setNX(List.of("", "1", "Hej"));
        assertThat(testDatabase.setNX(List.of("", "1", "Då"))).isEqualTo(":0\r\n");
    }

    @Test
    void testSetNxReturnOneWhenKeyDontExist() {
        assertThat(testDatabase.setNX(List.of("", "2", "Då"))).isEqualTo(":1\r\n");
    }
}
