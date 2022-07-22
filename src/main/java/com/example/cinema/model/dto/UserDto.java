package com.example.cinema.model.dto;

import com.example.cinema.model.entities.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Data
public class UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private Role role;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "You have to provide valid email address")
    @NotBlank(message = "You have to provide your email")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "You have to provide password")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return role == userDto.role && Objects.equals(email, userDto.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, email);
    }
}