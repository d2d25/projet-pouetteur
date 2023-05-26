package com.pouetteur.authservice.service.exception;

public class UsernameAlreadyExistsException extends Exception {
    public UsernameAlreadyExistsException(String format) {
        super(format);
    }
}
