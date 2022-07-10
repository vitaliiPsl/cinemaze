package com.example.cinema.web;

import com.example.cinema.model.dto.MovieSessionDto;
import com.example.cinema.service.MovieSessionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("api/movie-sessions")
public class MovieSessionController {
    private MovieSessionService movieSessionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieSessionDto saveMovieSession(@Valid @RequestBody MovieSessionDto movieSessionDto) {
        return movieSessionService.saveMovieSession(movieSessionDto);
    }

    @GetMapping("{id}")
    public MovieSessionDto getById(@PathVariable Long id) {
        return movieSessionService.getById(id);
    }

    @GetMapping
    public List<MovieSessionDto> getAllAvailable(@RequestParam(required = false) Long movieId, @RequestParam(required = false) LocalDate date) {
        if (date != null && movieId != null) {
            return movieSessionService.getAvailableByMovieAndDate(movieId, date);
        } else if (date != null) {
            return movieSessionService.getAvailableByDate(date);
        } else if (movieId != null) {
            return movieSessionService.getAvailableByMovie(movieId);
        } else {
            return movieSessionService.getAllAvailable();
        }
    }
}
