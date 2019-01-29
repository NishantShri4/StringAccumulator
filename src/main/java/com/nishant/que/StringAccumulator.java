package com.nishant.que;

import com.nishant.exception.NegativeNumbersFoundException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * code{{@link StringAccumulator}} is an utility to process a comma separated list of numbers.
 * It supports other delimiters (except comma).
 * Currently it supports addition of numbers in the comma separated list.
 * The utility doesn't support processing negative numbers.
 */
public class StringAccumulator {

    int maxCountAllowed; //Input String having numbers more this maxCountAllowed will throw IllegalArgumentException; Set it to zero to process any count of numbers.

    private static final Logger logger = LoggerFactory.getLogger(StringAccumulator.class);

    private static final String regexForInputStringWithOptionalCustomDelimiters  = "^(//.+\n)(.+)";
    private static final String regexForCustomDelimitersOnly  = "(//)(.+)(\n)";
    private static final Integer MAX_VAL_AFTER_WHICH_NUMBERS_ARE_IGNORED = 1000;
    private static final String defaultDelimiter1 = ",";
    private static final String defaultDelimiter2 = "\n";

     /**
     * This method returns the sum of the available number in a input string (which has some numbers separated by a delimiter).
     * The delimiter could be a default one i.e. comma or a custom one such as '\n' (new line), '|', '**'.
     * The custom delimiter could be a single character or a multi character string.
     * The method doesn't support a count of numbers more than maxCountAllowed.
     * All numbers greater than MAX_VAL_AFTER_WHICH_NUMBERS_ARE_IGNORED are ignored.
     * Negative numbers are not supported and an @{@link NegativeNumbersFoundException} is thrown.
     * @param numbers  - The inputString (delimiter separated)
     * @return - The sum of the numbers present inside @param numbers.
     */
    public  int add(String numbers) {

        //Initialize some variables
        int total = 0;
        Set<String> delimiterSet = new HashSet<>();
        List<Integer> listOfIntegers = new LinkedList<>();

        //Check the input string against below pattern.
        Pattern pat = Pattern.compile(regexForInputStringWithOptionalCustomDelimiters, Pattern.DOTALL);
        Matcher mat = pat.matcher(numbers);

        //Check Whether custom delimiters are present?
        boolean areCustomDelimitersPresent = mat.matches();

        if (areCustomDelimitersPresent) {
            logger.info("Custom Delimiters are present");

            //Extract the part which has custom Delimiters
            String customDelimiterString = mat.group(1);

            //Create a set of unique deliiters.
            delimiterSet = findAllDelimiters(customDelimiterString);

            //Set numbers to the actual string (without optional delimiter line).
            numbers = mat.group(2);

        } else { //If custom Delimiters are not present then use comma and new line i.e. ',' or '\n'.
            delimiterSet.add(defaultDelimiter1);
            delimiterSet.add(defaultDelimiter2);

        }

        //Start processing.
        if (numbers.isEmpty()) {
            return total;
        } else {
            //Extract numbers out of inputString based ont the available number of delimiters.
            splitUsingDelimiters(convertSetToArray(delimiterSet), numbers, listOfIntegers);

            //Handling for negative Integers.
            Stream<Integer> streamOfNegativeNumbers =  listOfIntegers.stream().filter(i -> i < 0);
            List<Integer> negatives  = streamOfNegativeNumbers.collect(Collectors.toList());
            if (negatives.size() > 0) {
                throw new NegativeNumbersFoundException(StringUtils.join(negatives, ','));
            } else { // Handling for non-negative numbers.
                IntStream streamOfNonNegativeNumbersFilteredByMaxValueAllowed = listOfIntegers.stream().mapToInt(Integer::valueOf).filter(i -> i < MAX_VAL_AFTER_WHICH_NUMBERS_ARE_IGNORED);
                total = streamOfNonNegativeNumbersFilteredByMaxValueAllowed.sum();
            }
        }

        logger.info("Total is : "+ total);
        return total;
    }

    /**
     * Find all unique delimiters in the input String. The delimiters should be present in the following patttern for this method to detect them.
     * "//<PipeSeparatedDelimiteList\n"
     * @param delimiterString - Input Delimiter String
     * @return - A {@link Set} of delimiters
     */
    private  Set<String> findAllDelimiters(String delimiterString) {

        /*Initialize some variables for further need*/
        //We want to order the delimiters in the descending order of their length. To avoid same character delimiters of muliple..
        //...length intervene with each other.
        Comparator<String> lenghtDescendingComparator = (String s1, String s2) ->
            {if(s1.length() > s2.length()) {
                return -1;
            }else if (s2.length() > s1.length()) {
                return 1;
            } else {
                return s1.compareTo(s2); //If both are of equal length, then no ordering is needed so fall back to natural order.
            }
        };
        Set<String>  delimiters = new TreeSet<>(lenghtDescendingComparator);
        String embeddedDelimStr = null;

        /*Find the embedded delimiter string between '//' and '\n'.*/
        Pattern pat = Pattern.compile(regexForCustomDelimitersOnly, Pattern.DOTALL);
        Matcher mat = pat.matcher(delimiterString);

        if(mat.matches()) {
            embeddedDelimStr = mat.group(2);
            //Extract '|' separated delimiters and add to a Set.
            String[] delimiterArray = embeddedDelimStr.split("\\|");
            logger.debug("Delimiters are...");
            Arrays.stream(delimiterArray).forEach((s) -> logger.debug(s));
            delimiters.addAll(Arrays.asList(delimiterArray));

        }
        return delimiters;
    }

    /**
     * Splits an inputString based on a a set of delimiters and returns the resulting elements in a {@link List<Integer>}
     * @param delimiterArray - Input array containing delimiters. Must be non-null.
     * @param numbersStr - Input String of numbers (Delimited by one or more of ebove delimiters)
     * @param listOfNumbers - This is populated as the numbersStr is split recursively to fetch the embedded integers.
     */
    private void splitUsingDelimiters(String[] delimiterArray, String numbersStr,  List<Integer> listOfNumbers) {

        // If the inputString is Empty then do nothing. This may happen sometimes during the recursive call
        if (numbersStr.isEmpty()) {
            return;
        }


        //Check for invalid cases such in which the string ends with a delimiter.
        for (String delim : delimiterArray) {
            if (numbersStr.endsWith(delim)) {
                throw new IllegalArgumentException("The input String is not valid, Ends with a delimiter.");
            }
        }

        //Split the numbersStr using first delimiter
        String[] arr = numbersStr.split(Pattern.quote(delimiterArray[0]));

        //Iterate over each element of the resulting split array
        for (String s : arr) {
            if (s.matches("-?\\d+")) { //Matches either a positive or negative number. If the element is an Integer, add to the output list;
                int element = Integer.parseInt(s);
                listOfNumbers.add(element);
            }  else { // If element is not a number, then send the element for further parsing using rest of the delimiters.
                String[] dest_arr = new String[delimiterArray.length -1];
                if (dest_arr.length >0) {
                    //Create an array of delimiters barring the one which is already processed above.
                    System.arraycopy(delimiterArray, 1, dest_arr, 0, delimiterArray.length - 1);
                    //call split(..) again
                    splitUsingDelimiters(dest_arr, s, listOfNumbers);
                } else  {
                    throw new IllegalArgumentException("Unknown Delimiters are present. Cannot Parse.");
                }
            }
        }

        /*If the count of the numbers inside numberStr is more than maxCountAllowed then throw an exception.
        However if the maxCountAllowed is 0 then we can process any amount of numbers (within the limits of memory!).*/
        if (maxCountAllowed != 0 && listOfNumbers.size() > maxCountAllowed) {
            throw new IllegalArgumentException("The string has more than "+ maxCountAllowed + " numbers embedded");
        }
    }

    /*
    This is a simple utility function which converts a Set of Strings into a corresponding array.
     */
    private String[] convertSetToArray(Set<String> inputSet) {

        int inputSetSize = inputSet.size();
        String[] arr = new String[inputSetSize];

        // Copying contents of s to arr[]
        System.arraycopy(inputSet.toArray(), 0, arr, 0, inputSetSize);
        return arr;
    }

    public int getMaxCountAllowed() {
        return maxCountAllowed;
    }

    public static void main (String[] args) {
        StringAccumulator stringAccumulator = new StringAccumulator(6);
        String unescapedString = StringEscapeUtils.unescapeJava(args[0]);
        int total = stringAccumulator.add(unescapedString);
        logger.info("The total for input string: \"" + args[0] + "\" is :"+ total);
    }

    public StringAccumulator(int maxCountAllowed) {
        this.maxCountAllowed = maxCountAllowed;
    }
}



