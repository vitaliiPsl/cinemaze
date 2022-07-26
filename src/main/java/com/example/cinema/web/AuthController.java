package com.example.cinema.web;

import com.example.cinema.model.dto.AuthenticationRequestDto;
import com.example.cinema.model.dto.AuthenticationResponseDto;
import com.example.cinema.model.dto.UserDto;
import com.example.cinema.model.entities.user.User;
import com.example.cinema.model.entities.user.token.RegistrationToken;
import com.example.cinema.service.AuthService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final ModelMapper modelMapper;
    private final AuthService authService;

    @PostMapping("/signup")
    public RegistrationToken signup(@Valid @RequestBody UserDto userDto) {
        String host = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String tokenConfirmationUrl = host + "/api/auth/confirm";

        User user = modelMapper.map(userDto, User.class);

        return authService.registerClient(user, tokenConfirmationUrl);
    }

    @PostMapping("/login")
    public AuthenticationResponseDto login(@Valid @RequestBody AuthenticationRequestDto request) {
        return authService.authenticateUser(request);
    }

    @GetMapping("/confirm")
    public void confirmToken(@RequestParam("token") String token) {
        authService.confirmRegistration(token);
    }
}
