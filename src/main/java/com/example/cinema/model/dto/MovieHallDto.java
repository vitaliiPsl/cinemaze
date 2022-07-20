package com.example.cinema.model.dto;

import com.example.cinema.model.entities.movie.Seat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class MovieHallDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @Min(value = 1, message = "There must be at least one seat row")
    private int numberOfSeatRows;

    @Min(value = 1, message = "There must be at least one seat per row")
    private int numberOfSeatsPerRow;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties("movieHall")
    private Set<Seat> seats = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieHallDto that = (MovieHallDto) o;
        return id == that.id && numberOfSeatRows == that.numberOfSeatRows && numberOfSeatsPerRow == that.numberOfSeatsPerRow;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numberOfSeatRows, numberOfSeatsPerRow);
    }
}
