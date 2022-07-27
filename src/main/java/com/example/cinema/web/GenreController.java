package com.example.cinema.web;

import com.example.cinema.model.dto.GenreDto;
import com.example.cinema.model.entities.movie.Genre;
import com.example.cinema.service.GenreService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final ModelMapper modelMapper;
    private final GenreService genreService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public GenreDto saveGenre(@Valid @RequestBody GenreDto genreDto) {
        Genre genre = modelMapper.map(genreDto, Genre.class);

        genre = genreService.saveGenre(genre);

        return mapGenreToGenreDto(genre);
    }

    @DeleteMapping("/{id}")
    public void deleteGenre(@PathVariable long id) {
        genreService.deleteGenre(id);
    }

    @PutMapping("/{id}")
    public GenreDto updateGenre(@PathVariable long id, @Valid @RequestBody GenreDto genreDto) {
        Genre genre = modelMapper.map(genreDto, Genre.class);
        genre = genreService.updateGenre(id, genre);

        return mapGenreToGenreDto(genre);
    }

    @GetMapping("/{id}")
    public GenreDto getGenre(@PathVariable(name = "id") long id) {
        Genre genre = genreService.getGenre(id);

        return mapGenreToGenreDto(genre);
    }

    @GetMapping
    public List<GenreDto> getAllGenres() {
        List<Genre> genres = genreService.getAllGenres();

        return genres.stream().map(this::mapGenreToGenreDto).collect(Collectors.toList());
    }

    private GenreDto mapGenreToGenreDto(Genre genre) {
        return modelMapper.map(genre, GenreDto.class);
    }
}
