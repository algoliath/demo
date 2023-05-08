package com.example.demo.domain.exception;

public class ForeignKeyException extends RuntimeException{

    public ForeignKeyException(String message) {
        super(message);
    }

    public ForeignKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForeignKeyException(Throwable cause) {
        super(cause);
    }

    protected ForeignKeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
