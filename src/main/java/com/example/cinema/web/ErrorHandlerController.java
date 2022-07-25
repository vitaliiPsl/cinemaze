package com.example.cinema.web;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.example.cinema.exceptions.EntityAlreadyExistsException;
import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.exceptions.InvalidRegistrationToken;
import com.example.cinema.exceptions.UnsupportedImageTypeException;
import com.example.cinema.model.errors.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class ErrorHandlerController {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(BadCredentialsException e) {
        log.error("handleAuthenticationException: {}", e.getMessage(), e);

        String error = "Invalid username or password";
        return buildResponseEntity(new ApiError(HttpStatus.FORBIDDEN, error, e));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException e) {
        log.error("handleAccessDenied: {}", e.getMessage(), e);

        String error = "JWT token cannot be trusted";
        return buildResponseEntity(new ApiError(HttpStatus.FORBIDDEN, error, e));
    }

    @ExceptionHandler(InvalidRegistrationToken.class)
    public ResponseEntity<ApiError> handleInvalidRegistrationToken(InvalidRegistrationToken e) {
        log.error("handleInvalidRegistrationToken: {}", e.getMessage(), e);

        String message = "Invalid registration token";
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, message, e));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.error("handleHttpMessageNotReadable: {}", e.getMessage(), e);

        String error = "Malformed JSON request";
        return buildResponseEntity(new ApiError(BAD_REQUEST, error, e));
    }

    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<ApiError> handleIllegalStateException(IllegalStateException e) {
        log.error("handleIllegalStateException: {}", e.getMessage(), e);

        return buildResponseEntity(new ApiError(BAD_REQUEST, e.getMessage(), e));
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    protected ResponseEntity<ApiError> handleEntityAlreadyExistsException(EntityAlreadyExistsException e) {
        log.error("handleEntityAlreadyExistsException: {}", e.getMessage(), e);

        return buildResponseEntity(new ApiError(BAD_REQUEST, e.getMessage(), e));
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ApiError> handleMethodArgumentNotValid(BindException e) {
        log.error("handleMessageArgNotValid: {}", e.getMessage(), e);

        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage("Validation error");
        apiError.setDebugMessage(e.getMessage());
        apiError.addValidationErrors(e.getBindingResult().getFieldErrors());

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("handleEntityNotFound: {}", e.getMessage(), e);

        return buildResponseEntity(new ApiError(NOT_FOUND, e.getMessage(), e));
    }

    @ExceptionHandler(AmazonS3Exception.class)
    protected ResponseEntity<ApiError> handleAmazonS3Exception(AmazonS3Exception e) {
        log.error("handleEntityAlreadyExists: {}", e.getMessage(), e);

        ApiError apiError = new ApiError(NOT_FOUND, "Couldn't find the image", e);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(UnsupportedImageTypeException.class)
    protected ResponseEntity<ApiError> handleUnsupportedImageTypeException(UnsupportedImageTypeException e) {
        log.error("handleUnsupportedImageTypeException: {}", e.getMessage(), e);

        return buildResponseEntity(new ApiError(BAD_REQUEST, e.getMessage(), e));
    }

    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
