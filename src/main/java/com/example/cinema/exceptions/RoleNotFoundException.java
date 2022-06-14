package com.example.cinema.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RoleNotFoundException extends RuntimeException{
    public RoleNotFoundException(String role){
        super(String.format("Role: %s not found", role));
    }

    public RoleNotFoundException(long roleId){
        super(String.format("Role: %d not found", roleId));
    }
}
