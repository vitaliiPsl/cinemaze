package com.example.cinema.web;

import com.example.cinema.model.dto.movie.MovieDto;
import com.example.cinema.model.entities.movie.Movie;
import com.example.cinema.model.errors.ApiError;
import com.example.cinema.service.ImageService;
import com.example.cinema.service.MovieService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Movies API")
@RestController
@RequestMapping("/api/movies")
@AllArgsConstructor
public class MovieController {

    private final ModelMapper modelMapper;
    private final MovieService movieService;
    private final ImageService imageService;

    @Operation(summary = "Save a movie", description = "Returns DTO of saved movie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved"),
            @ApiResponse(responseCode = "400", description = "Isn't valid", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public MovieDto saveMovie(
            @Parameter(description = "Movie to save") @Valid MovieDto movieDto,
            @Parameter(description = "Poster images") @RequestParam(value = "poster", required = false) MultipartFile posterImage,
            @Parameter(description = "Array of preview images") @RequestParam(value = "previews", required = false) MultipartFile[] previews
    ) {
        Movie movie = modelMapper.map(movieDto, Movie.class);
        movie = movieService.saveMovie(movie, posterImage, previews);

        return mapMovieToMovieDto(movie);
    }

    @Operation(summary = "Delete movie by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "The movie was not found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @DeleteMapping("/{id}")
    public void deleteMovie(
            @Parameter(description = "Movie id", example = "3")
            @PathVariable long id
    ) {
        movieService.deleteMovie(id);
    }

    @Operation(summary = "Get movie by id", description = "Returns the movie, if found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the movie"),
            @ApiResponse(responseCode = "404", description = "The movie was not found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @GetMapping("/{id}")
    public MovieDto getMovieById(
            @Parameter(description = "Movie id", example = "3")
            @PathVariable long id
    ) {
        Movie movie = movieService.getMovie(id);

        return mapMovieToMovieDto(movie);
    }

    @Operation(summary = "Get movies", description = "Returns a list of movies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found movies"),
    })
    @GetMapping
    public List<MovieDto> getMovies(
            @Parameter(description = "Name of the movie")
            @RequestParam(name = "name", required = false) String name
    ) {
        List<Movie> movies;

        if (name != null) {
            movies = movieService.getMoviesByName(name);
        } else {
            movies = movieService.getAllMovies();
        }

        return movies.stream().map(this::mapMovieToMovieDto).collect(Collectors.toList());
    }

    @Operation(summary = "Get poster image by identifier", description = "Returns poster image, if found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the poster"),
            @ApiResponse(responseCode = "404", description = "The poster wasn't found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @GetMapping(value = "/poster/{identifier}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getPosterImage(
            @Parameter(description = "Poster identifier") @PathVariable String identifier
    ) {
        return imageService.getPosterImage(identifier);
    }

    @Operation(summary = "Get preview image by identifier", description = "Returns preview image, if found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the preview"),
            @ApiResponse(responseCode = "404", description = "The preview wasn't found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @GetMapping(value = "/preview/{identifier}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getPreviewImage(
            @Parameter(description = "Preview identifier") @PathVariable String identifier
    ) {
        return imageService.getPreviewImage(identifier);
    }

    @Operation(summary = "Assign genre to movie", description = "Assign genre to movie by ids")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully assigned genre to movie"),
            @ApiResponse(responseCode = "404", description = "Genre or movie were not found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @PutMapping("/{movieId}/genres/{genreId}")
    public void addGenreToMovie(
            @Parameter(description = "Id of the movie") @PathVariable long movieId,
            @Parameter(description = "Id of the genre") @PathVariable long genreId
    ) {
        movieService.addGenreToMovie(movieId, genreId);
    }

    @Operation(summary = "Remove genre from movie", description = "Remove genre from movie by ids")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully removed genre from movie"),
            @ApiResponse(responseCode = "404", description = "Genre or movie were not found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @DeleteMapping("/{movieId}/genres/{genreId}")
    public void removeGenreFromMovie(
            @Parameter(description = "Id of the movie") @PathVariable long movieId,
            @Parameter(description = "Id of the genre") @PathVariable long genreId
    ) {
        movieService.removeGenreFromMovie(movieId, genreId);
    }

    private MovieDto mapMovieToMovieDto(Movie movie) {
        return modelMapper.map(movie, MovieDto.class);
    }
}
