package com.loopers.support.error;

public class PgServiceRetryException extends RuntimeException {

    public PgServiceRetryException(String message) {
        super(message);
    }

    public PgServiceRetryException(String message, Throwable cause) {
        super(message, cause);
    }
}
