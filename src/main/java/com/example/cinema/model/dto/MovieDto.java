package com.example.cinema.model.dto;

import com.example.cinema.model.entities.movie.Genre;
import com.example.cinema.model.entities.movie.MovieSession;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Long id;

    @NotBlank(message = "The name of movie the is required")
    private String name;

    @Size(min = 24, max = 2048, message = "You need to provide a short overview of this movie. From 24 up to 1024 characters")
    private String overview;

    @Min(value = 1, message = "Movie duration must be longer than 1 minute")
    private Long duration;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releaseDate;

    private String posterImage;
    private Set<String> previewImages = new HashSet<>();

    private String trailerUrl;

    @JsonIgnoreProperties("movies")
    private Set<Genre> genres = new HashSet<>();

    @JsonIgnoreProperties("movie")
    private Set<MovieSession> sessions = new HashSet<>();

    private Set<String> directors = new HashSet<>();
    private Set<String> actors = new HashSet<>();
}
