package com.example.cinema.model.entities.movie;

import com.example.cinema.model.entities.session.MovieSession;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movie")
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
    @ToString.Exclude
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

    @OneToMany(mappedBy = "movie", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private Set<MovieSession> sessions = new HashSet<>();

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
