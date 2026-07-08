package com.stockpilot.backend.identity.exception;

public class InvitationAlreadyUsedException extends RuntimeException {

    public InvitationAlreadyUsedException() {
        super("Invitation has already been used");
    }
}