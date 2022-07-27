package com.example.cinema.service;

import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.entities.movie.Genre;
import com.example.cinema.model.entities.movie.Movie;
import com.example.cinema.persistence.MovieRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class MovieService {
    private final MovieRepository movieRepository;

    private final GenreService genreService;
    private final ImageService imageService;

    public Movie saveMovie(Movie movie, MultipartFile posterImage, MultipartFile[] previewImages) {
        log.debug("save movie {}", movie);

        log.debug("poster image: {}", posterImage);
        if (posterImage != null) {
            String poster = imageService.savePosterImage(posterImage);
            movie.setPosterImage(poster);
        }

        log.debug("preview images: {}", Arrays.toString(previewImages));
        Set<String> previewsIdentifiers = savePreviewImages(previewImages);
        movie.setPreviewImages(previewsIdentifiers);

        return movieRepository.save(movie);
    }

    public Movie updateMovie(long id, Movie movie) {
        log.debug("update movie with id {}: {}", id, movie);
        movieRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, Movie.class));

        movie.setId(id);
        return movieRepository.save(movie);
    }

    public void deleteMovie(long id) {
        log.debug("delete movie by id: {}", id);

        Movie movie = getMovie(id);

        movieRepository.delete(movie);
    }

    @Transactional(readOnly = true)
    public Movie getMovie(long id) {
        log.debug("get movie by id: {}", id);

        return movieRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, Movie.class));
    }

    @Transactional(readOnly = true)
    public List<Movie> getMoviesByName(String name) {
        log.debug("get movie by name: {}", name);

        return movieRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<Movie> getAllMovies() {
        log.debug("get all movies");

        return movieRepository.findAll();
    }

    public void addGenreToMovie(long movieId, long genreId) {
        log.debug("add genre {} to movie {}", movieId, genreId);

        Movie movie = getMovie(movieId);
        Genre genre = genreService.getGenre(genreId);

        movie.getGenres().add(genre);
    }

    public void removeGenreFromMovie(long movieId, long genreId) {
        log.debug("remove genre {} from movie {}", movieId, genreId);

        Movie movie = getMovie(movieId);
        Genre genre = genreService.getGenre(genreId);

        movie.getGenres().remove(genre);
    }

    private Set<String> savePreviewImages(MultipartFile[] previewImages) {
        Set<String> previewsIdentifiers = new HashSet<>();

        if (previewImages == null || previewImages.length == 0) {
            return previewsIdentifiers;
        }

        for (MultipartFile previewImage : previewImages) {
            String preview = imageService.savePreviewImage(previewImage);
            previewsIdentifiers.add(preview);
        }

        return previewsIdentifiers;
    }
}
