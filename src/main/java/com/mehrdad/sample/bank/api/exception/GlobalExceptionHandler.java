package com.mehrdad.sample.bank.api.exception;


import com.mehrdad.sample.bank.api.error.ApiErrorResponse;
import com.mehrdad.sample.bank.core.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //CUSTOMER ********************
    //         ********************

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        Throwable root = ex.getMostSpecificCause();

        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                "DATA_INTEGRITY_VIOLATION",
                root != null ? root.getMessage() : ex.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(CustomerAlreadyActiveException.class)
    public ResponseEntity<ApiErrorResponse> handleCustomerAlreadyActive(
            CustomerAlreadyActiveException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                "CUSTOMER_ALREADY_ACTIVE",
                ex.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(CustomerAlreadyInactiveException.class)
    public ResponseEntity<ApiErrorResponse> handleCustomerAlreadyInactive(
            CustomerAlreadyInactiveException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                "CUSTOMER_ALREADY_INACTIVE",
                ex.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCustomerNotFound(
            CustomerNotFoundException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "CUSTOMER_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(CustomerAlreadyExistException.class)
    public ResponseEntity<ApiErrorResponse> handleCustomerExist(
            CustomerAlreadyExistException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                "CUSTOMER_ALREADY_EXIST",
                ex.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String firstError = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Validation failed");

        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_FAILED",
                firstError,
                request.getRequestURI(),
                OffsetDateTime.now(ZoneOffset.UTC)
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(PhoneNumberAlreadyExists.class)
    public ResponseEntity<ApiErrorResponse> handlePhoneNumberAlreadyExists(
            PhoneNumberAlreadyExists ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                "PHONE_NUMBER_ALREADY_EXISTS",
                ex.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(CustomerNameAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleCustomerNameExists(
            CustomerNameAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                "CUSTOMER_NAME_ALREADY_EXISTS",
                ex.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidPhoneNumberException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidPhoneNumber(

            CustomerNameAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                "INVALID_PHONE_NUMBER",
                ex.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // ACCOUNT ********************
    //         ********************

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountNotFound(AccountNotFoundException ex,
                                                                  HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "ACCOUNT_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(apiErrorResponse);
    }
}