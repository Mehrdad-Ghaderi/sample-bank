package com.mehrdad.sample.bank.api.exception;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.mehrdad.sample.bank.api.error.ApiErrorResponse;
import com.mehrdad.sample.bank.core.exception.ConcurrentUpdateException;
import com.mehrdad.sample.bank.core.exception.account.AccountNotActiveException;
import com.mehrdad.sample.bank.core.exception.account.AccountNotFoundException;
import com.mehrdad.sample.bank.core.exception.account.AccountStatusAlreadySetException;
import com.mehrdad.sample.bank.core.exception.customer.*;
import com.mehrdad.sample.bank.core.exception.transaction.CurrencyMismatchException;
import com.mehrdad.sample.bank.core.exception.transaction.IllegalTransactionTypeException;
import com.mehrdad.sample.bank.core.exception.transaction.InsufficientBalanceException;
import com.mehrdad.sample.bank.core.exception.transaction.InvalidAmountException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

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
                root.getMessage(),
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

            InvalidPhoneNumberException ex,
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

    /* ===================
           ACCOUNT
       =================== */

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


    @ExceptionHandler(AccountNotActiveException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountNotFound(AccountNotActiveException ex,
                                                                  HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "ACCOUNT_NOT_ACTIVE",
                ex.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(apiErrorResponse);
    }

    @ExceptionHandler(AccountStatusAlreadySetException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountAlreadyHasThatStatus(AccountStatusAlreadySetException ex,
                                                                              HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "ACCOUNT_STATUS_ALREADY_SET",
                ex.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiErrorResponse);
    }


    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex,
                                                                      HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "INSUFFICIENT_BALANCE",
                ex.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiErrorResponse);
    }

    @ExceptionHandler(CurrencyMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleCurrencyMismatchException(CurrencyMismatchException ex,
                                                                            HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "CURRENCY_MISMATCH",
                ex.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiErrorResponse);
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidAmountException(InvalidAmountException ex,
                                                                         HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_AMOUNT",
                ex.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiErrorResponse);
    }

    @ExceptionHandler(IllegalTransactionTypeException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalTransactionTypeException(IllegalTransactionTypeException ex,
                                                                                  HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "ILLEGAL_TRANSACTION_TYPE",
                ex.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiErrorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidEnum(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        String message = "Malformed request body";

        if (ex.getCause() instanceof InvalidFormatException ife
                && ife.getTargetType().isEnum()) {

            String fieldName = ife.getPath().getFirst().getFieldName();
            String invalidValue = ife.getValue().toString();

            message = String.format(
                    "Invalid value '%s' for field '%s'. Allowed values: %s",
                    invalidValue,
                    fieldName,
                    Arrays.toString(ife.getTargetType().getEnumConstants())
            );
        }

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_TRANSACTION_TYPE_VALUE",
                message,
                request.getRequestURI(),
                OffsetDateTime.now()
        );

        return ResponseEntity.badRequest().body(response);
    }

    /* ===================
           CONCURRENCY
       =================== */

    @ExceptionHandler(ConcurrentUpdateException.class)
    public ResponseEntity<ApiErrorResponse> handleOptimisticLock(ConcurrentUpdateException ex,
                                                 HttpServletRequest request) {

        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                "CONCURRENT_MODIFICATION",
                ex.getMessage(),
                request.getRequestURI(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(apiErrorResponse);
    }
}