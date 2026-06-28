package com.stockpilot.backend.common.utils;

import java.time.Duration;

public final class JwtTestConstants {

    private JwtTestConstants() {
    }

    public static final String TEST_SECRET =
            "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    public static final Duration TOKEN_EXPIRATION =
            Duration.ofMinutes(15);

}