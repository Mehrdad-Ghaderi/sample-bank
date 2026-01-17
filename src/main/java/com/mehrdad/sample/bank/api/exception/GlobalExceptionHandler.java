package com.mehrdad.sample.bank.api.exception;


import com.mehrdad.sample.bank.core.exception.ClientAlreadyActiveException;
import com.mehrdad.sample.bank.core.exception.ClientAlreadyInactiveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientAlreadyActiveException.class)
    public ResponseEntity<Map<String, Object>> handleClientAlreadyActive(
            ClientAlreadyActiveException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "timestamp", Instant.now(),
                        "error", "CLIENT_ALREADY_ACTIVE",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(ClientAlreadyInactiveException.class)
    public ResponseEntity<Map<String, Object>> handleClientAlreadyInactive(
            ClientAlreadyInactiveException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                "timestamp", Instant.now(),
                "error", "CLIENT_ALREADY_ACTIVE",
                "message", ex.getMessage()
        ));
    }
}