package com.example.cinema.model.dto.movie;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Data
public class GenreDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Genre id", example = "2")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Schema(title = "Genre name", example = "Action", required = true)
    @NotBlank(message = "You have to provide the name of the genre")
    private String genre;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenreDto genreDto = (GenreDto) o;
        return Objects.equals(genre, genreDto.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genre);
    }
}
