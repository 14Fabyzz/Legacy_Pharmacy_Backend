package com.legacy.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Esto le dice a Spring: "Oye, captura los errores de TODOS los controladores aquí"
public class GlobalExceptionHandler {

    // 1. CAPTURAR ERRORES DE VALIDACIÓN (@Valid)
    // Se activa cuando mandan un JSON con campos vacíos o precios negativos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Error de Validación");

        Map<String, String> erroresCampo = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            erroresCampo.put(error.getField(), error.getDefaultMessage());
        }

        response.put("detalles", erroresCampo);

        return ResponseEntity.badRequest().body(response);
    }

    // 2. CAPTURAR ERRORES DE LÓGICA (RuntimeException)
    // Se activa cuando lanzamos "throw new RuntimeException('Producto no encontrado')"
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeErrors(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value()); // O Not Found si prefieres
        response.put("error", "Error del Sistema");
        response.put("mensaje", ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    // 3. CAPTURAR CUALQUIER OTRO ERROR (GENÉRICO)
    // El último recurso por si pasa algo inesperado (NullPointer, Base de datos caída, etc.)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralErrors(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Error Interno Inesperado");
        response.put("mensaje", "Por favor contacte al administrador");
        response.put("debug_mensaje", ex.getMessage()); // Solo para desarrollo

        return ResponseEntity.internalServerError().body(response);
    }
}