package com.example.cinema.model.entities.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MovieHall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int numberOfSeatRows;
    private int numberOfSeatsPerRow;

    @OneToMany(mappedBy = "movieHall", cascade = CascadeType.ALL)
    private Set<MovieHallSeat> seats = new HashSet<>();

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
