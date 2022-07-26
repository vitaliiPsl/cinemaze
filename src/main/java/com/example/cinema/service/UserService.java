package com.example.cinema.service;

import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.entities.user.User;
import com.example.cinema.persistence.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

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
