package com.example.cinema.web;

import com.example.cinema.model.dto.MovieSessionDto;
import com.example.cinema.model.entities.movie.MovieSession;
import com.example.cinema.service.MovieSessionService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("api/movie-sessions")
public class MovieSessionController {
    private ModelMapper modelMapper;
    private MovieSessionService movieSessionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieSessionDto saveMovieSession(@Valid @RequestBody MovieSessionDto movieSessionDto) {
        MovieSession movieSession = modelMapper.map(movieSessionDto, MovieSession.class);
        movieSession = movieSessionService.saveMovieSession(movieSession, movieSessionDto.getMovieId(), movieSessionDto.getMovieHallId());

        return mapMovieSessionToMovieSessionDto(movieSession);
    }

    @GetMapping("{id}")
    public MovieSessionDto getById(@PathVariable Long id) {
        MovieSession movieSession = movieSessionService.getById(id);

        return mapMovieSessionToMovieSessionDto(movieSession);
    }

    @GetMapping
    public List<MovieSessionDto> getAllAvailable(@RequestParam(required = false) Long movieId, @RequestParam(required = false) LocalDate date) {
        List<MovieSession> movieSessions;

        if (date != null && movieId != null) {
            movieSessions = movieSessionService.getAvailableByMovieAndDate(movieId, date);
        } else if (date != null) {
            movieSessions = movieSessionService.getAvailableByDate(date);
        } else if (movieId != null) {
            movieSessions = movieSessionService.getAvailableByMovie(movieId);
        } else {
            movieSessions = movieSessionService.getAllAvailable();
        }

        return movieSessions.stream().map(this::mapMovieSessionToMovieSessionDto).collect(Collectors.toList());
    }

    private MovieSessionDto mapMovieSessionToMovieSessionDto(MovieSession movieSession) {
        return modelMapper.map(movieSession, MovieSessionDto.class);
    }
}
