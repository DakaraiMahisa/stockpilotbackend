package com.stockpilot.backend.shared.exception;

public class InvitationAlreadyUsedException extends RuntimeException {

    public InvitationAlreadyUsedException() {
        super("Invitation has already been used");
    }
}