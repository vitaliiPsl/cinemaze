package com.example.cinema.model.dto;

import com.example.cinema.model.entities.Role;
import lombok.Data;
import com.example.cinema.model.entities.User;

import java.util.Set;

@Data
public class UserDto {
    private long id;
    private String firstName;
    private String lastName;
    private String username;

    private Set<Role> roles;

    public UserDto(User user){
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getEmail();
        this.roles = user.getRoles();
    }
}