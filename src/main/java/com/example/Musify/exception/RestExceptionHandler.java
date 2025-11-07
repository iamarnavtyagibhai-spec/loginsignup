package com.example.Musify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "error", ex.getMessage() != null ? ex.getMessage() : "Unauthorized"
                ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", ex.getMessage() != null ? ex.getMessage() : "Bad request"
                ));
    }

    @ExceptionHandler(java.io.IOException.class)
    public ResponseEntity<Map<String, Object>> handleIOException(java.io.IOException ex) {
        String errorMessage = ex.getMessage();
        // Check if it's a Cloudinary signature error
        if (errorMessage != null && errorMessage.contains("Invalid Signature")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Cloudinary authentication failed. Please check your API credentials in application.yml. Make sure the API Secret is complete and correct.",
                            "details", errorMessage
                    ));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", errorMessage != null ? errorMessage : "File upload failed"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", ex.getMessage() != null ? ex.getMessage() : "Internal server error"
                ));
    }
}


