package com.example.cinema.service;

import com.example.cinema.exceptions.EntityAlreadyExistsException;
import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.entities.Role;
import com.example.cinema.model.entities.User;
import com.example.cinema.persistence.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public User saveClient(User user) {
        log.debug("register client with email: {}", user.getEmail());

        Optional<User> possibleUser = userRepository.findByEmail(user.getEmail());
        if (possibleUser.isPresent()) {
            log.warn("user with email: '{}' already exists", user.getEmail());
            throw new EntityAlreadyExistsException(user.getEmail(), User.class);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.CLIENT);

        return userRepository.save(user);
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
}
