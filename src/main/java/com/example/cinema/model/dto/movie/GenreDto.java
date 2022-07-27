package com.example.cinema.model.dto.movie;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Data
public class GenreDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

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
