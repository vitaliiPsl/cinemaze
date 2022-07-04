package com.example.cinema.model.dto;

import com.example.cinema.model.entities.movie.Movie;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreDto {
    private Long id;

    @NotBlank(message = "You have to provide the name of the genre")
    private String genre;

    @JsonIgnoreProperties("genres")
    private Set<Movie> movies = new HashSet<>();
}
