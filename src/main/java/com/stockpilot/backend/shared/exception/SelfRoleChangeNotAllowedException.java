package com.stockpilot.backend.shared.exception;

public class SelfRoleChangeNotAllowedException extends RuntimeException {

    public SelfRoleChangeNotAllowedException() {
        super("Owners cannot change their own role");
    }
}
