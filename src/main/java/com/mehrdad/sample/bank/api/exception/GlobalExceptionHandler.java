package com.mehrdad.sample.bank.api.exception;


import com.mehrdad.sample.bank.api.error.ApiError;
import com.mehrdad.sample.bank.core.exception.ClientAlreadyActiveException;
import com.mehrdad.sample.bank.core.exception.ClientAlreadyInactiveException;
import com.mehrdad.sample.bank.core.exception.ClientNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientAlreadyActiveException.class)
    public ResponseEntity<ApiError> handleClientAlreadyActive(
            ClientAlreadyActiveException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "CLIENT_ALREADY_ACTIVE",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(ClientAlreadyInactiveException.class)
    public ResponseEntity<ApiError> handleClientAlreadyInactive(
            ClientAlreadyInactiveException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "CLIENT_ALREADY_INACTIVE",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ApiError> handleClientNotFound(
            ClientNotFoundException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "CLIENT_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }
}