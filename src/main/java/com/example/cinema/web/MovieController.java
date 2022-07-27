package com.example.cinema.web;

import com.example.cinema.model.dto.movie.MovieDto;
import com.example.cinema.model.entities.movie.Movie;
import com.example.cinema.service.ImageService;
import com.example.cinema.service.MovieService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movies")
@AllArgsConstructor
public class MovieController {

    private final ModelMapper modelMapper;
    private final MovieService movieService;
    private final ImageService imageService;

    @PostMapping
    public MovieDto saveMovie(@Valid MovieDto movieDto, @RequestParam(value = "poster", required = false) MultipartFile posterImage, @RequestParam(value = "previews", required = false) MultipartFile[] previews) {
        Movie movie = modelMapper.map(movieDto, Movie.class);
        movie = movieService.saveMovie(movie, posterImage, previews);

        return mapMovieToMovieDto(movie);
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable(name = "id") long id) {
        movieService.deleteMovie(id);
    }

    @GetMapping("/{id}")
    public MovieDto getMovieById(@PathVariable long id) {
        Movie movie = movieService.getMovie(id);

        return mapMovieToMovieDto(movie);
    }

    @GetMapping
    public List<MovieDto> getMovies(@RequestParam(name = "name", required = false) String name) {
        List<Movie> movies;

        if (name != null) {
            movies = movieService.getMoviesByName(name);
        } else {
            movies = movieService.getAllMovies();
        }

        return movies.stream().map(this::mapMovieToMovieDto).collect(Collectors.toList());
    }

    @GetMapping(value = "/poster/{poster}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getPosterImage(@PathVariable(name = "poster") String poster) {
        return imageService.getPosterImage(poster);
    }

    @GetMapping(value = "/preview/{preview}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getPreviewImage(@PathVariable(name = "preview") String preview) {
        return imageService.getPreviewImage(preview);
    }

    @PutMapping("/{movieId}/genres/{genreId}")
    public void addGenreToMovie(@PathVariable(name = "movieId") long movieId, @PathVariable(name = "genreId") long genreId) {
        movieService.addGenreToMovie(movieId, genreId);
    }

    @DeleteMapping("/{movieId}/genres/{genreId}")
    public void removeGenreFromMovie(@PathVariable(name = "movieId") long movieId, @PathVariable(name = "genreId") long genreId) {
        movieService.removeGenreFromMovie(movieId, genreId);
    }

    private MovieDto mapMovieToMovieDto(Movie movie) {
        return modelMapper.map(movie, MovieDto.class);
    }
}
