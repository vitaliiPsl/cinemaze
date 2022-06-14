package com.example.cinema.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String email){
        super(String.format("Role with email: %s not found", email));
    }

    public UserNotFoundException(long userId){
        super(String.format("Role with id: %d not found", userId));
    }
}
