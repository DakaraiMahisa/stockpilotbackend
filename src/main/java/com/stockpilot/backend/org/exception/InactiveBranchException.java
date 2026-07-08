package com.stockpilot.backend.org.exception;

public class InactiveBranchException extends RuntimeException{
    public InactiveBranchException(String message) {
        super(message);
    }
}
