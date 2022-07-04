package com.example.cinema.service;

import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.dto.MovieDto;
import com.example.cinema.model.entities.movie.Genre;
import com.example.cinema.model.entities.movie.Movie;
import com.example.cinema.persistence.GenreRepository;
import com.example.cinema.persistence.ImageRepository;
import com.example.cinema.persistence.MovieRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class MovieService {
    public final ModelMapper modelMapper;
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ImageRepository imageRepository;

    public MovieDto saveMovie(MovieDto movieDto, MultipartFile posterImage, MultipartFile[] previewImages) {
        log.debug("save movie {}", movieDto);
        Movie movie = modelMapper.map(movieDto, Movie.class);

        String poster = imageRepository.savePosterImage(posterImage);
        movie.setPosterImage(poster);

        if (previewImages != null && previewImages.length != 0) {
            for (MultipartFile previewImage : previewImages) {
                String preview = imageRepository.savePreviewImage(previewImage);
                movie.addPreviewImage(preview);
            }
        }
        movieRepository.save(movie);
        return modelMapper.map(movie, MovieDto.class);
    }

    public void updateMovie(long id, MovieDto movieDto) {
        log.debug("update movie with id {}: {}", id, movieDto);
        movieRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, Movie.class));

        Movie movie = modelMapper.map(movieDto, Movie.class);
        movie.setId(id);

        movieRepository.save(movie);
    }

    public void deleteMovie(long id) {
        log.debug("delete movie by id: {}", id);

        Movie movie = movieRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, Movie.class));
        movieRepository.delete(movie);
    }

    @Transactional(readOnly = true)
    public MovieDto getMovie(long id) {
        log.debug("get movie by id: {}", id);

        return movieRepository.findById(id)
                .map(this::mapMovieToMovieDto)
                .orElseThrow(() -> new EntityNotFoundException(id, Movie.class));
    }

    public byte[] getPosterImage(String identifier) {
        log.debug("get poster by identifier: {}", identifier);

        return imageRepository.loadPosterImage(identifier);
    }

    public byte[] getPreviewImage(String identifier) {
        log.debug("get preview by identifier: {}", identifier);

        return imageRepository.loadPreviewImage(identifier);
    }

    @Transactional(readOnly = true)
    public List<MovieDto> getMoviesByName(String name) {
        log.debug("get movie by name: {}", name);

        return movieRepository.findByNameContainingIgnoreCase(name)
                .stream().map(this::mapMovieToMovieDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovieDto> getAllMovies() {
        log.debug("get all movies");

        return movieRepository.findAll()
                .stream().map(this::mapMovieToMovieDto)
                .collect(Collectors.toList());
    }

    public void addGenreToMovie(long movieId, long genreId) {
        log.debug("add genre {} to movie {}", movieId, genreId);

        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new EntityNotFoundException(movieId, Movie.class));
        Genre genre = genreRepository.findById(genreId).orElseThrow(() -> new EntityNotFoundException(genreId, Genre.class));

        movie.getGenres().add(genre);
    }

    public void removeGenreFromMovie(long movieId, long genreId) {
        log.debug("remove genre {} from movie {}", movieId, genreId);

        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new EntityNotFoundException(movieId, Movie.class));
        Genre genre = genreRepository.findById(genreId).orElseThrow(() -> new EntityNotFoundException(genreId, Genre.class));

        movie.getGenres().remove(genre);
    }

    public MovieDto mapMovieToMovieDto(Movie movie) {
        return modelMapper.map(movie, MovieDto.class);
    }
}
