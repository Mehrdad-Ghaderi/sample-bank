package com.mehrdad.sample.bank.core.exception;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Created by Mehrdad Ghaderi, S&M
 * Date: 7/29/2025
 * Time: 11:32 PM
 */
public record ErrorDetails(LocalDateTime timestamp, String message, String details) {
}
