package com.example.cinema.model.entities.session;


import com.example.cinema.model.entities.movie.Movie;
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
public class MovieSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double price;

    private LocalDateTime startsAt;

    private LocalDateTime endsAt;

    @ManyToOne(optional = false)
    private Movie movie;

    @ManyToOne(optional = false)
    private MovieHall movieHall;

    @OneToMany(mappedBy = "movieSession", cascade = CascadeType.ALL)
    private Set<MovieSessionSeat> seats = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieSession that = (MovieSession) o;
        return Objects.equals(startsAt, that.startsAt) && Objects.equals(movie, that.movie) && Objects.equals(movieHall, that.movieHall);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startsAt, movie, movieHall);
    }
}