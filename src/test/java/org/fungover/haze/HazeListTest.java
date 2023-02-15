package org.fungover.haze;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class HazeListTest {

    HazeList hazeList = new HazeList();


    @Test
    void assertThatLPUSHWithMiltipleValuesAddsInReverseOrder() {
        hazeList.lPush("key1", "value1", "value2");
        String indexZero = hazeList.database.get("key1").get(0);
        assertEquals("value2", indexZero);
    }

    @Test
    void assertThatLpushWithOneValueGetIndexZero() {
        hazeList.lPush("key1", "value1", "value2");
        hazeList.lPush("key1", "X");
        String indexZero = hazeList.database.get("key1").get(0);
        assertEquals("X", indexZero);
    }

    @Test
    void assertThatLlenReturnsCorrectValueAddedByLpushAndRpush() {
        hazeList.lPush("key1", "value1", "value2");
        hazeList.rPush("key1", "value1", "value2");
        assertEquals(":4\r\n", hazeList.lLen("key1"));
    }

    @Test
    void lpopShouldReturnNilStringWhenNoKeyIsPressent() {
        String nilTest= hazeList.lPop("keyThatDontExist");
        assertEquals("$5\r\n(nil)\r\n", nilTest);
    }

    @Test
    void lPopShouldReturnCorrectValues() {
        hazeList.rPush("key1", "value1", "value2");

        String expected = "*2\r\n$6\r\nvalue1\r\n$6\r\nvalue2\r\n";

        assertEquals(expected, hazeList.lPop("key1", 2));
    }

    @Test
    void rPopShouldReturnValue2RespString() {
        hazeList.rPush("key1", "value1", "value2");

        String expected = "$6\r\nvalue2\r\n";
        assertEquals(expected, hazeList.rPop("key1" ));
    }

    @Test
    void rPopShouldReturnValuesInReverseOrder() {
        hazeList.rPush("key1", "value1", "value2");

        String expected = "*2\r\n$6\r\nvalue2\r\n$6\r\nvalue1\r\n";
        assertEquals(expected, hazeList.rPop("key1", 2));
    }
    @Test
    void lMoveShouldMoveVal1FromLeftToLeft(){
        hazeList.rPush("key1", "val1", "val2");
        hazeList.rPush("key2", "val3", "val4");
        hazeList.lMove("key1", "key2", "LEFT", "LEFT");
        String shouldBeVal1 = hazeList.database.get("key2").get(0);
        assertEquals(shouldBeVal1, "val1");
    }

    @Test
    void lMoveShouldMoveVal1FromLeftToRight(){
        hazeList.rPush("key1", "val1", "val2");
        hazeList.rPush("key2", "val3", "val4");
        hazeList.lMove("key1", "key2", "LEFT", "RIGHT");
        String shouldBeVal1 = hazeList.database.get("key2").get(2);
        assertEquals(shouldBeVal1, "val1");
    }

    @Test
    void lMoveShouldRemoveValueWhenMoved(){
        hazeList.rPush("key1", "val1", "val2");
        hazeList.rPush("key2", "val3", "val4");
        hazeList.lMove("key1", "key2", "RIGHT", "LEFT");
        int shouldBeSizeOne = hazeList.database.get("key1").size();
        assertEquals(shouldBeSizeOne, 1);
    }

    @Test
    void destinationShouldHaveCorrectSizeAfterLmove(){
        hazeList.rPush("key1", "val1", "val2");
        hazeList.rPush("key2", "val3", "val4");
        hazeList.lMove("key1", "key2", "RIGHT", "RIGHT");
        int shouldBeSizeThree = hazeList.database.get("key2").size();
        assertEquals(shouldBeSizeThree, 3);
    }

    @Test
    void correctValuesShouldStayAfterTrim() {
        hazeList.rPush("key1", "val1", "val2", "val3", "val4", "val5");
        hazeList.lTrim("key1", 1,2);
        String valuesLeft = "[val2, val3]";

        assertEquals(valuesLeft, hazeList.database.get("key1").toString());
    }

    @Test
    void lTrimShouldReturnErrorCorrectErrorTextWhenInputsAreOutOfRange(){
        hazeList.rPush("key1", "val1", "val2", "val3", "val4", "val5");
        String correctErrorText = "-The inputs are outside the range of the list.\r\n";
        assertEquals(correctErrorText, hazeList.lTrim("key1", 2, 7));
    }
}
