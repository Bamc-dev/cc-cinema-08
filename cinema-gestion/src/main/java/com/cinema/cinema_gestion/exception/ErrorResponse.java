package com.cinema.cinema_gestion.exception;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp,
        String path,
        Map<String, String> fieldErrors) {

    public static ErrorResponse of(HttpStatus httpStatus, String message, String path) {
        return new ErrorResponse(
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                message,
                Instant.now(),
                path,
                null);
    }

    public static ErrorResponse of(HttpStatus httpStatus, String message, String path, Map<String, String> fieldErrors) {
        return new ErrorResponse(
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                message,
                Instant.now(),
                path,
                fieldErrors);
    }
}
