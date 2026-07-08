package com.stockpilot.backend.identity.exception;

public class SelfRoleChangeNotAllowedException extends RuntimeException {

    public SelfRoleChangeNotAllowedException() {
        super("You cannot change your own role");
    }
}
