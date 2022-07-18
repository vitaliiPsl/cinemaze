package com.example.cinema.service;

import com.example.cinema.exceptions.EntityAlreadyExistsException;
import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.dto.UserDto;
import com.example.cinema.model.entities.Role;
import com.example.cinema.model.entities.User;
import com.example.cinema.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    ModelMapper modelMapper;

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

        User user = getUser(0, email);
        UserDto userDto = getUserDto(email);
        userDto.setPassword(password);

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(getUserDto(email));
        UserDto result = userService.saveClient(userDto);

        // then
        assertThat(userDto.getPassword(), equalTo(null));
        assertThat(user.getPassword(), equalTo(encodedPassword));
        assertThat(user.getRole(), equalTo(Role.CLIENT));
        assertThat(result.getEmail(), equalTo(email));
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(user);
    }

    @Test
    void testRegisterClientThrowsExceptionIfUserAlreadyExists() {
        // given
        String email = "test@mail.com";
        UserDto userDto = getUserDto(email);
        User user = getUser(0, email);

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // then
        assertThrows(EntityAlreadyExistsException.class, () -> userService.saveClient(userDto));
    }

    @Test
    void testDeleteUser() {
        // given
        long id = 1;
        User user = getUser(id, "");

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
        User user = getUser(id, email);
        UserDto userDto = getUserDto(user.getEmail());

        // when
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);
        UserDto result = userService.getUser(id);

        // then
        verify(userRepository).findById(id);
        verify(modelMapper).map(user, UserDto.class);
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));
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
        User user = getUser(id, email);
        UserDto userDto = getUserDto(user.getEmail());

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);
        UserDto result = userService.getUser(email);

        // then
        verify(userRepository).findByEmail(email);
        verify(modelMapper).map(user, UserDto.class);
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));
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
        List<User> users = List.of(getUser(0, "test@mail.com"), getUser(0, "test1@mail.com"), getUser(0, "test2@mail.com"));

        // when
        when(userRepository.findAll()).thenReturn(users);
        for (var user : users) {
            when(modelMapper.map(user, UserDto.class)).thenReturn(getUserDto(user.getEmail()));
        }
        List<UserDto> resultDtoList = userService.getAllUsers();

        // then
        verify(userRepository).findAll();
        verify(modelMapper, times(users.size())).map(ArgumentMatchers.any(User.class), eq(UserDto.class));
        assertThat(resultDtoList, hasSize(emails.size()));
    }

    @Test
    void testMapUserToUserDto() {
        // given
        String email = "test@mail.com";
        User user = getUser(0, email);
        UserDto userDto = getUserDto(email);

        // when
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);
        UserDto result = userService.mapUserToUserDto(user);

        // then
        verify(modelMapper).map(user, UserDto.class);
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));
    }

    private UserDto getUserDto(String email) {
        UserDto userDto = new UserDto();
        userDto.setEmail(email);

        return userDto;
    }

    private User getUser(long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);

        return user;
    }
}