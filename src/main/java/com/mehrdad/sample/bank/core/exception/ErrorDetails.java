package com.mehrdad.sample.bank.core.exception;

import java.time.LocalDateTime;

/**
 * Created by Mehrdad Ghaderi, S&M
 * Date: 7/29/2025
 * Time: 11:32 PM
 */
public class ErrorDetails {

    private LocalDateTime timestamp;
    private String message;
    private String details;

    public ErrorDetails(LocalDateTime timestamp, String message, String details) {
        super();
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}
