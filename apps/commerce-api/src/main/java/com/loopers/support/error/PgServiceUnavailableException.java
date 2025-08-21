package com.loopers.support.error;

public class PgServiceUnavailableException extends RuntimeException {

    public PgServiceUnavailableException(String message) {
        super(message);
    }

    public PgServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
