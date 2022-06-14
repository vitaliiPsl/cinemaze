package com.example.cinema.model.errors;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ApiError {
    // Request status
    private HttpStatus status;

    // User friendly error message
    private String message;

    // Error information in details
    private String debugMessage;

    // Time when the error happened
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();

    // List of subErrors. Represent errors in single request
    private List<ApiSubError> subErrors = new ArrayList<>();

    public ApiError() {
    }

    public ApiError(HttpStatus status) {
        this.status = status;
    }

    public ApiError(HttpStatus status, Throwable throwable) {
        this.status = status;
        this.message = "Unexpected error";
        this.debugMessage = throwable.getLocalizedMessage();
    }

    public ApiError(HttpStatus status, String message, Throwable throwable) {
        this.status = status;
        this.message = message;
        this.debugMessage = throwable.getLocalizedMessage();
    }

    private void addSubError(ApiSubError error) {
        subErrors.add(error);
    }

    private void addValidationError(String object, String field, Object rejectedValue, String message) {
        addSubError(new ValidationError(object, field, rejectedValue, message));
    }

    private void addValidationError(String object, String message) {
        addSubError(new ValidationError(object, message));
    }

    private void addValidationError(FieldError fieldError) {
        this.addValidationError(fieldError.getObjectName(), fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage());
    }

    public void addValidationErrors(List<FieldError> fieldErrors) {
        fieldErrors.forEach(this::addValidationError);
    }
}
