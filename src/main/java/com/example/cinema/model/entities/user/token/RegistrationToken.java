package com.example.cinema.model.entities.user.token;

import com.example.cinema.model.entities.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RegistrationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    @ManyToOne(optional = false)
    private User user;

    public RegistrationToken(String token, User user) {
        this.token = token;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = createdAt.plusMinutes(15);
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrationToken that = (RegistrationToken) o;
        return Objects.equals(token, that.token) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, user);
    }
}
