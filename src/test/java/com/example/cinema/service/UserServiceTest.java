package com.example.cinema.service;

import com.example.cinema.model.entities.Role;
import com.example.cinema.model.entities.User;
import com.example.cinema.persistence.RoleRepository;
import com.example.cinema.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testSaveClient() {
        //given
        Role client = new Role(0, "CLIENT");
        User user = getTestUser();

        //when
        when(roleRepository.findByRole("CLIENT")).thenReturn(Optional.of(client));
        userService.saveClient(user);

        //then
        assertTrue(user.getRoles().contains(client));
        verify(userRepository).save(user);
    }

    @Test
    void testSaveAdmin() {
        //given
        Role client = new Role(0, "CLIENT");
        Role admin = new Role(0, "ADMIN");
        User user = getTestUser();

        //when
        when(roleRepository.findByRole("CLIENT")).thenReturn(Optional.of(client));
        when(roleRepository.findByRole("ADMIN")).thenReturn(Optional.of(admin));
        userService.saveAdmin(user);

        //then
        assertTrue(user.getRoles().contains(admin));
        assertTrue(user.getRoles().contains(client));
        verify(userRepository).save(user);
    }

    @Test
    void testSaveRole() {
        //given
        Role role = new Role(0, "TEST");

        //when
        userService.saveRole(role);

        //then
        verify(roleRepository).save(role);
    }

    @Test
    void testAddRoleToUser() {
        //given
        long userId = 1;
        long roleId = 2;
        User user = getTestUser();
        Role role = new Role(roleId, "TEST");

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        userService.addRoleToUser(userId, roleId);

        //then
        verify(userRepository).save(user);
        assertTrue(user.getRoles().contains(role));
    }

    @Test
    void testDeleteUserById() {
        //given
        long id = 3;
        User user = getTestUser();

        //when
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        userService.deleteUser(id);

        //then
        verify(userRepository).delete(user);
    }

    @Test
    void testDeleteUserByEmail() {
        //given
        String email = "default@mail.com";
        User user = getTestUser();

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        userService.deleteUser(email);

        //then
        verify(userRepository).delete(user);
    }

    @Test
    void testGetUserById() {
        //given
        long id = 2;
        User user = getTestUser();

        //when
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Optional<User> possibleUser = userService.getUser(id);

        //then
        verify(userRepository).findById(id);
        assertTrue(possibleUser.isPresent());
        assertEquals(user, possibleUser.get());
    }

    @Test
    void testGetUserByEmail() {
        //given
        String email = "default@mail.com";
        User user = getTestUser();

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Optional<User> possibleUser = userService.getUser(email);

        //then
        verify(userRepository).findByEmail(email);
        assertTrue(possibleUser.isPresent());
        assertEquals(user, possibleUser.get());
    }

    @Test
    void testGetAllUsers() {
        //given
        int numberOfUsers = 5;
        List<User> expected = IntStream.range(0, numberOfUsers)
                .mapToObj(i -> getTestUser())
                .collect(Collectors.toList());

        //when
        when(userRepository.findAll()).thenReturn(expected);
        List<User> result = userService.getAllUsers();

        //then
        verify(userRepository).findAll();
        assertEquals(expected, result);
    }

    private static User getTestUser() {
        return new User(0, "John", "Doe", "j.doe@mail.com", "password", new HashSet<>());
    }
}