package com.example.cinema.model.entities.user.token;

import com.example.cinema.model.entities.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

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
}
