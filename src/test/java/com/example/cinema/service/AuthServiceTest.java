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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authManager;

    @Mock
    UserRepository userRepository;

    @Mock
    RegistrationTokenService tokenService;

    @Mock
    EmailService emailService;

    @InjectMocks
    AuthService authService;

    @Test
    void testRegisterClient() {
        // given
        String email = "test@mail.com";
        String password = "password";
        String encodedPassword = "encoded";

        User user = getUser(0, email, password);

        // when
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(tokenService.saveToken(ArgumentMatchers.any(RegistrationToken.class))).then(returnsFirstArg());

        RegistrationToken result = authService.registerClient(user, "");

        // then
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(user);
        verify(tokenService).saveToken(ArgumentMatchers.any(RegistrationToken.class));
        verify(emailService).sendEmailHtml(anyString(), anyString(), eq(user.getEmail()));

        assertThat(user.getPassword(), is(encodedPassword));
        assertThat(user.getRole(), is(Role.CLIENT));

        assertThat(result.getUser(), is(user));
        assertThat(result.getToken(), notNullValue());
    }

    @Test
    void testRegisterClientSendsNewConfirmationEmailIfUserExistsButNotEnabled() {
        // given
        String email = "test@mail.com";
        String password = "password";
        User user = getUser(0, email, password);

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(tokenService.saveToken(ArgumentMatchers.any(RegistrationToken.class))).then(returnsFirstArg());

        RegistrationToken result = authService.registerClient(user, "");

        // then
        verify(userRepository, never()).save(user);
        verify(tokenService).saveToken(ArgumentMatchers.any(RegistrationToken.class));
        verify(emailService).sendEmailHtml(anyString(), anyString(), eq(user.getEmail()));

        assertThat(result.getUser(), is(user));
        assertThat(result.getToken(), notNullValue());
    }

    @Test
    void testRegisterClientThrowsExceptionIfUserAlreadyExistsAndEnabled() {
        // given
        String email = "test@mail.com";
        User user = getUser(0, email, "");
        user.setEnabled(true);

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // then
        assertThrows(EntityAlreadyExistsException.class, () -> authService.registerClient(user, ""));
    }

    @Test
    void testConfirmRegistration() {
        // given
        String token = "token";

        User user = getUser(0, "test@mail.com", "password");

        RegistrationToken registrationToken = new RegistrationToken(token, user);

        // when
        when(tokenService.getToken(token)).thenReturn(Optional.of(registrationToken));
        authService.confirmRegistration(token);

        // then
        verify(tokenService).getToken(token);
        assertThat(user.isEnabled(), is(true));
        assertThat(registrationToken.getConfirmedAt(), notNullValue());
    }

    @Test
    void testConfirmRegistrationThrowsExceptionIfTokenNotFound() {
        // given
        String token = "token";

        // when
        when(tokenService.getToken(token)).thenReturn(Optional.empty());

        // then
        assertThrows(InvalidRegistrationToken.class, () -> authService.confirmRegistration(token));

        verify(tokenService).getToken(token);
    }

    @Test
    void testConfirmRegistrationThrowsExceptionIfTokenAlreadyConfirmed() {
        // given
        String token = "token";
        RegistrationToken registrationToken = new RegistrationToken(token, null);
        registrationToken.setConfirmedAt(LocalDateTime.now());

        // when
        when(tokenService.getToken(token)).thenReturn(Optional.of(registrationToken));

        // then
        assertThrows(IllegalStateException.class, () -> authService.confirmRegistration(token));

        verify(tokenService).getToken(token);
    }

    @Test
    void testConfirmRegistrationThrowsExceptionIfTokenAlreadyExpired() {
        // given
        String token = "token";
        RegistrationToken registrationToken = new RegistrationToken(token, null);
        registrationToken.setExpiresAt(LocalDateTime.now().minusMinutes(10));

        // when
        when(tokenService.getToken(token)).thenReturn(Optional.of(registrationToken));

        // then
        assertThrows(IllegalStateException.class, () -> authService.confirmRegistration(token));

        verify(tokenService).getToken(token);
    }

    @Test
    void testAuthenticateUser() {
        // given
        String email = "test@mail.com";
        String password = "password";

        String jwt = "jwt";

        AuthenticationRequestDto authRequest = new AuthenticationRequestDto(email, password);
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        User user = getUser(1, email, password);

        // when
        MockedStatic<JwtUtils> jwtUtils = Mockito.mockStatic(JwtUtils.class);
        jwtUtils.when(() -> JwtUtils.buildToken(authentication)).thenReturn(jwt);

        when(authManager.authenticate(authentication)).thenReturn(authentication);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        AuthenticationResponseDto authResponse = authService.authenticateUser(authRequest);

        // then
        verify(authManager).authenticate(authentication);
        verify(userRepository).findByEmail(email);

        assertThat(authResponse.getUser(), is(user));
        assertThat(authResponse.getJwt(), is(jwt));
    }

    @Test
    void testAuthenticateUserThrowsExceptionIfUserNotFound() {
        // given
        String email = "test@mail.com";
        String password = "password";

        AuthenticationRequestDto authRequest = new AuthenticationRequestDto(email, password);
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);

        // when
        when(authManager.authenticate(authentication)).thenReturn(authentication);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> authService.authenticateUser(authRequest));

        verify(authManager).authenticate(authentication);
        verify(userRepository).findByEmail(email);
    }

    private User getUser(long id, String email, String password) {
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .build();
    }
}