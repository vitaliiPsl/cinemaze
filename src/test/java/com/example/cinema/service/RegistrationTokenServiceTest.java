package com.example.cinema.service;

import com.example.cinema.model.entities.user.token.RegistrationToken;
import com.example.cinema.persistence.RegistrationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationTokenServiceTest {

    @Mock
    RegistrationTokenRepository tokenRepository;

    @InjectMocks
    RegistrationTokenService tokenService;

    @Test
    void testSaveToken() {
        // given
        String token = "token";
        RegistrationToken registrationToken = new RegistrationToken(token, null);

        // when
        when(tokenRepository.save(registrationToken)).thenReturn(registrationToken);

        RegistrationToken result = tokenService.saveToken(registrationToken);

        // then
        verify(tokenRepository).save(registrationToken);
        assertThat(result, is(registrationToken));
    }

    @Test
    void testGetToken() {
        // given
        String token = "token";
        RegistrationToken registrationToken = new RegistrationToken(token, null);

        // when
        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(registrationToken));

        Optional<RegistrationToken> result = tokenService.getToken(token);

        // then
        verify(tokenRepository).findByToken(token);
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(registrationToken));
    }
}
