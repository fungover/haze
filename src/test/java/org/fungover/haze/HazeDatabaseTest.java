package org.fungover.haze;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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

    @Test
    void testGetStringWithValidKey() {
        testDatabase.set("key", "value");
        String result = testDatabase.getString("key");
        assertEquals("$5\r\nvalue\r\n", result);
    }

    @Test
    void testGetStringWithNullKey() {
        try {
            testDatabase.getString(null);
            fail();
        } catch (NullPointerException npe) {

        }
    }
}
