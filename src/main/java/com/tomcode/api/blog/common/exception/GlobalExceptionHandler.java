package com.tomcode.api.blog.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log =
          LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /* =========================
     404 – NOT FOUND
     ========================= */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(
          ResourceNotFoundException ex) {

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "code", "NOT_FOUND",
            "message", ex.getMessage()
    ));
  }

  /* =========================
     400 – INVALID PARAMETER
     ========================= */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Map<String, Object>> handleTypeMismatch(
          MethodArgumentTypeMismatchException ex) {

    String message = ex.getRequiredType() == java.util.UUID.class
            ? "Invalid ID format. Expected UUID"
            : "Invalid parameter value";

    return ResponseEntity.badRequest().body(Map.of(
            "code", "INVALID_PARAMETER",
            "message", message
    ));
  }

  /* =========================
     400 – VALIDATION ERROR
     ========================= */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationErrors(
          MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();

    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.put(error.getField(), error.getDefaultMessage());
    }

    return ResponseEntity.badRequest().body(Map.of(
            "code", "VALIDATION_ERROR",
            "message", "Validation error",
            "errors", errors
    ));
  }

  /* =========================
     409 – EMAIL ALREADY EXISTS
     ========================= */
  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<Map<String, Object>> handleEmailAlreadyExists(
          EmailAlreadyExistsException ex) {

    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
            "code", "EMAIL_ALREADY_EXISTS",
            "message", ex.getMessage()
    ));
  }

  /* =========================
     400 – BAD REQUEST
     ========================= */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(
          IllegalArgumentException ex) {

    return ResponseEntity.badRequest().body(Map.of(
            "code", "BAD_REQUEST",
            "message", ex.getMessage()
    ));
  }

  /* =========================
     401 – UNAUTHORIZED
     ========================= */
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<Map<String, Object>> handleUnauthorized(
          BadRequestException ex) {

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
            "code", "UNAUTHORIZED",
            "message", ex.getMessage()
    ));
  }

  /* =========================
     403 – ACCESS DENIED
     ========================= */
  @ExceptionHandler({
          ForbiddenException.class,
          AuthorizationDeniedException.class
  })
  public ResponseEntity<Map<String, Object>> handleAccessDenied(Exception ex) {

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
            "code", "ACCESS_DENIED",
            "message", "You do not have permission to perform this action"
    ));
  }

  /* =========================
     500 – INTERNAL ERROR
     ========================= */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneralException(
          Exception ex) {

    log.error("Unexpected error", ex);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "code", "INTERNAL_ERROR",
            "message", "Something went wrong. Try again later."
    ));
  }
}