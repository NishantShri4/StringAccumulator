package com.nishant.que;

import com.nishant.exception.NegativeNumbersFoundException;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

public class StringAccumulatorTest {
    StringAccumulator strAcc;

    @Before
    public void setUp() throws Exception {
        strAcc = new StringAccumulator(3); // Checks most of the test cases for the upfront requirement i.e. maxCountAllowed = 3.
    }

    //1a. Process an Empty String.
    @Test
    public void addEmptyString() {
        int expectedResponse = 0;
        int actualResponse = strAcc.add("");
        assertEquals(actualResponse,expectedResponse);
    }

    //1b. Process a valid simple strings of 1,2,or 3 elements.
    @Test
    public void addAValidStringWithOneOrTwoOrThreeNumbers() {
        int expectedResponse1 = 1;
        int actualResponse1 = strAcc.add("1");
        assertEquals(actualResponse1,expectedResponse1);

        int expectedResponse2 = 3;
        int actualResponse2 = strAcc.add("1,2");
        assertEquals(actualResponse2,expectedResponse2);

        int expectedResponse3 = 6;
        int actualResponse3 = strAcc.add("1,2,3");
        assertEquals(actualResponse3,expectedResponse3);
    }

    //2. Allow the add method to handle an unknown amount of numbers.
    @Test
    public void addStringWithUnknownAmountOfNumbers() {
        strAcc = new StringAccumulator(0); // constructor argument zero allows any count of numbers to be processed. See the javadocs for add() method.
        int expectedResponse = 15;
        int actualResponse = strAcc.add("//*|?|;\n1?2*3;4;5");
        assertEquals(actualResponse,expectedResponse);
    }


    //3a. Allow the add method to handle new lines between numbers (instead of commas).
    @Test
    public void addStringWithDelimiterAsNewLineOnly() {
        int expectedResponse = 30;
        int actualResponse = strAcc.add("12\n13\n5");
        assertEquals(actualResponse,expectedResponse);
    }

    //3a(variant). Allow the add method to handle new lines between numbers (instead of commas).
    @Test
    public void addStringWithDelimiterAsNewLineAndCommaOnly() {
        int expectedResponse = 29;
        int actualResponse = strAcc.add("12\n13,4");
        assertEquals(actualResponse,expectedResponse);
    }
    //3b.Reject string which ends in a delimiter.
    @Test
    public void rejectStringWhichEndsInADelimiter() {
        assertThatThrownBy(() -> { strAcc.add("1,\n"); }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("The input String is not valid, Ends with a delimiter.");
    }

    //4a. Support a single different delimiters (other than ',', which is the default delimiter).
    @Test
    public void addStringWithASingleOneCustomDelimiter () {
        int expectedResponse = 11;
        int actualResponse = strAcc.add("//&\n2&5&4");
        assertEquals(actualResponse,expectedResponse);
    }

    //4a(variant). Support multiple different delimiters (other than ',') of single character each.
    @Test
    public void addStringWithMultipelSingleCharacterCustomDelimiters () {
        int expectedResponse = 19;
        int actualResponse = strAcc.add("//$|&\n12&3$4");
        assertEquals(actualResponse,expectedResponse);
    }

    /*
    5.Calling add with a negative number will throw an exception with the message “negatives not allowed” - and the negative that was passed.
    a. If there are multiple negatives, show all of them in the exception message.
     */
    @Test
    public void addStringContainingNegativeNumbers() {
       // negatives not allowed
        assertThatThrownBy(() -> { strAcc.add("-2,5,-6");; }).isInstanceOf(NegativeNumbersFoundException.class)
                .hasMessage("negatives not allowed : -2,-6");
    }

    //6. Numbers bigger than 1000 should be ignored, so adding 2 + 1001 = 2.
    @Test
    public void addaValidStringContainingNumbersGreaterThan1000() {
        int expectedResponse = 11;
        int actualResponse = strAcc.add("2\n9,1001");
        assertEquals(actualResponse,expectedResponse);
    }

    //7. Delimiters can be of any length, for example: “//***\n1***2***3” should return 6.
    @Test
    public void addStringWithASingleCustomDelimiterOfMultiCharacterLength() {
        int expectedResponse1 = 31;
        int actualResponse1 = strAcc.add("//???\n2???6???23");
        assertEquals(actualResponse1,expectedResponse1);


        int expectedResponse2 = 6;
        int actualResponse2 = strAcc.add("//***\n1***2***3");
        assertEquals(actualResponse2,expectedResponse2);
    }

    //8. Make sure you can also handle multiple delimiters with length longer than one character..
    @Test
    public void addStringWithMultipleCustomDelimitersOfMultiCharacterLength() {
       /* int expectedResponse1 = 33;
        int actualResponse1 = strAcc.add("//*|***\n4*6***23");
        assertEquals(actualResponse1,expectedResponse1);*/

        int expectedResponse2 = 6;
        int actualResponse2 = strAcc.add("//*|%\n1*2%3");
        assertEquals(actualResponse2,expectedResponse2);

    }

    // Don't allow processing numbers more than maxCountAllowed;
    @Test
    public void addStringWithMoreThanThreeNumbers() {
        assertThatThrownBy(() -> { strAcc.add("2,5,6,12"); }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("The string has more than "+ strAcc.maxCountAllowed+ " numbers embedded");
    }

    // Don't allow processing numbers more than maxCountAllowed;
    @Test
    public void rejectParsingIfUnknownDelimiters() {
        assertThatThrownBy(() -> { strAcc.add("//*|?\n1*2%3"); }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown Delimiters are present. Cannot Parse.");
    }

}
