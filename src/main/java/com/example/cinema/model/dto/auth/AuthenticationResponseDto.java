package com.example.cinema.model.dto.auth;

import com.example.cinema.model.entities.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDto {

    @JsonIgnoreProperties("password")
    private User user;

    private String jwt;
}
