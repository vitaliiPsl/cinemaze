package com.example.cinema.model.entities.movie;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
public class MovieHall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int numberOfSeatRows;
    private int numberOfSeatsPerRow;

    @OneToMany(mappedBy = "movieHall", cascade = CascadeType.ALL)
    private Set<Seat> seats = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieHall movieHall = (MovieHall) o;
        return id == movieHall.id && numberOfSeatRows == movieHall.numberOfSeatRows && numberOfSeatsPerRow == movieHall.numberOfSeatsPerRow;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numberOfSeatRows, numberOfSeatsPerRow);
    }
}
