package com.example.cinema.web;

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

@RestControllerAdvice
public class ErrorHandlerController {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleAuthenticationException(BadCredentialsException e) {
        String error = "Invalid username or password";
        return buildResponseEntity(new ApiError(HttpStatus.FORBIDDEN, error, e));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e) {
        String error = "JWT token cannot be trusted";
        return buildResponseEntity(new ApiError(HttpStatus.FORBIDDEN, error, e));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String error = "Malformed JSON request";
        return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    protected ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException e){
        return buildResponseEntity(new ApiError(BAD_REQUEST, e.getMessage(), e));
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(BindException ex) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage("Validation error");
        apiError.setDebugMessage(ex.getMessage());
        apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());

        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
