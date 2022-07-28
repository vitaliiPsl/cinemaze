package com.example.cinema.model.dto.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Movie id", example = "4")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Schema(title = "Movie name", example = "Now you see me", required = true)
    @NotBlank(message = "The name of movie the is required")
    private String name;

    @Schema(
            title = "Short movie overview",
            example = "Movie about the team of talented illusionists called the Four Horsemen",
            minLength = 24,
            maxLength = 2048,
            required = true
    )
    @Size(min = 24, max = 2048, message = "You need to provide a short overview of this movie. From 24 up to 1024 characters")
    private String overview;

    @Schema(title = "Movie duration in minutes", example = "153", minimum = "1", required = true)
    @Min(value = 1, message = "Movie duration must be longer than 1 minute")
    private Long duration;

    @Schema(title = "Movie release date", example = "2013-03-31", required = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotBlank(message = "The release date is required")
    private LocalDate releaseDate;

    @Schema(title = "Movie trailer URL", example = "https://www.youtube.com/watch?v=mqqft2x_Aa4")
    private String trailerUrl;

    @Schema(
            accessMode = Schema.AccessMode.READ_ONLY,
            title = "Identifier of the movie poster image",
            example = "5ea2a63f-070b-491f-9b20-074e438f92b6.jpg"
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String posterImage;

    @Schema(
            accessMode = Schema.AccessMode.READ_ONLY,
            title = "List of the movie preview images identifiers",
            example = "['5q...b6.jpg', '3t...qw.jpg']"
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<String> previewImages = new HashSet<>();

    @Schema(
            accessMode = Schema.AccessMode.READ_ONLY,
            title = "List of movie genres",
            implementation = GenreDto.class
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties("movies")
    private Set<GenreDto> genres = new HashSet<>();

    @Schema(
            title = "List of the movie directors",
            example = "['Matt Reeves']"
    )
    private Set<String> directors = new HashSet<>();

    @Schema(
            title = "List of the movie cast",
            example = "['Colin Farrell', 'Zoe Kravitz']"
    )
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
