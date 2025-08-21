package com.loopers.support.error;

public class RetryableBusinessException extends RuntimeException {

    public RetryableBusinessException(String message) {
        super(message);
    }

    public RetryableBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

}
