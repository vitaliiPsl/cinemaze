package com.example.cinema.model.entities.movie;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieSessionSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean booked;

    @ManyToOne
    private Seat hallSeat;

    @JsonIgnore
    @ManyToOne
    private MovieSession movieSession;

    public MovieSessionSeat(MovieSession movieSession, Seat seat) {
        this.movieSession = movieSession;
        this.hallSeat = seat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieSessionSeat that = (MovieSessionSeat) o;
        return booked == that.booked && Objects.equals(hallSeat, that.hallSeat) && Objects.equals(movieSession, that.movieSession);
    }

    @Override
    public int hashCode() {
        return Objects.hash(booked, hallSeat, movieSession);
    }
}
