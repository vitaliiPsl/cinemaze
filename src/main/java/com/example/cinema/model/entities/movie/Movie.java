package com.example.cinema.model.entities.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
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
    private String name;
    private String trailerUrl;

    @Column(length = 2048)
    private String overview;
    private long duration;

    @JsonIgnoreProperties("movies")
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Genre> genres = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> directors = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> actors = new HashSet<>();

    private String posterImage;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> previewImages = new HashSet<>();

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
