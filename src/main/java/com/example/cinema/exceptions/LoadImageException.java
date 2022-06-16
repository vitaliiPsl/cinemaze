package com.example.cinema.exceptions;


public class LoadImageException extends RuntimeException{
    public LoadImageException(String imageName){
        super("Error loading image: " + imageName);
    }
}
