package com.stockpilot.backend.shared.exception;

public class InactiveBranchException extends RuntimeException{
    public InactiveBranchException(String message) {
        super(message);
    }
}
