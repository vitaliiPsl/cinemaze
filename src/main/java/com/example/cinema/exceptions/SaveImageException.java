package com.example.cinema.exceptions;

public class SaveImageException extends RuntimeException {
    public SaveImageException(String imageName){
        super("Error saving image: " + imageName);
    }
}
