package com.example.cinema.model.dto;

import com.example.cinema.model.entities.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDto {
    private User user;
    private String jwt;
}
