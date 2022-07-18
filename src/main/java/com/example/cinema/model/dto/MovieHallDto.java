package com.example.cinema.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.Objects;

@Data
public class MovieHallDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @Min(1)
    private int numberOfSeatRows;

    @Min(1)
    private int numberOfSeatsPerRow;

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
