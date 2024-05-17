package com.toolrental.demo.exception;

/**
 * Custom exception for invalid numeric range.
 */
public class InvalidNumericRangeException extends RuntimeException {

    public InvalidNumericRangeException(String message) {
        super(message);
    }

}
