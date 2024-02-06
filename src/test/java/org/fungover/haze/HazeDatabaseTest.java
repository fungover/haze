package org.fungover.haze;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        assertThat(testDatabase.get(List.of("", "1"))).isEqualTo("$-1\r\n");
    }

    @Test
    void callingGetReturnsTheCorrectValueIfItExists() {
        testDatabase.setNX(List.of("SETNX", "someKey", "someValue"));
        assertThat(testDatabase.get(List.of("", "someKey"))).isEqualTo("$9\r\nsomeValue\r\n");
    }

    @Test
    void testSetNxReturnZeroWhenExistingKeyAreUsedWithDifferentValue() {
        testDatabase.setNX(List.of("", "1", "Hej"));
        assertThat(testDatabase.setNX(List.of("", "1", "Då"))).isEqualTo(":0\r\n");
    }


    @Test
    void testSettingOneKeyInDatabaseMakesExistsFunctionReturnOneInstanceOfKeyExistingInTheDatabase() {
        testDatabase.setNX(List.of("", "name", "saher"));

        assertThat(testDatabase.exists(List.of("name"))).isEqualTo(":1\r\n");
    }

    @Test
    void testSettingTwoKeysInDatabaseMakesExistsFunctionReturnOneInstanceOfKeyExistingInTheDatabase() {
        testDatabase.setNX(List.of("", "name", "saher"));
        testDatabase.setNX(List.of("", "1", "Hej"));

        assertThat(testDatabase.exists(List.of("name"))).isEqualTo(":1\r\n");
    }

    @Test
    void testAskingExistsFunctionForNumerousKeysWhereOneKeyHasTwoOccurrencesAndTheOtherKeyHasOneOccurrenceShouldReturnThree() {
        testDatabase.setNX(List.of("", "name", "saher"));
        testDatabase.setNX(List.of("", "1", "Hej"));

        assertThat(testDatabase.exists(List.of("name", "1", "name", "2"))).isEqualTo(":3\r\n");
    }

    @Test
    void testSendingInNoParametersToExistsMethodReturnsZero() {
        testDatabase.setNX(List.of("", "name", "saher"));
        testDatabase.setNX(List.of("", "1", "Hej"));

        assertThat(testDatabase.exists(Collections.EMPTY_LIST)).isEqualTo(":0\r\n");
    }

    @Test
    void testSetNxReturnOneWhenKeyDontExist() {
        assertThat(testDatabase.setNX(List.of("", "2", "Då"))).isEqualTo(":1\r\n");
    }

    @Test
    void testPingResponseReturnsPong() {
        assertThat(testDatabase.ping(List.of("PING"))).isEqualTo("+PONG\r\n");
    }

    @Test
    void testPingResponseShouldBeSameAsValue() {
        assertThat(testDatabase.ping(List.of("PING", "test message"))).isEqualTo("$12\r\ntest message\r\n");
    }

    @Test
    void testSetWithValidKeyValuePair() {
        String result = testDatabase.set(List.of("", "key", "value"));
        assertEquals("+OK\r\n", result);
    }

    @Test
    void testSetWithNullValue() {
        String result = testDatabase.set(Arrays.asList("", "key", null));
        assertEquals("+OK\r\n", result);
    }

    @Test
    void testGetWithValidKey() {
        testDatabase.set(List.of("", "key", "value"));
        String result = testDatabase.get(List.of("", "key"));
        assertEquals("$5\r\nvalue\r\n", result);
    }

    @Test
    void testGetWithInvalidKey() {
        String result = testDatabase.get(List.of("", "invalidKey"));
        assertEquals("$-1\r\n", result);
    }

    @Test
    void testGetWithNullKey() {
        String result = testDatabase.get(Arrays.asList("", null));
        assertEquals("$-1\r\n", result);
    }

    @Test
    void testThatIfYouPutKeyAndValueYouGetOutAMap() {
        testDatabase.set(List.of("", "1", "test"));
        testDatabase.set(List.of("", "2", "hast"));
        assertThat(testDatabase.copy())
                .containsEntry("1", "test")
                .containsEntry("2", "hast");
    }

    @Test
    void callingSetWithWrongNumberOfArgumentsResultsInErrorMessage() {
        assertThat(testDatabase.set(List.of("", "key"))).isEqualTo("-ERR wrong number of arguments for command\r\n");
    }

    @Test
    void callingSetNXWithWrongNumberOfArgumentsResultsInErrorMessage() {
        assertThat(testDatabase.setNX(List.of("", "key"))).isEqualTo("-ERR wrong number of arguments for command\r\n");
    }

    @Test
    void callingGetWithWrongNumberOfArgumentsResultsInErrorMessage() {
        assertThat(testDatabase.get(List.of(""))).isEqualTo("-ERR wrong number of arguments for command\r\n");
    }

    @Test
    void getValueShouldReturnCorrectValue(){
        testDatabase.addValue("key1", "Gunnar");
        assertThat(testDatabase.getValue("key1")).isEqualTo("Gunnar");
    }

    @Test
    void shouldShouldReturnTrue(){
        testDatabase.addValue("key1", "Gunnar");
        assertThat(testDatabase.containsKey("key1")).isTrue();
    }

    @Test
    void callingGetIndexOfWithInvalidKeyReturnMinusOne(){
        testDatabase.addValue("key1", "hej");
        testDatabase.addValue("key2", "bonjour");

        assertThat(testDatabase.getIndex(List.of("INDEX", "hello"))).isEqualTo("$-1\r\n");
    }

    @Test
    void callingGetIndexOfWithValidKeyReturnIndex() {
        testDatabase.addValue("key1", "hej");
        testDatabase.addValue("key2", "bonjour");
        testDatabase.addValue("key3", "hello");
        assertThat(testDatabase.getIndex(List.of("INDEX", "hello"))).isEqualTo("$2\r\n");
    }

    @Test
    void callingIndexOfWithInvalidArgumentsReturnErrorMessage() {
        testDatabase.addValue("key1", "hej");
        testDatabase.addValue("key2", "bonjour");
        testDatabase.addValue("key3", "hello");
        assertThat(testDatabase.getIndex(List.of( "hello"))).isEqualTo("-ERR wrong number of arguments for command\r\n");
    }

}
