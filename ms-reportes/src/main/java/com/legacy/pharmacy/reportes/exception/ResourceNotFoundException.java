package com.legacy.pharmacy.reportes.exception;

/**
 * Excepción cuando no se encuentran recursos.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
