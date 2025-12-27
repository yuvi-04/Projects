package com.demo.demoproject.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.demo.demoproject.dto.ApiError;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger errorLogger = LoggerFactory.getLogger("ERROR_LOGGER");

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        errorLogger.warn("[NOT_FOUND] path={} msg={}", req.getRequestURI(), ex.getMessage());
        ApiError err = new ApiError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleForbidden(UnauthorizedException ex, HttpServletRequest req) {
        errorLogger.warn("[UNAUTHORIZED] path={} msg={}", req.getRequestURI(), ex.getMessage());
        ApiError err = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
        errorLogger.warn("[BAD REQUEST] path={} msg={}", req.getRequestURI(), ex.getMessage());
        ApiError err = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(SaveFailureException.class)
    public ResponseEntity<ApiError> handleSaveFailure(SaveFailureException ex, HttpServletRequest req) {
        errorLogger.error("[SAVE_FAILURE] path={} msg={}", req.getRequestURI(), ex.getMessage(), ex);
        ApiError err = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Save Failure", ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        errorLogger.warn("[ACCESS_DENIED] path={} msg={}", req.getRequestURI(), ex.getMessage());
        ApiError err = new ApiError(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(err);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex, HttpServletRequest req) {
        errorLogger.warn("[AUTHENTICATION_FAILURE] path={} msg={}", req.getRequestURI(), ex.getMessage());
        ApiError err = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> handleJwtException(JwtException ex, HttpServletRequest req) {
        errorLogger.warn("[JWT_EXCEPTION] path={} msg={}", req.getRequestURI(), ex.getMessage());
        ApiError err = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), "Invalid or expired token", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex, HttpServletRequest req) {
        errorLogger.error("[SERVER ERROR] path={} msg={}", req.getRequestURI(), ex.getMessage());
        ApiError err = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal server error: " + ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}
