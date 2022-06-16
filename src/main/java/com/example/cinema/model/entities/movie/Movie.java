package com.example.cinema.model.entities.movie;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity @Table(name = "movie")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Movie name is required")
    private String name;

    private String posterImage;

    private String trailerUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> previewImages = new HashSet<>();

    @Size(min = 24, max = 1024, message = "You need to provide a short overview of this movie")
    private String overview;

    @Min(value = 1, message = "Movie duration cannot be shorter that 1 minute")
    private long duration;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releaseDate;

    public void addPreviewImage(String preview){
        previewImages.add(preview);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(name, movie.name) && Objects.equals(releaseDate, movie.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, releaseDate);
    }
}
