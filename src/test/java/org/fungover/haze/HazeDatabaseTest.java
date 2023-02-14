package org.fungover.haze;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HazeDatabaseTest {

    HazeDatabase testDatabase = new HazeDatabase();

    @Test
    void testSetNxReturnZeroWhenExistingKeyAreUsedWithDifferentValue() {
        testDatabase.setNX("1", "Hej");
        assertThat(testDatabase.setNX("1", "Då")).isEqualTo(":0\r\n");
    }

    @Test
    void testSetNxReturnOneWhenKeyDontExist() {
        assertThat(testDatabase.setNX("2", "Då")).isEqualTo(":1\r\n");
    }

    @Test
    void testPingResponseReturnsPong() {
        assertThat(testDatabase.ping(null)).isEqualTo("+PONG\r\n");

    }

    @Test
    void testPingResponseShouldBeSameAsValue() {
        assertThat(testDatabase.ping("Test ping")).isEqualTo("$9\r\nTest ping\r\n");

    }
}
