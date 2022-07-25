package com.example.cinema.service;

import com.example.cinema.exceptions.EntityAlreadyExistsException;
import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.exceptions.InvalidRegistrationToken;
import com.example.cinema.model.entities.user.Role;
import com.example.cinema.model.entities.user.User;
import com.example.cinema.model.entities.user.token.RegistrationToken;
import com.example.cinema.persistence.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final RegistrationTokenService tokenService;

    public RegistrationToken registerClient(User user) {
        log.debug("register client with email: {}", user.getEmail());

        Optional<User> possibleUser = userRepository.findByEmail(user.getEmail());

        if (possibleUser.isPresent() && !possibleUser.get().isEnabled()) {
            log.debug("Generate new email confirmation token for user: {}", possibleUser.get());
            return generateRegistrationToken(possibleUser.get());
        }

        if (possibleUser.isPresent()) {
            log.warn("User with email: '{}' already exists", user.getEmail());
            throw new EntityAlreadyExistsException(user.getEmail(), User.class);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.CLIENT);
        userRepository.save(user);

        return generateRegistrationToken(user);
    }

    public void confirmRegistrationToken(String token) {
        log.debug("Confirm registration token: {}", token);

        RegistrationToken registrationToken = tokenService.getToken(token).orElseThrow(() -> new InvalidRegistrationToken(token));

        if (registrationToken.getConfirmedAt() != null) {
            log.error("Token already confirmed: {}", registrationToken);
            throw new IllegalStateException("Token has already been confirmed");
        }

        if (registrationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.error("Token expired: {}", registrationToken);
            throw new IllegalStateException("Token has already expired");
        }

        User user = registrationToken.getUser();
        user.setEnabled(true);

        registrationToken.setConfirmedAt(LocalDateTime.now());
    }

    public void deleteUser(long id) {
        log.debug("delete user by id: {}", id);

        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, User.class));
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public User getUser(long id) {
        log.debug("get user by id: {}", id);

        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, User.class));
    }

    @Transactional(readOnly = true)
    public User getUser(String email) {
        log.debug("get user by email: {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(email, User.class));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.debug("get all users");

        return userRepository.findAll();
    }

    private RegistrationToken generateRegistrationToken(User user) {
        String token = UUID.randomUUID().toString();
        RegistrationToken registrationToken = new RegistrationToken(token, user);

        tokenService.saveToken(registrationToken);

        return registrationToken;
    }
}
