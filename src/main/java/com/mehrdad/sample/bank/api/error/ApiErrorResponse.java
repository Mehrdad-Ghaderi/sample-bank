package com.mehrdad.sample.bank.api.error;

import java.time.OffsetDateTime;

public record ApiErrorResponse(
        int status,
        String errorCode,
        String message,
        String path,
        OffsetDateTime timestamp) {
}
