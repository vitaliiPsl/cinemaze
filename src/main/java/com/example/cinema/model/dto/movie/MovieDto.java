package com.example.cinema.model.dto.movie;

import com.example.cinema.model.dto.session.MovieSessionDto;
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
import java.util.Objects;
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

    private String trailerUrl;
    private String posterImage;
    private Set<String> previewImages = new HashSet<>();

    @JsonIgnoreProperties("movies")
    private Set<GenreDto> genres = new HashSet<>();

    @JsonIgnoreProperties("movie")
    private Set<MovieSessionDto> sessions = new HashSet<>();

    private Set<String> directors = new HashSet<>();
    private Set<String> actors = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieDto movieDto = (MovieDto) o;
        return Objects.equals(name, movieDto.name) && Objects.equals(releaseDate, movieDto.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, releaseDate);
    }
}
