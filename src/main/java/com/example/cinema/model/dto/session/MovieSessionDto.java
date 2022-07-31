package com.example.cinema.model.dto.session;

import com.example.cinema.model.dto.movie.MovieDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class MovieSessionDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "MovieSession id", example = "4")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @Schema(title = "Session start time", example = "2022-07-29T18:00", required = true)
    @NotNull(message = "You have to provide the time of the session beginning")
    private LocalDateTime startsAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Session end time", example = "2022-07-29T20:32")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime endsAt;

    @Schema(title = "Session price", description = "Booking price", example = "5.55", minimum = "0", required = true)
    @Min(value = 0, message = "Session price cannot be negative")
    private double price;

    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, title = "Id of the movie", example = "1", required = true)
    @NotNull(message = "You need to provide id of the movie that will be played during the session")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long movieId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Movie", implementation = MovieDto.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties("sessions")
    private MovieDto movie;

    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, title = "Id of the movie hall", example = "3", required = true)
    @NotNull(message = "You need to provide id of the movie hall where session will take place")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long movieHallId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Movie hall", implementation = MovieHallDto.class)
    @JsonIgnoreProperties("seats")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private MovieHallDto movieHall;

    @Schema(
            accessMode = Schema.AccessMode.READ_ONLY,
            title = "Movie session seats",
            description = "List of movie session seats",
            implementation = MovieSessionSeatDto.class
    )
    @JsonIgnoreProperties({"booking", "movieSession"})
    private Set<MovieSessionSeatDto> seats = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieSessionDto that = (MovieSessionDto) o;
        return movieId == that.movieId && movieHallId == that.movieHallId && Objects.equals(startsAt, that.startsAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, movieHallId, startsAt);
    }
}