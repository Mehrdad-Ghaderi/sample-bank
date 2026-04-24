package com.mehrdad.sample.bank.api.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.mehrdad.sample.bank.api.error.ProblemDetailsFactory;
import com.mehrdad.sample.bank.domain.exception.ConcurrentUpdateException;
import com.mehrdad.sample.bank.domain.exception.account.AccountNotActiveException;
import com.mehrdad.sample.bank.domain.exception.account.AccountNotFoundException;
import com.mehrdad.sample.bank.domain.exception.account.AccountStatusAlreadySetException;
import com.mehrdad.sample.bank.domain.exception.customer.*;
import com.mehrdad.sample.bank.domain.exception.transaction.CurrencyMismatchException;
import com.mehrdad.sample.bank.domain.exception.transaction.IdempotencyKeyConflictException;
import com.mehrdad.sample.bank.domain.exception.transaction.IllegalTransactionTypeException;
import com.mehrdad.sample.bank.domain.exception.transaction.InsufficientBalanceException;
import com.mehrdad.sample.bank.domain.exception.transaction.InvalidIdempotencyKeyException;
import com.mehrdad.sample.bank.domain.exception.transaction.InvalidAmountException;
import com.mehrdad.sample.bank.domain.exception.user.UserAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ProblemDetailsFactory problemDetailsFactory;

    public GlobalExceptionHandler(ProblemDetailsFactory problemDetailsFactory) {
        this.problemDetailsFactory = problemDetailsFactory;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        log.warn("Data integrity violation on {}", request.getRequestURI(), ex);

        return problem(
                HttpStatus.CONFLICT,
                "DATA_INTEGRITY_VIOLATION",
                "Data conflict",
                "Request conflicts with existing data.",
                request
        );
    }

    @ExceptionHandler(CustomerAlreadyActiveException.class)
    public ResponseEntity<ProblemDetail> handleCustomerAlreadyActive(
            CustomerAlreadyActiveException ex,
            HttpServletRequest request) {

        return problem(
                HttpStatus.CONFLICT,
                "CUSTOMER_ALREADY_ACTIVE",
                "Customer already active",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(CustomerAlreadyInactiveException.class)
    public ResponseEntity<ProblemDetail> handleCustomerAlreadyInactive(
            CustomerAlreadyInactiveException ex,
            HttpServletRequest request) {

        return problem(
                HttpStatus.CONFLICT,
                "CUSTOMER_ALREADY_INACTIVE",
                "Customer already inactive",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleCustomerNotFound(
            CustomerNotFoundException ex,
            HttpServletRequest request) {

        return problem(
                HttpStatus.NOT_FOUND,
                "CUSTOMER_NOT_FOUND",
                "Customer not found",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(CustomerAlreadyExistException.class)
    public ResponseEntity<ProblemDetail> handleCustomerExist(
            CustomerAlreadyExistException ex,
            HttpServletRequest request) {

        return problem(
                HttpStatus.CONFLICT,
                "CUSTOMER_ALREADY_EXIST",
                "Customer already exists",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<FieldViolation> violations = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new FieldViolation(err.getField(), err.getDefaultMessage()))
                .toList();

        ResponseEntity<ProblemDetail> response = problem(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_FAILED",
                "Validation failed",
                "Request validation failed.",
                request
        );

        response.getBody().setProperty("violations", violations);
        return response;
    }

    @ExceptionHandler(PhoneNumberAlreadyExists.class)
    public ResponseEntity<ProblemDetail> handlePhoneNumberAlreadyExists(
            PhoneNumberAlreadyExists ex,
            HttpServletRequest request
    ) {
        return problem(
                HttpStatus.CONFLICT,
                "PHONE_NUMBER_ALREADY_EXISTS",
                "Phone number already exists",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyExists(
            UserAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        return problem(
                HttpStatus.CONFLICT,
                "USER_ALREADY_EXISTS",
                "User already exists",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(InvalidPhoneNumberException.class)
    public ResponseEntity<ProblemDetail> handleInvalidPhoneNumber(
            InvalidPhoneNumberException ex,
            HttpServletRequest request
    ) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "INVALID_PHONE_NUMBER",
                "Invalid phone number",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleAccountNotFound(AccountNotFoundException ex,
                                                               HttpServletRequest request) {
        return problem(
                HttpStatus.NOT_FOUND,
                "ACCOUNT_NOT_FOUND",
                "Account not found",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(AccountNotActiveException.class)
    public ResponseEntity<ProblemDetail> handleAccountNotActive(AccountNotActiveException ex,
                                                                HttpServletRequest request) {
        return problem(
                HttpStatus.FORBIDDEN,
                "ACCOUNT_NOT_ACTIVE",
                "Account not active",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(AccountStatusAlreadySetException.class)
    public ResponseEntity<ProblemDetail> handleAccountAlreadyHasThatStatus(AccountStatusAlreadySetException ex,
                                                                           HttpServletRequest request) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "ACCOUNT_STATUS_ALREADY_SET",
                "Account status already set",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ProblemDetail> handleInsufficientBalance(InsufficientBalanceException ex,
                                                                   HttpServletRequest request) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "INSUFFICIENT_BALANCE",
                "Insufficient balance",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(CurrencyMismatchException.class)
    public ResponseEntity<ProblemDetail> handleCurrencyMismatchException(CurrencyMismatchException ex,
                                                                         HttpServletRequest request) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "CURRENCY_MISMATCH",
                "Currency mismatch",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ProblemDetail> handleInvalidAmountException(InvalidAmountException ex,
                                                                      HttpServletRequest request) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "INVALID_AMOUNT",
                "Invalid amount",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(IllegalTransactionTypeException.class)
    public ResponseEntity<ProblemDetail> handleIllegalTransactionTypeException(IllegalTransactionTypeException ex,
                                                                               HttpServletRequest request) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "ILLEGAL_TRANSACTION_TYPE",
                "Illegal transaction type",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(InvalidIdempotencyKeyException.class)
    public ResponseEntity<ProblemDetail> handleInvalidIdempotencyKey(InvalidIdempotencyKeyException ex,
                                                                     HttpServletRequest request) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "INVALID_IDEMPOTENCY_KEY",
                "Invalid idempotency key",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ProblemDetail> handleMissingRequestHeader(MissingRequestHeaderException ex,
                                                                    HttpServletRequest request) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "MISSING_REQUEST_HEADER",
                "Missing request header",
                "Missing required header: " + ex.getHeaderName(),
                request
        );
    }

    @ExceptionHandler(IdempotencyKeyConflictException.class)
    public ResponseEntity<ProblemDetail> handleIdempotencyKeyConflict(IdempotencyKeyConflictException ex,
                                                                      HttpServletRequest request) {
        return problem(
                HttpStatus.CONFLICT,
                "IDEMPOTENCY_KEY_CONFLICT",
                "Idempotency key conflict",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleInvalidJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        String message = "Malformed request body";
        String errorCode = "MALFORMED_REQUEST_BODY";
        String title = "Malformed request body";

        if (ex.getCause() instanceof InvalidFormatException ife
                && ife.getTargetType().isEnum()) {

            String fieldName = ife.getPath().getFirst().getFieldName();
            String invalidValue = String.valueOf(ife.getValue());

            message = String.format(
                    "Invalid value '%s' for field '%s'. Allowed values: %s",
                    invalidValue,
                    fieldName,
                    Arrays.toString(ife.getTargetType().getEnumConstants())
            );
            errorCode = "INVALID_ENUM_VALUE";
            title = "Invalid enum value";
        }

        return problem(
                HttpStatus.BAD_REQUEST,
                errorCode,
                title,
                message,
                request
        );
    }

    @ExceptionHandler(ConcurrentUpdateException.class)
    public ResponseEntity<ProblemDetail> handleOptimisticLock(ConcurrentUpdateException ex,
                                                              HttpServletRequest request) {

        return problem(
                HttpStatus.CONFLICT,
                "CONCURRENT_MODIFICATION",
                "Concurrent modification",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException ex,
                                                            HttpServletRequest request) {
        return problem(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "Access denied",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error on {}", request.getRequestURI(), ex);

        return problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Internal server error",
                "An unexpected error occurred.",
                request
        );
    }

    private ResponseEntity<ProblemDetail> problem(
            HttpStatus status,
            String errorCode,
            String title,
            String detail,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(status)
                .body(problemDetailsFactory.create(status, errorCode, title, detail, request));
    }

    private record FieldViolation(String field, String message) {
    }
}
