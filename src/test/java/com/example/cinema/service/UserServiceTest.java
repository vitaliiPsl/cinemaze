package com.example.cinema.service;

import com.example.cinema.exceptions.EntityAlreadyExistsException;
import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.entities.user.Role;
import com.example.cinema.model.entities.user.User;
import com.example.cinema.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

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

        User result = userService.saveClient(user);

        // then
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(user);

        assertThat(result.getPassword(), equalTo(encodedPassword));
        assertThat(result.getRole(), equalTo(Role.CLIENT));
        assertThat(result.getEmail(), equalTo(email));
    }

    @Test
    void testRegisterClientThrowsExceptionIfUserAlreadyExists() {
        // given
        String email = "test@mail.com";
        User user = getUser(0, email, "");

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // then
        assertThrows(EntityAlreadyExistsException.class, () -> userService.saveClient(user));
    }

    @Test
    void testDeleteUser() {
        // given
        long id = 1;
        User user = getUser(id, "", "");

        // when
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        userService.deleteUser(id);

        // then
        verify(userRepository).findById(id);
        verify(userRepository).delete(user);
    }

    @Test
    void testDeleteUserThrowsExceptionIfUserDoesntExist() {
        // given
        long id = 1;

        // when
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(id));
    }

    @Test
    void testGetUserById() {
        // given
        long id = 1;
        String email = "test@mail.com";
        User user = getUser(id, email, "");

        // when
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        User result = userService.getUser(id);

        // then
        verify(userRepository).findById(id);
        assertThat(result.getEmail(), is(email));
    }

    @Test
    void testGetUserByIdThrowsExceptionIfUserDoesntExist() {
        // given
        long id = 1;

        // when
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(id));
    }


    @Test
    void testGetUserByEmail() {
        // given
        long id = 1;
        String email = "test@mail.com";
        User user = getUser(id, email, "");

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        User result = userService.getUser(email);

        // then
        verify(userRepository).findByEmail(email);
        assertThat(result.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void testGetUserByEmailThrowsExceptionIfUserDoesntExist() {
        // given
        String email = "test@mail.com";

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(email));
    }

    @Test
    void testGetAllUsers() {
        // given
        List<String> emails = List.of("test@mail.com", "test1@email.com", "test2@email.com");
        List<User> users = List.of(
                getUser(0, "test@mail.com", ""),
                getUser(0, "test1@mail.com", ""),
                getUser(0, "test2@mail.com", "")
        );

        // when
        when(userRepository.findAll()).thenReturn(users);
        List<User> resultList = userService.getAllUsers();

        // then
        verify(userRepository).findAll();
        assertThat(resultList, hasSize(emails.size()));
    }

    private User getUser(long id, String email, String password) {
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .build();
    }
}