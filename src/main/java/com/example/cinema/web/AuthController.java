package com.example.cinema.web;

import com.example.cinema.config.security.jwt.JwtUtils;
import com.example.cinema.model.dto.AuthenticationRequestDto;
import com.example.cinema.model.dto.UserDto;
import com.example.cinema.model.entities.user.User;
import com.example.cinema.model.entities.user.token.RegistrationToken;
import com.example.cinema.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public RegistrationToken signup(@Valid @RequestBody UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);

        return userService.registerClient(user);
    }

    @PostMapping("/confirm")
    public void confirmToken(@RequestParam("token") String token) {
        userService.confirmRegistrationToken(token);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AuthenticationRequestDto request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        String token = JwtUtils.buildToken(authentication);

        String username = authentication.getName();
        User user = userService.getUser(username);

        UserDto userDto = modelMapper.map(user, UserDto.class);

        Map<String, Object> response = Map.of("user", userDto, "jwt", token);
        return ResponseEntity.ok().body(response);
    }
}
