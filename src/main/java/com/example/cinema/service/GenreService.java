package com.example.cinema.service;

import com.example.cinema.exceptions.EntityAlreadyExistsException;
import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.entities.movie.Genre;
import com.example.cinema.model.entities.movie.Movie;
import com.example.cinema.persistence.GenreRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class GenreService {
    private final GenreRepository genreRepository;

    public Genre saveGenre(Genre genre) {
        log.debug("save genre {}", genre);

        Optional<Genre> possibleGenre = genreRepository.findByGenre(genre.getGenre());
        if (possibleGenre.isPresent()) {
            log.warn("provided genre already exists");
            throw new EntityAlreadyExistsException(genre.getGenre(), Genre.class);
        }

        return genreRepository.save(genre);
    }

    public void deleteGenre(long id) {
        log.debug("delete genre {}", id);

        Genre genre = genreRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, Genre.class));

        genreRepository.delete(genre);
    }

    public Genre updateGenre(long id, Genre genre) {
        log.debug("update genre {} with {}", genre, id);

        genreRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, Movie.class));
        genre.setId(id);

        return genreRepository.save(genre);
    }

    @Transactional(readOnly = true)
    public Genre getGenre(long id) {
        log.debug("get genre by id: {}", id);

        return genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, Genre.class));
    }

    @Transactional(readOnly = true)
    public Genre getGenre(String genre) {
        log.debug("get genre by genre name: {}", genre);

        return genreRepository.findByGenre(genre)
                .orElseThrow(() -> new EntityNotFoundException(genre, Genre.class));
    }

    @Transactional(readOnly = true)
    public List<Genre> getAllGenres() {
        log.debug("get all genres");

        return genreRepository.findAll();
    }
}
