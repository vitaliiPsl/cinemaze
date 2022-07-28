package com.example.cinema.web;

import com.example.cinema.model.dto.session.MovieHallDto;
import com.example.cinema.model.entities.session.MovieHall;
import com.example.cinema.model.errors.ApiError;
import com.example.cinema.service.MovieHallService;
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
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Movie hall API")
@AllArgsConstructor
@RestController
@RequestMapping("api/movie-halls")
public class MovieHallController {
    private final ModelMapper modelMapper;
    private final MovieHallService movieHallService;

    @Operation(summary = "Save a movie hall", description = "Returns DTO of saved movie hall")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved"),
            @ApiResponse(responseCode = "400", description = "Isn't valid", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Movie hall to save")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public MovieHallDto saveMovieHall(@Valid @RequestBody MovieHallDto movieHallDto) {
        MovieHall movieHall = modelMapper.map(movieHallDto, MovieHall.class);
        movieHall = movieHallService.saveMovieHall(movieHall);

        return mapMovieHallToMovieHallDto(movieHall);
    }

    @Operation(summary = "Delete movie hall by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "The movie hall was not found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @DeleteMapping("{id}")
    public void deleteMovieHall(
            @Parameter(description = "Movie hall id", example = "3")
            @PathVariable long id)
    {
        movieHallService.deleteMovieHall(id);
    }

    @Operation(summary = "Get movie hall by id", description = "Returns movie hall, if found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the movie hall"),
            @ApiResponse(responseCode = "404", description = "The movie hall was not found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @GetMapping("{id}")
    public MovieHallDto getHallById(
            @Parameter(description = "Movie hall id", example = "3")
            @PathVariable long id
    ) {
        MovieHall movieHall = movieHallService.getMovieHall(id);

        return mapMovieHallToMovieHallDto(movieHall);
    }

    @Operation(summary = "Get movie halls", description = "Returns a list of movie halls")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found movie halls"),
    })
    @GetMapping
    public List<MovieHallDto> getAll() {
        List<MovieHall> movieHalls = movieHallService.getAll();

        return movieHalls.stream().map(this::mapMovieHallToMovieHallDto).collect(Collectors.toList());
    }

    private MovieHallDto mapMovieHallToMovieHallDto(MovieHall movieHall) {
        return modelMapper.map(movieHall, MovieHallDto.class);
    }
}
