package com.demo.demoproject.exception;

public class SaveFailureException extends RuntimeException {
    public SaveFailureException(String message) { super(message); }
    public SaveFailureException(String message, Throwable cause) { super(message, cause); }
}
