package com.stockpilot.backend.shared.exception;

public class InvalidInvitationTokenException extends RuntimeException {

    public InvalidInvitationTokenException() {
        super("Invitation link is invalid or has expired");
    }
}