package org.fungover.haze;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

class HazeListTest {

    HazeDatabase hazeDatabase = new HazeDatabase();
    HazeList hazeList = new HazeList(hazeDatabase);


    @Test
    void rPushWithTwoValuesShouldReturnTwo() {
        hazeList.rPush(List.of("", "key1", "value1"));
        String actual = hazeList.rPush(List.of("", "key1", "value2"));
        assertEquals(":2\r\n",actual);
    }

    @Test
    void lPushWithTwoValuesShouldReturnTwo() {
        hazeList.lPush(List.of("", "key1", "value1"));
        String actual = hazeList.lPush(List.of("", "key1", "value2"));
        assertEquals(":2\r\n",actual);
    }

    @Test
    void assertThatLPushWithMultipleValuesAddsInReverseOrder() {
        hazeList.lPush(List.of("", "key1", "value1", "value2"));
        String actual = hazeDatabase.getValue("key1");
        assertEquals("value2\r\nvalue1",actual);
    }

    @Test
    void assertThatLPushMultipleTimesAddsInReverseOrder() {
        hazeList.lPush(List.of("", "key1", "value1"));
        hazeList.lPush(List.of("", "key1", "value2"));
        String actual = hazeDatabase.getValue("key1");
        assertEquals("value2\r\nvalue1",actual);
    }

    @Test
    void assertThatRPushWithMultipleValuesAddsInCorrectOrder() {
        hazeList.rPush(List.of("", "key1", "value1", "value2"));
        String actual = hazeDatabase.getValue("key1");
        assertEquals("value1\r\nvalue2",actual);
    }

    @Test
    void assertThatRPushMultipleTimesAddsInCorrectOrder() {
        hazeList.rPush(List.of("", "key1", "value1"));
        hazeList.rPush(List.of("", "key1", "value2"));
        String actual = hazeDatabase.getValue("key1");
        assertEquals("value1\r\nvalue2",actual);
    }

    @Test
    void lPopShouldReturnNilStringWhenNoKeyIsPresent() {
        String nilTest= hazeList.lPop("keyThatDontExist");
        assertEquals("$5\r\n(nil)\r\n", nilTest);
    }

    @Test
    void lPopShouldReturnCorrectValues() {
        hazeList.rPush(List.of("", "key1", "value1", "value2"));
        String expected = "*2\r\n$6\r\nvalue1\r\n$6\r\nvalue2\r\n";
        assertEquals(expected, hazeList.lPop("key1", 2));
    }

    @Test
    void lPopShouldReturnBulkStringWithSix(){
        hazeList.rPush(List.of("", "key1", "value1", "value2"));
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
        hazeList.rPush(List.of("", "key1", "val1"));
        hazeList.rPush(List.of("", "key2", "val"));
        hazeList.lMove(List.of("", "key1", "key2", "LEFT", "RIGHT"));
        String nilTest = hazeList.rPop("keyThatDontExist");
        assertEquals("$5\r\n(nil)\r\n", nilTest);
    }

    @Test
    void rPopShouldReturnValue2RespString() {
        hazeList.rPush(List.of("", "key1", "value1", "value2"));

        String expected = "$6\r\nvalue2\r\n";
        assertEquals(expected, hazeList.rPop("key1" ));
    }

    @Test
    void rPopShouldReturnValuesInReverseOrder() {
        hazeList.rPush(List.of("", "key1", "value1", "value2"));

        String expected = "*2\r\n$6\r\nvalue2\r\n$6\r\nvalue1\r\n";
        assertEquals(expected, hazeList.rPop("key1", 2));
    }

    @Test
    void rPopWithCountShouldReturnNilBulkStringWhenKeyIsMissing(){
        String nilFiveBulk = "$5\r\n(nil)\r\n";
        assertEquals(nilFiveBulk, hazeList.rPop("noKey", 2));
    }

    @Test
    void assertThatLLenReturnsCorrectValueAddedByLPushAndRPush() {
        hazeList.lPush(List.of("", "key1", "value1", "value2"));
        hazeList.rPush(List.of("", "key1", "value1", "value2"));
        assertEquals(":4\r\n", hazeList.lLen(List.of("", "key1")));
    }

    @Test
    void alLenWithMissingKeyShouldReturRspZero() {
        assertEquals(":0\r\n", hazeList.lLen(List.of("", "missingKey")));
    }

    @Test
    void lMoveShouldMoveVal1FromLeftToLeft(){
        hazeList.rPush(List.of("", "key1", "val1", "val2"));
        hazeList.rPush(List.of("", "key2", "val3", "val4"));
        hazeList.lMove(List.of("", "key1", "key2", "LEFT", "LEFT"));

        String sourceValues = hazeList.hazeDatabase.getValue("key1");
        String destinationValues = hazeList.hazeDatabase.getValue("key2");

        List<String> list1 = HazeList.getValueAsList(sourceValues);
        List<String> list2 = HazeList.getValueAsList(destinationValues);

        assertEquals(List.of("val2"), list1);
        assertEquals(List.of("val1", "val3", "val4"), list2);

    }

    @Test
    void lMoveShouldMoveVal1FromLeftToRight(){
        hazeList.rPush(List.of("", "key1", "val1", "val2"));
        hazeList.rPush(List.of("", "key2", "val3", "val4"));
        hazeList.lMove(List.of("", "key1", "key2", "LEFT", "RIGHT"));

        String sourceValues = hazeList.hazeDatabase.getValue("key1");
        String destinationValues = hazeList.hazeDatabase.getValue("key2");

        List<String> list1 = HazeList.getValueAsList(sourceValues);
        List<String> list2 = HazeList.getValueAsList(destinationValues);

        assertEquals(List.of("val2"), list1);
        assertEquals(List.of("val3", "val4", "val1"), list2);
    }

    @Test
    void lMoveShouldMoveValuesFromRightToLeft(){
        hazeList.rPush(List.of("", "key1", "val1", "val2"));
        hazeList.rPush(List.of("", "key2", "val3", "val4"));
        hazeList.lMove(List.of("", "key1", "key2", "RIGHT", "LEFT"));

        String sourceValues = hazeList.hazeDatabase.getValue("key1");
        String destinationValues = hazeList.hazeDatabase.getValue("key2");

        List<String> list1 = HazeList.getValueAsList(sourceValues);
        List<String> list2 = HazeList.getValueAsList(destinationValues);

        assertEquals(List.of("val2", "val3", "val4"), list2);
        assertEquals(List.of("val1"), list1);
    }

    @Test
    void lMoveShouldReturnCorrectErrorMessageWhenKeyIsMissing(){
        String errorText = "-One or both keys is missing.\r\n";
        assertEquals(errorText,hazeList.lMove(List.of("", "key1", "key2", "LEFT", "RIGHT")));
    }

    @Test
    void lMoveShouldReturnCorrectErrorMessageWhenListIsEmpty(){
        hazeList.rPush(List.of("", "key1", "val1"));
        hazeList.rPush(List.of("", "key2", "val2"));
        hazeList.lMove(List.of("", "key1", "key2", "LEFT", "RIGHT"));
        String errorText = "-The source list is empty.\r\n";
        assertEquals(errorText,hazeList.lMove(List.of("", "key1", "key2", "LEFT", "RIGHT")));
    }

    @Test
    void destinationShouldHaveCorrectSizeAfterLMove() {
        hazeList.rPush(List.of("", "key1", "val1", "val2"));
        hazeList.rPush(List.of("", "key2", "val3", "val4"));
        hazeList.lMove(List.of("", "key1", "key2", "RIGHT", "RIGHT"));
        String destinationList = hazeDatabase.getValue("key2");
        List<String> parsedList = HazeList.getValueAsList(destinationList);
        assertEquals(3, parsedList.size());
    }

    @Test
    void testLMoveInvalidFromAndTo() {
        hazeList.lPush(List.of("", "key1", "val1"));
        hazeList.lPush(List.of("", "key2", "val2"));
        String result = hazeList.lMove(List.of("", "key1", "key2", "UPP", "DOWN"));
        assertEquals("-Invalid input for FROM and WHERE.\r\n", result);
    }

    @Test
    void correctValuesShouldStayAfterTrim() {
        hazeList.rPush(List.of("", "key1", "val1", "val2", "val3", "val4", "val5"));
        hazeList.lTrim("key1", 1,2);
        String databaseCsv = hazeList.hazeDatabase.getValue("key1");
        List<String> list1 = HazeList.getValueAsList(databaseCsv);

        assertEquals(List.of("val2", "val3"), list1);
    }

    @Test
    void lTrimShouldReturnErrorCorrectErrorTextWhenInputsAreOutOfRange(){
        hazeList.rPush(List.of("", "key1", "val1", "val2", "val3", "val4", "val5"));
        String correctErrorText = "-The inputs are outside the range of the list.\r\n";
        assertEquals(correctErrorText, hazeList.lTrim("key1", 2, 7));
    }

    @Test
    void lTrimShouldReturnCorrectErrorTextWhenKeyIsMissing(){
        String correctErrorText = "-The key is not present in the database.\r\n";
        assertEquals(correctErrorText, hazeList.lTrim("key1", 2, 7));
    }

    @Test
    void callLPopWithEmptyCountArrayShouldCallLopWithoutCount(){
        hazeList.rPush(List.of("", "key1", "val1", "val2", "val3"));
        String result = hazeList.callLPop(List.of("", "key1"));
        String expected = "$4\r\nval1\r\n";
        assertEquals(expected, result);
    }

    @Test
    void callLPopWithPopulatedArrayShouldCallLPopWithCount(){
        hazeList.rPush(List.of("", "key1", "val1", "val2", "val3"));
        String result = hazeList.callLPop(List.of("", "key1", "2", "3"));
        String expected = "*2\r\n$4\r\nval1\r\n$4\r\nval2\r\n";
        assertEquals(expected, result);
    }

    @Test
    void callRPopWithEmptyCountArrayShouldCallRopWithoutCount(){
        hazeList.rPush(List.of("", "key1", "val1", "val2", "val3"));
        String result = hazeList.callRPop(List.of("", "key1"));
        String expected = "$4\r\nval3\r\n";
        assertEquals(expected, result);
    }

    @Test
    void callLRopWithPopulatedArrayShouldCallRPopWithCount(){
        hazeList.rPush(List.of("", "key1", "val1", "val2", "val3"));
        String result = hazeList.callRPop(List.of("", "key1", "2", "3"));
        String expected = "*2\r\n$4\r\nval3\r\n$4\r\nval2\r\n";
        assertEquals(expected, result);
    }

    @Test
    void CallLtrimShouldCallLtrimWhenGivenAKeyAndTwoNummersAsArgument(){
        hazeList.rPush(List.of("", "key1", "val1", "val2", "val3", "val4", "val5"));
        String actual = hazeList.callLtrim(List.of("", "key1","2", "4"));
        assertEquals("+OK\r\n", actual);
    }

    @Test
    void CallLReturnCorrectErrorMessageWhenIncorrectNumberOfArgumentsIsReceived () {
        String expected = "-Wrong number of arguments for LTRIM\r\n";
        assertEquals(expected, hazeList.callLtrim(List.of("", "key1")));
    }

    @Test
    void CallLReturnCorrectErrorMessageWhenNotGivenNumbersAsArguments () {
        String expected = "-Value is not an integer or out of range\r\n";
        assertEquals(expected, hazeList.callLtrim(List.of("", "key1", "horse", "Gunnar!")));
    }

    @Test
    void parserWithBadInputShouldReturnZero(){
        assertEquals(0, HazeList.parser("This is not a number"));
    }


    @Test
    void callingLindexWithValidPositiveIndexReturnValue(){
        hazeList.rPush(List.of("", "key2", "val1", "val2", "val3"));
        assertThat(hazeList.lIndex(List.of("", "key2", "2"))).isEqualTo("$4\r\nval3\r\n");
    }

    @Test
    void callingLindexWithIndexOutOfBoundsReturnNil(){
        hazeList.rPush(List.of("", "key2", "val1", "val2", "val3"));
        assertThat(hazeList.lIndex(List.of("", "key2", "3"))).isEqualTo("$5\r\n(nil)\r\n");
    }

    @Test
    void callingLindexWithValidNegativeIndexReturnValue(){
        hazeList.rPush(List.of("", "key2", "val1", "val2", "val3"));
        assertThat(hazeList.lIndex(List.of("", "key2", "-1"))).isEqualTo("$4\r\nval3\r\n");
    }

    @Test
    void callingLindexWithValidIndexZeroReturnFirstValue(){
        hazeList.rPush(List.of("", "key2", "val1", "val2", "val3"));
        assertThat(hazeList.lIndex(List.of("", "key2", "0"))).isEqualTo("$4\r\nval1\r\n");
    void lSetShouldUpdateValue()
    {
       hazeList.rPush(List.of("", "key1", "val1", "val2", "val3", "val4", "val5"));
       hazeList.lSet(List.of("", "key1", "0", "hej"));
       String asString = hazeDatabase.getValue("key1");
       assertThat(asString).isEqualTo("hej\r\nval2\r\nval3\r\nval4\r\nval5");
    }

    @Test
    void lSetWithIndexOutOfBoundsShouldReturnErrorMessage() {
        hazeList.rPush(List.of("", "key1", "val1", "val2", "val3", "val4", "val5"));
        assertThat(hazeList.lSet(List.of("", "key1", "6", "hej"))).isEqualTo("-Err index out of bounds\r\n");
    } @Test
    void lSetWithNonExistingKey() {
        hazeList.rPush(List.of("", "key1", "val1", "val2", "val3", "val4", "val5"));
        assertThat(hazeList.lSet(List.of("", "key2", "3", "hej"))).isEqualTo("-Err Key does not exist\r\n");
    }
    @Test
    void lSetIndexWithValidNegativeIndexReturnValue(){
        hazeList.rPush(List.of("", "key1", "val1", "val2", "val3", "val4", "val5"));
        hazeList.lSet(List.of("", "key1", "-1", "howdy"));
        String asString = hazeDatabase.getValue("key1");
        assertThat(asString).isEqualTo("val1\r\nval2\r\nval3\r\nval4\r\nhowdy");
    }

 @Test
 void lSetNoKey() {
        hazeList.rPush(List.of("","","val1","val2","val3"));
        hazeList.lSet(List.of("","","0","val1"));
        String asString = hazeDatabase.getValue("");

     assertThat(asString).isEqualTo("val1\r\nval2\r\nval3");
 }
    @Test
    void lSetWithWrongNumberOFArguments() {
        hazeList.rPush(List.of("", "key1", "val1"));
        assertThat(hazeList.lSet(List.of("", "key1", "hej"))).isEqualTo("-Err Wrong number of arguments for LSET\r\n");
    }

}
