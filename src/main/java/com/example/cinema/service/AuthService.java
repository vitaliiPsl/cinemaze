package com.example.cinema.service;

import com.example.cinema.config.security.jwt.JwtUtils;
import com.example.cinema.exceptions.EntityAlreadyExistsException;
import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.exceptions.InvalidRegistrationToken;
import com.example.cinema.model.dto.auth.AuthenticationRequestDto;
import com.example.cinema.model.dto.auth.AuthenticationResponseDto;
import com.example.cinema.model.entities.user.Role;
import com.example.cinema.model.entities.user.User;
import com.example.cinema.model.entities.user.token.RegistrationToken;
import com.example.cinema.persistence.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;

    private final RegistrationTokenService tokenService;
    private final EmailService emailService;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public RegistrationToken registerClient(User user, String tokenConfirmationUrl) {
        log.debug("register client with email: {}", user.getEmail());

        Optional<User> possibleUser = userRepository.findByEmail(user.getEmail());

        if (possibleUser.isPresent() && !possibleUser.get().isEnabled()) {
            log.debug("Generate new email confirmation token for user: {}", possibleUser.get());
            return sendRegistrationToken(possibleUser.get(), tokenConfirmationUrl);
        }

        if (possibleUser.isPresent()) {
            log.warn("User with email: '{}' already exists", user.getEmail());
            throw new EntityAlreadyExistsException(user.getEmail(), User.class);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.CLIENT);
        userRepository.save(user);

        return sendRegistrationToken(user, tokenConfirmationUrl);
    }

    public void confirmRegistration(String token) {
        log.debug("Confirm registration. Token: {}", token);

        RegistrationToken registrationToken = tokenService.getToken(token).orElseThrow(() -> new InvalidRegistrationToken(token));

        if (registrationToken.getConfirmedAt() != null) {
            log.error("Token already confirmed: {}", registrationToken);
            throw new IllegalStateException("Registration has already been confirmed");
        }

        if (registrationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.error("Token expired: {}", registrationToken);
            throw new IllegalStateException("Token has already expired");
        }

        User user = registrationToken.getUser();
        user.setEnabled(true);

        registrationToken.setConfirmedAt(LocalDateTime.now());
    }

    public AuthenticationResponseDto authenticateUser(AuthenticationRequestDto authRequest) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
        authentication = authenticationManager.authenticate(authentication);

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(email, User.class));

        String token = JwtUtils.buildToken(authentication);

        return new AuthenticationResponseDto(user, token);
    }

    private RegistrationToken sendRegistrationToken(User user, String confirmationUrl) {
        RegistrationToken token = generateRegistrationToken(user);

        String emailContent = getConfirmationEmailContent(confirmationUrl, token);

        emailService.sendEmailHtml("Email confirmation", emailContent, user.getEmail());

        return token;
    }

    private RegistrationToken generateRegistrationToken(User user) {
        String token = UUID.randomUUID().toString();
        RegistrationToken registrationToken = new RegistrationToken(token, user);

        return tokenService.saveToken(registrationToken);
    }

    private String getConfirmationEmailContent(String confirmationUrl, RegistrationToken token) {
        return "<div style=\"width: 100%;  display: flex; flex-direction: column; gap: 24px; align-items: center; font-size: 24px; color: #ffffff;\">" +
                "<h1>Follow this link to confirm you email: </h1>" +
                "<a target=\"_blank\" href=\"" + confirmationUrl + "?token=" + token.getToken() + "\">Confirm</a>" +
                "</div>";
    }
}
