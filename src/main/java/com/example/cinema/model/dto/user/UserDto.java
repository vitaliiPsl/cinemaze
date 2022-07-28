package com.example.cinema.model.dto.user;

import com.example.cinema.model.entities.user.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Data
public class UserDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "User id", example = "6")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "User role", example = "CLIENT", implementation = Role.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Role role;

    @Schema(title = "First name", example = "John", required = true)
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(title = "Last name", example = "Doe", required = true)
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(title = "Email address", example = "j.doe@mail.com", required = true)
    @Email(message = "You have to provide valid email address")
    @NotBlank(message = "You have to provide your email")
    private String email;

    @Schema(title = "Password", example = "qW5_?te3", minLength = 6, required = true)
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