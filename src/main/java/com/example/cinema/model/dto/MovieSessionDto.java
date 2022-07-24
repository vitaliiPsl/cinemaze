package com.example.cinema.model.dto;

import com.example.cinema.model.entities.movie.Movie;
import com.example.cinema.model.entities.movie.MovieHall;
import com.example.cinema.model.entities.movie.MovieSessionSeat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class MovieSessionDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @NotNull(message = "You need to provide id of the movie that will be played during the session")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long movieId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties("sessions")
    private Movie movie;

    @NotNull(message = "You need to provide id of the movie hall where session will take place")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long movieHallId;

    @JsonIgnoreProperties("seats")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private MovieHall movieHall;

    @NotNull(message = "You have to provide the time of the session beginning")
    private LocalDateTime startsAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime endsAt;

    @Min(value = 0, message = "Session price cannot be negative")
    private double price;

    @JsonIgnoreProperties("movieSession")
    private Set<MovieSessionSeat> seats = new HashSet<>();

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