package com.budget.app.exception;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of("error", e.getMessage()));
    }

    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<String> handleGeneric(Exception e) {
    //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //             .body("Unexpected error");
    // }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {

        String message = "Duplicate value";

        if (ex.getMostSpecificCause() != null) {
            String cause = ex.getMostSpecificCause().getMessage();

            if (cause.contains("uq_username")) {
                message = "Username already exists";
            } else if (cause.contains("uq_email")) {
                message = "Email already exists";
            } else if (cause.contains("uq_phone_number")) {
                message = "Phone number already exists";
            } else if (cause.contains("uq_user_phone")) {
                message = "Phone number already exists";
            } else if (cause.contains("uq_user_budget")) {
                message = "Link for this user/budget already exists";
            }

            
        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", message));
    }
}
