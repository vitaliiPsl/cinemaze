package com.example.cinema.model.entities.movie;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int row;
    private int number;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    private MovieHall movieHall;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return row == seat.row && number == seat.number && Objects.equals(movieHall, seat.movieHall);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, number, movieHall);
    }
}
