package org.fungover.haze;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


class HazeListTest {

    HazeList hazeList = new HazeList();


    @Test
    void assertThatLPUSHWithMultipleValuesAddsInReverseOrder() {
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
    void alLenWithMissingKeyShouldReturRspZero() {
        assertEquals(":0\r\n", hazeList.lLen("missingKey"));
    }


    @Test
    void lPopShouldReturnNilStringWhenNoKeyIsPresent() {
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
    void lPopShouldReturnBulkStringWithSix(){
        hazeList.rPush("key1", "value1", "value2");
        String bulkStringSixChars = "$6\r\nvalue1\r\n";
        assertEquals(bulkStringSixChars, hazeList.lPop("key1"));
    }

    @Test
    void lPopWithoutKeyShouldReturnCorrectErrorText(){
        assertEquals("$5\r\n(nil)\r\n", hazeList.lPop("key1",1));
    }

    @Test
    void rPopShouldReturnNilStringWhenNoKeyIsPresent() {
        String nilTest = hazeList.rPop("keyThatDontExist");
        assertEquals("$5\r\n(nil)\r\n", nilTest);
    }

    @Test
    void rPopShouldReturnNilStringWhenNoKeyIsEmpty() {
        hazeList.rPush("key1", "val1");
        hazeList.rPush("key2", "val");
        hazeList.lMove("key1", "key2", "LEFT", "RIGHT");
        String nilTest = hazeList.rPop("keyThatDontExist");
        assertEquals("$5\r\n(nil)\r\n", nilTest);
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
    void rPopWithCountShouldReturnNilBulkStringWhenKeyIsMissing(){
        String nilFiveBulk = "$5\r\n(nil)\r\n";
        assertEquals(nilFiveBulk, hazeList.rPop("noKey", 2));
    }

    @Test
    void lMoveShouldMoveVal1FromLeftToLeft(){
        hazeList.rPush("key1", "val1", "val2");
        hazeList.rPush("key2", "val3", "val4");
        hazeList.lMove("key1", "key2", "LEFT", "LEFT");
        String shouldBeVal1 = hazeList.database.get("key2").get(0);
        assertEquals("val1", shouldBeVal1);
    }

    @Test
    void lMoveShouldMoveVal1FromLeftToRight(){
        hazeList.rPush("key1", "val1", "val2");
        hazeList.rPush("key2", "val3", "val4");
        hazeList.lMove("key1", "key2", "LEFT", "RIGHT");
        String shouldBeVal1 = hazeList.database.get("key2").get(2);
        assertEquals("val1", shouldBeVal1);
    }

    @Test
    void lMoveShouldRemoveValueWhenMoved(){
        hazeList.rPush("key1", "val1", "val2");
        hazeList.rPush("key2", "val3", "val4");
        hazeList.lMove("key1", "key2", "RIGHT", "LEFT");
        int shouldBeSizeOne = hazeList.database.get("key1").size();
        assertEquals(1, shouldBeSizeOne);
    }

    @Test
    void lMoveShouldReturnCorrectErrorMessageWhenKeyIsMissing(){
        String errorText = "-One or both keys is missing.\r\n";
        assertEquals(errorText,hazeList.lMove("key1", "key2", "LEFT", "RIGHT"));
    }

    @Test
    void lMoveShouldReturnCorrectErrorMessageWhenListIsEmpty(){
        hazeList.rPush("key1", "val1");
        hazeList.rPush("key2", "val2");
        hazeList.lMove("key1", "key2", "LEFT", "RIGHT");
        String errorText = "-The source list is empty.\r\n";
        assertEquals(errorText,hazeList.lMove("key1", "key2", "LEFT", "RIGHT"));
    }

    @Test
    void destinationShouldHaveCorrectSizeAfterLmove(){
        hazeList.rPush("key1", "val1", "val2");
        hazeList.rPush("key2", "val3", "val4");
        hazeList.lMove("key1", "key2", "RIGHT", "RIGHT");
        int shouldBeSizeThree = hazeList.database.get("key2").size();
        assertEquals(3, shouldBeSizeThree);
    }

    @Test
    void testLMoveInvalidFromAndTo() {
        hazeList.lPush("key1", "val1");
        hazeList.lPush("key2", "val2");
        String result = hazeList.lMove("key1", "key2", "UPP", "DOWN");
        assertEquals("-Invalid input for FROM and WHERE.\r\n", result);
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

    @Test
    void lTrimShouldReturnCorrectErrorTextWhenKeyIsMissing(){
        String correctErrorText = "-The key is not present in the database.\r\n";
        assertEquals(correctErrorText, hazeList.lTrim("key1", 2, 7));
    }

    @Test
    void toStringShouldContainDatabaseContents() {
        HazeList hazeList = new HazeList();
        hazeList.rPush("key1", "val1", "val2");
        hazeList.rPush("key2", "val3");
        String expected = "HazeList{database={key1=[val1, val2], key2=[val3]}}";
        assertEquals(expected, hazeList.toString());
    }

    @Test
    void callLPopWithEmptyCountArrayShouldCallLopWithoutCount(){
        hazeList.rPush("key1", "val1", "val2", "val3");
        String[] emptyStringArray = {};
        String result = hazeList.callLPop("key1", emptyStringArray);
        String expected = "$4\r\nval1\r\n";
        assertEquals(expected, result);
    }

    @Test
    void callLPopWithPopulatedArrayShouldCallLPopWithCount(){
        hazeList.rPush("key1", "val1", "val2", "val3");
        String[] hasSomeNumbers = {"2", "3"};
        String result = hazeList.callLPop("key1", hasSomeNumbers);
        String expected = "*2\r\n$4\r\nval1\r\n$4\r\nval2\r\n";
        assertEquals(expected, result);
    }

    @Test
    void callRPopWithEmptyCountArrayShouldCallRopWithoutCount(){
        hazeList.rPush("key1", "val1", "val2", "val3");
        String[] emptyStringArray = {};
        String result = hazeList.callRpop("key1", emptyStringArray);
        String expected = "$4\r\nval3\r\n";
        assertEquals(expected, result);
    }

    @Test
    void callLRopWithPopulatedArrayShouldCallRPopWithCount(){
        hazeList.rPush("key1", "val1", "val2", "val3");
        String[] hasSomeNumbers = {"2", "3"};
        String result = hazeList.callRpop("key1", hasSomeNumbers);
        String expected = "*2\r\n$4\r\nval3\r\n$4\r\nval2\r\n";
        assertEquals(expected, result);
    }

    @Test
    void CallLtrimShouldCallLtrimWhenGivenAKeyAndTwoNummersAsArgument(){
        hazeList.rPush("key1", "val1", "val2", "val3", "val4", "val5");
        String actual = hazeList.callLtrim("key1","2", "4");
        assertEquals("+OK\r\n", actual);
    }

    @Test
    void CallLReturnCorrectErrorMessageWhenIncorrectNumberOfArgumentsIsReceived () {
        String expected = "-Wrong number of arguments for LTRIM\r\n";
        assertEquals(expected, hazeList.callLtrim("key1"));
    }

    @Test
    void CallLReturnCorrectErrorMessageWhenNotGivenNumbersAsArguments () {
        String expected = "-Value is not an integer or out of range\r\n";
        assertEquals(expected, hazeList.callLtrim("key1", "horse", "Gunnar!"));
    }

    @Test
    void parserWithBadInputShouldReturnZero(){
        assertEquals(0, HazeList.parser("This is not a number"));
    }
}
