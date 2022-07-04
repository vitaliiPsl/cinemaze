package com.example.cinema.web;

import com.example.cinema.model.dto.MovieDto;
import com.example.cinema.service.MovieService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    public MovieDto saveMovie(
            @Valid MovieDto movie,
            @RequestParam(value = "poster", required = false) MultipartFile posterImage,
            @RequestParam(value = "previews", required = false) MultipartFile[] previews
    ) {
        return movieService.saveMovie(movie, posterImage, previews);
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable(name = "id") long id){
        movieService.deleteMovie(id);
    }

    @GetMapping("/{id}")
    public MovieDto getMovieById(@PathVariable long id){
        return movieService.getMovie(id);
    }

    @GetMapping
    public List<MovieDto> getMovies(@RequestParam(name = "name", required = false) String name){
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

    @PutMapping("/{movieId}/genres/{genreId}")
    public void addGenreToMovie(@PathVariable(name = "movieId") long movieId, @PathVariable(name = "genreId") long genreId) {
        movieService.addGenreToMovie(movieId, genreId);
    }

    @DeleteMapping("/{movieId}/genres/{genreId}")
    public void removeGenreFromMovie(@PathVariable(name = "movieId") long movieId, @PathVariable(name = "genreId") long genreId) {
        movieService.removeGenreFromMovie(movieId, genreId);
    }
}
