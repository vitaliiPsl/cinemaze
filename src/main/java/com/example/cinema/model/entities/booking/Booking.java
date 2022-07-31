package com.example.cinema.model.entities.booking;

import com.example.cinema.model.entities.session.MovieSession;
import com.example.cinema.model.entities.session.MovieSessionSeat;
import com.example.cinema.model.entities.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @Column(nullable = false)
    private double totalPrice;

    private LocalDateTime bookedAt = LocalDateTime.now();

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private MovieSession movieSession;

    @OneToMany(mappedBy = "booking")
    private Set<MovieSessionSeat> seats = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(user, booking.user) && Objects.equals(movieSession, booking.movieSession);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, movieSession);
    }
}
