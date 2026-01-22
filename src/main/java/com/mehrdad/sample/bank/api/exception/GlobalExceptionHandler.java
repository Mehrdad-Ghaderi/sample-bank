package com.mehrdad.sample.bank.api.exception;


import com.mehrdad.sample.bank.api.error.ApiError;
import com.mehrdad.sample.bank.core.exception.CustomerAlreadyActiveException;
import com.mehrdad.sample.bank.core.exception.CustomerAlreadyExistException;
import com.mehrdad.sample.bank.core.exception.CustomerAlreadyInactiveException;
import com.mehrdad.sample.bank.core.exception.CustomerNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerAlreadyActiveException.class)
    public ResponseEntity<ApiError> handleCustomerAlreadyActive(
            CustomerAlreadyActiveException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "Customer_ALREADY_ACTIVE",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(CustomerAlreadyInactiveException.class)
    public ResponseEntity<ApiError> handleCustomerAlreadyInactive(
            CustomerAlreadyInactiveException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "Customer_ALREADY_INACTIVE",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiError> handleCustomerNotFound(
            CustomerNotFoundException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "Customer_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(CustomerAlreadyExistException.class)
    public ResponseEntity<ApiError> handleCustomerExist(
            CustomerAlreadyExistException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "Customer_ALREADY_EXIST",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }
}