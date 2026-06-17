package com.stockpilot.backend.shared.exception;

public class SelfDeactivationNotAllowedException extends RuntimeException {

    public SelfDeactivationNotAllowedException() {
        super("Owners cannot deactivate their own account");
    }
}
