package com.example.cinema.model.errors;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationError extends ApiSubError{

    @Schema(title = "Name of the request object", example = "movieDto")
    private String object;

    @Schema(title = "Object field", example = "duration")
    private String field;

    @Schema(title = "Rejected value", example = "-10")
    private Object rejectedValue;

    @Schema(title = "Validation message", example = "Movie duration must be longer than 1 minute")
    private String message;

    public ValidationError(String object, String message) {
        this.object = object;
        this.message = message;
    }
}
