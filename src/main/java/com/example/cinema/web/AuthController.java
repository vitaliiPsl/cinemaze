package com.example.cinema.web;

import com.example.cinema.config.security.jwt.JwtUtils;
import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.exceptions.UserAlreadyExistsException;
import com.example.cinema.model.dto.AuthenticationRequestDto;
import com.example.cinema.model.dto.UserDto;
import com.example.cinema.model.entities.User;
import com.example.cinema.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody User user){
        // throw exception if there already is user with provided email
        String email = user.getEmail();
        Optional<User> existing = userService.getUser(email);
        if(existing.isPresent()) {
            throw new UserAlreadyExistsException(email);
        }

        // encode password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userService.saveClient(user);

        UserDto userDto = new UserDto(user);
        return ResponseEntity.ok().body(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthenticationRequestDto request) {
        // Authenticate user. It will throw AuthenticationExc if credentials are invalid
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // Build jwt token
        String token = JwtUtils.buildToken(authentication);

        // Find user by email
        String username = authentication.getName();
        User user = userService.getUser(username).orElseThrow(() -> new EntityNotFoundException(username, User.class));
        UserDto userDto = new UserDto(user);

        // return response entity containing user and authentication token
        Map<String, Object> response = Map.of("user", userDto, "jwt", token);
        return ResponseEntity.ok().body(response);
    }
}
