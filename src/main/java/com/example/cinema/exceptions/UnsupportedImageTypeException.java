package com.example.cinema.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedImageTypeException extends RuntimeException {
    public UnsupportedImageTypeException(String imageType, String supportedTypes){
        super(String.format("Unsupported image type: %s. Supported types: %s", imageType, supportedTypes));
    }
}
