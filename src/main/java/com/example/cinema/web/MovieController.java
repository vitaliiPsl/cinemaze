package com.example.cinema.web;

import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.entities.movie.Movie;
import com.example.cinema.service.MovieService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    public void saveMovie(
            @Valid Movie movie,
            @RequestParam(value = "poster", required = false) MultipartFile posterImage,
            @RequestParam(value = "previews", required = false) MultipartFile[] previews
    ) {
        movieService.saveMovie(movie, posterImage, previews);
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable(name = "id") long id){
        movieService.deleteMovie(id);
    }

    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable long id){
        return movieService.getMovie(id).orElseThrow(() -> new EntityNotFoundException(id, Movie.class));
    }

    @GetMapping
    public List<Movie> getMovies(@RequestParam(name = "name", required = false) String name){
        if(name != null) {
            return movieService.getMoviesByName(name);
        }

        return movieService.getAllMovies();
    }

    @GetMapping(value = "/poster/{poster}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getPosterImage(@PathVariable(name = "poster") String poster){
        return movieService.getPosterImage(poster);
    }

    @GetMapping(value = "/preview/{preview}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getPreviewImage(@PathVariable(name = "preview") String preview){
        return movieService.getPreviewImage(preview);
    }
}
