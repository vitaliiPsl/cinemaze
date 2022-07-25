package com.example.cinema.exceptions;

public class InvalidRegistrationToken extends RuntimeException {
    private static final String MESSAGE = "Invalid registration token: %s";

    public InvalidRegistrationToken(String token) {
        super(String.format(MESSAGE, token));
    }
}
