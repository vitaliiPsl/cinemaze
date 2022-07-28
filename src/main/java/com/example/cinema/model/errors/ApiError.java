package com.example.cinema.model.errors;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ApiError {
    @Schema(title = "Response status", example = "404")
    private HttpStatus status;

    @Schema(title = "User-friendly error message", example = "Validation error")
    private String message;

    @Schema(title = "Error information in details", description = "Stack trace")
    private String debugMessage;

    @Schema(title = "Time when the error happened", example = "2022-07-31T15:55:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Schema(
            title = "List of sub errors",
            description = "Represents errors in single request",
            implementation = ValidationError.class
    )
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
