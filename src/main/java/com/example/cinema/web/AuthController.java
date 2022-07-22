package com.example.cinema.web;

import com.example.cinema.config.security.jwt.JwtUtils;
import com.example.cinema.model.dto.AuthenticationRequestDto;
import com.example.cinema.model.dto.UserDto;
import com.example.cinema.model.entities.User;
import com.example.cinema.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public UserDto signup(@Valid @RequestBody UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        user = userService.saveClient(user);

        return modelMapper.map(user, UserDto.class);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AuthenticationRequestDto request) {
        // Authenticate user. It will throw AuthenticationExc if credentials are invalid
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // Build jwt token
        String token = JwtUtils.buildToken(authentication);

        // Find user by email
        String username = authentication.getName();
        User user = userService.getUser(username);

        UserDto userDto = modelMapper.map(user, UserDto.class);

        // return response entity containing user and authentication token
        Map<String, Object> response = Map.of("user", userDto, "jwt", token);
        return ResponseEntity.ok().body(response);
    }
}
