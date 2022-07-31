package com.example.cinema.model.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthenticationRequestDto {

    @Email(message = "You have to provide valid email address")
    @NotBlank(message = "You have to provide your email")
    private String email;

    @NotBlank(message = "You have to provide password")
    private String password;
}
