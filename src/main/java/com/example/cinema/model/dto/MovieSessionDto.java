package com.example.cinema.model.dto;

import com.example.cinema.model.entities.movie.Movie;
import com.example.cinema.model.entities.movie.MovieHall;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private MovieHall movieHall;

    @Min(0)
    private double price;

    @NotNull(message = "You have to provide the date of movie session")
    private LocalDate date;

    @NotNull(message = "You have to provide starting time of the session")
    private LocalTime startsAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalTime endsAt;
}