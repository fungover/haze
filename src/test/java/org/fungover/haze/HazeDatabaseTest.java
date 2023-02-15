package org.fungover.haze;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HazeDatabaseTest {

    HazeDatabase testDatabase = new HazeDatabase();

    @Test
    void callingDeleteReturnsZeroWhenKeyDoesNotExist() {
        assertThat(testDatabase.delete(Collections.singletonList("2"))).isEqualTo(":0\r\n");
    }

    @Test
    void callingDeleteReturnsNumberOfSuccessfullyRemovedKeys() {
        testDatabase.setNX(List.of("SETNX", "1", "test"));
        testDatabase.setNX(List.of("SETNX", "2", "test"));
        testDatabase.setNX(List.of("SETNX", "22", "thisShouldNotBeRemoved"));
        assertThat(testDatabase.delete(List.of("1", "2", "3", "4"))).isEqualTo(":2\r\n");
    }

    @Test
    void callingDeleteRemovesTheSpecifiedKey() {
        testDatabase.setNX(List.of("SETNX", "1", "thisWillBeRemoved"));
        testDatabase.delete(Collections.singletonList("1"));
        assertThat(testDatabase.get("1")).isEqualTo("$-1\r\n");
    }

    @Test
    void callingGetReturnsTheCorrectValueIfItExists() {
        testDatabase.setNX(List.of("SETNX", "someKey", "someValue"));
        assertThat(testDatabase.get("someKey")).isEqualTo("$9\r\nsomeValue\r\n");
    }

    @Test
    void testSetNxReturnZeroWhenExistingKeyAreUsedWithDifferentValue() {
        testDatabase.setNX(List.of("", "1", "Hej"));
        assertThat(testDatabase.setNX(List.of("", "1", "Då"))).isEqualTo(":0\r\n");
    }

    @Test
    void testSetNxReturnOneWhenKeyDontExist() {
        assertThat(testDatabase.setNX(List.of("", "2", "Då"))).isEqualTo(":1\r\n");
    }

    @Test
    void testSetWithValidKeyValuePair() {
        String result = testDatabase.set("key", "value");
        assertEquals("+OK\r\n", result);
    }

    @Test
    void testSetWithNullValue() {
        String result = testDatabase.set("key", null);
        assertEquals("+OK\r\n", result);
    }

    @Test
    void testGetWithValidKey() {
        testDatabase.set("key", "value");
        String result = testDatabase.get("key");
        assertEquals("$5\r\nvalue\r\n", result);
    }

    @Test
    void testGetWithInvalidKey() {
        String result = testDatabase.get("invalidKey");
        assertEquals("$-1\r\n", result);
    }

    @Test
    void testGetWithNullKey() {
        String result = testDatabase.get(null);
        assertEquals("$-1\r\n", result);
    }
}
