package com.example.cinema.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Objects;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "person")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "You need to provide your first name")
    private String firstName;

    @NotBlank(message = "You need to provide your last name")
    private String lastName;

    @Column(unique = true)
    @NotBlank(message = "You have to provide your email")
    @Email(message = "Your need to provide correct email address")
    private String email;

    @NotBlank
    @Size(min = 6, message = "Your password must contain at least 6 symbols")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
