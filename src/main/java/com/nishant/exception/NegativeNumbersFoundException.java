package com.nishant.exception;

import java.util.stream.Stream;

public class NegativeNumbersFoundException extends RuntimeException{

    private static final String MESSAGE  = "negatives not allowed : ";

    public NegativeNumbersFoundException(String message) {
        super(MESSAGE + message, null, true, true);
    }


}
