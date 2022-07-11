package com.example.cinema.model.entities.movie;


import com.example.cinema.config.jpa.converters.LocalTimeConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MovieSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double price;

    private LocalDate date;

    @Convert(converter = LocalTimeConverter.class)
    private LocalTime startsAt;

    @Convert(converter = LocalTimeConverter.class)
    private LocalTime endsAt;

    @ManyToOne(optional = false)
    private Movie movie;

    @ManyToOne(optional = false)
    private MovieHall movieHall;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieSession that = (MovieSession) o;
        return Objects.equals(date, that.date) && Objects.equals(startsAt, that.startsAt) && Objects.equals(movie, that.movie) && Objects.equals(movieHall, that.movieHall);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, startsAt, movie, movieHall);
    }
}