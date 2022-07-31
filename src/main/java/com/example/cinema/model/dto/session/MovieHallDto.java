package com.example.cinema.model.dto.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class MovieHallDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Hall id", example = "5")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @Schema(title = "Number of seat rows", example = "5", required = true)
    @Min(value = 1, message = "There must be at least one seat row")
    private int numberOfSeatRows;

    @Schema(title = "Number of seat in row", example = "12", required = true)
    @Min(value = 1, message = "There must be at least one seat per row")
    private int numberOfSeatsPerRow;

    @Schema(
            accessMode = Schema.AccessMode.READ_ONLY,
            title = "List of seats",
            description = "Represents hall seats",
            implementation = MovieHallSeatDto.class
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<MovieHallSeatDto> seats = new HashSet<>();

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
