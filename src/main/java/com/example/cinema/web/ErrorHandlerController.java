package com.example.cinema.web;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.exceptions.UnsupportedImageTypeException;
import com.example.cinema.exceptions.UserAlreadyExistsException;
import com.example.cinema.model.errors.ApiError;
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

@RestControllerAdvice
public class ErrorHandlerController {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(BadCredentialsException e) {
        String error = "Invalid username or password";
        return buildResponseEntity(new ApiError(HttpStatus.FORBIDDEN, error, e));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException e) {
        String error = "JWT token cannot be trusted";
        return buildResponseEntity(new ApiError(HttpStatus.FORBIDDEN, error, e));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String error = "Malformed JSON request";
        return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    protected ResponseEntity<ApiError> handleUserAlreadyExistsException(UserAlreadyExistsException e){
        return buildResponseEntity(new ApiError(BAD_REQUEST, e.getMessage(), e));
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ApiError> handleMethodArgumentNotValid(BindException ex) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage("Validation error");
        apiError.setDebugMessage(ex.getMessage());
        apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException e) {
        return buildResponseEntity(new ApiError(NOT_FOUND, e.getMessage(), e));
    }

        @ExceptionHandler(AmazonS3Exception.class)
    protected ResponseEntity<ApiError> handleAmazonS3Exception(AmazonS3Exception e){
        ApiError apiError = new ApiError(NOT_FOUND, "Couldn't find the image", e);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(UnsupportedImageTypeException.class)
    protected ResponseEntity<ApiError> handleUnsupportedImageTypeException(UnsupportedImageTypeException e) {
        return buildResponseEntity(new ApiError(BAD_REQUEST, e.getMessage(), e));
    }

    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
