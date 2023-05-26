package com.pouetteur.authservice.service.exception;

public class NotFoundException extends Exception {
    public NotFoundException(String format) {
        super(format);
    }
}
