package com.example.cinema.web;

import com.example.cinema.model.dto.movie.GenreDto;
import com.example.cinema.model.entities.movie.Genre;
import com.example.cinema.model.errors.ApiError;
import com.example.cinema.service.GenreService;
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

@Tag(name = "Genre API")
@AllArgsConstructor
@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final ModelMapper modelMapper;
    private final GenreService genreService;

    @Operation(summary = "Save a genre", description = "Returns DTO of saved genre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved"),
            @ApiResponse(responseCode = "400", description = "Isn't valid or already exists", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Genre to save")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public GenreDto saveGenre(
            @Valid @RequestBody GenreDto genreDto
    ) {
        Genre genre = modelMapper.map(genreDto, Genre.class);

        genre = genreService.saveGenre(genre);

        return mapGenreToGenreDto(genre);
    }

    @Operation(summary = "Delete genre by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "The genre was not found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @DeleteMapping("/{id}")
    public void deleteGenre(
            @Parameter(description = "Genre id", example = "2")
            @PathVariable long id
    ) {
        genreService.deleteGenre(id);
    }

    @Operation(summary = "Update genre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "400", description = "Isn't valid", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "404", description = "The genre was not found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Genre to save")
    @PutMapping("/{id}")
    public GenreDto updateGenre(
            @Parameter(description = "Genre id", example = "2") @PathVariable long id,
            @Valid @RequestBody GenreDto genreDto
    ) {
        Genre genre = modelMapper.map(genreDto, Genre.class);
        genre = genreService.updateGenre(id, genre);

        return mapGenreToGenreDto(genre);
    }

    @Operation(summary = "Get genre by id", description = "Returns the genre, if found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the movie"),
            @ApiResponse(responseCode = "404", description = "The genre was not found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @GetMapping("/{id}")
    public GenreDto getGenre(@Parameter(description = "Genre id", example = "2") @PathVariable long id) {
        Genre genre = genreService.getGenre(id);

        return mapGenreToGenreDto(genre);
    }

    @Operation(summary = "Get genres", description = "Returns a list of genres")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found genres"),
    })
    @GetMapping
    public List<GenreDto> getAllGenres() {
        List<Genre> genres = genreService.getAllGenres();

        return genres.stream().map(this::mapGenreToGenreDto).collect(Collectors.toList());
    }

    private GenreDto mapGenreToGenreDto(Genre genre) {
        return modelMapper.map(genre, GenreDto.class);
    }
}
