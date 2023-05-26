package com.pouetteur.authservice.service.exception;

public class EmailAlreadyExistsException extends Exception {
    public EmailAlreadyExistsException(String format) {
        super(format);
    }
}
