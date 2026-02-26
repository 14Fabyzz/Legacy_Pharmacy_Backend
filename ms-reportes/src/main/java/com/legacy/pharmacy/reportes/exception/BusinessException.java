package com.legacy.pharmacy.reportes.exception;

/**
 * Excepción para errores de lógica de negocio.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
