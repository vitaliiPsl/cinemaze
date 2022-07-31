package com.example.cinema.web;

import com.example.cinema.model.dto.session.MovieSessionDto;
import com.example.cinema.model.entities.session.MovieSession;
import com.example.cinema.model.errors.ApiError;
import com.example.cinema.service.MovieSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Movie session API")
@AllArgsConstructor
@RestController
@RequestMapping("api/movie-sessions")
public class MovieSessionController {
    private ModelMapper modelMapper;
    private MovieSessionService movieSessionService;

    @Operation(summary = "Save a movie session", description = "Returns DTO of saved session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved"),
            @ApiResponse(responseCode = "400", description = "Isn't valid", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "404", description = "Movie or movie hall were not found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Movie session to save")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public MovieSessionDto saveMovieSession(@Valid @RequestBody MovieSessionDto movieSessionDto) {
        MovieSession movieSession = modelMapper.map(movieSessionDto, MovieSession.class);
        movieSession = movieSessionService.saveMovieSession(movieSession, movieSessionDto.getMovieId(), movieSessionDto.getMovieHallId());

        return mapMovieSessionToMovieSessionDto(movieSession);
    }

    @Operation(summary = "Get movie session by id", description = "Returns the movie session, if found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the movie session"),
            @ApiResponse(responseCode = "404", description = "The movie session was not found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @GetMapping("{id}")
    public MovieSessionDto getById(
            @Parameter(description = "Movie session id", example = "7")
            @PathVariable Long id
    ) {
        MovieSession movieSession = movieSessionService.getMovieSession(id);

        return mapMovieSessionToMovieSessionDto(movieSession);
    }

    @Operation(summary = "Get movie sessions", description = "Returns list of movie sessions. Allows to search by movie or session date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the movie session"),
            @ApiResponse(responseCode = "404", description = "Movie was not found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @GetMapping
    public List<MovieSessionDto> getAllAvailable(
            @Parameter(description = "Id of the movie")
            @RequestParam(required = false) Long movieId,
            @Parameter(description = "Session date")
            @RequestParam(required = false) LocalDate date
    ) {
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
