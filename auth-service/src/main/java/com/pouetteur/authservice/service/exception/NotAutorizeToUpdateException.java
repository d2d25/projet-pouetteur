package com.pouetteur.authservice.service.exception;

public class NotAutorizeToUpdateException extends Exception {
    public NotAutorizeToUpdateException(String format) {
        super(format);
    }
}
