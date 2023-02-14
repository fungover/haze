package org.fungover.haze;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
    public void testSetWithValidKeyValuePair() {
        String result = testDatabase.set("key", "value");
        assertEquals("+OK\r\n", result);
    }

    @Test
    public void testSetWithNullValue() {
        String result = testDatabase.set("key", null);
        assertEquals("+OK\r\n", result);
    }

    @Test
    public void testGetWithValidKey() {
        testDatabase.set("key", "value");
        String result = testDatabase.get("key");
        assertEquals("$5\r\nvalue\r\n", result);
    }

    @Test
    public void testGetWithInvalidKey() {
        String result = testDatabase.get("invalidKey");
        assertEquals("$-1\r\n", result);
    }

    @Test
    public void testGetWithNullKey() {
        String result = testDatabase.get(null);
        assertEquals("$-1\r\n", result);
    }

    @Test
    public void testGetStringWithValidKey() {
        testDatabase.set("key", "value");
        String result = testDatabase.getString("key");
        assertEquals("$5\r\nvalue\r\n", result);
    }

    @Test
    public void testGetStringWithNullKey() {
        try {
            testDatabase.getString(null);
            fail();
        } catch (NullPointerException npe) {

        }
    }
}
