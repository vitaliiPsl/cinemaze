package com.example.cinema.service;

import com.example.cinema.model.entities.user.token.RegistrationToken;
import com.example.cinema.persistence.RegistrationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class RegistrationTokenService {
    private final RegistrationTokenRepository tokenRepository;

    public RegistrationToken saveToken(RegistrationToken token) {
        log.debug("Save registration token: {}", token);

        return tokenRepository.save(token);
    }

    @Transactional(readOnly = true)
    public Optional<RegistrationToken> getToken(String token) {
        log.debug("Get token: {}", token);

        return tokenRepository.findByToken(token);
    }
}
