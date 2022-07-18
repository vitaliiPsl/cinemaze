package com.example.cinema.model.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class AuthenticationRequestDto {

    @Email(message = "You have to provide valid email address")
    @NotBlank(message = "You have to provide your email")
    private String email;

    @NotBlank(message = "You have to provide password")
    private String password;
}
