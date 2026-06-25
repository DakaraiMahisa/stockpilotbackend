package com.stockpilot.backend.shared.exception;

public class SelfRoleChangeNotAllowedException extends RuntimeException {

    public SelfRoleChangeNotAllowedException() {
        super("You cannot change your own role");
    }
}
